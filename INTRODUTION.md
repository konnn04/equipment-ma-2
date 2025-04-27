# Giới thiệu về Hệ thống Quản lý Thiết bị và Bảo trì

## 1. Tổng quan dự án

Dự án "Equipment Management System" là một ứng dụng desktop được phát triển bằng JavaFX, giúp quản lý thông tin thiết bị, lịch bảo trì, lịch sử bảo trì và sửa chữa. Hệ thống hỗ trợ theo dõi thiết bị, lên lịch bảo trì định kỳ, ghi nhận kết quả bảo trì và sửa chữa, quản lý người dùng với phân quyền, và cung cấp thông báo liên quan đến lịch bảo trì.

### 1.1. Kiến trúc dự án

Dự án được xây dựng theo mô hình MVC (Model-View-Controller) với cấu trúc thư mục:
- `com.hatecode.pojo`: Các model đơn giản (Plain Old Java Objects)
- `com.hatecode.services`: Giao diện xác định các thao tác nghiệp vụ
- `com.hatecode.services.impl`: Các lớp thực thi các giao diện service
- `com.hatecode.equipmentma2`: Các controller quản lý giao diện người dùng
- `com.hatecode.utils`: Các lớp tiện ích và hỗ trợ
- `com.hatecode.security`: Phân quyền và bảo mật

### 1.2. Các thành phần chính

- **Giao diện người dùng**: Sử dụng JavaFX và FXML để xây dựng UI với nhiều tab chức năng
- **Xử lý nghiệp vụ**: Các service đảm nhiệm việc thực thi logic nghiệp vụ
- **Truy cập dữ liệu**: Sử dụng JDBC để thao tác với cơ sở dữ liệu
- **Kiểm thử đơn vị**: JUnit 5 và các công cụ liên quan

## 2. Chi tiết các Service

### 2.1. CategoryService

Service này quản lý các danh mục thiết bị trong hệ thống.

**Phương thức chính**:
- `getCategories()`: Lấy danh sách tất cả các danh mục đang hoạt động
- `getCategories(String query)`: Tìm kiếm danh mục theo tên
- `getCategoryById(int id)`: Lấy thông tin danh mục theo ID
- `getEquipmentByCategory(int id)`: Lấy danh sách thiết bị thuộc danh mục
- `addCategory(Category cate)`: Thêm danh mục mới
- `updateCategory(Category cate)`: Cập nhật thông tin danh mục
- `deleteCategory(int id)`: Xóa danh mục theo ID

### 2.2. EquipmentService

Service quản lý các thiết bị trong hệ thống.

**Phương thức chính**:
- `getEquipments()`: Lấy tất cả thiết bị
- `getEquipments(String query, int page, int limit, String key, String value)`: Tìm kiếm và lọc thiết bị
- `getEquipmentById(int id)`: Lấy thiết bị theo ID
- `addEquipment(Equipment equipment)`: Thêm thiết bị mới
- `updateEquipment(Equipment equipment)`: Cập nhật thông tin thiết bị
- `deleteEquipment(int id)`: Xóa thiết bị

Service này còn quản lý trạng thái của thiết bị (hoạt động, đang bảo trì, lỗi), chu kỳ bảo trì định kỳ, và liên kết với danh mục thiết bị.

### 2.3. MaintenanceService

Service quản lý các lịch bảo trì thiết bị.

**Phương thức chính**:
- `getMaintenances()`: Lấy tất cả lịch bảo trì
- `getMaintenances(String query)`: Tìm kiếm lịch bảo trì theo từ khóa
- `getMaintenances(String query, MaintenanceStatus status)`: Tìm kiếm và lọc theo trạng thái
- `getMaintenanceById(int id)`: Lấy lịch bảo trì theo ID
- `getCurrentMaintenances(String query)`: Lấy các lịch bảo trì đang diễn ra
- `getEquipmentMaintenancesByMaintenance(int maintenanceId)`: Lấy danh sách thiết bị trong một đợt bảo trì
- `addMaintenance(Maintenance maintenance, List<EquipmentMaintenance> equipments)`: Thêm lịch bảo trì mới với danh sách thiết bị
- `updateMaintenance(Maintenance maintenance)`: Cập nhật thông tin lịch bảo trì
- `deleteMaintenance(int id)`: Xóa lịch bảo trì

