package edu.bulsu.equipment.backend.repository;

import edu.bulsu.equipment.backend.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Integer> {
    List<Equipment> findByIsActiveTrueOrderByEquipmentIdAsc();
    List<Equipment> findByCategoryCategoryIdAndIsActiveTrue(Integer categoryId);
    List<Equipment> findByNameContainingIgnoreCaseAndIsActiveTrue(String query);
    List<Equipment> findByCategoryCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrue(Integer categoryId, String query);
    List<Equipment> findByIsFeaturedTrueAndIsActiveTrue();

    @Query("SELECT COALESCE(SUM(e.availableQuantity), 0) FROM Equipment e WHERE e.isActive = true")
    Long sumAvailableQuantity();
}
