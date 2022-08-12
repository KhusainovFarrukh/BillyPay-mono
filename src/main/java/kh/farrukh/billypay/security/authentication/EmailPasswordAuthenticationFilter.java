package kh.farrukh.billypay.security.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import kh.farrukh.billypay.apis.auth.AuthResponse;
import kh.farrukh.billypay.apis.auth.LoginRequest;
import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.PhoneNumberPasswordWrongException;
import kh.farrukh.billypay.security.authorization.TokenProvider;
import kh.farrukh.billypay.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * It extends the default UsernamePasswordAuthenticationFilter and overrides the attemptAuthentication and
 * successfulAuthentication methods
 */
@RequiredArgsConstructor
public class EmailPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    public static final String ENDPOINT_LOGIN = "/api/v1/login";

    private final AuthenticationManager authenticationManager;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver resolver;

    /**
     * This function is called when the user submits the login form. It takes the username and password from the form,
     * creates a UsernamePasswordAuthenticationToken, and passes it to the AuthenticationManager
     *
     * @param request  The request object that contains the username and password.
     * @param response The response object that will be used to send the token to the client.
     * @return An Authentication object.
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        LoginRequest loginRequest;

        try {
            loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getPhoneNumber(), loginRequest.getPassword());
        return authenticationManager.authenticate(authenticationToken);
    }

    /**
     * If the user is authenticated, generate a JWT token and send it in the response.
     *
     * @param request        The request object
     * @param response       The response object that will be sent to the client.
     * @param chain          The FilterChain object that is used to invoke the next filter in the chain.
     * @param authentication The Authentication object that was created by the AuthenticationManager.
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        UserDetails user = (UserDetails) authentication.getPrincipal();
        AppUser appUser = userRepository.findByPhoneNumber(user.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException("User not found in the database")
        );
        AuthResponse authResponse = tokenProvider.generateTokens(appUser);
        SecurityUtils.sendTokenInResponse(authResponse, response);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        LoginRequest loginRequest = new ObjectMapper().readValue(request.getInputStream(), LoginRequest.class);
        PhoneNumberPasswordWrongException.Type type;
        if (userRepository.existsByPhoneNumber(loginRequest.getPhoneNumber())) {
            type = PhoneNumberPasswordWrongException.Type.PASSWORD;
        } else {
            type = PhoneNumberPasswordWrongException.Type.PHONE_NUMBER;
        }
        resolver.resolveException(request, response, null, new PhoneNumberPasswordWrongException(type));
    }
}
