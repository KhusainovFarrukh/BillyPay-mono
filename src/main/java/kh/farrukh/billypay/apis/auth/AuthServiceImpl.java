package kh.farrukh.billypay.apis.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.AppUserDTO;
import kh.farrukh.billypay.apis.user.UserRole;
import kh.farrukh.billypay.apis.user.UserService;
import kh.farrukh.billypay.global.exception.custom.exceptions.BadRequestException;
import kh.farrukh.billypay.security.authorization.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final TokenProvider tokenProvider;

/*
    @Override
    public AuthResponse signIn(LoginRequest loginRequest) {
        return null;
    }
*/

    @Override
    public AppUser signUp(RegistrationRequest registrationRequest) {
        return userService.addUser(new AppUserDTO(
            registrationRequest.getName(),
            registrationRequest.getEmail(),
            registrationRequest.getPhoneNumber(),
            registrationRequest.getPassword(),
            UserRole.USER,
            registrationRequest.getImageId()
        ));
    }

    @Override
    public AuthResponse refreshToken(String tokenHeader) {
        try {
            DecodedJWT decodedJWT = tokenProvider.validateToken(tokenHeader, true);
            if (decodedJWT != null) {
                String phoneNumber = decodedJWT.getSubject();
                AppUser user = userService.getUserByPhoneNumber(phoneNumber);
                return tokenProvider.generateTokens(user);
            } else {
                throw new BadRequestException("Refresh token");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BadRequestException("Refresh token");
        }
    }
}
