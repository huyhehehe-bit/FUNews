package com.funews.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * QA - quan ly tai khoan va ho so ca nhan. CHUA VIET.
 *
 * Can co:
 *   GET    /api/accounts          danh sach tai khoan (Admin)
 *   POST   /api/accounts          tao tai khoan (Admin)
 *   GET    /api/accounts/{id}     xem mot tai khoan (Admin)
 *   PUT    /api/accounts/{id}     sua tai khoan (Admin)
 *   DELETE /api/accounts/{id}     xoa tai khoan (Admin)
 *   GET    /api/accounts/lookup   ID + ten tac gia, cho ca Admin lan Staff
 *   GET    /api/profile           xem ho so cua chinh minh
 *   PUT    /api/profile           cap nhat ten va email cua chinh minh
 *
 * Phan quyen theo URL da co trong SecurityConfig.
 * Kieu du lieu vao/ra lay tu ApiDtos cua Long.
 */
@RestController
@RequestMapping("/api")
public class AccountController {
}
