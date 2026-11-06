package edu.bulsu.equipment.backend.controller;

import edu.bulsu.equipment.backend.dto.BorrowDtos.*;
import edu.bulsu.equipment.backend.service.BorrowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrow")
@CrossOrigin(origins = "*")
public class BorrowController {

    private final BorrowService borrowService;

    @Autowired
    public BorrowController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @PostMapping
    public ResponseEntity<TransactionDto> submitBorrowRequest(@Valid @RequestBody BorrowRequestDto request) {
        return ResponseEntity.ok(borrowService.submitBorrowRequest(request));
    }

    @PostMapping("/return")
    public ResponseEntity<TransactionDto> submitReturnRequest(@Valid @RequestBody ReturnRequestDto request) {
        return ResponseEntity.ok(borrowService.submitReturnRequest(request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionDto>> getUserTransactions(
            @PathVariable Long userId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(borrowService.getUserTransactions(userId, status));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionDetails(@PathVariable String transactionId) {
        return ResponseEntity.ok(borrowService.getTransactionById(transactionId));
    }
}
