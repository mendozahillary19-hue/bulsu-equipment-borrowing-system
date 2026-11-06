package edu.bulsu.equipmentborrowing.model;

import java.io.Serializable;

public class DashboardStats implements Serializable {
    private long availableEquipmentCount;
    private long myRequestsCount;
    private String studentName;
    private String studentId;

    public DashboardStats() {}

    public DashboardStats(long availableEquipmentCount, long myRequestsCount, String studentName, String studentId) {
        this.availableEquipmentCount = availableEquipmentCount;
        this.myRequestsCount = myRequestsCount;
        this.studentName = studentName;
        this.studentId = studentId;
    }

    public long getAvailableEquipmentCount() { return availableEquipmentCount; }
    public void setAvailableEquipmentCount(long availableEquipmentCount) { this.availableEquipmentCount = availableEquipmentCount; }

    public long getMyRequestsCount() { return myRequestsCount; }
    public void setMyRequestsCount(long myRequestsCount) { this.myRequestsCount = myRequestsCount; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
}
