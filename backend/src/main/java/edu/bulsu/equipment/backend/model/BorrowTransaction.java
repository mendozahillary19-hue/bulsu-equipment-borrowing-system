package edu.bulsu.equipment.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "borrow_transactions")
public class BorrowTransaction {

    public enum Status {
        PENDING,
        APPROVED,
        RETURNED,
        REJECTED,
        OVERDUE
    }

    @Id
    @Column(name = "transaction_id", length = 50)
    private String transactionId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "borrow_date", nullable = false, length = 30)
    private String borrowDate;

    @Column(name = "expected_return_date", nullable = false, length = 30)
    private String expectedReturnDate;

    @Column(name = "actual_return_date", length = 30)
    private String actualReturnDate;

    @Column(name = "borrow_condition", nullable = false, length = 50)
    private String borrowCondition = "Good";

    @Column(name = "borrow_accessories", nullable = false, length = 255)
    private String borrowAccessories = "None";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private Status status = Status.PENDING;

    @Column(name = "return_condition", length = 50)
    private String returnCondition;

    @Column(name = "return_accessories", length = 255)
    private String returnAccessories;

    @Column(name = "return_remarks", columnDefinition = "TEXT")
    private String returnRemarks;

    @Column(name = "admin_remarks", columnDefinition = "TEXT")
    private String adminRemarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private User approvedBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public BorrowTransaction() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

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

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getReturnCondition() { return returnCondition; }
    public void setReturnCondition(String returnCondition) { this.returnCondition = returnCondition; }

    public String getReturnAccessories() { return returnAccessories; }
    public void setReturnAccessories(String returnAccessories) { this.returnAccessories = returnAccessories; }

    public String getReturnRemarks() { return returnRemarks; }
    public void setReturnRemarks(String returnRemarks) { this.returnRemarks = returnRemarks; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public User getApprovedBy() { return approvedBy; }
    public void setApprovedBy(User approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
