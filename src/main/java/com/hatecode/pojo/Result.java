package com.hatecode.pojo;

import java.util.List;

public enum Result {
    // Enum values
    NORMALLY(1, "Bình thường"),
    NEED_REPAIR(2, "Cần sửa chữa"),
    BROKEN(3, "Hỏng"),
    NEEDS_DISPOSAL(4, "Cần thanh lý");

    private final int code;
    private final String name;

    Result(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static Result fromCode(int code) {
        for (Result result : Result.values()) {
            if (result.getCode() == code) {
                return result;
            }
        }
        throw new IllegalArgumentException("Invalid code: " + code);
    }

    public static List<Result> getAllResults() {
        return List.of(Result.values());
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
