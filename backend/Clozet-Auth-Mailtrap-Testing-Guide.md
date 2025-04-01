# Testing Clozet Authentication with Mailtrap Email Integration

This guide explains how to use the Postman Collection to test the Clozet authentication system, including email verification and password reset features that integrate with Mailtrap.

## Prerequisites

1. Make sure the Clozet backend is running locally on port 8080
2. Ensure you have access to the Mailtrap account configured in the application
3. Import the `Clozet-Auth-Mailtrap-Tests.postman_collection.json` collection into Postman

## Mailtrap Configuration

The system is using Mailtrap with the following configuration (from `application-dev.yml`):

```yaml
spring:
  mail:
    host: sandbox.smtp.mailtrap.io
    port: 2525
    username: 5cb908f76b3044
    password: d8ea28cdded2c1
    properties:
      mail:
        smtp:
          auth: true
          starttls.enable: true

app:
  email:
    base-url: ${FRONTEND_BASE_URL:http://localhost:5173}
```

## Testing Flow

### 1. User Registration and Email Verification

1. Send the **Register User** request to create a new user
   - This will register a user with username `testuser` and email `test@example.com`
   - A verification email will be sent to Mailtrap
   
2. Check your Mailtrap inbox for the verification email
   - Subject: "Bekreft din konto på Clozet"
   - Extract the verification token from the email link (after `token=` in the URL)
   
3. Set the `verificationToken` variable in Postman with the extracted token
   - Click on the collection (Clozet-Auth-Mailtrap-Tests)
   - Go to the Variables tab
   - Update the `verificationToken` value
   
4. Send the **Verify User Email** request
   - This will verify the user's email and automatically log them in
   - JWT cookies will be set

### 2. Login/Logout

5. Send the **Logout User** request to test logout functionality
   - This will invalidate the JWT tokens and remove cookies

6. Send the **Login User** request to test login functionality
   - This will authenticate with the registered user credentials
   - JWT cookies will be set

### 3. Password Reset Flow

7. Send the **Request Password Reset** request
   - This will trigger a password reset email to be sent to Mailtrap
   
8. Check your Mailtrap inbox for the password reset email
   - Subject: "Tilbakestill ditt passord på Clozet"
   - Extract the reset token from the email link (after `token=` in the URL)
   
9. Set the `resetToken` variable in Postman with the extracted token
   - Click on the collection (Clozet-Auth-Mailtrap-Tests)
   - Go to the Variables tab
   - Update the `resetToken` value
   
10. Send the **Validate Reset Token** request
    - This validates that the token is valid and not expired
    
11. Send the **Reset Password** request
    - This uses the token to reset the password to `newpassword123`
    
12. Send the **Login with New Password** request
    - This tests logging in with the newly reset password

## Extracting Tokens from Mailtrap Emails

### For Verification Token:
1. Log into Mailtrap
2. Find the email with subject "Bekreft din konto på Clozet"
3. Open the email and look for the verification link
4. Extract the token part of the URL (after `token=`)
   - Example link: `http://localhost:5173/verify?token=12345abc-67de-89f0-ghij-klmnopqrstuv`
   - Token to extract: `12345abc-67de-89f0-ghij-klmnopqrstuv`

### For Password Reset Token:
1. Log into Mailtrap
2. Find the email with subject "Tilbakestill ditt passord på Clozet"
3. Open the email and look for the reset password link
4. Extract the token part of the URL (after `token=`)
   - Example link: `http://localhost:5173/reset-password?token=abcde123-45fg-67hi-jklm-nopqrstuvwxy`
   - Token to extract: `abcde123-45fg-67hi-jklm-nopqrstuvwxy`

## Notes

- Verification tokens are valid for 24 hours
- Password reset tokens are valid for 1 hour
- The test user account is created with:
  - Username: `testuser`
  - Email: `test@example.com`
  - Initial password: `password123`
- After password reset, the new password will be `newpassword123`
- Emails are sent when:
  - A user registers (verification email)
  - A user requests a password reset (reset email)
- If tests fail, check that the backend is running and the Mailtrap configuration is correct 