package kh.farrukh.billypay.security.utils;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.billypay.apis.auth.AuthResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Utils for spring security base logic
 */
public class SecurityUtils {

    public static final String KEY_ROLE = "role";
    public static final String KEY_ACCESS_TOKEN = "access_token";
    public static final String KEY_REFRESH_TOKEN = "refresh_token";

    /**
     * It takes a decoded JWT and returns a UsernamePasswordAuthenticationToken with the username and permissions
     * from the JWT
     *
     * @param decodedJWT The decoded JWT.
     * @return A UsernamePasswordAuthenticationToken object
     */
    public static UsernamePasswordAuthenticationToken getAuthenticationFromDecodedJWT(
        DecodedJWT decodedJWT
    ) {
        String username = decodedJWT.getSubject();
        String roleName = decodedJWT.getClaim(KEY_ROLE).asString();

        return new UsernamePasswordAuthenticationToken(
            username,
            null,
            Collections.singletonList(new SimpleGrantedAuthority(roleName))
        );
    }

    /**
     * It takes a map of data and an HttpServletResponse object, and writes the data to the response as JSON
     *
     * @param authResponse This is the AuthResponse that you want to send back to the client.
     * @param response     The HttpServletResponse object.
     */
    public static void sendTokenInResponse(AuthResponse authResponse, HttpServletResponse response) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> data = new ObjectMapper().convertValue(authResponse,
            new TypeReference<>() {
            }
        );
        new ObjectMapper().writeValue(response.getOutputStream(), data);
    }

    /**
     * It sets the access token and refresh token in the response header
     *
     * @param data     The map that contains the access token and refresh token.
     * @param response The response object of the request.
     */
    public static void sendTokenInHeader(Map<String, String> data, HttpServletResponse response) {
        response.setHeader(KEY_ACCESS_TOKEN, data.get(KEY_ACCESS_TOKEN));
        response.setHeader(KEY_REFRESH_TOKEN, data.get(KEY_REFRESH_TOKEN));
    }

    /**
     * It takes a UserDetails object and returns the first role name from the list of roles
     *
     * @param user The user object that contains the user's details.
     * @return The first role of the user.
     */
    public static List<String> getPermissionNames(UserDetails user) {
        return user.getAuthorities()
            .stream().map(GrantedAuthority::getAuthority).toList().stream()
            .toList();
    }

    public static String withChildEndpoints(String endpoint) {
        return endpoint + "/**";
    }
}
