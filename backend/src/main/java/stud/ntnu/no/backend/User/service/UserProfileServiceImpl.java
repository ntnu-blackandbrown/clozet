package stud.ntnu.no.backend.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.service.FileStorageService;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

import java.util.Set;

@Service
public class UserProfileServiceImpl implements UserProfileService {
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg", "image/png", "image/jpg", "image/gif");
    
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Autowired
    public UserProfileServiceImpl(UserRepository userRepository,
                              FileStorageService fileStorageService) {
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public String uploadProfilePicture(MultipartFile file, Long userId) throws Exception {
        validateFile(file);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
            
        // Store file in "users/{userId}" folder
        String url = fileStorageService.storeFile(file, userId);
        
        // Update user with profile picture URL
        user.setProfilePictureUrl(url);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
        
        return url;
    }
    
    @Override
    public void deleteProfilePicture(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        user.setProfilePictureUrl(null);
        user.setUpdatedAt(java.time.LocalDateTime.now());
        userRepository.save(user);
    }
    
    @Override
    public String getProfilePictureUrl(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        return user.getProfilePictureUrl();
    }
    
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new EmptyFileException("Failed to store empty file");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
            throw new InvalidFileTypeException("File type not supported. Only JPEG, PNG, and GIF are allowed");
        }
    }
}