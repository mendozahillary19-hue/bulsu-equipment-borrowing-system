package edu.bulsu.equipment.backend.controller;

import edu.bulsu.equipment.backend.dto.BorrowDtos.DashboardStatsDto;
import edu.bulsu.equipment.backend.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final BorrowService borrowService;

    @Autowired
    public DashboardController(BorrowService borrowService) {
        this.borrowService = borrowService;
    }

    @GetMapping("/stats/{userId}")
    public ResponseEntity<DashboardStatsDto> getDashboardStats(@PathVariable Long userId) {
        return ResponseEntity.ok(borrowService.getDashboardStats(userId));
    }
}
