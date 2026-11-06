package edu.bulsu.equipmentborrowing.utils;

import android.content.Context;
import android.content.SharedPreferences;
import edu.bulsu.equipmentborrowing.model.User;

public class SessionManager {
    private static final String PREF_NAME = "BulSUEquipmentPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_STUDENT_ID = "studentId";
    private static final String KEY_FULL_NAME = "fullName";
    private static final String KEY_COURSE_SECTION = "courseAndSection";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT = "contactNumber";
    private static final String KEY_ROLE = "role";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_BORROWED_COUNT = "borrowedCount";
    private static final String KEY_PENDING_COUNT = "pendingCount";
    private static final String KEY_RETURNED_COUNT = "returnedCount";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        this.editor = prefs.edit();
    }

    public void saveUserSession(User user, String token) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putLong(KEY_USER_ID, user.getUserId() != null ? user.getUserId() : 1L);
        editor.putString(KEY_STUDENT_ID, user.getStudentId());
        editor.putString(KEY_FULL_NAME, user.getFullName());
        editor.putString(KEY_COURSE_SECTION, user.getCourseAndSection());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_CONTACT, user.getContactNumber());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_TOKEN, token);
        editor.putLong(KEY_BORROWED_COUNT, user.getBorrowedCount());
        editor.putLong(KEY_PENDING_COUNT, user.getPendingCount());
        editor.putLong(KEY_RETURNED_COUNT, user.getReturnedCount());
        editor.apply();
    }

    public void updateUserProfile(String fullName, String courseSection, String email, String contact) {
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putString(KEY_COURSE_SECTION, courseSection);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_CONTACT, contact);
        editor.apply();
    }

    public void updateStats(long borrowed, long pending, long returned) {
        editor.putLong(KEY_BORROWED_COUNT, borrowed);
        editor.putLong(KEY_PENDING_COUNT, pending);
        editor.putLong(KEY_RETURNED_COUNT, returned);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public User getUser() {
        if (!isLoggedIn()) {
            // Default demo student Juan Dela Cruz matching Figma prototype
            User defaultUser = new User(1L, "2024101234", "Juan Dela Cruz",
                    "BS Computer Engineering, CpE-3A", "juan@bulsu.edu.ph", "09123456789", "BORROWER");
            defaultUser.setBorrowedCount(5);
            defaultUser.setPendingCount(2);
            defaultUser.setReturnedCount(3);
            return defaultUser;
        }
        User user = new User();
        user.setUserId(prefs.getLong(KEY_USER_ID, 1L));
        user.setStudentId(prefs.getString(KEY_STUDENT_ID, "2024101234"));
        user.setFullName(prefs.getString(KEY_FULL_NAME, "Juan Dela Cruz"));
        user.setCourseAndSection(prefs.getString(KEY_COURSE_SECTION, "BS Computer Engineering, CpE-3A"));
        user.setEmail(prefs.getString(KEY_EMAIL, "juan@bulsu.edu.ph"));
        user.setContactNumber(prefs.getString(KEY_CONTACT, "09123456789"));
        user.setRole(prefs.getString(KEY_ROLE, "BORROWER"));
        user.setBorrowedCount(prefs.getLong(KEY_BORROWED_COUNT, 5));
        user.setPendingCount(prefs.getLong(KEY_PENDING_COUNT, 2));
        user.setReturnedCount(prefs.getLong(KEY_RETURNED_COUNT, 3));
        return user;
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, "");
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}
