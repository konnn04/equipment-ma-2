package com.hatecode.utils;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.regex.Pattern;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String plainText){
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String plainPassword,String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        // Kiểm tra có ít nhất 1 chữ thường
        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();

        // Kiểm tra có ít nhất 1 chữ hoa
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();

        // Kiểm tra có ít nhất 1 số
        boolean hasDigit = Pattern.compile("[0-9]").matcher(password).find();

        // Kiểm tra có ít nhất 1 ký tự đặc biệt
        boolean hasSpecial = Pattern.compile("[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]").matcher(password).find();

        return hasLower && hasUpper && hasDigit && hasSpecial;
    }

    public static String getPasswordRequirements() {
        return "Password must contain:\n" +
                "- At least 8 characters\n" +
                "- At least 1 lowercase letter\n" +
                "- At least 1 uppercase letter\n" +
                "- At least 1 digit\n" +
                "- At least 1 special character (!@#$%^&*()_+-=[]{};':\"|,.<>/?)\n";
    }
}
