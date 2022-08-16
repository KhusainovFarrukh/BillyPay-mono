package kh.farrukh.billypay.apis.auth;

import kh.farrukh.billypay.apis.user.AppUser;

public interface AuthService {

//    AuthResponse signIn(LoginRequest loginRequest);

    AppUser signUp(RegistrationRequest registrationRequest);

    AuthResponse refreshToken(String tokenHeader);

}