Service này xử lý việc kiểm tra trùng lịch bảo trì, đảm bảo thiết bị không được lên lịch bảo trì đồng thời, và quản lý trạng thái của lịch bảo trì (đang chờ, đang thực hiện, đã hoàn thành).

### 2.4. EquipmentMaintenanceService

Service quản lý mối quan hệ giữa thiết bị và lịch bảo trì.

**Phương thức chính**:
- `getEquipmentMaintenance()`: Lấy tất cả bản ghi bảo trì thiết bị
- `getEquipmentMaintenance(String query)`: Tìm kiếm bảo trì thiết bị theo từ khóa
- `getEquipmentMaintenance(Maintenance m)`: Lấy danh sách thiết bị theo lịch bảo trì
- `getEquipmentMaintenance(Maintenance m, User u)`: Lấy danh sách thiết bị theo lịch bảo trì và kỹ thuật viên
- `getEquipmentMaintenanceById(int id)`: Lấy thông tin bảo trì thiết bị theo ID
- `addEquipmentMaintenance(EquipmentMaintenance equipmentMaintenance)`: Thêm mối quan hệ giữa thiết bị và lịch bảo trì
- `updateEquipmentMaintenance(EquipmentMaintenance equipmentMaintenance)`: Cập nhật thông tin
- `deleteEquipmentMaintenance(int id)`: Xóa mối quan hệ

Service này lưu trữ thông tin về kỹ thuật viên phụ trách, mô tả công việc, kết quả kiểm tra, và chi phí sửa chữa (nếu có).

### 2.5. UserService

Service quản lý người dùng trong hệ thống.

**Phương thức chính**:
- `addUser(User user, File avatarFile)`: Thêm người dùng mới với ảnh đại diện
- `updateUser(User user, File avatarFile)`: Cập nhật thông tin người dùng
- `deleteUser(int id)`: Xóa người dùng
- `getUserById(int id)`: Lấy thông tin người dùng theo ID
- `getUsers(String query)`: Tìm kiếm người dùng theo từ khóa
- `getUsersByRole(Role role)`: Lấy danh sách người dùng theo vai trò
- `authenticateUser(String username, String password)`: Xác thực người dùng
- `uploadUserImage(File file)`: Tải lên ảnh đại diện người dùng

Service này quản lý thông tin người dùng, phân quyền (vai trò Admin, Technician), và xử lý xác thực mật khẩu an toàn.

### 2.6. ImageService

Service quản lý hình ảnh trong hệ thống.

**Phương thức chính**:
- `addImage(Image image)`: Thêm thông tin hình ảnh mới
- `updateImage(Image image)`: Cập nhật thông tin hình ảnh
- `deleteImage(int id)`: Xóa hình ảnh
- `getImageById(int id)`: Lấy thông tin hình ảnh theo ID

Service kết hợp với CloudinaryService để lưu trữ hình ảnh trên cloud.

### 2.7. NotificationService

Service quản lý thông báo trong hệ thống.

**Phương thức chính**:
- `getNotifications()`: Lấy tất cả thông báo
- `getUnreadNotifications()`: Lấy thông báo chưa đọc
- `markAsRead(int id)`: Đánh dấu thông báo đã đọc
- `addNotification(Notification notification)`: Thêm thông báo mới

Service phục vụ việc thông báo về lịch bảo trì sắp diễn ra và nhắc nhở bảo trì định kỳ.

## 3. Chi tiết các Controller

### 3.1. MainController

Controller chính quản lý giao diện người dùng với các tab chức năng.

**Chức năng chính**:
- Quản lý chuyển đổi giữa các tab (Equipment, Maintenance, Maintenance History, Record Repair, User Manager)
- Cập nhật thông báo
- Khởi tạo scheduler cho việc kiểm tra trạng thái bảo trì
- Xử lý đăng xuất

### 3.2. EquipmentManagerController

Controller quản lý giao diện quản lý thiết bị.

**Chức năng chính**:
- Hiển thị danh sách thiết bị trong TableView
- Thêm, sửa, xóa thiết bị
- Tìm kiếm và lọc thiết bị theo danh mục, trạng thái
- Tải lên và hiển thị hình ảnh thiết bị
- Quản lý trường thông tin của thiết bị (mã, tên, mô tả, chu kỳ bảo trì...)

