package edu.bulsu.equipment.backend.controller;

import edu.bulsu.equipment.backend.dto.EquipmentDto;
import edu.bulsu.equipment.backend.model.Category;
import edu.bulsu.equipment.backend.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @Autowired
    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentDto>> getEquipment(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer categoryId) {
        if ((search != null && !search.trim().isEmpty()) || (categoryId != null && categoryId > 0)) {
            return ResponseEntity.ok(equipmentService.searchEquipment(search, categoryId));
        }
        return ResponseEntity.ok(equipmentService.getAllEquipment());
    }

    @GetMapping("/featured")
    public ResponseEntity<List<EquipmentDto>> getFeatured() {
        return ResponseEntity.ok(equipmentService.getFeaturedEquipment());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(equipmentService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(equipmentService.getEquipmentById(id));
    }
}
