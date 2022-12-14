package kh.farrukh.billypay.apis.auth;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.InvalidClaimException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import kh.farrukh.billypay.apis.image.ImageRepository;
import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.BadRequestException;
import kh.farrukh.billypay.global.exception.custom.exceptions.DuplicateResourceException;
import kh.farrukh.billypay.global.exception.custom.exceptions.PhoneNumberPasswordWrongException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import kh.farrukh.billypay.global.exception.custom.exceptions.token_exceptions.ExpiredTokenException;
import kh.farrukh.billypay.global.exception.custom.exceptions.token_exceptions.InvalidRoleTokenException;
import kh.farrukh.billypay.global.exception.custom.exceptions.token_exceptions.InvalidSignatureTokenException;
import kh.farrukh.billypay.global.exception.custom.exceptions.token_exceptions.WrongTypeTokenException;
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
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new DuplicateResourceException("User", "email", signUpRequest.getEmail());
        }
        if (userRepository.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            throw new DuplicateResourceException("User", "phone number", signUpRequest.getPhoneNumber());
        }
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
        } catch (AlgorithmMismatchException exception) {
            throw new WrongTypeTokenException();
        } catch (SignatureVerificationException exception) {
            throw new InvalidSignatureTokenException();
        } catch (TokenExpiredException exception) {
            throw new ExpiredTokenException();
        } catch (InvalidClaimException exception) {
            throw new InvalidRoleTokenException();
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BadRequestException("Refresh token");
        }
    }
}
