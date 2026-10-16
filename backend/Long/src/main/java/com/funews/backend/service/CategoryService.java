package com.funews.backend.service;

import org.springframework.stereotype.Service;

/**
 * Long - nghiep vu danh muc. CHUA VIET.
 *
 * Can co: listCategories, getCategory, createCategory, updateCategory, deleteCategory.
 *
 * Quy tac nghiep vu phai giu:
 *   - Khong cho chon chinh no hoac hau due lam danh muc cha (tranh vong lap).
 *   - Khong xoa duoc danh muc con con hoac danh muc dang co bai viet, nem ApiException 409.
 *   - Ten danh muc bat buoc, parentId = 0 nghia la khong co cha.
 *
 * Dung CategoryRepository va ApiDtos.
 */
@Service
public class CategoryService {
}
