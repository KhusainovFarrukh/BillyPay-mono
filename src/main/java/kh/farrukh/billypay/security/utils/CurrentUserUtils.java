package kh.farrukh.billypay.security.utils;

import kh.farrukh.billypay.apis.user.AppUser;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.apis.user.UserRole;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;

public class CurrentUserUtils {


    public static boolean isAdminOrAuthor(
        long authorId,
        UserRepository userRepository
    ) {
        try {
            AppUser currentUser = getCurrentUser(userRepository);
            return currentUser.getRole() == UserRole.SUPER_ADMIN ||
                currentUser.getRole() == UserRole.ADMIN ||
                currentUser.getId() == authorId;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public static boolean isAdmin(UserRepository userRepository) {
        try {
            AppUser currentUser = getCurrentUser(userRepository);
            return currentUser.getRole() == UserRole.SUPER_ADMIN || currentUser.getRole() == UserRole.ADMIN;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    public static boolean isAuthor(long authorId, UserRepository userRepository) {
        try {
            AppUser currentUser = getCurrentUser(userRepository);
            return currentUser.getId() == authorId;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    /**
     * Get the currently logged-in user.
     *
     * @return The user that is currently logged in.
     */
    public static AppUser getCurrentUser(UserRepository userRepository) {
        String email = getEmail();
        return userRepository.findByEmail(email).orElseThrow(
            () -> new ResourceNotFoundException("User", "email", email)
        );
    }

    /**
     * Get the email of the currently logged-in user.
     *
     * @return The email of the user that is currently logged in.
     */
    private static String getEmail() {
        try {
            Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

            if (principal instanceof User) {
                return ((User) principal).getUsername();
            } else {
                return (String) principal;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
