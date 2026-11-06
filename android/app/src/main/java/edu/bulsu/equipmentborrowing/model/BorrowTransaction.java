package edu.bulsu.equipmentborrowing.model;

import java.io.Serializable;

public class BorrowTransaction implements Serializable {
    private String transactionId;
    private Long userId;
    private String borrowerName;
    private String studentId;
    private Integer equipmentId;
    private String equipmentName;
    private String categoryName;
    private String imageName;
    private Integer quantity;
    private String borrowDate;
    private String expectedReturnDate;
    private String actualReturnDate;
    private String borrowCondition;
    private String borrowAccessories;
    private String status; // PENDING, APPROVED, RETURNED, REJECTED, OVERDUE
    private String returnCondition;
    private String returnAccessories;
    private String returnRemarks;
    private String adminRemarks;

    public BorrowTransaction() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getBorrowerName() { return borrowerName; }
    public void setBorrowerName(String borrowerName) { this.borrowerName = borrowerName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public Integer getEquipmentId() { return equipmentId; }
    public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }

    public String getEquipmentName() { return equipmentName; }
    public void setEquipmentName(String equipmentName) { this.equipmentName = equipmentName; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getBorrowDate() { return borrowDate; }
    public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }

    public String getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(String expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public String getActualReturnDate() { return actualReturnDate; }
    public void setActualReturnDate(String actualReturnDate) { this.actualReturnDate = actualReturnDate; }

    public String getBorrowCondition() { return borrowCondition; }
    public void setBorrowCondition(String borrowCondition) { this.borrowCondition = borrowCondition; }

    public String getBorrowAccessories() { return borrowAccessories; }
    public void setBorrowAccessories(String borrowAccessories) { this.borrowAccessories = borrowAccessories; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReturnCondition() { return returnCondition; }
    public void setReturnCondition(String returnCondition) { this.returnCondition = returnCondition; }

    public String getReturnAccessories() { return returnAccessories; }
    public void setReturnAccessories(String returnAccessories) { this.returnAccessories = returnAccessories; }

    public String getReturnRemarks() { return returnRemarks; }
    public void setReturnRemarks(String returnRemarks) { this.returnRemarks = returnRemarks; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }
}
