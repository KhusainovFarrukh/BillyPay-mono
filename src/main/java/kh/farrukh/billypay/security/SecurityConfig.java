package kh.farrukh.billypay.security;

import kh.farrukh.billypay.apis.user.UserRole;
import kh.farrukh.billypay.security.authorization.JWTAccessDeniedHandler;
import kh.farrukh.billypay.security.authorization.JWTAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static kh.farrukh.billypay.apis.auth.Constants.*;
import static kh.farrukh.billypay.apis.bill.Constants.SECURITY_ENDPOINT_BILL;
import static kh.farrukh.billypay.apis.image.Constants.ENDPOINT_IMAGE;
import static kh.farrukh.billypay.apis.user.Constants.ENDPOINT_USER;
import static kh.farrukh.billypay.apis.user.Constants.SECURITY_ENDPOINT_USER_ROLE;
import static kh.farrukh.billypay.security.utils.SecurityUtils.withChildEndpoints;


/**
 * It configures the security of the application using Spring Security via JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * A function that is used to configure the security filter chain.
     *
     * @param http the HttpSecurity object
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(
        HttpSecurity http,
        JWTAuthorizationFilter authorizationFilter,
        JWTAccessDeniedHandler accessDeniedHandler
    ) throws Exception {
        // Disabling the CSRF and making the session stateless.
        http.csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // Adding the custom JWT authorization filter.
        http.addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class);

        http.formLogin().disable();

        http.authorizeRequests()
            //image endpoints
            .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_IMAGE)).permitAll()
            .antMatchers(HttpMethod.POST, withChildEndpoints(ENDPOINT_IMAGE)).permitAll()
            .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_IMAGE)).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_IMAGE)).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_IMAGE)).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            //user endpoints
            .antMatchers(HttpMethod.GET, withChildEndpoints(ENDPOINT_USER)).permitAll()
            .antMatchers(HttpMethod.PATCH, withChildEndpoints(SECURITY_ENDPOINT_USER_ROLE)).hasAnyAuthority(UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.PUT, withChildEndpoints(ENDPOINT_USER)).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.PATCH, withChildEndpoints(ENDPOINT_USER)).hasAnyAuthority(UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, withChildEndpoints(ENDPOINT_USER)).hasAnyAuthority(UserRole.SUPER_ADMIN.name())
            //bill endpoints
            .antMatchers(HttpMethod.GET, withChildEndpoints(SECURITY_ENDPOINT_BILL)).hasAnyAuthority(UserRole.USER.name(), UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.PATCH, withChildEndpoints(SECURITY_ENDPOINT_BILL)).hasAnyAuthority(UserRole.USER.name(), UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.PUT, withChildEndpoints(SECURITY_ENDPOINT_BILL)).hasAnyAuthority(UserRole.USER.name(), UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.PATCH, withChildEndpoints(SECURITY_ENDPOINT_BILL)).hasAnyAuthority(UserRole.USER.name(), UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())
            .antMatchers(HttpMethod.DELETE, withChildEndpoints(SECURITY_ENDPOINT_BILL)).hasAnyAuthority(UserRole.USER.name(), UserRole.ADMIN.name(), UserRole.SUPER_ADMIN.name())

            //auth endpoints
            .antMatchers(
                withChildEndpoints(ENDPOINT_SIGN_UP),
                withChildEndpoints(ENDPOINT_SIGN_IN),
                withChildEndpoints(ENDPOINT_REFRESH_TOKEN)
            ).permitAll()
            .anyRequest().authenticated()
            .and().exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler);

        return http.build();
    }
}
