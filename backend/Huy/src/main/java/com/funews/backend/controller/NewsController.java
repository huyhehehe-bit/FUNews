package com.funews.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Huy - bai viet va dashboard. CHUA VIET.
 *
 * Can co:
 *   GET    /api/news        danh sach bai viet
 *   POST   /api/news        tao bai viet
 *   GET    /api/news/{id}   xem mot bai viet
 *   PUT    /api/news/{id}   sua bai viet
 *   DELETE /api/news/{id}   xoa bai viet
 *   GET    /api/dashboard   thong ke va 5 bai moi nhat
 */
@RestController
@RequestMapping("/api")
public class NewsController {
}