### 3.3. MaintenanceManagerController

Controller quản lý lịch bảo trì thiết bị.

**Chức năng chính**:
- Hiển thị danh sách lịch bảo trì
- Thêm, sửa, xóa lịch bảo trì
- Quản lý danh sách thiết bị trong một đợt bảo trì
- Phân công kỹ thuật viên cho từng thiết bị
- Kiểm tra trùng lịch bảo trì
- Thông báo thay đổi lịch bảo trì (OperationType.CREATE, UPDATE, DELETE)

### 3.4. MaintenanceHistoryController

Controller quản lý giao diện xem lịch sử bảo trì.

**Chức năng chính**:
- Hiển thị lịch sử các đợt bảo trì đã hoàn thành
- Tìm kiếm lịch sử bảo trì theo từ khóa và thời gian
- Hiển thị danh sách thiết bị đã được bảo trì trong mỗi đợt
- Xem chi tiết thông tin bảo trì của từng thiết bị (kỹ thuật viên, kết quả, chi phí...)

### 3.5. RecordNewRepairManagerController

Controller quản lý việc ghi nhận kết quả bảo trì thiết bị.

**Chức năng chính**:
- Hiển thị danh sách lịch bảo trì đang diễn ra
- Ghi nhận thông tin bảo trì cho từng thiết bị (kết quả kiểm tra, chi phí sửa chữa)
- Cập nhật trạng thái bảo trì
- Lưu kết quả bảo trì

### 3.6. UserManagerController

Controller quản lý người dùng trong hệ thống.

**Chức năng chính**:
- Hiển thị danh sách người dùng
- Thêm, sửa, xóa người dùng
- Quản lý thông tin người dùng (tên, email, số điện thoại, vai trò...)
- Tải lên ảnh đại diện người dùng

## 4. Kiểm thử

### 4.1. Cấu trúc kiểm thử

Dự án sử dụng JUnit 5 để thực hiện các bài kiểm thử đơn vị với cấu trúc:
- `src/test/java/com/hatecode/services/`: Các lớp kiểm thử cho service
- `src/test/resources/`: Dữ liệu CSV và tài nguyên khác cho kiểm thử
- `src/test/resources/com/hatecode/services/`: Chứa các file CSV dùng cho các test tham số hóa

### 4.2. Các loại kiểm thử

#### 4.2.1. Kiểm thử Service
- **CategoryServiceImplTest**: Kiểm thử các thao tác CRUD cho danh mục thiết bị
- **EquipmentServiceImplTest**: Kiểm thử các thao tác CRUD cho thiết bị, tìm kiếm và lọc
- **UserServiceImplTest**: Kiểm thử quản lý tài khoản và xác thực người dùng
- **MaintenanceServiceImplTest**: Kiểm thử lập lịch bảo trì và xử lý trùng lịch
- **EquipmentMaintenanceServiceImplTest**: Kiểm thử liên kết giữa thiết bị và bảo trì
- **MaintenanceRepairSuggestionServiceImplTest**: Kiểm thử gợi ý sửa chữa bảo trì
- **ImageServiceImplTest**: Kiểm thử quản lý hình ảnh
- **CloudinaryServiceTestSuite**: Kiểm thử tích hợp với dịch vụ lưu trữ hình ảnh Cloudinary

#### 4.2.2. Phương pháp kiểm thử theo chức năng
- **Kiểm thử CRUD**: Kiểm tra thêm, đọc, cập nhật, xóa dữ liệu
- **Kiểm thử xác thực**: Kiểm tra đăng nhập, đăng xuất, mật khẩu
- **Kiểm thử nghiệp vụ**: Kiểm tra logic lập lịch bảo trì, phát hiện trùng lịch
- **Kiểm thử ràng buộc**: Kiểm tra các ràng buộc dữ liệu và xử lý ngoại lệ
- **Kiểm thử giao dịch**: Kiểm tra tính nhất quán khi thực hiện nhiều thao tác cùng lúc

### 4.3. Phương pháp kiểm thử

