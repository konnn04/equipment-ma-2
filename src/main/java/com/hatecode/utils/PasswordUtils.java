package com.hatecode.utils;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    public static String hashPassword(String plainText){
        return BCrypt.hashpw(plainText, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String plainPassword,String hashedPassword){
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}
