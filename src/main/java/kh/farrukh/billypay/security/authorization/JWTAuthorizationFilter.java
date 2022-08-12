package kh.farrukh.billypay.security.authorization;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.billypay.global.exception.custom.exceptions.token_exceptions.*;
import kh.farrukh.billypay.security.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static kh.farrukh.billypay.apis.auth.Constants.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.billypay.apis.auth.Constants.ENDPOINT_REGISTRATION;
import static kh.farrukh.billypay.apis.image.Constants.ENDPOINT_IMAGE;
import static kh.farrukh.billypay.security.authentication.EmailPasswordAuthenticationFilter.ENDPOINT_LOGIN;

/**
 * If the request is not for the login or refresh token endpoints, then decode the JWT and set the authentication in the
 * security context
 */
@Component
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final HandlerExceptionResolver resolver;

    public JWTAuthorizationFilter(
        TokenProvider tokenProvider,
        @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver
    ) {
        this.tokenProvider = tokenProvider;
        this.resolver = resolver;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // This is the shouldNotFilter logic of the filter. If the request is for the home/register/login/refresh-token endpoints or
        // simple user request for getting list of languages/frameworks/reviews, then don't check JWT token.

        return // register request
            request.getRequestURI().equals(ENDPOINT_REGISTRATION) ||

                // login request
                request.getRequestURI().equals(ENDPOINT_LOGIN) ||

                // refresh token request
                request.getRequestURI().equals(ENDPOINT_REFRESH_TOKEN) ||

                // upload or download image request
                (request.getRequestURI().contains(ENDPOINT_IMAGE) && (request.getMethod().equals(HttpMethod.GET.name()) || request.getMethod().equals(HttpMethod.POST.name())));
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) {
        try {
            DecodedJWT decodedJWT = tokenProvider.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION), false);
            UsernamePasswordAuthenticationToken authenticationToken =
                SecurityUtils.getAuthenticationFromDecodedJWT(decodedJWT);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            filterChain.doFilter(request, response);
        } catch (AlgorithmMismatchException exception) {
            resolver.resolveException(request, response, null, new WrongTypeTokenException());
        } catch (SignatureVerificationException exception) {
            resolver.resolveException(request, response, null, new InvalidSignatureTokenException());
        } catch (TokenExpiredException exception) {
            resolver.resolveException(request, response, null, new ExpiredTokenException());
        } catch (InvalidClaimException exception) {
            resolver.resolveException(request, response, null, new InvalidRoleTokenException());
        } catch (Exception exception) {
            exception.printStackTrace();
            resolver.resolveException(request, response, null, new UnknownTokenException());
        }
    }
}
