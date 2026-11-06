package edu.bulsu.equipmentborrowing.model;

import java.io.Serializable;

public class User implements Serializable {
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

    public User() {}

    public User(Long userId, String studentId, String fullName, String courseAndSection, String email, String contactNumber, String role) {
        this.userId = userId;
        this.studentId = studentId;
        this.fullName = fullName;
        this.courseAndSection = courseAndSection;
        this.email = email;
        this.contactNumber = contactNumber;
        this.role = role;
    }

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

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
}
