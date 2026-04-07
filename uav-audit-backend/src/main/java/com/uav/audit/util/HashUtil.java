package com.uav.audit.util;

import com.uav.audit.entity.AuditLog;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;

@Component
public class HashUtil {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public String calculateHash(AuditLog log) {
        StringBuilder sb = new StringBuilder();
        sb.append(log.getPrevHash() != null ? log.getPrevHash() : "");
        sb.append(log.getUserId() != null ? log.getUserId() : "");
        sb.append(log.getUsername() != null ? log.getUsername() : "");
        sb.append(log.getOperationType() != null ? log.getOperationType() : "");
        sb.append(log.getOperationDesc() != null ? log.getOperationDesc() : "");
        sb.append(log.getOperationObject() != null ? log.getOperationObject() : "");
        sb.append(log.getIpAddress() != null ? log.getIpAddress() : "");
        sb.append(log.getLogTime() != null ? log.getLogTime().format(FORMATTER) : "");

        return sha256(sb.toString());
    }

    public String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
