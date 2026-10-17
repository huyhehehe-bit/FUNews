# FU News

Hệ thống quản lý tin tức FU News. Một repo, hai folder: [backend/](backend) (Java 17 + Spring Boot 3.5.16 + H2) và [frontend/](frontend) (React 19 + Vite).

Backend chạy ở `http://localhost:8080`, frontend ở `http://localhost:5173`.

## Trạng thái hiện tại

| Phần | Trạng thái |
| --- | --- |
| **frontend/** | Đầy đủ, chạy được. `npm run build` và `npm run lint` đều sạch. |
| **backend/** — tầng dữ liệu và hợp đồng API | Đầy đủ: entity, repository, DTO, xử lý lỗi, bảo mật, CORS, cấu hình database. |
| **backend/Huy/** — controller và service | Xong. `NewsController`, `NewsService`, `DataInitializer` và 7 test đều chạy. |
| **backend/QA/**, **backend/Long/** — controller và service | **Để trống.** Mỗi người tự viết phần của mình. |

Các file còn để trống chỉ có khai báo class và phần mô tả những gì cần viết:

```text
backend/QA/   AuthController, AccountController, AccountService
backend/Long/ CategoryController, CategoryService
```

Backend **compile, khởi động và chạy test được**. `DataInitializer` đã tạo dữ liệu mẫu nên database có sẵn tài khoản và danh mục. Nhưng `POST /api/auth/login` của QA chưa có, nên chưa đăng nhập được từ frontend — mọi API đều trả 401 cho tới khi QA viết xong.

## Ba người, ba folder

Mỗi người sở hữu một folder trong `backend/` và một folder trong `frontend/`. **Chỉ sửa folder của mình** — nhờ vậy ba người commit song song mà không bao giờ conflict.

| Người | Màn hình | Folder |
| --- | --- | --- |
| QA | Login, Users | `backend/QA/`, `frontend/QA/` — [hướng dẫn](backend/QA/README.md) |
| Long | Categories, Settings | `backend/Long/`, `frontend/Long/` — [hướng dẫn](backend/Long/README.md) |
| Huy | News, Dashboard | `backend/Huy/`, `frontend/Huy/` — [hướng dẫn](backend/Huy/README.md) |

Nhóm dùng ba tầng nhánh: `main` là bản ổn định, `main2` là nơi cả ba đẩy code vào và ghép với nhau, còn `qa` / `long` / `huy` là nhánh làm việc riêng. Chỉ gộp `main2` sang `main` khi không còn xung đột và test chạy xanh.

Quy trình commit đầy đủ: [CONTRIBUTING.md](CONTRIBUTING.md). Cách nhờ Claude viết code phần của mình: [PROMPT.md](PROMPT.md).

## Cấu trúc

```text
FUNews/
├── backend/
│   ├── pom.xml                  # DUNG CHUNG - doi phai bao ca nhom
│   ├── mvnw, mvnw.cmd, .mvn/    # Maven Wrapper, khong can cai Maven
│   ├── shared/                  # DUNG CHUNG
│   │   ├── src/main/java/       #   BackendApplication.java
│   │   ├── src/main/resources/  #   application.properties
│   │   └── src/test/java/       #   BackendIntegrationSupport.java
│   ├── QA/src/main/java/        # Account, AccountRepository, SecurityConfig,
│   │                            # AccountPrincipal, RefreshAccountFilter
│   │                            # + AuthController, AccountController, AccountService (trong)
│   ├── Long/src/main/java/      # Category, CategoryRepository, ApiDtos,
│   │                            # ApiException, ApiExceptionHandler
│   │                            # + CategoryController, CategoryService (trong)
│   └── Huy/src/main/java/       # NewsArticle, NewsArticleRepository, CorsConfig
│                                # + NewsController, NewsService, DataInitializer (trong)
└── frontend/
    ├── package.json             # DUNG CHUNG
    ├── vite.config.js, eslint.config.js, index.html, public/
    ├── src/main.jsx             # DUNG CHUNG - diem khoi dong, khong ai can sua nua
    ├── QA/                      # App.jsx, api.js, data.js, pages/Login, pages/Users
    ├── Long/                    # index.css, components/, pages/Categories, pages/Settings
    └── Huy/                     # useList.js, useAction.js, pages/Dashboard, pages/News
```

Ba folder `QA`, `Long`, `Huy` **chạy chung một ứng dụng**, không chạy riêng từng folder. Bên backend, `pom.xml` gom source của cả ba vào một bản build qua `build-helper-maven-plugin`. Bên frontend, `src/main.jsx` nạp `../QA/App.jsx`, rồi `App.jsx` nạp màn hình của Long và Huy.

Package Java vẫn là `com.funews.backend` cho cả ba folder — folder chỉ chia **quyền sở hữu file**, không chia package.

## Chạy backend

Cần **JDK 17 trở lên**. Kiểm tra bằng `java -version`.

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Nếu terminal chưa nhận JDK, đặt `JAVA_HOME` trước:

```powershell
$env:JAVA_HOME = 'C:\Users\dhnga\.jdks\ms-21.0.11'
```

Database H2 tự tạo ở `backend/data/funews.mv.db` ngay lần chạy đầu, không cần cài database server. Folder này đã nằm trong `.gitignore` nên mỗi người có dữ liệu riêng. Muốn tạo lại từ đầu: dừng backend, xóa folder `backend/data`, chạy lại.

### Mở bằng IntelliJ IDEA

1. **File → Open**, chọn `backend/pom.xml`, chọn **Open as Project**.
2. **File → Project Structure → Project SDK**: chọn **JDK 17** trở lên, Language level 17.
3. **Maven → Reload All Maven Projects** để IntelliJ nhận ba folder source QA, Long, Huy.
4. Mở `backend/shared/src/main/java/com/funews/backend/BackendApplication.java`, bấm Run cạnh `main()`.
5. Trong Run Configuration, đặt **Working directory** là thư mục `backend`.

Nếu IntelliJ báo `configured for JVM target 5`: **Settings → Build, Execution, Deployment → Compiler → Java Compiler**, đặt **Project bytecode version = 17**, rồi **Build → Rebuild Project**.

## Chạy frontend

Cần **Node.js 20 trở lên**.

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Mở http://localhost:5173. Vite cố định cổng 5173 để khớp CORS; nếu cổng bị chiếm thì dừng tiến trình cũ chứ đừng đổi cổng.

Frontend mặc định gọi `http://localhost:8080`. Muốn đổi, copy `.env.example` thành `.env.local`, sửa `VITE_API_BASE_URL`, rồi khởi động lại Vite. URL này **không** kèm `/api`.

Dùng cùng hostname cho cả hai bên. Nếu mở frontend bằng `127.0.0.1` thì đặt `VITE_API_BASE_URL=http://127.0.0.1:8080`, nếu không cookie `SameSite=lax` sẽ không được gửi.

## Kiểm tra

```powershell
# Tai backend
.\mvnw.cmd test        # chay test tich hop
.\mvnw.cmd verify      # test + dong goi JAR vao target/

# Tai frontend
npm.cmd run lint
npm.cmd run build
```

Test backend dùng H2 in-memory riêng, không đụng vào `backend/data/funews.mv.db`.

## Hợp đồng API

Đây là phần **đã chốt**, nằm trong [ApiDtos.java](backend/Long/src/main/java/com/funews/backend/dto/ApiDtos.java) của Long. Ba người viết controller/service theo đúng bảng này thì ghép vào là chạy.

Mọi API yêu cầu đăng nhập, trừ `GET /api/auth/csrf` và `POST /api/auth/login`. POST/PUT/DELETE yêu cầu CSRF, kể cả login và logout. Trong Postman: gọi `GET /api/auth/csrf`, giữ cookie `JSESSIONID`, gửi `token` trong header `X-CSRF-TOKEN`. Sau khi login phải lấy token mới.

| Phương thức | Endpoint | Chức năng | Ai viết |
| --- | --- | --- | --- |
| GET | `/api/auth/csrf` | Lấy CSRF token | QA |
| POST | `/api/auth/login` | `{ "username": "Admin", "password": "Admin" }` | QA |
| GET | `/api/auth/me` | Tài khoản đang đăng nhập | QA |
| POST | `/api/auth/logout` | Đăng xuất, trả 204 | *đã có sẵn trong SecurityConfig* |
| GET, POST | `/api/accounts` | Danh sách / tạo tài khoản, Admin | QA |
| GET, PUT, DELETE | `/api/accounts/{id}` | Xem / sửa / xóa tài khoản, Admin | QA |
| GET | `/api/accounts/lookup` | ID và tên tác giả | QA |
| GET, PUT | `/api/profile` | Xem / sửa tên và email của chính mình | QA |
| GET, POST | `/api/categories` | Danh sách / tạo danh mục; ghi cần Admin | Long |
| GET, PUT, DELETE | `/api/categories/{id}` | Xem / sửa / xóa danh mục; ghi cần Admin | Long |
| GET, POST | `/api/news` | Danh sách / tạo bài viết | Huy |
| GET, PUT, DELETE | `/api/news/{id}` | Xem / sửa / xóa bài viết | Huy |
| GET | `/api/dashboard` | Thống kê và 5 bài mới nhất | Huy |

`role`: 1 = Admin, 2 = Staff. `status` / `isActive`: 0 hoặc 1. `parentId: 0`: không có danh mục cha.

Lỗi trả JSON `{ "message": "..." }`: 400 dữ liệu sai, 401 chưa đăng nhập, 403 thiếu quyền hoặc CSRF, 404 không tìm thấy, 409 trùng dữ liệu hoặc đang được tham chiếu. Phần này do [ApiExceptionHandler](backend/Long/src/main/java/com/funews/backend/exception/ApiExceptionHandler.java) lo, controller chỉ cần `throw new ApiException(...)`.

Phân quyền theo URL đã khai sẵn trong [SecurityConfig](backend/QA/src/main/java/com/funews/backend/config/SecurityConfig.java) — controller không phải tự kiểm tra lại.

## Tài khoản chạy thử

`DataInitializer` đã tạo sẵn các tài khoản dưới đây, chỉ tạo khi database trống. Đăng nhập được từ giao diện sau khi QA viết xong `AuthController`.

| Tên đăng nhập | Mật khẩu | Quyền |
| --- | --- | --- |
| `Admin` hoặc `admin@fu.edu.vn` | `Admin` | Admin |
| `a@fu.edu.vn` | `123` | Admin |
| `b@fu.edu.vn` | `123` | Staff |
| `c@fu.edu.vn` | `123` | Staff |

Admin vào được cả 6 màn hình; Staff chỉ Login, Dashboard, News và Settings.
