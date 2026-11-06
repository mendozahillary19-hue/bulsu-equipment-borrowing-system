package edu.bulsu.equipmentborrowing.repository;

import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.network.ApiClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BorrowRepository {

    public interface TransactionCallback {
        void onSuccess(BorrowTransaction transaction);
        void onError(String message);
    }

    public interface TransactionListCallback {
        void onSuccess(List<BorrowTransaction> list);
        void onError(String message);
    }

    private static BorrowRepository instance;
    private final List<BorrowTransaction> mockTransactions = new ArrayList<>();

    public static synchronized BorrowRepository getInstance() {
        if (instance == null) {
            instance = new BorrowRepository();
        }
        return instance;
    }

    private BorrowRepository() {
        initMockTransactions();
    }

    private void initMockTransactions() {
        // Matches Figma screens Page 22, 31, 32, 35, 36, 37, 40, 41
        BorrowTransaction t1 = new BorrowTransaction();
        t1.setTransactionId("TRX-20261020-001");
        t1.setUserId(1L);
        t1.setBorrowerName("Juan Delacruz");
        t1.setStudentId("2024101234");
        t1.setEquipmentId(1);
        t1.setEquipmentName("Digital Multimeter");
        t1.setCategoryName("Tools & Repairs");
        t1.setImageName("img_multimeter");
        t1.setQuantity(1);
        t1.setBorrowDate("Oct 20, 2026");
        t1.setExpectedReturnDate("Oct 24, 2026");
        t1.setBorrowCondition("Good");
        t1.setBorrowAccessories("None");
        t1.setStatus("PENDING");
        t1.setAdminRemarks("Awaiting Lab Custodian approval for Laboratory Activity #3.");
        mockTransactions.add(t1);

        BorrowTransaction t2 = new BorrowTransaction();
        t2.setTransactionId("TRX-20261009-002");
        t2.setUserId(1L);
        t2.setBorrowerName("Juan Delacruz");
        t2.setStudentId("2024101234");
        t2.setEquipmentId(1);
        t2.setEquipmentName("Digital Multimeter");
        t2.setCategoryName("Tools & Repairs");
        t2.setImageName("img_multimeter");
        t2.setQuantity(1);
        t2.setBorrowDate("Oct 9, 2026");
        t2.setExpectedReturnDate("Oct 25, 2026");
        t2.setBorrowCondition("Good");
        t2.setBorrowAccessories("Test Probes");
        t2.setStatus("APPROVED");
        t2.setAdminRemarks("Approved by Engr. Roberto Santos. Please handle with care.");
        mockTransactions.add(t2);

        BorrowTransaction t3 = new BorrowTransaction();
        t3.setTransactionId("TRX-20260925-003");
        t3.setUserId(1L);
        t3.setBorrowerName("Juan Delacruz");
        t3.setStudentId("2024101234");
        t3.setEquipmentId(5);
        t3.setEquipmentName("Arduino Uno");
        t3.setCategoryName("Electronics & Components");
        t3.setImageName("img_arduino_uno");
        t3.setQuantity(1);
        t3.setBorrowDate("Sep 25, 2026");
        t3.setExpectedReturnDate("Oct 2, 2026");
        t3.setActualReturnDate("Oct 2, 2026");
        t3.setBorrowCondition("Good");
        t3.setBorrowAccessories("USB Cable, Jumper Wires");
        t3.setStatus("RETURNED");
        t3.setReturnCondition("Good");
        t3.setReturnAccessories("Complete");
        t3.setReturnRemarks("Returned in good condition, all wires complete.");
        t3.setAdminRemarks("Returned on time. Equipment verified functional.");
        mockTransactions.add(t3);

        BorrowTransaction t4 = new BorrowTransaction();
        t4.setTransactionId("TRX-20260806-004");
        t4.setUserId(1L);
        t4.setBorrowerName("Juan Delacruz");
        t4.setStudentId("2024101234");
        t4.setEquipmentId(7);
        t4.setEquipmentName("Digital Oscilloscope");
        t4.setCategoryName("Testing & Management");
        t4.setImageName("img_oscilloscope");
        t4.setQuantity(1);
        t4.setBorrowDate("Aug 06, 2026");
        t4.setExpectedReturnDate("Aug 12, 2026");
        t4.setBorrowCondition("Good");
        t4.setBorrowAccessories("BNC Probes");
        t4.setStatus("REJECTED");
        t4.setAdminRemarks("Equipment reserved for Senior Capstone Laboratory examinations.");
        mockTransactions.add(t4);
    }

    public void submitBorrow(Long userId, Equipment equipment, int quantity,
                             String borrowDate, String expectedReturnDate,
                             String condition, String accessories,
                             TransactionCallback callback) {

        Map<String, Object> req = new HashMap<>();
        req.put("userId", userId);
        req.put("equipmentId", equipment.getEquipmentId());
        req.put("quantity", quantity);
        req.put("borrowDate", borrowDate);
        req.put("expectedReturnDate", expectedReturnDate);
        req.put("borrowCondition", condition);
        req.put("borrowAccessories", accessories);

        ApiClient.getService().submitBorrowRequest(req).enqueue(new Callback<BorrowTransaction>() {
            @Override
            public void onResponse(Call<BorrowTransaction> call, Response<BorrowTransaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mockTransactions.add(0, response.body());
                    EquipmentRepository.getInstance().deductStockLocal(equipment.getEquipmentId(), quantity);
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(submitBorrowLocal(userId, equipment, quantity, borrowDate, expectedReturnDate, condition, accessories));
                }
            }

            @Override
            public void onFailure(Call<BorrowTransaction> call, Throwable t) {
                callback.onSuccess(submitBorrowLocal(userId, equipment, quantity, borrowDate, expectedReturnDate, condition, accessories));
            }
        });
    }

    private BorrowTransaction submitBorrowLocal(Long userId, Equipment equipment, int quantity,
                                                String borrowDate, String expectedReturnDate,
                                                String condition, String accessories) {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int rand = 1000 + new Random().nextInt(9000);
        String txId = "TRX-" + dateStr + "-" + rand;

        BorrowTransaction tx = new BorrowTransaction();
        tx.setTransactionId(txId);
        tx.setUserId(userId);
        tx.setBorrowerName("Juan Delacruz");
        tx.setStudentId("2024101234");
        tx.setEquipmentId(equipment.getEquipmentId());
        tx.setEquipmentName(equipment.getName());
        tx.setCategoryName(equipment.getCategoryName());
        tx.setImageName(equipment.getImageName());
        tx.setQuantity(quantity);
        tx.setBorrowDate(borrowDate);
        tx.setExpectedReturnDate(expectedReturnDate);
        tx.setBorrowCondition(condition);
        tx.setBorrowAccessories(accessories);
        tx.setStatus("PENDING");
        tx.setAdminRemarks("Submitted by borrower. Pending custodian review.");

        mockTransactions.add(0, tx);
        EquipmentRepository.getInstance().deductStockLocal(equipment.getEquipmentId(), quantity);
        return tx;
    }

    public void submitReturn(String transactionId, String returnDate,
                             String condition, String accessories, String remarks,
                             TransactionCallback callback) {

        Map<String, String> req = new HashMap<>();
        req.put("transactionId", transactionId);
        req.put("returnDate", returnDate);
        req.put("returnCondition", condition);
        req.put("returnAccessories", accessories);
        req.put("returnRemarks", remarks);

        ApiClient.getService().submitReturnRequest(req).enqueue(new Callback<BorrowTransaction>() {
            @Override
            public void onResponse(Call<BorrowTransaction> call, Response<BorrowTransaction> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateMockTransaction(response.body());
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(submitReturnLocal(transactionId, returnDate, condition, accessories, remarks));
                }
            }

            @Override
            public void onFailure(Call<BorrowTransaction> call, Throwable t) {
                callback.onSuccess(submitReturnLocal(transactionId, returnDate, condition, accessories, remarks));
            }
        });
    }

    private BorrowTransaction submitReturnLocal(String transactionId, String returnDate,
                                                String condition, String accessories, String remarks) {
        for (BorrowTransaction tx : mockTransactions) {
            if (tx.getTransactionId().equalsIgnoreCase(transactionId)) {
                tx.setStatus("RETURNED");
                tx.setActualReturnDate(returnDate);
                tx.setReturnCondition(condition);
                tx.setReturnAccessories(accessories);
                tx.setReturnRemarks(remarks);
                tx.setAdminRemarks("Equipment returned and verified by Lab Custodian.");
                EquipmentRepository.getInstance().restoreStockLocal(tx.getEquipmentId(), tx.getQuantity());
                return tx;
            }
        }
        return mockTransactions.get(0);
    }

    public void getUserTransactions(Long userId, String statusFilter, TransactionListCallback callback) {
        ApiClient.getService().getUserTransactions(userId, statusFilter).enqueue(new Callback<List<BorrowTransaction>>() {
            @Override
            public void onResponse(Call<List<BorrowTransaction>> call, Response<List<BorrowTransaction>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(filterMockTransactions(statusFilter));
                }
            }

            @Override
            public void onFailure(Call<List<BorrowTransaction>> call, Throwable t) {
                callback.onSuccess(filterMockTransactions(statusFilter));
            }
        });
    }

    public List<BorrowTransaction> filterMockTransactions(String statusFilter) {
        if (statusFilter == null || statusFilter.trim().isEmpty() || statusFilter.equalsIgnoreCase("ALL")) {
            return new ArrayList<>(mockTransactions);
        }
        List<BorrowTransaction> result = new ArrayList<>();
        for (BorrowTransaction tx : mockTransactions) {
            if (tx.getStatus().equalsIgnoreCase(statusFilter.trim())) {
                result.add(tx);
            }
        }
        return result;
    }

    public BorrowTransaction getTransactionById(String transactionId) {
        for (BorrowTransaction tx : mockTransactions) {
            if (tx.getTransactionId().equalsIgnoreCase(transactionId)) {
                return tx;
            }
        }
        return mockTransactions.get(0);
    }

    private void updateMockTransaction(BorrowTransaction updated) {
        for (int i = 0; i < mockTransactions.size(); i++) {
            if (mockTransactions.get(i).getTransactionId().equalsIgnoreCase(updated.getTransactionId())) {
                mockTransactions.set(i, updated);
                return;
            }
        }
        mockTransactions.add(0, updated);
    }

    public long getPendingCountLocal() {
        long count = 0;
        for (BorrowTransaction tx : mockTransactions) {
            if ("PENDING".equalsIgnoreCase(tx.getStatus())) count++;
        }
        return count;
    }

    public long getApprovedCountLocal() {
        long count = 0;
        for (BorrowTransaction tx : mockTransactions) {
            if ("APPROVED".equalsIgnoreCase(tx.getStatus())) count++;
        }
        return count;
    }

    public long getReturnedCountLocal() {
        long count = 0;
        for (BorrowTransaction tx : mockTransactions) {
            if ("RETURNED".equalsIgnoreCase(tx.getStatus())) count++;
        }
        return count;
    }
}
