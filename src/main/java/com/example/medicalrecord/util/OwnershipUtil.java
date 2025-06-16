package com.example.medicalrecord.util;
import org.springframework.stereotype.Component;

@Component
public class OwnershipUtil {
    public static void verifyOwnership(Long ownerId, Long currentUserId, String errorMessage) {
        if (ownerId == null || !ownerId.equals(currentUserId)) {
            throw new SecurityException(errorMessage);
        }
    }
}
