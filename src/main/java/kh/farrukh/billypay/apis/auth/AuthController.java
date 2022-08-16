package kh.farrukh.billypay.apis.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static kh.farrukh.billypay.apis.auth.Constants.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(ENDPOINT_SIGN_IN)
    public ResponseEntity<AuthResponse> signIn(@RequestBody SignInRequest signInRequest) {
        return new ResponseEntity<>(authService.signIn(signInRequest), HttpStatus.OK);
    }

    @PostMapping(ENDPOINT_SIGN_UP)
    public ResponseEntity<AuthResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        return new ResponseEntity<>(authService.signUp(signUpRequest), HttpStatus.OK);
    }

    @GetMapping(ENDPOINT_REFRESH_TOKEN)
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String tokenHeader) {
        return new ResponseEntity<>(authService.refreshToken(tokenHeader), HttpStatus.OK);
    }
}
