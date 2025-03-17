//package org.example.bidflow;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.security.crypto.password.PasswordEncoder;
//
//@SpringBootTest
//public class PasswordEncoderTest {
//
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    @Test
//    public void generateEncryptedPassword() {
//        String rawPassword = "adminpassword";
//        String encodedPassword = passwordEncoder.encode(rawPassword);
//        System.out.println("Encrypted password: " + encodedPassword);
//    }
//}