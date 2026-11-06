package edu.bulsu.equipment.backend.service;

import edu.bulsu.equipment.backend.dto.EquipmentDto;
import edu.bulsu.equipment.backend.model.Category;
import edu.bulsu.equipment.backend.model.Equipment;
import edu.bulsu.equipment.backend.repository.CategoryRepository;
import edu.bulsu.equipment.backend.repository.EquipmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public EquipmentService(EquipmentRepository equipmentRepository, CategoryRepository categoryRepository) {
        this.equipmentRepository = equipmentRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<EquipmentDto> getAllEquipment() {
        return equipmentRepository.findByIsActiveTrueOrderByEquipmentIdAsc()
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<EquipmentDto> getEquipmentByCategory(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            return getAllEquipment();
        }
        return equipmentRepository.findByCategoryCategoryIdAndIsActiveTrue(categoryId)
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public List<EquipmentDto> searchEquipment(String query, Integer categoryId) {
        if (query == null || query.trim().isEmpty()) {
            return getEquipmentByCategory(categoryId);
        }
        String cleanQuery = query.trim();
        if (categoryId != null && categoryId > 0) {
            return equipmentRepository.findByCategoryCategoryIdAndNameContainingIgnoreCaseAndIsActiveTrue(categoryId, cleanQuery)
                    .stream().map(this::convertToDto).collect(Collectors.toList());
        } else {
            return equipmentRepository.findByNameContainingIgnoreCaseAndIsActiveTrue(cleanQuery)
                    .stream().map(this::convertToDto).collect(Collectors.toList());
        }
    }

    public List<EquipmentDto> getFeaturedEquipment() {
        return equipmentRepository.findByIsFeaturedTrueAndIsActiveTrue()
                .stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public EquipmentDto getEquipmentById(Integer id) {
        Equipment equipment = equipmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Equipment not found with ID: " + id));
        return convertToDto(equipment);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByDisplayOrderAsc();
    }

    public long getTotalAvailableCount() {
        Long sum = equipmentRepository.sumAvailableQuantity();
        return sum != null ? sum : 0;
    }

    public EquipmentDto convertToDto(Equipment eq) {
        return new EquipmentDto(
                eq.getEquipmentId(),
                eq.getItemCode(),
                eq.getName(),
                eq.getCategory() != null ? eq.getCategory().getCategoryId() : null,
                eq.getCategoryName(),
                eq.getDescription(),
                eq.getTotalQuantity(),
                eq.getAvailableQuantity(),
                eq.getConditionStatus(),
                eq.getDefaultAccessories(),
                eq.getImageName(),
                eq.getIsFeatured()
        );
    }
}
