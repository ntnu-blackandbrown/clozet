package stud.ntnu.no.backend.user.dto;

public class LoginDTO {
    private String usernameOrEmail;
    private String password;

    // Getters and setters
    public String getUsernameOrEmail() {
        return usernameOrEmail;
    }
    public void setUsernameOrEmail(String usernameOrEmail) {
        this.usernameOrEmail = usernameOrEmail;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}