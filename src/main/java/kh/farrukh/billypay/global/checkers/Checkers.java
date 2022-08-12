package kh.farrukh.billypay.global.checkers;


import kh.farrukh.billypay.apis.image.ImageRepository;
import kh.farrukh.billypay.apis.user.AppUserDTO;
import kh.farrukh.billypay.apis.user.UserRepository;
import kh.farrukh.billypay.global.exception.custom.exceptions.BadRequestException;
import kh.farrukh.billypay.global.exception.custom.exceptions.DuplicateResourceException;
import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;

/**
 * It contains static methods that check if a resource exists in the database,
 * is unique, request parameter is valid and etc.
 */
public class Checkers {

    /**
     * If the image with the given ID doesn't exist, throw a ResourceNotFoundException
     *
     * @param imageRepository The repository that will be used to check if the image exists.
     * @param imageId         The id of the image to be checked.
     */
    public static void checkImageId(ImageRepository imageRepository, long imageId) {
        if (!imageRepository.existsById(imageId)) {
            throw new ResourceNotFoundException("Image", "id", imageId);
        }
    }

    /**
     * If the page number is less than 1, throw a BadRequestException.
     *
     * @param page The page number to return.
     */
    public static void checkPageNumber(int page) {
        if (page < 1) {
            throw new BadRequestException("Page index");
        }
    }

    /**
     * If the user doesn't exist, throw a ResourceNotFoundException.
     *
     * @param userRepository The repository that we are using to check if the user exists.
     * @param id             The id of the user to be checked
     */
    public static void checkUserId(UserRepository userRepository, long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
    }

    /**
     * If the user with given username or email already exists, throw an exception.
     *
     * @param userRepository The repository that will be used to check if the user exists.
     * @param appUserDto     The DTO that is being validated.
     */
    public static void checkUserIsUnique(UserRepository userRepository, AppUserDTO appUserDto) {
        if (userRepository.existsByPhoneNumber(appUserDto.getPhoneNumber())) {
            throw new DuplicateResourceException("User", "phone number", appUserDto.getPhoneNumber());
        }
        if (userRepository.existsByEmail(appUserDto.getEmail())) {
            throw new DuplicateResourceException("User", "email", appUserDto.getEmail());
        }
    }
}
