package service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Service for handling file uploads for messaging attachments
 * In production, this would integrate with cloud storage (AWS S3, Google Cloud Storage, etc.)
 */
@Service
public class FileUploadService {
    
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/messages/";
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );
    private static final List<String> ALLOWED_FILE_TYPES = Arrays.asList(
        "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        "text/plain", "text/csv", "application/zip", "application/x-zip-compressed"
    );
    
    /**
     * Upload a file and return upload information
     */
    public Map<String, Object> uploadFile(MultipartFile file) throws IOException {
        // Validate file
        Map<String, Object> validation = validateFile(file);
        if (!(Boolean) validation.get("valid")) {
            throw new IllegalArgumentException((String) validation.get("message"));
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + "_" + 
                               System.currentTimeMillis() + fileExtension;
        
        // Save file
        Path filePath = uploadPath.resolve(uniqueFilename);
        file.transferTo(filePath.toFile());
        
        // Prepare response
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("filename", uniqueFilename);
        result.put("originalName", originalFilename);
        result.put("url", "/uploads/messages/" + uniqueFilename);
        result.put("type", file.getContentType());
        result.put("size", file.getSize());
        result.put("isImage", isImageFile(file.getContentType()));
        
        return result;
    }
    
    /**
     * Validate uploaded file
     */
    public Map<String, Object> validateFile(MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        if (file.isEmpty()) {
            result.put("valid", false);
            result.put("message", "File is empty");
            return result;
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            result.put("valid", false);
            result.put("message", "File size exceeds 10MB limit");
            return result;
        }
        
        String contentType = file.getContentType();
        if (contentType == null) {
            result.put("valid", false);
            result.put("message", "Unable to determine file type");
            return result;
        }
        
        boolean isAllowedType = ALLOWED_IMAGE_TYPES.contains(contentType) || 
                               ALLOWED_FILE_TYPES.contains(contentType);
        
        if (!isAllowedType) {
            result.put("valid", false);
            result.put("message", "File type not allowed. Allowed types: images, PDF, Office documents, text files, ZIP");
            return result;
        }
        
        // Additional filename validation
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            result.put("valid", false);
            result.put("message", "Invalid filename");
            return result;
        }
        
        // Check for malicious file extensions in filename
        if (containsMaliciousExtension(filename)) {
            result.put("valid", false);
            result.put("message", "File type not allowed for security reasons");
            return result;
        }
        
        result.put("valid", true);
        result.put("message", "File is valid");
        return result;
    }
    
    /**
     * Delete an uploaded file
     */
    public boolean deleteFile(String filename) {
        try {
            if (filename == null || filename.trim().isEmpty()) {
                return false;
            }
            
            // Security check - ensure filename doesn't contain path traversal
            if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
                return false;
            }
            
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            return Files.deleteIfExists(filePath);
            
        } catch (IOException e) {
            System.err.println("Error deleting file: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if file exists
     */
    public boolean fileExists(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        try {
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            return Files.exists(filePath);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get file information
     */
    public Map<String, Object> getFileInfo(String filename) {
        Map<String, Object> info = new HashMap<>();
        
        try {
            Path filePath = Paths.get(UPLOAD_DIR, filename);
            
            if (!Files.exists(filePath)) {
                info.put("exists", false);
                return info;
            }
            
            File file = filePath.toFile();
            String contentType = Files.probeContentType(filePath);
            
            info.put("exists", true);
            info.put("filename", filename);
            info.put("size", file.length());
            info.put("type", contentType);
            info.put("isImage", isImageFile(contentType));
            info.put("lastModified", file.lastModified());
            
        } catch (IOException e) {
            info.put("exists", false);
            info.put("error", e.getMessage());
        }
        
        return info;
    }
    
    /**
     * Generate thumbnail for image files
     */
    public String generateThumbnail(String filename, int width, int height) {
        // In production, this would generate actual thumbnails using libraries like ImageIO, Thumbnailator, etc.
        // For now, return the original image URL
        return "/uploads/messages/" + filename;
    }
    
    /**
     * Get human-readable file size
     */
    public String formatFileSize(long bytes) {
        if (bytes <= 0) return "0 B";
        
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
        
        return "%.1f %s".formatted(bytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }
    
    // Helper methods
    
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }
    
    private boolean isImageFile(String contentType) {
        return contentType != null && ALLOWED_IMAGE_TYPES.contains(contentType);
    }
    
    private boolean containsMaliciousExtension(String filename) {
        String lowerFilename = filename.toLowerCase();
        String[] maliciousExtensions = {
            ".exe", ".bat", ".cmd", ".com", ".pif", ".scr", ".vbs", ".js", ".jar", 
            ".sh", ".php", ".asp", ".aspx", ".jsp", ".pl", ".py", ".rb"
        };
        
        for (String ext : maliciousExtensions) {
            if (lowerFilename.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Clean up old files (can be called by scheduled task)
     */
    public void cleanupOldFiles(int daysOld) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                return;
            }
            
            long cutoffTime = System.currentTimeMillis() - (daysOld * 24L * 60 * 60 * 1000);
            
            Files.list(uploadPath)
                .filter(Files::isRegularFile)
                .filter(path -> {
                    try {
                        return Files.getLastModifiedTime(path).toMillis() < cutoffTime;
                    } catch (IOException e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        System.out.println("Deleted old file: " + path.getFileName());
                    } catch (IOException e) {
                        System.err.println("Failed to delete old file: " + path.getFileName() + " - " + e.getMessage());
                    }
                });
                
        } catch (IOException e) {
            System.err.println("Error during file cleanup: " + e.getMessage());
        }
    }
}