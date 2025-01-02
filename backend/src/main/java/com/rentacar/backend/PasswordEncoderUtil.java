package com.rentacar.backend;


import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String[] passwords = {
            "admin123",
            "worker123",
            "client123",
            "contraseña"
        };
        for (String password : passwords) {
            String encodedPassword = encoder.encode(password);
            System.out.println(password);
            System.out.println(encodedPassword);
        }
    }
}