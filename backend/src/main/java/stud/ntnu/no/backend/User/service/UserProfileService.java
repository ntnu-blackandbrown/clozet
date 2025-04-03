package stud.ntnu.no.backend.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {
    String uploadProfilePicture(MultipartFile file, Long userId) throws Exception;
    void deleteProfilePicture(Long userId);
    String getProfilePictureUrl(Long userId);
}