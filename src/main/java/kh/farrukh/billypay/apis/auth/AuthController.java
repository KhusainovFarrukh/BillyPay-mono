package kh.farrukh.billypay.apis.auth;

import kh.farrukh.billypay.apis.user.AppUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kh.farrukh.billypay.apis.auth.Constants.ENDPOINT_REFRESH_TOKEN;
import static kh.farrukh.billypay.apis.auth.Constants.ENDPOINT_REGISTRATION;
import static kh.farrukh.billypay.security.authentication.EmailPasswordAuthenticationFilter.ENDPOINT_LOGIN;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

/*    @PostMapping(ENDPOINT_LOGIN)
    public ResponseEntity<AuthResponse> signIn(@RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(authService.signIn(loginRequest), HttpStatus.OK);
    }*/

    @PostMapping(ENDPOINT_REGISTRATION)
    public ResponseEntity<AppUser> signUp(@RequestBody RegistrationRequest registrationRequest) {
        return new ResponseEntity<>(authService.signUp(registrationRequest), HttpStatus.OK);
    }

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader) {
        return new ResponseEntity<>(authService.refreshToken(tokenHeader), HttpStatus.OK);
    }
}
