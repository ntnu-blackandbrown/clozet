package stud.ntnu.no.backend.user.dto;

public class LoginDTO {
    private String username;
    private String password;

    // Getters og setters
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
