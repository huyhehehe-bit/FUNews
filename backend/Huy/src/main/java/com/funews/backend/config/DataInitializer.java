package com.funews.backend.config;

import org.springframework.context.annotation.Configuration;

/**
 * Huy - tao du lieu mau. CHUA VIET.
 *
 * Chi chay khi database trong, va chi khi app.seed-data=true
 * (xem shared/src/main/resources/application.properties).
 *
 * Can tao:
 *   Admin / Admin        (admin@fu.edu.vn, role 1)
 *   a@fu.edu.vn / 123    (role 1, Admin)
 *   b@fu.edu.vn / 123    (role 2, Staff)
 *   c@fu.edu.vn / 123    (role 2, Staff)
 * cung vai danh muc va bai viet mau.
 *
 * Mat khau phai ma hoa bang PasswordEncoder, khong luu dang thuong.
 */
@Configuration
public class DataInitializer {
}
