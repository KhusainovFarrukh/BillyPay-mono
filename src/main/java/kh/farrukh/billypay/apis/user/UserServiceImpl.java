package kh.farrukh.billypay.apis.user;

import kh.farrukh.billypay.apis.image.ImageRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.BadRequestException;
import kh.farrukh.billypay.global.exception.custom.exceptions.DuplicateResourceException;
import kh.farrukh.billypay.global.exception.custom.exceptions.NotEnoughPermissionException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import kh.farrukh.billypay.global.paging.PagingResponse;
import kh.farrukh.billypay.security.utils.CurrentUserUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static kh.farrukh.billypay.global.checkers.Checkers.*;

/**
 * It implements the UserService interface and the UserDetailsService interface,
 * uses the UserRepository, PasswordEncoder and ImageRepository to perform CRUD operations on the AppUser entity
 * <p>
 * Implements UserDetailsService to be used in Spring Security.
 * It means that this class is injected as bean dependency to Spring Security Configurations
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageRepository imageRepository;

    /**
     * If the user exists in the database, return the user, otherwise throw an exception.
     *
     * @param username The username of the user we're trying to authenticate.
     * @return UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByPhoneNumber(username).orElseThrow(
            () -> new UsernameNotFoundException("User not found in the database")
        );
    }

    /**
     * "Get all users from the database, sort them by the given sortBy and orderBy parameters, and return a PagingResponse
     * object containing the users in the given page."
     *
     * @param page     The page number to return.
     * @param pageSize The number of items to return per page.
     * @return A PagingResponse object.
     */
    @Override
    public PagingResponse<AppUser> getUsers(int page, int pageSize) {
        checkPageNumber(page);
        return new PagingResponse<>(userRepository.findAll(
            PageRequest.of(page - 1, pageSize)
        ));
    }

    /**
     * If the user exists, return the user, otherwise throw an exception.
     *
     * @param id The id of the user to retrieve
     * @return The userRepository.findById(id) is being returned.
     */
    @Override
    public AppUser getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );
    }

    /**
     * If the username or email is different from the existing one, check if it exists in the database. If it does, throw
     * an exception. If it doesn't, update the user
     *
     * @param id         The id of the user to be updated.
     * @param appUserDto The DTO object that contains the new values for the user.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser updateUser(long id, AppUserDTO appUserDto) {
        AppUser existingAppUser = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );

        if (CurrentUserUtils.isAdminOrAuthor(id, userRepository)) {

            // It checks if the username of the user is changed and if the new username is already taken.
            if (!appUserDto.getPhoneNumber().equals(existingAppUser.getPhoneNumber()) &&
                userRepository.existsByPhoneNumber(appUserDto.getPhoneNumber())) {
                throw new DuplicateResourceException("User", "phone number", appUserDto.getPhoneNumber());
            }

            // It checks if the email of the user is changed and if the new email is already taken.
            if (!appUserDto.getEmail().equals(existingAppUser.getEmail()) &&
                userRepository.existsByEmail(appUserDto.getEmail())) {
                throw new DuplicateResourceException("User", "email", appUserDto.getEmail());
            }

            existingAppUser.setName(appUserDto.getName());
            existingAppUser.setEmail(appUserDto.getEmail());
            existingAppUser.setPhoneNumber(appUserDto.getPhoneNumber());
            existingAppUser.setImage(imageRepository.findById(appUserDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", appUserDto.getImageId())
            ));

            return existingAppUser;
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * It deletes a user from the database
     *
     * @param id The id of the user to be deleted.
     */
    @Override
    public void deleteUser(long id) {
        checkUserId(userRepository, id);
        userRepository.deleteById(id);
    }

    /**
     * It takes in a user id and a UserRoleDTO object, finds the user in the database,
     * sets the user's role to the role in the UserRoleDTO object, and returns the user
     *
     * @param id      The id of the user to be updated
     * @param roleDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser setUserRole(long id, UserRoleDTO roleDto) {
        AppUser user = userRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("User", "id", id)
        );
        user.setRole(roleDto.getRole());
        return user;
    }

    /**
     * It takes in a user id and a UserImageDTO object, finds the user in the database,
     * sets the user's image to the image with id in the UserImageDTO object, and returns the user
     *
     * @param id       The id of the user to be updated
     * @param imageDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser setUserImage(long id, UserImageDTO imageDto) {
        if (CurrentUserUtils.isAdminOrAuthor(id, userRepository)) {
            AppUser user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
            );

            user.setImage(imageRepository.findById(imageDto.getImageId()).orElseThrow(
                () -> new ResourceNotFoundException("Image", "id", imageDto.getImageId())
            ));
            return user;
        } else {
            throw new NotEnoughPermissionException();
        }
    }

    /**
     * It takes in a user id and a UserPasswordDTO object, finds the user in the database,
     * checks the current password, sets the user's password to the password in the UserPasswordDTO object,
     * and returns the user
     *
     * @param id          The id of the user to be updated
     * @param passwordDto This is the object that will be passed in the request body.
     * @return The updated user.
     */
    @Override
    @Transactional
    public AppUser setUserPassword(long id, UserPasswordDTO passwordDto) {
        if (CurrentUserUtils.isAdminOrAuthor(id, userRepository)) {

            AppUser currentUser = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
            );
            if (passwordEncoder.matches(passwordDto.getPassword(), currentUser.getPassword())) {
                currentUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));
                return currentUser;
            } else {
                throw new BadRequestException("Password");
            }
        } else {
            throw new NotEnoughPermissionException();
        }
    }
}
