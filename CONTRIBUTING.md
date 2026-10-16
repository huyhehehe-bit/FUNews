# Quy trình commit cho nhóm 3 người

Repo chia theo **folder sở hữu**. Nguyên tắc duy nhất phải nhớ:

> **Chỉ `git add` folder của mình. Không bao giờ `git add .`**

Git chỉ xung đột khi hai người sửa **cùng một file**. Mỗi người có folder riêng, nên làm đúng điều này thì ba người commit và push song song cả ngày cũng không conflict.

| Người | Chỉ được sửa |
| --- | --- |
| QA | `backend/QA/` và `frontend/QA/` |
| Long | `backend/Long/` và `frontend/Long/` |
| Huy | `backend/Huy/` và `frontend/Huy/` |

## 0. Lần đầu đưa repo lên GitHub — chỉ một người làm, một lần

1. Vào https://github.com/new, đặt tên repo (ví dụ `FUNews`), chọn Private hoặc Public.
   **Không** tích "Add a README file", "Add .gitignore" hay "Choose a license". Repo phải rỗng hoàn toàn, nếu không lúc push sẽ bị lệch lịch sử.
2. Chạy tại thư mục gốc repo, thay `<owner>` và `<ten-repo>`:

```powershell
git remote add origin https://github.com/<owner>/<ten-repo>.git
git push -u origin main
```

3. **Settings → Collaborators → Add people**, mời hai người còn lại, chọn quyền **Write**.
4. Mở `.github/CODEOWNERS`, thay các chỗ `@...-github-username` bằng username GitHub thật, rồi commit.
5. Khuyến nghị: **Settings → Rules → Rulesets → New branch ruleset** cho `main`, bật **Require a pull request before merging** và **Require review from Code Owners**. Khi đó không ai sửa được file dùng chung mà không qua duyệt.

Hai người còn lại chỉ cần làm mục 1.

## 1. Cài đặt một lần cho mỗi máy

```powershell
git clone https://github.com/<owner>/<ten-repo>.git
cd <ten-repo>
git config user.name "Ten cua ban"
git config user.email "email-github-cua-ban@example.com"
```

Email phải trùng email trên GitHub, nếu không commit sẽ không hiện tên bạn trong Contributors — lúc chấm điểm nhóm sẽ thiệt.

Rồi cài phụ thuộc:

```powershell
cd frontend
npm.cmd install
```

## 2. Cách làm được khuyên dùng: mỗi người một nhánh, gộp bằng Pull Request

Lịch sử rõ ràng, có chỗ review, `main` luôn chạy được. Giảng viên nhìn Pull Request là thấy ai làm gì.

### Bắt đầu một việc mới

```powershell
git checkout main
git pull
git checkout -b huy/api-news
```

Đặt tên nhánh theo mẫu `<ten>/<viec-dang-lam>`: `qa/login-api`, `long/validate-category`, `huy/api-news`.

### Trong lúc làm

```powershell
git status                    # xem minh dang sua nhung file nao

git add backend/Huy/          # CHI folder cua minh
git add frontend/Huy/         # neu co sua ca frontend

git status                    # kiem tra lai: chi duoc thay file trong folder cua minh

git commit -m "Huy: viet NewsController va NewsService cho CRUD bai viet"
```

Commit nhiều lần nhỏ tốt hơn một commit to. Mỗi commit là một ý trọn vẹn.

### Đẩy lên và mở Pull Request

```powershell
git push -u origin huy/api-news
```

Git in ra một đường link — mở link đó để tạo Pull Request. Chọn base là `main`, ghi mô tả ngắn, gán một người trong nhóm làm reviewer, rồi **Merge pull request** sau khi được duyệt.

### Sau khi Pull Request được merge

```powershell
git checkout main
git pull
git branch -d huy/api-news
```

## 3. Cách làm nhanh: commit thẳng lên `main`

Chấp nhận được vì ba người sửa ba folder khác nhau. Đổi lại không có review.

```powershell
git pull --rebase             # luon pull truoc khi bat dau lam
# ... sua code trong folder cua minh ...
git add backend/Huy/
git commit -m "Huy: them endpoint GET /api/dashboard"
git pull --rebase             # keo ve commit cua nguoi khac
git push
```

