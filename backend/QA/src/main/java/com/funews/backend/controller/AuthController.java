package com.funews.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * QA - dang nhap va phien lam viec. CHUA VIET.
 *
 * Can co:
 *   GET  /api/auth/csrf  tra ve { token }
 *   POST /api/auth/login nhan { username, password }, tao session
 *   GET  /api/auth/me    tra ve tai khoan dang dang nhap
 *
 * Logout da duoc SecurityConfig xu ly san o POST /api/auth/logout (tra 204).
 * Dung AuthenticationManager, SecurityContextRepository, CsrfTokenRepository
 * va SessionAuthenticationStrategy - tat ca da khai bao trong SecurityConfig.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
}
