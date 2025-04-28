# Quản lý Thiết bị

![Java](https://img.shields.io/badge/Java-17-blue)
![JavaFX](https://img.shields.io/badge/JavaFX-17-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)
![Maven](https://img.shields.io/badge/Maven-3.8-blue)
![License](https://img.shields.io/badge/License-Apache%202.0-blue)

## Tóm tắt kết quả kiểm thử
| Số lượng unit test | Số lượng testcase | Tỉ lệ bao phủ mã nguồn (service) |
|--------------------|------------------|-----------------------|
| 193/194                | >=  114         | 78%                   |



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

## Kiểm thử

### Chạy kiểm thử
Chạy toàn bộ bộ kiểm thử:
```
mvn test
```

Chạy một bộ kiểm thử cụ thể:
```
mvn -Dtest=EquipmentServiceImplTest test
```

### Cấu trúc kiểm thử
Dự án sử dụng JUnit 5 để thực hiện các bài kiểm thử đơn vị. Các bài kiểm thử được tổ chức theo cấu trúc sau:

- `src/test/java/com/hatecode/services/`: Chứa các lớp kiểm thử cho các service
- `src/test/resources/`: Chứa dữ liệu CSV và các tài nguyên khác cho kiểm thử

### Cấu hình cơ sở dữ liệu kiểm thử
- Các kiểm thử sử dụng `TestDatabaseConfig` thông qua annotation `@ExtendWith(TestDatabaseConfig.class)`
- Trước mỗi kiểm thử, database được reset thông qua `JdbcUtils.resetDatabase()`
- Dữ liệu kiểm thử được nạp vào database thông qua các lệnh SQL trong phương thức `@BeforeEach`

### Các loại kiểm thử

#### Kiểm thử Service
- **EquipmentServiceImplTest**: Kiểm thử các thao tác CRUD cho thiết bị, tìm kiếm, lọc và các xử lý nghiệp vụ liên quan
- **UserServiceImplTest**: Kiểm thử xác thực người dùng, quản lý tài khoản
- **MaintenanceServiceImplTest**: Kiểm thử lịch bảo trì, các tác vụ liên quan đến bảo trì
- **CategoryServiceImplTest**: Kiểm thử quản lý danh mục thiết bị
- **EquipmentMaintenanceServiceImplTest**: Kiểm thử liên kết giữa thiết bị và bảo trì
- **MaintenanceRepairSuggestionServiceImplTest**: Kiểm thử gợi ý sửa chữa bảo trì
- **ImageServiceImplTest**: Kiểm thử quản lý hình ảnh
- **CloudinaryServiceTestSuite**: Kiểm thử tích hợp với dịch vụ lưu trữ hình ảnh Cloudinary

#### Phương pháp kiểm thử
- **Unit Test**: Kiểm thử đơn vị cơ bản với `@Test`
- **Parameterized Test**: Kiểm thử với nhiều bộ dữ liệu sử dụng `@ParameterizedTest`
- **CSV Source Test**: Sử dụng dữ liệu từ file CSV với `@CsvFileSource` hoặc `@CsvSource`

#### Ví dụ kiểm thử
1. Kiểm thử đơn vị cơ bản:
```java
@Test
void testGetEquipmentById_NotFound() throws SQLException {
    EquipmentService equipmentService = new EquipmentServiceImpl();
    // Act
    Equipment equipment = equipmentService.getEquipmentById(999);

    // Assert
    assertNull(equipment, "Should return null for non-existent equipment");
}
```

2. Kiểm thử tham số hóa:
```java
@ParameterizedTest
@CsvSource({
    "1, ELEC001, Laptop, 2, 1"
})
void testGetEquipments(int id, String code, String name, int statusId, int categoryId) throws SQLException {
    // Thực hiện kiểm thử với các giá trị từ CsvSource
}
```

### Viết kiểm thử mới
1. Tạo lớp kiểm thử trong package `com.hatecode.services`
2. Sử dụng annotation `@ExtendWith(TestDatabaseConfig.class)` để sử dụng cấu hình database kiểm thử
3. Trong phương thức `@BeforeEach`, reset và khởi tạo dữ liệu kiểm thử
4. Viết các phương thức kiểm thử với annotation `@Test` hoặc `@ParameterizedTest`
5. Sử dụng các phương thức assert để kiểm tra kết quả

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
- JUnit 5: Framework kiểm thử

## Bản quyền
Phần mềm được phát triển dưới giấy phép Apache License 2.0.
