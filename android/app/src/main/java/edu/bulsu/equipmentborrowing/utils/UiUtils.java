package edu.bulsu.equipmentborrowing.utils;

import android.content.Context;
import androidx.core.content.ContextCompat;
import edu.bulsu.equipmentborrowing.R;

public class UiUtils {

    public static int getEquipmentDrawableId(String imageName) {
        if (imageName == null) return R.drawable.img_multimeter;

        switch (imageName.toLowerCase().trim()) {
            case "img_multimeter":
            case "digital multimeter":
                return R.drawable.img_multimeter;
            case "img_arduino_uno":
            case "arduino uno":
                return R.drawable.img_arduino_uno;
            case "img_oscilloscope":
            case "digital oscilloscope":
                return R.drawable.img_oscilloscope;
            case "img_soldering_iron":
            case "soldering iron":
                return R.drawable.img_soldering_iron;
            case "img_screwdriver_set":
            case "screwdriver set":
                return R.drawable.img_screwdriver_set;
            case "img_breadboard":
            case "breadboard":
                return R.drawable.img_breadboard;
            case "img_jumper_wires":
            case "jumper wires":
                return R.drawable.img_jumper_wires;
            case "img_digital_caliper":
            case "digital caliper":
                return R.drawable.img_digital_caliper;
            case "img_thermometer":
            case "thermometer":
                return R.drawable.img_thermometer;
            case "img_safety_kit":
            case "safety goggles & kit":
                return R.drawable.img_safety_kit;
            default:
                return R.drawable.img_multimeter;
        }
    }

    public static int getStatusBgDrawable(String status) {
        if (status == null) return R.drawable.bg_badge_pending;
        switch (status.toUpperCase().trim()) {
            case "APPROVED":
                return R.drawable.bg_badge_approved;
            case "RETURNED":
                return R.drawable.bg_badge_returned;
            case "REJECTED":
                return R.drawable.bg_badge_rejected;
            case "PENDING":
            default:
                return R.drawable.bg_badge_pending;
        }
    }

    public static int getStatusTextColor(Context context, String status) {
        if (status == null) return ContextCompat.getColor(context, R.color.status_pending_text);
        switch (status.toUpperCase().trim()) {
            case "APPROVED":
                return ContextCompat.getColor(context, R.color.status_approved_text);
            case "RETURNED":
                return ContextCompat.getColor(context, R.color.status_returned_text);
            case "REJECTED":
                return ContextCompat.getColor(context, R.color.status_rejected_text);
            case "PENDING":
            default:
                return ContextCompat.getColor(context, R.color.status_pending_text);
        }
    }
}
