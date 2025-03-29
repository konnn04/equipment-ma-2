# Quản lý Thiết bị

![Java](https://img.shields.io/badge/Java-17-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.8-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## Giới thiệu
Phần mềm Quản lý Thiết bị là ứng dụng desktop được phát triển bằng JavaFX, giúp quản lý danh mục thiết bị, lịch bảo trì và phân quyền người dùng.

## Tính năng
- Đăng nhập bảo mật
- Quản lý thiết bị (thêm, sửa, xóa)
- Phân loại thiết bị theo danh mục
- Theo dõi trạng thái thiết bị
- Lập lịch bảo trì
- Báo cáo thống kê
- Quản lý người dùng và phân quyền

## Yêu cầu hệ thống
- JDK 23 trở lên
- MySQL 8.0 trở lên
- Maven 3.8 trở lên

## Cài đặt

### 1. Tạo cơ sở dữ liệu
```sql
CREATE DATABASE equipmentma2;
```

### 2. Import dữ liệu mẫu
```
mysql -u root -p equipmentma2 < etc/db/equipmentma2.sql
```
Hoặc import riêng từng tệp:
```
mysql -u root -p equipmentma2 < etc/db/create_tables.sql
mysql -u root -p equipmentma2 < etc/db/create_sample_data.sql
```

### 3. Cấu hình kết nối MySQL
Mở tệp JdbcUtils.java và thay đổi thông tin kết nối:
```java
public static Connection getConn() throws SQLException {
    return DriverManager.getConnection("jdbc:mysql://localhost/equipmentma2", "tên_đăng_nhập", "mật_khẩu");
}
```

### 4. Build dự án
```
mvn clean install
```

## Chạy ứng dụng

### Cách 1: Sử dụng Maven
```
mvn javafx:run
```

### Cách 2: Chạy file JAR
```
java -jar target/equipment-ma-2-1.0-SNAPSHOT.jar
```

## Đăng nhập mặc định
- Tên đăng nhập: `admin`
- Mật khẩu: `1`

## Cấu trúc dự án
- java
  - `com.hatecode.equipmentma2`: Lớp chính của ứng dụng
  - `com.hatecode.equipmentma2.Controllers`: Các bộ điều khiển giao diện
  - `com.hatecode.pojo`: Các đối tượng Java đơn giản
  - `com.hatecode.services`: Giao diện xử lý nghiệp vụ
  - `com.hatecode.services.impl`: Các lớp thực thi nghiệp vụ

- resources: Tệp FXML và tài nguyên khác
- db: Script cơ sở dữ liệu

## Công nghệ sử dụng
- JavaFX: Xây dựng giao diện người dùng
- MySQL: Cơ sở dữ liệu
- Maven: Quản lý dự án và thư viện

## Bản quyền
Phần mềm được phát triển dưới giấy phép Apache License 2.0.
