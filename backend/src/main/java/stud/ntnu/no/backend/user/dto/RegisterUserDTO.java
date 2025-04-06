package stud.ntnu.no.backend.user.dto;

/**
 * Data Transfer Object for registering a new user.
 * <p>
 * This class holds the information required to register a new user,
 * including username, email, password, first name, and last name.
 */
public class RegisterUserDTO {
    private String username;
    private String email;
    private String password;
    private String firstName;
    private String lastName;

    // Getters og setters
    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    /**
     * Sets the username.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    /**
     * Returns the email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * Sets the email.
     *
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * Returns the password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Sets the password.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Returns the first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Sets the first name.
     *
     * @param firstName the first name to set
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Returns the last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Sets the last name.
     *
     * @param lastName the last name to set
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