`git pull --rebase` xếp commit của bạn lên trên commit của người khác, lịch sử thẳng hàng, không sinh commit merge thừa.

Nếu `git push` bị từ chối với `rejected ... fetch first`, nghĩa là có người push trước bạn: chạy `git pull --rebase` rồi `git push` lại.

## 4. Phần dùng chung — phải thống nhất trước khi sửa

Bốn thứ này không thuộc về ai, sửa là cả nhóm bị ảnh hưởng:

| File | Ảnh hưởng |
| --- | --- |
| `backend/pom.xml` | Thêm dependency, đổi cấu hình build |
| `backend/shared/` | Entry point, `application.properties` (cổng, database, CORS, session), lớp hỗ trợ test |
| `frontend/package.json` | Thêm thư viện npm |
| `frontend/src/main.jsx` | Điểm khởi động React — **đã xong, không ai cần sửa nữa** |

Ngoài ra có ba file nằm trong folder riêng nhưng cả nhóm phụ thuộc:

| File | Chủ | Ai dùng |
| --- | --- | --- |
| `backend/Long/.../dto/ApiDtos.java` | Long | QA và Huy đều import |
| `frontend/QA/api.js` | QA | Long và Huy đều gọi |
| `frontend/Huy/useList.js`, `useAction.js` | Huy | QA và Long đều dùng |

Đổi tên trường, kiểu dữ liệu hay giá trị trả về của ba file trên là **đổi hợp đồng** — nhắn nhóm trước, và người sửa phải chạy lại test trước khi push.

Khi buộc phải sửa file dùng chung: báo nhóm, làm nhánh riêng, mở Pull Request, để hai người còn lại duyệt. `.github/CODEOWNERS` đã khai sẵn việc này.

## 5. Trước khi push, luôn chạy

```powershell
# Tai backend
.\mvnw.cmd test

# Tai frontend
npm.cmd run lint
npm.cmd run build
```

Push code làm hỏng build là chặn cả hai người còn lại.

## 6. Khi vẫn dính conflict

Chỉ xảy ra khi hai người lỡ sửa cùng một file, thường là file dùng chung.

```powershell
git pull --rebase
# Git bao: CONFLICT (content): Merge conflict in backend/pom.xml
```

Mở file bị báo, tìm các dấu:

```text
<<<<<<< HEAD
phan cua nguoi khac
=======
phan cua ban
>>>>>>> commit cua ban
```

Giữ lại phần đúng, xóa cả ba dòng dấu `<<<<<<<`, `=======`, `>>>>>>>`, rồi:

```powershell
git add backend/pom.xml
git rebase --continue
```

Chạy lại test, rồi `git push`. Muốn bỏ cuộc và quay lại như trước khi pull: `git rebase --abort`.

## 7. Những lỗi hay gặp

| Tình huống | Cách xử lý |
| --- | --- |
| Lỡ `git add .` và add cả folder người khác | Chưa commit: `git restore --staged backend/Long/`. Đã commit nhưng chưa push: `git reset --soft HEAD~1` rồi add lại cho đúng |
| Lỡ commit `node_modules`, `target` hay `data` | Đã có trong `.gitignore`. Nếu lỡ commit rồi: `git rm -r --cached frontend/node_modules backend/target backend/data` rồi commit lại |
| Push bị từ chối `rejected` | `git pull --rebase` rồi `git push` lại |
| Muốn xem mình đã sửa gì | `git diff` (chưa add) hoặc `git diff --staged` (đã add) |
| Bỏ hết thay đổi chưa commit của một file | `git restore backend/Huy/src/main/java/com/funews/backend/service/NewsService.java` |
| Không nhớ đang ở nhánh nào | `git branch` — nhánh hiện tại có dấu `*` |
| Cổng 8080 đã bị chiếm | Dừng tiến trình cũ, hoặc `.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8099"` |

## 8. Bảng lệnh rút gọn

Thay `Huy` bằng tên folder của bạn.

```powershell
git pull --rebase
git status
git add backend/Huy/ frontend/Huy/
git commit -m "Huy: mo ta ngan viec da lam"
git push
```