#### 4.3.1. Cài đặt kiểm thử
- Sử dụng annotation `@ExtendWith(TestDatabaseConfig.class)` để cấu hình database kiểm thử
- Phương thức `@BeforeEach` khởi tạo dữ liệu kiểm thử mới trước mỗi test
- Phương thức `@AfterEach` dọn dẹp kết nối database sau mỗi test
- Phương thức `@BeforeAll` thiết lập cấu hình chung cho tất cả các test trong class

#### 4.3.2. Kiểu kiểm thử
- **@Test**: Các phương thức kiểm thử đơn giản với một trường hợp
- **@ParameterizedTest**: Kiểm thử có tham số từ nhiều nguồn (CSV, mảng,...)
- **@CsvSource**: Đưa dữ liệu test từ chuỗi CSV
- **@CsvFileSource**: Đưa dữ liệu test từ file CSV

#### 4.3.3. Thực thi kiểm thử
Chạy toàn bộ bộ kiểm thử:
```
mvn test
```

Chạy một bộ kiểm thử cụ thể:
```
mvn -Dtest=EquipmentServiceImplTest test
```

#### 4.3.4. Mẫu kiểm thử
Mỗi phương thức kiểm thử tuân theo mẫu AAA (Arrange-Act-Assert):

```java
@Test
void testGetEquipmentById_Success() throws SQLException {
    // Arrange - Chuẩn bị dữ liệu và đối tượng cần test
    EquipmentService equipmentService = new EquipmentServiceImpl();
    
    // Act - Thực thi phương thức cần kiểm tra
    Equipment equipment = equipmentService.getEquipmentById(1);
    
    // Assert - Kiểm tra kết quả
    assertNotNull(equipment, "Equipment should not be null");
    assertEquals("ELEC001", equipment.getCode(), "Equipment code should match");
    assertEquals("Laptop", equipment.getName(), "Equipment name should match");
}
```

#### 4.3.5. Kiểm thử tham số hóa
Sử dụng dữ liệu từ nhiều nguồn khác nhau để kiểm thử cùng một logic:

```java
@ParameterizedTest
@CsvSource({
    "1, ELEC001, Laptop, 2, 1, 1, 180, Máy tính xách tay văn phòng",
    "2, MACH001, Máy khoan, 3, 2, 1, 90, Máy khoan công nghiệp"
})
void testGetEquipmentById(int id, String code, String name, int statusId, 
                         int categoryId, int imageId, int regularMaintenanceDay,
                         String description) throws SQLException {
    EquipmentService equipmentService = new EquipmentServiceImpl();
    Equipment equipment = equipmentService.getEquipmentById(id);
    
    assertEquals(id, equipment.getId());
    assertEquals(code, equipment.getCode());
    assertEquals(name, equipment.getName());
    // Thêm các kiểm tra khác...
}
```

#### 4.3.6. Kiểm thử ngoại lệ
Xác minh rằng ngoại lệ được phát sinh đúng cách:

```java
@Test
void testAddEquipmentWithInvalidData() {
    EquipmentService equipmentService = new EquipmentServiceImpl();
    Equipment e = new Equipment(null, "Test Equipment", Status.NORMAL, 0, 1, 30, "Description");
    
    assertThrows(SQLException.class, () -> equipmentService.addEquipment(e),
            "Adding equipment with invalid data should throw SQLException");
}
```

### 4.4. Tình huống kiểm thử đặc biệt

#### 4.4.1. Kiểm thử giao dịch
```java
@Test
void testTransactionalRollbackWhenUserUpdateFails() {
    UserService userService = new UserServiceImpl();
    User user = new User();
    // Setup user with invalid data that will cause update to fail
    
    assertThrows(SQLException.class, () -> userService.updateUser(user, null));
    // Verify database remains unchanged
}
```

#### 4.4.2. Kiểm thử xác thực người dùng
```java
@Test
void testAuthenticateUser_Success() {
    UserService userService = new UserServiceImpl();
    
    // Setup test user
    setupTestUser();
    
    // Test authentication
    User user = userService.authenticateUser("testuser", "1");
    
    // Verify
    assertNotNull(user);
    assertEquals("testuser", user.getUsername());
    
    // Cleanup
    cleanupTestUser();
}
```

