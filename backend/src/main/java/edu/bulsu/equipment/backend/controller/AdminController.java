package edu.bulsu.equipment.backend.controller;

import edu.bulsu.equipment.backend.dto.BorrowDtos.TransactionDto;
import edu.bulsu.equipment.backend.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final BorrowService borrowService;

    @Autowired
    public AdminController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/transactions")
    public ResponseEntity<List<TransactionDto>> getAllTransactions(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(borrowService.getAllTransactionsForAdmin(status));
    }

    @PostMapping("/transactions/{transactionId}/approve")
    public ResponseEntity<TransactionDto> approveTransaction(
            @PathVariable String transactionId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long adminId = null;
        String remarks = "Approved by Lab Custodian.";
        if (body != null) {
            if (body.containsKey("adminId") && body.get("adminId") != null) {
                adminId = Long.valueOf(body.get("adminId").toString());
            }
            if (body.containsKey("remarks") && body.get("remarks") != null) {
                remarks = body.get("remarks").toString();
            }
        }
        return ResponseEntity.ok(borrowService.approveTransaction(transactionId, adminId, remarks));
    }

    @PostMapping("/transactions/{transactionId}/reject")
    public ResponseEntity<TransactionDto> rejectTransaction(
            @PathVariable String transactionId,
            @RequestBody(required = false) Map<String, Object> body) {
        Long adminId = null;
        String reason = "Rejected by Lab Custodian.";
        if (body != null) {
            if (body.containsKey("adminId") && body.get("adminId") != null) {
                adminId = Long.valueOf(body.get("adminId").toString());
            }
            if (body.containsKey("reason") && body.get("reason") != null) {
                reason = body.get("reason").toString();
            }
        }
        return ResponseEntity.ok(borrowService.rejectTransaction(transactionId, adminId, reason));
    }
}
