package com.cms.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String[] passwords = {"admin123", "student123", "secretary123", "leader123"};

        
        for (String pwd : passwords) {
            System.out.println(pwd + " -> " + encoder.encode(pwd));
        }
    }
}
