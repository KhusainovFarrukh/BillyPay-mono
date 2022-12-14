package kh.farrukh.billypay.apis.image;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static kh.farrukh.billypay.apis.image.Constants.ENDPOINT_IMAGE;

/**
 * Controller that exposes endpoints for managing images
 */
@RestController
@RequestMapping(ENDPOINT_IMAGE)
@RequiredArgsConstructor
@Validated
public class ImageController {

    private final ImageService imageService;

    /**
     * It takes a multipart image, adds it to the database, and returns the image as a response entity
     *
     * @param multipartImage The image file that is being uploaded.
     * @return The image object is being returned.
     */
    @PostMapping
    public ResponseEntity<Image> uploadImage(@RequestParam("image") MultipartFile multipartImage) {
        return new ResponseEntity<>(imageService.addImage(multipartImage), HttpStatus.CREATED);
    }

    /**
     * This function is a GET request that takes in an id and returns an image object
     *
     * @param id The id of the image you want to retrieve.
     * @return A ResponseEntity object is being returned.
     */
    @GetMapping(value = "{id}")
    public ResponseEntity<Image> getImageById(@PathVariable long id) {
        return new ResponseEntity<>(imageService.getImageById(id), HttpStatus.OK);
    }

    /**
     * It takes an id, finds the image with that id, and returns it as a resource
     *
     * @param id The id of the image to download
     * @return A Resource object.
     */
    @GetMapping(value = "{id}/download", produces = MediaType.IMAGE_JPEG_VALUE)
    public Resource downloadImageById(@PathVariable long id) {
        return imageService.downloadImage(id);
    }
}
