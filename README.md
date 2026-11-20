# Hệ thống quản lý cửa hàng giày

Ứng dụng desktop Java Swing, sử dụng MySQL.

## Yêu cầu

- JDK 17 trở lên và MySQL 8 trở lên.
- Đặt file MySQL Connector/J (`mysql-connector-j-*.jar`) vào thư mục `lib/`.

## Cách chạy

1. Khởi động MySQL, mở và chạy `database/quanlycuahanggiay.sql` bằng MySQL Workbench để tạo database và dữ liệu mẫu (chỉ dành cho cài đặt mới).
2. Sửa `URL`, `USER`, `PASSWORD` trong `src/config/DatabaseConnection.java` theo MySQL trên máy. Tên database trong script là `QuanLyCuaHangGiay`; URL tương ứng: `jdbc:mysql://localhost:3306/QuanLyCuaHangGiay`.
3. Mở PowerShell tại thư mục dự án và chạy:

   ```powershell
   $sources = Get-ChildItem src -Recurse -Filter '*.java' | ForEach-Object { $_.FullName }
   javac -encoding UTF-8 -cp "lib/*" -d out $sources
   java -cp "out;lib/*" App
   ```

4. Đăng nhập bằng tài khoản: **admin** / **123**.

## Phân quyền

- Mặc định: **NQ001** có toàn quyền; **NQ002** quản lý sản phẩm, thuộc tính, phiếu nhập và nhà cung cấp; **NQ003** xem sản phẩm, xử lý hóa đơn, thêm/sửa khách hàng và xem khuyến mãi.
