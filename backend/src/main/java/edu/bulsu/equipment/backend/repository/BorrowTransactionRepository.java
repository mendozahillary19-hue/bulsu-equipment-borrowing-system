package edu.bulsu.equipment.backend.repository;

import edu.bulsu.equipment.backend.model.BorrowTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BorrowTransactionRepository extends JpaRepository<BorrowTransaction, String> {
    List<BorrowTransaction> findByUserUserIdOrderByCreatedAtDesc(Long userId);
    List<BorrowTransaction> findByUserUserIdAndStatusOrderByCreatedAtDesc(Long userId, BorrowTransaction.Status status);
    
    long countByUserUserIdAndStatus(Long userId, BorrowTransaction.Status status);
    long countByUserUserIdAndStatusIn(Long userId, List<BorrowTransaction.Status> statuses);
    long countByUserUserId(Long userId);

    List<BorrowTransaction> findAllByOrderByCreatedAtDesc();
    List<BorrowTransaction> findByStatusOrderByCreatedAtDesc(BorrowTransaction.Status status);
}
