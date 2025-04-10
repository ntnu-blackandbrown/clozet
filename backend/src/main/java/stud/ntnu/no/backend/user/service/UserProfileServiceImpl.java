package stud.ntnu.no.backend.user.service;

import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import stud.ntnu.no.backend.itemimage.exception.EmptyFileException;
import stud.ntnu.no.backend.itemimage.exception.InvalidFileTypeException;
import stud.ntnu.no.backend.itemimage.service.FileStorageService;
import stud.ntnu.no.backend.user.entity.User;
import stud.ntnu.no.backend.user.repository.UserRepository;

/**
 * Implementation of UserProfileService for managing user profiles.
 */
@Service
public class UserProfileServiceImpl implements UserProfileService {

  private static final Logger logger = LoggerFactory.getLogger(UserProfileServiceImpl.class);
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

  /**
   * Uploads a profile picture for a user.
   *
   * @param file   the profile picture file
   * @param userId the ID of the user
   * @return the URL of the uploaded profile picture
   * @throws Exception if an error occurs during upload
   */
  @Override
  public String uploadProfilePicture(MultipartFile file, Long userId) throws Exception {
    logger.debug("Uploading profile picture for user ID: {}", userId);
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
    logger.debug("Deleting profile picture for user ID: {}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    user.setProfilePictureUrl(null);
    user.setUpdatedAt(java.time.LocalDateTime.now());
    userRepository.save(user);
  }

  @Override
  public String getProfilePictureUrl(Long userId) {
    logger.debug("Retrieving profile picture URL for user ID: {}", userId);
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

    return user.getProfilePictureUrl();
  }

  private void validateFile(MultipartFile file) {
    logger.debug("Validating file upload");
    if (file.isEmpty()) {
      throw new EmptyFileException("Failed to store empty file");
    }

    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType)) {
      throw new InvalidFileTypeException(
          "File type not supported. Only JPEG, PNG, and GIF are allowed");
    }
  }
}
