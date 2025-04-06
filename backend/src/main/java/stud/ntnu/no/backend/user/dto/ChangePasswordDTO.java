package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for changing a user's password.
 * <p>
 * This class holds the current and new passwords required to change a user's password.
 */
public class ChangePasswordDTO {
    private String currentPassword;
    private String newPassword;

    /**
     * Returns the current password.
     *
     * @return the current password
     */
    public String getCurrentPassword() {
        return currentPassword;
    }

    /**
     * Sets the current password.
     *
     * @param currentPassword the current password to set
     */
    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    /**
     * Returns the new password.
     *
     * @return the new password
     */
    public String getNewPassword() {
        return newPassword;
    }

    /**
     * Sets the new password.
     *
     * @param newPassword the new password to set
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 