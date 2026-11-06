package edu.bulsu.equipmentborrowing.repository;

import edu.bulsu.equipmentborrowing.model.Category;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class EquipmentRepository {

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String message);
    }

    private static EquipmentRepository instance;
    private final List<Equipment> mockEquipmentList = new ArrayList<>();
    private final List<Category> mockCategoryList = new ArrayList<>();

    public static synchronized EquipmentRepository getInstance() {
        if (instance == null) {
            instance = new EquipmentRepository();
        }
        return instance;
    }

    private EquipmentRepository() {
        initMockData();
    }

    private void initMockData() {
        mockCategoryList.add(new Category(1, "Tools & Repairs", "tools-repairs", "ic_category_tools", "Hand tools, soldering, precision bits", 1));
        mockCategoryList.add(new Category(2, "Electronics & Components", "electronics-components", "ic_category_electronics", "Microcontrollers, breadboards, jumper wires", 2));
        mockCategoryList.add(new Category(3, "Testing & Management", "testing-management", "ic_category_testing", "Oscilloscopes, multimeters, calipers", 3));
        mockCategoryList.add(new Category(4, "Others", "others", "ic_category_others", "Protective equipment, safety kits", 4));

        // 10 Seed items exactly matching Figma screens
        mockEquipmentList.add(new Equipment(1, "EQ-001", "Digital Multimeter", 1, "Tools & Repairs",
                "High precision auto-ranging digital multimeter with test leads for AC/DC voltage and resistance measurement.",
                30, 26, "Good", "Test Probes, Manual", "img_multimeter", true));

        mockEquipmentList.add(new Equipment(2, "EQ-002", "Soldering Iron", 1, "Tools & Repairs",
                "60W adjustable temperature soldering iron with ceramic heater and heat-resistant stand.",
                10, 7, "Good", "Stand, Solder Wire", "img_soldering_iron", true));

        mockEquipmentList.add(new Equipment(3, "EQ-003", "Screwdriver Set", 1, "Tools & Repairs",
                "Multi-piece precision screwdriver set suitable for electronics and lab equipment maintenance.",
                5, 4, "Good", "Precision Bits Case", "img_screwdriver_set", true));

        mockEquipmentList.add(new Equipment(4, "EQ-004", "Breadboard", 2, "Electronics & Components",
                "830 tie-point solderless breadboard with dual power rails for electronics prototyping.",
                25, 20, "Good", "None", "img_breadboard", true));

        mockEquipmentList.add(new Equipment(5, "EQ-005", "Arduino Uno", 2, "Electronics & Components",
                "Microcontroller board based on ATmega328P with 14 digital I/O pins and 6 analog inputs.",
                10, 5, "Good", "USB Cable, Jumper Wires", "img_arduino_uno", true));

        mockEquipmentList.add(new Equipment(6, "EQ-006", "Jumper Wires", 2, "Electronics & Components",
                "40-piece flexible jumper wire ribbon cable set.",
                20, 15, "Good", "40-pin Ribbon Cable", "img_jumper_wires", true));

        mockEquipmentList.add(new Equipment(7, "EQ-007", "Digital Oscilloscope", 3, "Testing & Management",
                "Dual-channel 100MHz digital storage oscilloscope with real-time waveform display.",
                6, 5, "Good", "BNC Probes, Power Cable", "img_oscilloscope", true));

        mockEquipmentList.add(new Equipment(8, "EQ-008", "Digital Caliper", 3, "Testing & Management",
                "Electronic digital vernier caliper 0-150mm with LCD display.",
                12, 10, "Good", "Hard Case, Battery", "img_digital_caliper", true));

        mockEquipmentList.add(new Equipment(9, "EQ-009", "Thermometer", 3, "Testing & Management",
                "High-accuracy digital contact probe thermometer for thermal testing.",
                10, 9, "Good", "Protective Sheath", "img_thermometer", true));

        mockEquipmentList.add(new Equipment(10, "EQ-010", "Safety Goggles & Kit", 4, "Others",
                "Laboratory safety eye protection goggles with anti-fog coating and ESD wrist strap.",
                15, 12, "Good", "Storage Bag, ESD Strap", "img_safety_kit", false));
    }

    public void getEquipment(String search, Integer categoryId, DataCallback<List<Equipment>> callback) {
        ApiClient.getService().getEquipment(search, categoryId).enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(filterMockEquipment(search, categoryId));
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
                callback.onSuccess(filterMockEquipment(search, categoryId));
            }
        });
    }

    public void getFeaturedEquipment(DataCallback<List<Equipment>> callback) {
        ApiClient.getService().getFeaturedEquipment().enqueue(new Callback<List<Equipment>>() {
            @Override
            public void onResponse(Call<List<Equipment>> call, Response<List<Equipment>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(getMockFeatured());
                }
            }

            @Override
            public void onFailure(Call<List<Equipment>> call, Throwable t) {
                callback.onSuccess(getMockFeatured());
            }
        });
    }

    public Equipment getEquipmentByIdLocal(int id) {
        for (Equipment eq : mockEquipmentList) {
            if (eq.getEquipmentId() == id) {
                return eq;
            }
        }
        return mockEquipmentList.get(0);
    }

    public long getTotalAvailableCountLocal() {
        long sum = 0;
        for (Equipment eq : mockEquipmentList) {
            sum += eq.getAvailableQuantity();
        }
        return sum;
    }

    private List<Equipment> filterMockEquipment(String search, Integer categoryId) {
        List<Equipment> result = new ArrayList<>();
        for (Equipment eq : mockEquipmentList) {
            boolean matchesCategory = (categoryId == null || categoryId <= 0 || eq.getCategoryId().equals(categoryId));
            boolean matchesSearch = (search == null || search.trim().isEmpty() ||
                    eq.getName().toLowerCase().contains(search.trim().toLowerCase()) ||
                    eq.getCategoryName().toLowerCase().contains(search.trim().toLowerCase()));

            if (matchesCategory && matchesSearch) {
                result.add(eq);
            }
        }
        return result;
    }

    public List<Equipment> getFeaturedForCategory(int categoryId) {
        List<Equipment> list = new ArrayList<>();
        if (categoryId == 1) { // Tools & Repairs (Figma Page 29)
            Equipment e1 = getEquipmentByIdDirect(1); // Digital Multimeter
            Equipment e2 = getEquipmentByIdDirect(2); // Soldering Iron
            Equipment e3 = getEquipmentByIdDirect(3); // Screwdriver Set
            if (e1 != null) list.add(e1);
            if (e2 != null) list.add(e2);
            if (e3 != null) list.add(e3);
        } else if (categoryId == 2) { // Electronics & Components (Figma Page 33)
            Equipment e4 = getEquipmentByIdDirect(4); // Breadboard
            Equipment e5 = getEquipmentByIdDirect(5); // Arduino Uno
            Equipment e6 = getEquipmentByIdDirect(6); // Jumper Wires
            if (e4 != null) list.add(e4);
            if (e5 != null) list.add(e5);
            if (e6 != null) list.add(e6);
        } else if (categoryId == 3) { // Testing & Management (Figma Page 38)
            Equipment e8 = getEquipmentByIdDirect(8); // Digital Caliper
            Equipment e9 = getEquipmentByIdDirect(9); // Thermometer
            Equipment e7 = getEquipmentByIdDirect(7); // Digital Oscilloscope
            if (e8 != null) list.add(e8);
            if (e9 != null) list.add(e9);
            if (e7 != null) list.add(e7);
        } else { // ALL (Figma Page 19)
            Equipment e1 = getEquipmentByIdDirect(1); // Digital Multimeter
            Equipment e5 = getEquipmentByIdDirect(5); // Arduino Uno
            Equipment e7 = getEquipmentByIdDirect(7); // Digital Oscilloscope
            if (e1 != null) list.add(e1);
            if (e5 != null) list.add(e5);
            if (e7 != null) list.add(e7);
        }
        return list;
    }

    public Equipment getEquipmentByIdDirect(int id) {
        for (Equipment eq : mockEquipmentList) {
            if (eq.getEquipmentId() == id) return eq;
        }
        return null;
    }

    private List<Equipment> getMockFeatured() {
        List<Equipment> result = new ArrayList<>();
        for (Equipment eq : mockEquipmentList) {
            if (Boolean.TRUE.equals(eq.getIsFeatured())) {
                result.add(eq);
            }
        }
        return result;
    }

    public void deductStockLocal(int equipmentId, int qty) {
        for (Equipment eq : mockEquipmentList) {
            if (eq.getEquipmentId() == equipmentId) {
                eq.setAvailableQuantity(Math.max(0, eq.getAvailableQuantity() - qty));
                break;
            }
        }
    }

    public void restoreStockLocal(int equipmentId, int qty) {
        for (Equipment eq : mockEquipmentList) {
            if (eq.getEquipmentId() == equipmentId) {
                eq.setAvailableQuantity(Math.min(eq.getTotalQuantity(), eq.getAvailableQuantity() + qty));
                break;
            }
        }
    }
}
