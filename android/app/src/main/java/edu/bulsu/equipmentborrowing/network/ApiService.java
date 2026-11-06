package edu.bulsu.equipmentborrowing.network;

import edu.bulsu.equipmentborrowing.model.BorrowTransaction;
import edu.bulsu.equipmentborrowing.model.Category;
import edu.bulsu.equipmentborrowing.model.DashboardStats;
import edu.bulsu.equipmentborrowing.model.Equipment;
import edu.bulsu.equipmentborrowing.model.User;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.Map;

public interface ApiService {

    // Auth
    @POST("api/auth/login")
    Call<AuthResponse> login(@Body Map<String, String> request);

    @POST("api/auth/register")
    Call<AuthResponse> register(@Body Map<String, String> request);

    @GET("api/auth/profile/{userId}")
    Call<User> getProfile(@Path("userId") Long userId);

    @PUT("api/auth/profile/{userId}")
    Call<User> updateProfile(@Path("userId") Long userId, @Body Map<String, String> request);

    // Dashboard
    @GET("api/dashboard/stats/{userId}")
    Call<DashboardStats> getDashboardStats(@Path("userId") Long userId);

    // Equipment
    @GET("api/equipment")
    Call<List<Equipment>> getEquipment(
            @Query("search") String search,
            @Query("categoryId") Integer categoryId
    );

    @GET("api/equipment/featured")
    Call<List<Equipment>> getFeaturedEquipment();

    @GET("api/equipment/categories")
    Call<List<Category>> getCategories();

    @GET("api/equipment/{id}")
    Call<Equipment> getEquipmentById(@Path("id") Integer id);

    // Borrow & Return
    @POST("api/borrow")
    Call<BorrowTransaction> submitBorrowRequest(@Body Map<String, Object> request);

    @POST("api/borrow/return")
    Call<BorrowTransaction> submitReturnRequest(@Body Map<String, String> request);

    @GET("api/borrow/user/{userId}")
    Call<List<BorrowTransaction>> getUserTransactions(
            @Path("userId") Long userId,
            @Query("status") String status
    );

    @GET("api/borrow/{transactionId}")
    Call<BorrowTransaction> getTransactionDetails(@Path("transactionId") String transactionId);

    // Admin
    @GET("api/admin/transactions")
    Call<List<BorrowTransaction>> getAdminTransactions(@Query("status") String status);

    @POST("api/admin/transactions/{transactionId}/approve")
    Call<BorrowTransaction> approveTransaction(
            @Path("transactionId") String transactionId,
            @Body Map<String, Object> body
    );

    @POST("api/admin/transactions/{transactionId}/reject")
    Call<BorrowTransaction> rejectTransaction(
            @Path("transactionId") String transactionId,
            @Body Map<String, Object> body
    );

    class AuthResponse {
        public boolean success;
        public String message;
        public String token;
        public User user;
    }
}