#### 4.4.3. Kiểm thử phát hiện lịch trùng
```java
@Test
void testAddMaintenance_OverlappingSchedule() {
    MaintenanceService maintenanceService = new MaintenanceServiceImpl();
    
    Maintenance maintenance = new Maintenance(
        "Overlapping Maintenance",
        "Should detect schedule conflict",
        LocalDateTime.of(2025, 8, 1, 10, 0),
        LocalDateTime.of(2025, 8, 10, 15, 0),
        MaintenanceStatus.PENDING
    );
    
    // Verify exception is thrown for overlapping maintenance
    assertThrows(IllegalArgumentException.class, () -> maintenanceService.addMaintenance(maintenance));
}
```

## 5. Luồng hoạt động chính của ứng dụng

### 5.1. Quản lý thiết bị
1. Người dùng thêm danh mục thiết bị qua CategoryService
2. Người dùng thêm thiết bị mới với thông tin cơ bản và chu kỳ bảo trì định kỳ
3. Hệ thống tự động tính toán và đề xuất lịch bảo trì dựa trên chu kỳ

### 5.2. Lập lịch bảo trì và phát hiện xung đột
1. Người dùng tạo lịch bảo trì mới với thời gian bắt đầu và kết thúc
2. Người dùng chọn danh sách thiết bị cần bảo trì
3. Người dùng phân công kỹ thuật viên cho từng thiết bị
4. Hệ thống kiểm tra và phát hiện các xung đột:
   - Xung đột thời gian: thiết bị đã có lịch bảo trì khác
   - Xung đột kỹ thuật viên: kỹ thuật viên đã được phân công cho lịch khác
   - Lịch trong quá khứ: ngăn chặn lập lịch cho thời gian đã qua
5. Hệ thống thông báo xung đột và yêu cầu điều chỉnh

### 5.3. Ghi nhận kết quả bảo trì
1. Kỹ thuật viên xem danh sách lịch bảo trì đang thực hiện
2. Kỹ thuật viên ghi nhận kết quả kiểm tra, chi phí sửa chữa (nếu có)
3. Hệ thống cập nhật trạng thái của thiết bị và lịch bảo trì

### 5.4. Quản lý người dùng
1. Quản trị viên quản lý người dùng (thêm, sửa, xóa)
2. Hệ thống phân quyền dựa trên vai trò (Admin, Technician)

### 5.5. Thông báo và nhắc nhở
1. Hệ thống tự động kiểm tra lịch bảo trì sắp đến hạn
2. Hệ thống tự động tạo thông báo cho người dùng
3. Người dùng xem và đánh dấu đã đọc thông báo

### 5.6. Báo cáo và thống kê
1. Tạo báo cáo thời gian hoạt động/ngừng hoạt động của thiết bị
2. Phân tích chi phí bảo trì và sửa chữa theo thời gian
3. So sánh hiệu quả bảo trì giữa các kỹ thuật viên
4. Dự báo nhu cầu bảo trì dựa trên lịch sử

### 5.7. Xử lý ngoại lệ và phục hồi dữ liệu
1. Hệ thống ghi log cho mọi thao tác trên dữ liệu
2. Cơ chế sao lưu và phục hồi dữ liệu tự động
3. Xử lý lỗi giao dịch đảm bảo tính nhất quán dữ liệu
4. Thông báo chi tiết giúp người dùng khắc phục lỗi

### 5.8. Quản lý người dùng và phân quyền nâng cao
1. Quản trị viên quản lý người dùng (thêm, sửa, xóa)
2. Hệ thống phân quyền dựa trên vai trò (Admin, Technician, Manager)
3. Phân quyền chi tiết đến từng chức năng
4. Ghi lại lịch sử đăng nhập và thao tác của người dùng

### 5.9. Tích hợp dịch vụ đám mây
1. Lưu trữ hình ảnh thiết bị và người dùng trên Cloudinary
2. Sao lưu dữ liệu tự động lên đám mây
3. Đồng bộ hóa dữ liệu giữa nhiều thiết bị

## 6. Kết luận

Hệ thống quản lý thiết bị và bảo trì cung cấp giải pháp toàn diện để theo dõi thiết bị, lên lịch bảo trì, ghi nhận kết quả và quản lý người dùng. Kiến trúc MVC và việc phân tách rõ ràng các tầng nghiệp vụ giúp dễ dàng mở rộng và bảo trì. Hệ thống kiểm thử toàn diện đảm bảo độ tin cậy của phần mềm.