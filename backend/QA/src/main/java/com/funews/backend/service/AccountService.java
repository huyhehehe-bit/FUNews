package com.funews.backend.service;

import org.springframework.stereotype.Service;

/**
 * QA - nghiep vu tai khoan va ho so. CHUA VIET.
 *
 * Can co: listAccounts, getAccount, createAccount, updateAccount, deleteAccount,
 * lookupAccounts, getProfile, updateProfile.
 *
 * Quy tac nghiep vu phai giu:
 *   - Mat khau ma hoa BCrypt; API khong bao gio tra password hay hash.
 *   - Sua tai khoan ma de trong Password thi giu nguyen mat khau cu.
 *   - Email trung (khong phan biet hoa thuong) bi tu choi, nem ApiException 409.
 *   - Khong xoa duoc tai khoan dang dang nhap, tai khoan da co bai viet,
 *     va khong xoa hay ha quyen Admin cuoi cung.
 *
 * Dung AccountRepository, PasswordEncoder va ApiDtos cua Long.
 */
@Service
public class AccountService {
}
