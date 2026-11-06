package edu.bulsu.equipment.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank(message = "Student ID or Account ID is required")
        private String studentId;

        @NotBlank(message = "Password is required")
        private String password;

        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class RegisterRequest {
        @NotBlank(message = "Full Name is required")
        private String fullName;

        @NotBlank(message = "Student ID is required")
        private String studentId;

        @NotBlank(message = "Course and Section is required")
        private String courseAndSection;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        @NotBlank(message = "Contact Number is required")
        private String contactNumber;

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        private String password;

        @NotBlank(message = "Confirm Password is required")
        private String confirmPassword;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getCourseAndSection() { return courseAndSection; }
        public void setCourseAndSection(String courseAndSection) { this.courseAndSection = courseAndSection; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        public String getConfirmPassword() { return confirmPassword; }
        public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    }

    public static class UpdateProfileRequest {
        private String fullName;
        private String courseAndSection;
        private String email;
        private String contactNumber;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getCourseAndSection() { return courseAndSection; }
        public void setCourseAndSection(String courseAndSection) { this.courseAndSection = courseAndSection; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    }

    public static class UserDto {
        private Long userId;
        private String studentId;
        private String fullName;
        private String courseAndSection;
        private String email;
        private String contactNumber;
        private String role;
        private long borrowedCount;
        private long pendingCount;
        private long returnedCount;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getCourseAndSection() { return courseAndSection; }
        public void setCourseAndSection(String courseAndSection) { this.courseAndSection = courseAndSection; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getContactNumber() { return contactNumber; }
        public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        public long getBorrowedCount() { return borrowedCount; }
        public void setBorrowedCount(long borrowedCount) { this.borrowedCount = borrowedCount; }
        public long getPendingCount() { return pendingCount; }
        public void setPendingCount(long pendingCount) { this.pendingCount = pendingCount; }
        public long getReturnedCount() { return returnedCount; }
        public void setReturnedCount(long returnedCount) { this.returnedCount = returnedCount; }
    }

    public static class AuthResponse {
        private boolean success;
        private String message;
        private String token;
        private UserDto user;

        public AuthResponse() {}
        public AuthResponse(boolean success, String message, String token, UserDto user) {
            this.success = success;
            this.message = message;
            this.token = token;
            this.user = user;
        }

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
        public UserDto getUser() { return user; }
        public void setUser(UserDto user) { this.user = user; }
    }
}
