package com.hatecode.utils;

public class PhoneNumberValidator {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        // Kiểm tra null hoặc rỗng
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }

        // Loại bỏ tất cả khoảng trắng và dấu cách
        String cleanedPhone = phoneNumber.replaceAll("\\s+", "");

        // Kiểm tra chỉ chứa số và có đúng 10 ký tự
        return cleanedPhone.matches("^[0-9]{10}$");
    }
}
