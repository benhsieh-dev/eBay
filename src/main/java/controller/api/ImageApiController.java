package controller.api;

import entity.ProductImage;
import service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/images")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:4200", "https://ebay-u3h1.onrender.com"})
public class ImageApiController {

    @Autowired
    private ProductService productService;

    /**
     * Serve image data from database BLOB storage
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<byte[]> getImage(@PathVariable Integer imageId) {
        try {
            ProductImage productImage = productService.getProductImageById(imageId);
            
            if (productImage == null || !productImage.hasImageData()) {
                return ResponseEntity.notFound().build();
            }
            
            // Determine content type
            String contentType = productImage.getContentType();
            MediaType mediaType;
            
            try {
                mediaType = MediaType.parseMediaType(contentType);
            } catch (Exception e) {
                // Default to JPEG if content type is invalid
                mediaType = MediaType.IMAGE_JPEG;
            }
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(productImage.getImageData().length);
            
            // Add cache headers for better performance
            headers.setCacheControl("public, max-age=31536000"); // Cache for 1 year
            
            // Add filename for download if needed
            if (productImage.getOriginalFilename() != null) {
                headers.setContentDispositionFormData("inline", productImage.getOriginalFilename());
            }
            
            return new ResponseEntity<>(productImage.getImageData(), headers, HttpStatus.OK);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Get image metadata without the actual image data
     */
    @GetMapping("/{imageId}/info")
    public ResponseEntity<Object> getImageInfo(@PathVariable Integer imageId) {
        try {
            ProductImage productImage = productService.getProductImageById(imageId);
            
            if (productImage == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Return metadata without image data
            var info = new java.util.HashMap<String, Object>();
            info.put("imageId", productImage.getImageId());
            info.put("contentType", productImage.getContentType());
            info.put("originalFilename", productImage.getOriginalFilename());
            info.put("fileSize", productImage.getFileSize());
            info.put("isPrimary", productImage.getIsPrimary());
            info.put("uploadedDate", productImage.getUploadedDate());
            info.put("hasImageData", productImage.hasImageData());
            
            return ResponseEntity.ok(info);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}