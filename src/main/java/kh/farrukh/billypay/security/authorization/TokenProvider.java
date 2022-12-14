package kh.farrukh.billypay.security.authorization;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.billypay.apis.auth.AuthResponse;
import kh.farrukh.billypay.apis.user.AppUser;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static kh.farrukh.billypay.security.utils.SecurityUtils.KEY_ROLE;

@Getter
@Component
public class TokenProvider implements InitializingBean {

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm";
    private static final DateFormat formatter = new SimpleDateFormat(DATE_FORMAT);

    private final String secret;
    private final long accessTokenValidityInMinutes;
    private final long refreshTokenValidityInMinutes;

    private Algorithm accessTokenAlgorithm;
    private Algorithm refreshTokenAlgorithm;

    public TokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access-token-validity-in-minutes}") long accessTokenValidityInMinutes,
        @Value("${jwt.refresh-token-validity-in-minutes}") long refreshTokenValidityInMinutes) {
        this.secret = secret;
        this.accessTokenValidityInMinutes = accessTokenValidityInMinutes;
        this.refreshTokenValidityInMinutes = refreshTokenValidityInMinutes;
    }

    @Override
    public void afterPropertiesSet() {
        accessTokenAlgorithm = Algorithm.HMAC256(secret);
        refreshTokenAlgorithm = Algorithm.HMAC384(secret);
    }

    /**
     * It creates two tokens, one for access and one for refresh, and returns them in an AuthResponse object
     *
     * @param user The user details object that contains the user's information.
     */
    public AuthResponse generateTokens(AppUser user) {
        long currentMillis = System.currentTimeMillis();
        Date accessExpireDate = new Date(currentMillis + accessTokenValidityInMinutes * 60 * 1000);
        Date refreshExpireDate = new Date(currentMillis + refreshTokenValidityInMinutes * 60 * 1000);

        return new AuthResponse(
            user.getRole(),
            createToken(user, accessExpireDate, accessTokenAlgorithm),
            createToken(user, refreshExpireDate, refreshTokenAlgorithm),
            formatter.format(accessExpireDate),
            formatter.format(refreshExpireDate)
        );
    }

    /**
     * If the request has an Authorization header with a Bearer token, then verify the token and return the decoded JWT
     *
     * @param authHeader The refresh token in request header
     * @return A DecodedJWT object
     */
    public DecodedJWT validateToken(String authHeader, boolean isRefresh) {
        Algorithm algorithm;
        if (isRefresh) {
            algorithm = refreshTokenAlgorithm;
        } else {
            algorithm = accessTokenAlgorithm;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            JWTVerifier jwtVerifier = JWT.require(algorithm).build();
            return jwtVerifier.verify(token);
        } else {
            throw new RuntimeException("Token is empty");
        }
    }

    /**
     * It creates a JWT token with the username as the subject, the expiration date as the expiration date,
     * and the permission names list as the claim
     *
     * @param user       The user object that contains the username and role.
     * @param expireDate The date when the token will expire.
     * @param algorithm  The algorithm to use for signing the token.
     * @return A JWT token
     */
    private String createToken(AppUser user, Date expireDate, Algorithm algorithm) {
        return JWT.create()
            .withSubject(user.getPhoneNumber())
            .withExpiresAt(expireDate)
            .withClaim(KEY_ROLE, user.getRole().name())
            .sign(algorithm);
    }
}
