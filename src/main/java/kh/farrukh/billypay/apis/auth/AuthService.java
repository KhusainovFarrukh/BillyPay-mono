package kh.farrukh.billypay.apis.auth;

import kh.farrukh.billypay.apis.user.AppUser;

public interface AuthService {

    AuthResponse signIn(SignInRequest signInRequest);

    AuthResponse signUp(SignUpRequest signUpRequest);

    AuthResponse refreshToken(String tokenHeader);

}
