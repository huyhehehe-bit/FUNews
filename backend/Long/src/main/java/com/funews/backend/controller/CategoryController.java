package com.funews.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Long - quan ly danh muc. CHUA VIET.
 *
 * Can co:
 *   GET    /api/categories        danh sach danh muc (Admin va Staff deu doc duoc)
 *   POST   /api/categories        tao danh muc (Admin)
 *   GET    /api/categories/{id}   xem mot danh muc
 *   PUT    /api/categories/{id}   sua danh muc (Admin)
 *   DELETE /api/categories/{id}   xoa danh muc (Admin)
 *
 * Phan quyen ghi (chi Admin) da khai trong SecurityConfig cua QA.
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {
}
