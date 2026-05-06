# CinemaTicket - Ứng dụng quản lý bán vé rạp chiếu phim (Swing)

![SQL Server](https://img.shields.io/badge/SQL%20Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)

Mục tiêu: cung cấp một ứng dụng desktop nhỏ gọn cho việc quản lý bán vé rạp chiếu phim, hỗ trợ đăng nhập theo vai trò, xem lịch chiếu, bán vé (POS), quản lý khách hàng và hoá đơn.

Ngôn ngữ & công nghệ chính
- Java 11+ (hoặc tương đương)
- Swing cho giao diện người dùng (UI)
- Maven để quản lý build
- JDBC để kết nối cơ sở dữ liệu (cấu hình trong `src/main/resources/config.properties`)
- Sử dụng TMDB API cho một số thông tin phim (có lớp `TMDBApiService`)

Tổng quan kiến trúc

Ứng dụng theo mô hình tổ chức thư mục theo chức năng (tương tự MVC-lite):

- `com.cinemabooking.model` — các POJO/Entity (User, Movie, ShowTime, Seat, Ticket, Invoice, Customer, ...).
- `com.cinemabooking.dao` — lớp truy cập dữ liệu (DAO) chịu trách nhiệm đọc/ghi dữ liệu vào cơ sở dữ liệu.
- `com.cinemabooking.service` — business logic (đăng nhập, booking, truy vấn lịch chiếu, gọi API TMDB...).
- `com.cinemabooking.view` — các Frame/Panel Swing chịu trách nhiệm hiển thị giao diện (ví dụ: `LoginFrame`, `MainDashboardFrame`, `POSPanel`, `TicketDialog`).
- `com.cinemabooking.connectdb` — kết nối CSDL (ví dụ: `DatabaseConnection`).
- `com.cinemabooking.utils` — tiện ích chung (ví dụ: `ConfigLoader`, `SessionManager`).

Luồng chính
1. Ứng dụng khởi động bằng `com.cinemabooking.main.Main` và hiển thị `LoginFrame`.
2. Người dùng nhập tài khoản/mật khẩu -> `AuthService` xử lý xác thực thông qua `UserDAO` và `DatabaseConnection`.
3. Nếu đăng nhập thành công, `MainDashboardFrame` được mở, từ đó truy cập các chức năng: bán vé (POS), quản lý khách hàng, xem thống kê, v.v.
4. Khi bán vé, `BookingService` / `ShowTimeService` xử lý logic ghế, tạo `Ticket` và `Invoice` rồi lưu vào DB thông qua DAO.

Tính năng chính
- Đăng nhập với phân quyền (role) (mô-đun `AuthService`, `Role` enum).
- Giao diện POS cho việc chọn phim, suất chiếu, ghế và in vé (dialogs & panels trong `view`).
- Quản lý khách hàng và hoá đơn (`CustomerDAO`, `InvoiceDAO`).
- Đồng bộ hoặc truy vấn thông tin phim từ TMDB thông qua `TMDBApiService`.
- Tài nguyên sẵn có: ảnh, icon, và script SQL mẫu (`src/main/resources/sql/insertShowTimeForMovie.sql`).

Cấu trúc file quan trọng
- `pom.xml` — khai báo build/dependency (Maven).
- `src/main/resources/config.properties` — cấu hình ứng dụng (DB connection string, TMDB key nếu cần).
- `src/main/resources/sql/*.sql` — script SQL hỗ trợ để thêm dữ liệu mẫu.
- `src/main/java/com/cinemabooking/...` — mã nguồn Java.

Cấu hình cơ sở dữ liệu
1. Mở file `src/main/resources/config.properties` và cấu hình các giá trị DB (ví dụ URL, user, password).
2. Tạo database và chạy các script SQL nếu cần (ví dụ các script tạo bảng nếu không có sẵn). File `insertShowTimeForMovie.sql` là ví dụ để chèn lịch chiếu mẫu.
3. `DatabaseConnection` sử dụng `config.properties` để kết nối; đảm bảo driver JDBC tương ứng đã có trong `pom.xml` (ví dụ: MySQL/MariaDB/Postgres).

Chạy ứng dụng
- Trong IDE (IntelliJ IDEA / Eclipse):
  1. Import dự án như Maven project.
  2. Cấu hình `config.properties` với thông tin DB.
  3. Chạy `com.cinemabooking.main.Main`.

- Dòng lệnh (PowerShell/Windows):
  mvn clean package; # build project
  # Chạy trực tiếp class chính (giả sử các dependency đã được xử lý bằng plugin hoặc chạy từ IDE):
  mvn exec:java -Dexec.mainClass="com.cinemabooking.main.Main"

Lưu ý: nếu `exec-maven-plugin` không được cấu hình trong `pom.xml`, chạy bằng IDE thường là cách nhanh nhất. Ngoài ra có thể xuất thành jar chứa tất cả dependency (fat/uber jar) nếu cần triển khai độc lập.

Hướng dẫn phát triển & đóng góp
- Mã nguồn tuân theo bố cục `package` như trên — thêm DAO/Service/Model mới tương ứng khi mở rộng tính năng.
- Viết unit test cho logic trong folder `src/test/java` (hiện chưa có test mẫu trong repo). 
- Trước khi gửi pull request: đảm bảo build thành công (`mvn -DskipTests package`) và mô tả rõ thay đổi.

Tài nguyên bổ sung
 - Script mẫu: `src/main/resources/sql/insertShowTimeForMovie.sql`.
 - Hình ảnh/icon UI: `src/main/resources/icons/`, `src/main/resources/images/`.
 - [Hướng dẫn chạy dự án / cấu hình DB](Doc/guideline-run-project.md)
 - [Class diagram (Mermaid)](Doc/class-diagram.md)

Bảo mật & lưu ý
- Không lưu trữ mật khẩu dạng plain text trong mã nguồn; hiện nay `config.properties` chỉ chứa cấu hình DB — hãy dùng cơ chế an toàn hơn khi đưa vào môi trường thật (environment variables, vault, ...).
- Kiểm tra injection khi xử lý tham số SQL (sử dụng PreparedStatement trong DAO).

License
- Mặc định đặt license MIT. Bạn có thể thay đổi nếu muốn một license khác.

Liên hệ
- Dự án được phát triển phục vụ học tập/đồ án môn học. Nếu cần trợ giúp hoặc mở rộng tính năng, bạn có thể mở issue hoặc liên hệ trực tiếp với tôi nguyenhaidangnt2006@gmail.com.




