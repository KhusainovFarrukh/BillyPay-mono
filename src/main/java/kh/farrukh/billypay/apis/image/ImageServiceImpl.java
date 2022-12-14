package kh.farrukh.billypay.apis.image;

import kh.farrukh.billypay.global.exception.custom.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * It implements the ImageService interface and uses the ImageRepository and FileStoreRepository
 * to save and retrieve images
 */
@Service
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

    private final ImageRepository imageRepository;

    /**
     * We're taking a multipart file, saving image to the image repository
     *
     * @param multipartImage The image file that is being uploaded.
     * @return The image object is being returned.
     */
    @Override
    public Image addImage(MultipartFile multipartImage) {
        try {
            return imageRepository.save(new Image(multipartImage.getBytes()));
        } catch (Exception exception) {
            throw new RuntimeException("Error on image upload: " + exception.getMessage());
        }
    }

    /**
     * If the image exists, return it, otherwise throw an exception.
     *
     * @param id The id of the image to be retrieved.
     * @return The image with the given id.
     */
    @Override
    public Image getImageById(long id) {
        return imageRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Image", "id", id)
        );
    }

    /**
     * Find the image in the db and return its content as a resource.
     *
     * @param id The id of the image you want to download.
     * @return A resource
     */
    @Override
    public Resource downloadImage(long id) {
        byte[] content = imageRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Image", "id", id)
        ).getContent();

        return new ByteArrayResource(content);
    }
}
