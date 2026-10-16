package com.funews.backend.service;

import org.springframework.stereotype.Service;

/**
 * Huy - nghiep vu bai viet va thong ke. CHUA VIET.
 *
 * Can co: listNews, getNews, createNews, updateNews, deleteNews, dashboard.
 *
 * Quy tac nghiep vu phai giu:
 *   - Server tu xac dinh tac gia (tai khoan dang dang nhap) va ngay tao;
 *     client gui createdById hay createdDate thi bo qua.
 *   - Sua bai giu nguyen tac gia va ngay tao ban dau.
 *   - Tieu de bat buoc, status chi nhan 0 hoac 1, categoryId phai ton tai.
 *   - Dashboard tra tong tai khoan, danh muc, bai viet, bai dang hoat dong
 *     va toi da 5 bai moi nhat.
 *
 * Dung NewsArticleRepository, CategoryRepository, AccountRepository va ApiDtos.
 */
@Service
public class NewsService {
}
