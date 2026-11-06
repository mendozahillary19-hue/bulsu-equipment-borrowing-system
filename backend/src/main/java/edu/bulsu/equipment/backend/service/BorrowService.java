package edu.bulsu.equipment.backend.service;

import edu.bulsu.equipment.backend.dto.BorrowDtos.*;
import edu.bulsu.equipment.backend.model.BorrowTransaction;
import edu.bulsu.equipment.backend.model.Equipment;
import edu.bulsu.equipment.backend.model.Notification;
import edu.bulsu.equipment.backend.model.User;
import edu.bulsu.equipment.backend.repository.BorrowTransactionRepository;
import edu.bulsu.equipment.backend.repository.EquipmentRepository;
import edu.bulsu.equipment.backend.repository.NotificationRepository;
import edu.bulsu.equipment.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class BorrowService {

    private final BorrowTransactionRepository transactionRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Autowired
    public BorrowService(BorrowTransactionRepository transactionRepository,
                         EquipmentRepository equipmentRepository,
                         UserRepository userRepository,
                         NotificationRepository notificationRepository) {
        this.transactionRepository = transactionRepository;
        this.equipmentRepository = equipmentRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Transactional
    public TransactionDto submitBorrowRequest(BorrowRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Borrower not found with ID: " + request.getUserId()));

        Equipment equipment = equipmentRepository.findById(request.getEquipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found with ID: " + request.getEquipmentId()));

        if (request.getQuantity() == null || request.getQuantity() < 1) {
            throw new IllegalArgumentException("Quantity must be at least 1 unit.");
        }

        if (request.getQuantity() > equipment.getAvailableQuantity()) {
            throw new IllegalArgumentException("Requested quantity (" + request.getQuantity()
                    + ") exceeds available stock (" + equipment.getAvailableQuantity() + ").");
        }

        // Generate unique Transaction ID e.g. TRX-20261105-4829
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = 1000 + new Random().nextInt(9000);
        String transactionId = "TRX-" + dateStr + "-" + rand;

        BorrowTransaction tx = new BorrowTransaction();
        tx.setTransactionId(transactionId);
        tx.setUser(user);
        tx.setEquipment(equipment);
        tx.setQuantity(request.getQuantity());
        tx.setBorrowDate(request.getBorrowDate());
        tx.setExpectedReturnDate(request.getExpectedReturnDate());
        tx.setBorrowCondition(request.getBorrowCondition() != null ? request.getBorrowCondition() : "Good");
        tx.setBorrowAccessories(request.getBorrowAccessories() != null ? request.getBorrowAccessories() : "None");
        tx.setStatus(BorrowTransaction.Status.PENDING);
        tx.setAdminRemarks("Submitted by borrower. Pending custodian review.");

        BorrowTransaction saved = transactionRepository.save(tx);

        // Create in-app notification
        Notification notif = new Notification(user, "Borrow Request Submitted",
                "Your request " + transactionId + " for " + equipment.getName() + " has been submitted for review.");
        notificationRepository.save(notif);

        return convertToDto(saved);
    }

    @Transactional
    public TransactionDto submitReturnRequest(ReturnRequestDto request) {
        BorrowTransaction tx = transactionRepository.findById(request.getTransactionId())
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + request.getTransactionId()));

        if (tx.getStatus() != BorrowTransaction.Status.APPROVED) {
            throw new IllegalStateException("Only approved and checked-out equipment can be returned. Current status: " + tx.getStatus());
        }

        tx.setActualReturnDate(request.getReturnDate());
        tx.setReturnCondition(request.getReturnCondition() != null ? request.getReturnCondition() : "Good");
        tx.setReturnAccessories(request.getReturnAccessories() != null ? request.getReturnAccessories() : "Complete");
        tx.setReturnRemarks(request.getReturnRemarks());
        tx.setStatus(BorrowTransaction.Status.RETURNED);
        tx.setAdminRemarks("Equipment return recorded on " + request.getReturnDate() + ". Condition: " + request.getReturnCondition());

        // Restore equipment available inventory
        Equipment equipment = tx.getEquipment();
        equipment.setAvailableQuantity(equipment.getAvailableQuantity() + tx.getQuantity());
        equipmentRepository.save(equipment);

        BorrowTransaction saved = transactionRepository.save(tx);

        Notification notif = new Notification(tx.getUser(), "Equipment Returned",
                "Your return for " + equipment.getName() + " (" + tx.getTransactionId() + ") has been processed.");
        notificationRepository.save(notif);

        return convertToDto(saved);
    }

    public List<TransactionDto> getUserTransactions(Long userId, String statusFilter) {
        List<BorrowTransaction> list;
        if (statusFilter == null || statusFilter.trim().isEmpty() || statusFilter.equalsIgnoreCase("ALL")) {
            list = transactionRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
        } else {
            try {
                BorrowTransaction.Status status = BorrowTransaction.Status.valueOf(statusFilter.trim().toUpperCase());
                list = transactionRepository.findByUserUserIdAndStatusOrderByCreatedAtDesc(userId, status);
            } catch (IllegalArgumentException e) {
                list = transactionRepository.findByUserUserIdOrderByCreatedAtDesc(userId);
            }
        }
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public TransactionDto getTransactionById(String transactionId) {
        BorrowTransaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));
        return convertToDto(tx);
    }

    public DashboardStatsDto getDashboardStats(Long userId) {
        Long availableCount = equipmentRepository.sumAvailableQuantity();
        long pendingRequests = transactionRepository.countByUserUserIdAndStatus(userId, BorrowTransaction.Status.PENDING);
        User user = userRepository.findById(userId).orElse(null);

        return new DashboardStatsDto(
                availableCount != null ? availableCount : 0,
                pendingRequests,
                user != null ? user.getFullName() : "Student",
                user != null ? user.getStudentId() : ""
        );
    }

    // Admin methods
    @Transactional
    public TransactionDto approveTransaction(String transactionId, Long adminId, String adminRemarks) {
        BorrowTransaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        if (tx.getStatus() != BorrowTransaction.Status.PENDING) {
            throw new IllegalStateException("Only pending requests can be approved.");
        }

        Equipment eq = tx.getEquipment();
        if (eq.getAvailableQuantity() < tx.getQuantity()) {
            throw new IllegalStateException("Insufficient equipment stock to approve this request.");
        }

        // Deduct available quantity upon approval/checkout
        eq.setAvailableQuantity(eq.getAvailableQuantity() - tx.getQuantity());
        equipmentRepository.save(eq);

        tx.setStatus(BorrowTransaction.Status.APPROVED);
        tx.setAdminRemarks(adminRemarks != null ? adminRemarks : "Approved by Lab Custodian.");
        if (adminId != null) {
            userRepository.findById(adminId).ifPresent(tx::setApprovedBy);
        }

        BorrowTransaction saved = transactionRepository.save(tx);
        Notification notif = new Notification(tx.getUser(), "Request Approved",
                "Your borrowing request " + tx.getTransactionId() + " has been approved.");
        notificationRepository.save(notif);

        return convertToDto(saved);
    }

    @Transactional
    public TransactionDto rejectTransaction(String transactionId, Long adminId, String reason) {
        BorrowTransaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        tx.setStatus(BorrowTransaction.Status.REJECTED);
        tx.setAdminRemarks(reason != null ? reason : "Request rejected by Lab Custodian.");
        if (adminId != null) {
            userRepository.findById(adminId).ifPresent(tx::setApprovedBy);
        }

        BorrowTransaction saved = transactionRepository.save(tx);
        Notification notif = new Notification(tx.getUser(), "Request Rejected",
                "Your borrowing request " + tx.getTransactionId() + " was not approved. Reason: " + tx.getAdminRemarks());
        notificationRepository.save(notif);

        return convertToDto(saved);
    }

    public List<TransactionDto> getAllTransactionsForAdmin(String status) {
        List<BorrowTransaction> list;
        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("ALL")) {
            list = transactionRepository.findByStatusOrderByCreatedAtDesc(BorrowTransaction.Status.valueOf(status.toUpperCase()));
        } else {
            list = transactionRepository.findAllByOrderByCreatedAtDesc();
        }
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public TransactionDto convertToDto(BorrowTransaction tx) {
        TransactionDto dto = new TransactionDto();
        dto.setTransactionId(tx.getTransactionId());
        dto.setUserId(tx.getUser().getUserId());
        dto.setBorrowerName(tx.getUser().getFullName());
        dto.setStudentId(tx.getUser().getStudentId());
        dto.setEquipmentId(tx.getEquipment().getEquipmentId());
        dto.setEquipmentName(tx.getEquipment().getName());
        dto.setCategoryName(tx.getEquipment().getCategoryName());
        dto.setImageName(tx.getEquipment().getImageName());
        dto.setQuantity(tx.getQuantity());
        dto.setBorrowDate(tx.getBorrowDate());
        dto.setExpectedReturnDate(tx.getExpectedReturnDate());
        dto.setActualReturnDate(tx.getActualReturnDate());
        dto.setBorrowCondition(tx.getBorrowCondition());
        dto.setBorrowAccessories(tx.getBorrowAccessories());
        dto.setStatus(tx.getStatus().name());
        dto.setReturnCondition(tx.getReturnCondition());
        dto.setReturnAccessories(tx.getReturnAccessories());
        dto.setReturnRemarks(tx.getReturnRemarks());
        dto.setAdminRemarks(tx.getAdminRemarks());
        dto.setCreatedAt(tx.getCreatedAt());
        return dto;
    }
}
