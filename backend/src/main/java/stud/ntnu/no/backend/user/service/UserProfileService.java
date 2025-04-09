package stud.ntnu.no.backend.user.service;

import org.springframework.web.multipart.MultipartFile;

/** Service interface for managing user profiles. */
public interface UserProfileService {

  String uploadProfilePicture(MultipartFile file, Long userId) throws Exception;

  void deleteProfilePicture(Long userId);

  String getProfilePictureUrl(Long userId);
}
