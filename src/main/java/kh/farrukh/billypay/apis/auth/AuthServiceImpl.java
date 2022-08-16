package kh.farrukh.billypay.apis.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.billypay.apis.image.ImageRepository;
import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.BadRequestException;
import kh.farrukh.billypay.global.exception.custom.exceptions.PhoneNumberPasswordWrongException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import kh.farrukh.billypay.security.authorization.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AuthResponse signIn(SignInRequest signInRequest) {
        AppUser appUser = userRepository.findByPhoneNumber(signInRequest.getPhoneNumber()).orElseThrow(
            () -> new PhoneNumberPasswordWrongException(PhoneNumberPasswordWrongException.Type.PHONE_NUMBER)
        );
        if (passwordEncoder.matches(signInRequest.getPassword(), appUser.getPassword())) {
            return tokenProvider.generateTokens(appUser);
        } else {
            throw new PhoneNumberPasswordWrongException(PhoneNumberPasswordWrongException.Type.PASSWORD);
        }
    }

    @Override
    public AuthResponse signUp(SignUpRequest signUpRequest) {
        AppUser user = userRepository.save(new AppUser(signUpRequest, passwordEncoder, imageRepository));
        AuthResponse authResponse = tokenProvider.generateTokens(user);
        authResponse.setUser(user);
        return authResponse;
    }

    @Override
    public AuthResponse refreshToken(String tokenHeader) {
        try {
            DecodedJWT decodedJWT = tokenProvider.validateToken(tokenHeader, true);
            if (decodedJWT != null) {
                String phoneNumber = decodedJWT.getSubject();
                AppUser user = userRepository.findByPhoneNumber(phoneNumber).orElseThrow(
                    () -> new ResourceNotFoundException("User", "phone number", phoneNumber)
                );
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
