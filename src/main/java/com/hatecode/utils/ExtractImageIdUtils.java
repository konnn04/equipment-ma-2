package com.hatecode.utils;

public class ExtractImageIdUtils {
    public static String extractPublicIdFromUrl(String imageUrl) {
        // URL có dạng: https://res.cloudinary.com/demo/image/upload/v1234567/public_id.jpg
        String[] parts = imageUrl.split("/upload/")[1].split("/");
        String lastPart = parts[parts.length - 1];
        return lastPart.contains(".")
                ? lastPart.substring(0, lastPart.lastIndexOf('.'))
                : lastPart;
    }
}
