# Hướng dẫn chạy dự án CinemaTicket

Badges (logo):

![SQL Server](https://img.shields.io/badge/SQL%20Server-CC2927?style=for-the-badge&logo=microsoft-sql-server&logoColor=white)
![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=java&logoColor=white)

Mục đích của tài liệu này
- Hướng dẫn các developer pull mã nguồn, cấu hình kết nối cơ sở dữ liệu và khởi tạo (init) bảng để chạy ứng dụng local.

Checklist (những bước sẽ thực hiện)
- [ ] Chuẩn bị môi trường (JDK, Maven, SQL Server / tools).
- [ ] Pull mã nguồn và build project.
- [ ] Cấu hình thông tin kết nối DB trong `DatabaseConnection.java` hoặc `config.properties` (nếu bạn thay đổi mã nguồn để đọc từ file).
- [ ] Chạy script SQL `initDatabase.sql` và (tùy chọn) `insertShowTimeForMovie.sql` để tạo bảng và dữ liệu mẫu.
- [ ] Chạy ứng dụng (IDE hoặc command-line).

1. Yêu cầu (Prerequisites)
- Java JDK 21 (project dùng `maven.compiler.source` và `target` = 21 trong `pom.xml`).
- Maven (để build project): mvn
- Microsoft SQL Server (local hoặc remote) và một user có quyền tạo database / bảng. Có thể dùng SQL Server Express.
- Công cụ để chạy SQL script: SQL Server Management Studio (SSMS) hoặc `sqlcmd` (Command-line). Nếu dùng PowerShell, các lệnh dưới đây tương thích.

2. Lấy mã nguồn

Từ PowerShell (hoặc Git GUI):

```powershell
git clone <repo-url>
cd "E:\IUH HK2 2025-2026\Lap-trinh-huong-su-kien\CinemaTicket"  # or open the folder in your IDE (this repo root)
git checkout <branch-name>
git pull
```

3. Build project

```powershell
mvn clean package
```

Gợi ý: nếu muốn chạy bằng command-line sau khi build, hãy copy dependencies:

```powershell
mvn dependency:copy-dependencies -DoutputDirectory=target/dependency
java -cp "target/classes;target/dependency/*" com.cinemabooking.main.Main
```

Hoặc mở project với IntelliJ IDEA: File -> Open -> chọn `[pom.xml](../pom.xml)`, rồi Run lớp `[Main.java](../src/main/java/com/cinemabooking/main/Main.java)` (Main class nằm ở `src/main/java/com/cinemabooking/main/Main.java`).

4. Cấu hình kết nối Database

File cần chỉnh: [`src/main/java/com/cinemabooking/utils/DatabaseConnection.java`](../src/main/java/com/cinemabooking/utils/DatabaseConnection.java)

Trong file này hiện tại các tham số DB được khai báo tĩnh như sau (đường dẫn file trong project):

```
private static final String SERVER_NAME = "localhost";
private static final String DATABASE_NAME = "CinemaBookingDB";
private static final String USERNAME = "sa";
private static final String PASSWORD = "123";
private static final String PORT = "1433";
```

Bạn thay trực tiếp các giá trị trên cho phù hợp môi trường SSMS của bạn (host, port, database, username, password). Nếu muốn an toàn hơn, bạn có thể thay đổi code để đọc từ `config.properties` hoặc biến môi trường.

Ví dụ JDBC URL chuẩn cho SQL Server được dùng trong `DatabaseConnection`:

```
jdbc:sqlserver://<SERVER_NAME>:<PORT>;databaseName=<DATABASE_NAME>;user=<USERNAME>;password=<PASSWORD>;encrypt=true;trustServerCertificate=true;
```

Lưu ý: nếu bạn dùng SQL Server với chứng chỉ tự ký hoặc dev local, `trustServerCertificate=true` giúp tránh lỗi kết nối do chứng chỉ.

5. Khởi tạo database và bảng (init)

Script chính nằm ở: [`src/main/resources/sql/initDatabase.sql`](../src/main/resources/sql/initDatabase.sql) (đã bao gồm tạo database, bảng, dữ liệu mẫu và lịch chiếu tự sinh). Nếu muốn chạy thêm dữ liệu showtime riêng, có thể dùng [`src/main/resources/sql/insertShowTimeForMovie.sql`](../src/main/resources/sql/insertShowTimeForMovie.sql).

Hai cách để chạy script:

a) Dùng SQL Server Management Studio (SSMS)
- Mở SSMS, kết nối tới server (ví dụ: localhost, đăng nhập bằng `sa` hoặc Windows Auth).
- File -> Open -> File -> chọn `initDatabase.sql` rồi nhấn Execute.

b) Dùng `sqlcmd` (command-line). Ví dụ trên PowerShell:

```powershell
# chạy initDatabase.sql (relative path to repo)
sqlcmd -S localhost,1433 -U sa -P "123" -i "E:\IUH HK2 2025-2026\Lap-trinh-huong-su-kien\CinemaTicket\src\main\resources\sql\initDatabase.sql"

# (tùy chọn) chạy thêm file insertShowTimeForMovie.sql
sqlcmd -S localhost,1433 -U sa -P "123" -i "E:\IUH HK2 2025-2026\Lap-trinh-huong-su-kien\CinemaTicket\src\main\resources\sql\insertShowTimeForMovie.sql"
```

Nếu bạn sử dụng Windows Authentication (Integrated), thay `-U` `-P` bằng `-E` để dùng credentials hiện tại:

```powershell
sqlcmd -S localhost,1433 -E -i "path\to\initDatabase.sql"
```

Nếu gặp lỗi `sqlcmd` không tìm thấy, cài `Microsoft Command Line Utilities` hoặc dùng SSMS.

6. Kiểm tra kết nối từ ứng dụng

- Chạy app (IDE hoặc command-line). Nếu cấu hình chính xác và database đã được tạo, ứng dụng sẽ kết nối và hiển thị dữ liệu mẫu (Users, Movies, Rooms, ShowTimes,...).
- Nếu lỗi kết nối: kiểm tra lại `SERVER_NAME`, `PORT`, `USERNAME`, `PASSWORD`. Kiểm tra TCP/IP trong SQL Server Configuration Manager đã enable và SQL Server service đang chạy.

7. Vấn đề thường gặp và cách khắc phục nhanh
- Lỗi login failed: sai username/password hoặc authentication mode của SQL Server đang là Windows only -> bật Mixed Mode.
- Lỗi timeout/kết nối: kiểm tra firewall, SQL Server TCP/IP, port (mặc định 1433).
- Lỗi driver: project đã include dependency `mssql-jdbc` trong `pom.xml`.

--
Tài liệu này cung cấp bước đủ để một dev mới có thể pull code, cấu hình DB, khởi tạo schema & seed data, rồi chạy app cục bộ.


