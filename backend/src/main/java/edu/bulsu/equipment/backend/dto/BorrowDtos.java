package edu.bulsu.equipment.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class BorrowDtos {

    public static class BorrowRequestDto {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotNull(message = "Equipment ID is required")
        private Integer equipmentId;

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        private Integer quantity;

        @NotBlank(message = "Borrow date is required")
        private String borrowDate;

        @NotBlank(message = "Expected return date is required")
        private String expectedReturnDate;

        private String borrowCondition = "Good";
        private String borrowAccessories = "None";

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getEquipmentId() { return equipmentId; }
        public void setEquipmentId(Integer equipmentId) { this.equipmentId = equipmentId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public String getBorrowDate() { return borrowDate; }
        public void setBorrowDate(String borrowDate) { this.borrowDate = borrowDate; }
        public String getExpectedReturnDate() { return expectedReturnDate; }
        public void setExpectedReturnDate(String expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }
        public String getBorrowCondition() { return borrowCondition; }
        public void setBorrowCondition(String borrowCondition) { this.borrowCondition = borrowCondition; }
        public String getBorrowAccessories() { return borrowAccessories; }
        public void setBorrowAccessories(String borrowAccessories) { this.borrowAccessories = borrowAccessories; }
    }

    public static class ReturnRequestDto {
        @NotBlank(message = "Transaction ID is required")
        private String transactionId;

        @NotBlank(message = "Return date is required")
        private String returnDate;

        private String returnCondition = "Good";
        private String returnAccessories = "Complete";
        private String returnRemarks;

        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        public String getReturnDate() { return returnDate; }
        public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
        public String getReturnCondition() { return returnCondition; }
        public void setReturnCondition(String returnCondition) { this.returnCondition = returnCondition; }
        public String getReturnAccessories() { return returnAccessories; }
        public void setReturnAccessories(String returnAccessories) { this.returnAccessories = returnAccessories; }
        public String getReturnRemarks() { return returnRemarks; }
        public void setReturnRemarks(String returnRemarks) { this.returnRemarks = returnRemarks; }
    }

    public static class TransactionDto {
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
        private String status;
        private String returnCondition;
        private String returnAccessories;
        private String returnRemarks;
        private String adminRemarks;
        private LocalDateTime createdAt;

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
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    public static class DashboardStatsDto {
        private long availableEquipmentCount;
        private long myRequestsCount;
        private String studentName;
        private String studentId;

        public DashboardStatsDto() {}
        public DashboardStatsDto(long availableEquipmentCount, long myRequestsCount, String studentName, String studentId) {
            this.availableEquipmentCount = availableEquipmentCount;
            this.myRequestsCount = myRequestsCount;
            this.studentName = studentName;
            this.studentId = studentId;
        }

        public long getAvailableEquipmentCount() { return availableEquipmentCount; }
        public void setAvailableEquipmentCount(long availableEquipmentCount) { this.availableEquipmentCount = availableEquipmentCount; }
        public long getMyRequestsCount() { return myRequestsCount; }
        public void setMyRequestsCount(long myRequestsCount) { this.myRequestsCount = myRequestsCount; }
        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }
    }
}
