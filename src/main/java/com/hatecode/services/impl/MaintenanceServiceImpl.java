package com.hatecode.services.impl;
import com.hatecode.pojo.EquipmentMaintenance;
import com.hatecode.pojo.MaintenanceStatus;
import com.hatecode.utils.ExceptionMessage;
import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.Maintenance;
import com.hatecode.services.MaintenanceService;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;

public class MaintenanceServiceImpl implements MaintenanceService {
    public static Maintenance extractMaintenance(ResultSet rs) throws SQLException {
        return new Maintenance(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("start_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                MaintenanceStatus.fromCode(rs.getInt("status")),
                rs.getBoolean("is_active"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    private List<Maintenance> getMaintenances(String query, List<Maintenance> res, String sql) throws SQLException {

        try (Connection conn = JdbcUtils.getConn();PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res.add(extractMaintenance(rs));
            }
        }
        return res;
    }

    @Override
    public List<Maintenance> getMaintenances() throws SQLException {
        List<Maintenance> maintenances = new ArrayList<>();
        String sql = "SELECT * FROM maintenance where is_active=true";
        try (Connection conn = JdbcUtils.getConn();Statement stm = conn.createStatement()) {
            ResultSet rs = stm.executeQuery(sql);
            while (rs.next()) {
                maintenances.add(extractMaintenance(rs));
            }
        }

        return maintenances;
    }

    @Override
    public List<Maintenance> getMaintenances(String query, MaintenanceStatus status) throws SQLException {
        if (query == null || query.isEmpty()) {
            query = "";
        }
        String sql = "SELECT * FROM maintenance  WHERE (title LIKE ? OR description LIKE ?) AND is_active=true AND ";
        List<Maintenance> res = new ArrayList<>();
        switch (status) {
            case PENDING:
                sql += "start_datetime > NOW()";
                break;
            case IN_PROGRESS:
                sql += "start_datetime <= NOW() AND end_datetime >= NOW()";
                break;
            case COMPLETED:
                sql += "end_datetime < NOW()";
                break;
            default:
                throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_INVALID_STATUS);
        }
        return getMaintenances(query, res, sql);
    }

    @Override
    public List<Maintenance> getMaintenances(String query) throws SQLException {
        if (query == null || query.isEmpty()) {
            query = "";
        }
        List<Maintenance> res = new ArrayList<>();
        String sql = "SELECT * FROM maintenance WHERE title LIKE ? OR description LIKE ? AND is_active=true";
        return getMaintenances(query, res, sql);
    }

    @Override
    public List<Maintenance> getCurrentMaintenances(String query) throws SQLException {
        if (query == null || query.isEmpty()) {
            query = "";
        }
        List<Maintenance> res = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        String sql = "SELECT * FROM maintenance " +
                "WHERE (title LIKE ? OR description LIKE ?) " +
                "AND is_active = true " +
                "AND start_datetime <= ? " +
                "AND end_datetime >= ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + query + "%");
            stmt.setString(2, "%" + query + "%");
            stmt.setTimestamp(3, Timestamp.valueOf(now));
            stmt.setTimestamp(4, Timestamp.valueOf(now));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                res.add(extractMaintenance(rs));
            }
        }
        return res;
    }

    @Override
    public Maintenance getMaintenanceById(int id) throws SQLException {
        Maintenance maintenance = null;
        String sql = "SELECT * FROM Maintenance WHERE id = ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maintenance = extractMaintenance(rs);
            }
        }
        return maintenance;
    }

    @Override
    public List<EquipmentMaintenance> getEquipmentMaintenancesByMaintenance(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_ID_NULL);
        }
        String sql = "SELECT em.*, e.name AS equipmentName " +
                "FROM equipment_maintenance em " +
                "JOIN equipment e ON em.equipment_id = e.id " +
                "WHERE em.is_active = true and em.maintenance_id = ?";
        List<EquipmentMaintenance> equipmentMaintenances = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                equipmentMaintenances.add(EquipmentMaintenanceServiceImpl.extractEquipmentMaintenance(rs));
            }
        }
        return equipmentMaintenances;
    }

    @Override
    public List<Maintenance> getMaintenances(String kw, LocalDate fromDate, LocalDate toDate) throws SQLException {
        if (kw == null)
            kw = "";

        List<Maintenance> res = new ArrayList<>();
        try (Connection conn = JdbcUtils.getConn()) {
            String sql = "SELECT * FROM maintenance WHERE 1=1";

            boolean hasKw = kw != null && !kw.isEmpty();
            boolean hasDate = fromDate != null && toDate != null;

            if (hasKw)
                sql += " AND (title LIKE ? OR description LIKE ?)";

            if (hasDate)
                sql += " AND start_datetime >= ? AND end_datetime <= ?";

            PreparedStatement stmt = conn.prepareStatement(sql);
            int index = 1;

            if (hasKw) {
                stmt.setString(index++, "%" + kw + "%");
                stmt.setString(index++, "%" + kw + "%");
            }

            if (hasDate) {
                stmt.setDate(index++, Date.valueOf(fromDate));
                stmt.setDate(index++, Date.valueOf(toDate));
            }

            System.out.println(stmt);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Maintenance maintenance = extractMaintenance(rs);
                res.add(maintenance);
            }
        }
        return res;
    }


    @Override
    public boolean addMaintenance(Maintenance maintenance) throws SQLException {
        if (maintenance == null) {
            return false;
        }
        if (maintenance.getTitle() == null || maintenance.getTitle().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_NAME_EMPTY);
        }
        if (maintenance.getStartDateTime().isAfter(maintenance.getEndDateTime())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_INVALID);
        }
        
        // Kiểm tra ngày bắt đầu phải là tương lai
        if (maintenance.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_IN_FUTURE);
        }
        
        // Kiểm tra xung đột lịch bảo trì
        checkOverlappingMaintenances(maintenance);
        
        String sql = "INSERT INTO Maintenance (title, description, start_datetime, end_datetime) VALUES (?, ?, ? ,?)";
        int rowsAffected = 0;
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    maintenance.setId(rs.getInt(1));
                }
            }
            return rowsAffected > 0;
        }
    }

    @Override
    public boolean addMaintenance(Maintenance maintenance, List<EquipmentMaintenance> equipmentMaintenances) throws SQLException {
        // Validate input
        if (maintenance == null) {
            return false;
        }
        if (maintenance.getTitle() == null || maintenance.getTitle().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_NAME_EMPTY);
        }
        if (maintenance.getStartDateTime().isAfter(maintenance.getEndDateTime())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_INVALID);
        }
        
        // Kiểm tra ngày bắt đầu phải là tương lai
        if (maintenance.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_IN_FUTURE);
        }
        
        Connection conn = null;
        boolean result = false;
        
        try {
            conn = JdbcUtils.getConn();
            // Tắt auto-commit để bật transaction
            conn.setAutoCommit(false);
            
            // Kiểm tra xung đột lịch bảo trì
            String checkOverlapSql = 
                "SELECT COUNT(*) FROM maintenance " +
                "WHERE is_active = true AND " +
                "(" +
                "   (? BETWEEN start_datetime AND end_datetime) OR " +  // Start time trong khoảng thời gian hiện có
                "   (? BETWEEN start_datetime AND end_datetime) OR " +  // End time trong khoảng thời gian hiện có
                "   (start_datetime BETWEEN ? AND ?) OR " +            // Start time của lịch hiện tại trong khoảng mới
                "   (end_datetime BETWEEN ? AND ?)" +                 // End time của lịch hiện tại trong khoảng mới
                ")";
            
            try (PreparedStatement checkStmt = conn.prepareStatement(checkOverlapSql)) {
                checkStmt.setTimestamp(1, Timestamp.valueOf(maintenance.getStartDateTime()));
                checkStmt.setTimestamp(2, Timestamp.valueOf(maintenance.getEndDateTime()));
                checkStmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
                checkStmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
                checkStmt.setTimestamp(5, Timestamp.valueOf(maintenance.getStartDateTime()));
                checkStmt.setTimestamp(6, Timestamp.valueOf(maintenance.getEndDateTime()));
                
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_OVERLAP);
                }
            }
            
            // Thêm bảo trì mới
            String insertMaintenanceSql = 
                "INSERT INTO Maintenance (title, description, start_datetime, end_datetime) " +
                "VALUES (?, ?, ?, ?)";
                
            try (PreparedStatement maintenanceStmt = 
                    conn.prepareStatement(insertMaintenanceSql, Statement.RETURN_GENERATED_KEYS)) {
                
                maintenanceStmt.setString(1, maintenance.getTitle());
                maintenanceStmt.setString(2, maintenance.getDescription());
                maintenanceStmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
                maintenanceStmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
                
                int maintenanceRows = maintenanceStmt.executeUpdate();
                
                if (maintenanceRows > 0) {
                    // Lấy ID được sinh tự động
                    try (ResultSet generatedKeys = maintenanceStmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            int maintenanceId = generatedKeys.getInt(1);
                            maintenance.setId(maintenanceId);
                            
                            // Thêm các bản ghi equipment maintenance nếu có
                            if (equipmentMaintenances != null && !equipmentMaintenances.isEmpty()) {
                                String insertEquipmentSql = 
                                    "INSERT INTO equipment_maintenance " +
                                    "(equipment_id, maintenance_id, technician_id, description, equipment_code, equipment_name) "+
                                    "VALUES (?, ?, ?, ?, ?, ?)";
                                    
                                try (PreparedStatement equipmentStmt = conn.prepareStatement(insertEquipmentSql)) {
                                    for (EquipmentMaintenance em : equipmentMaintenances) {
                                        em.setMaintenanceId(maintenanceId);
                                        equipmentStmt.setInt(1, em.getEquipmentId());
                                        equipmentStmt.setInt(2, maintenanceId);
                                        equipmentStmt.setInt(3, em.getTechnicianId());
                                        equipmentStmt.setString(4, em.getDescription());
                                        equipmentStmt.setString(5, em.getEquipmentCode());
                                        equipmentStmt.setString(6, em.getEquipmentName());
                                        equipmentStmt.addBatch();
                                    }
                                    equipmentStmt.executeBatch();
                                }
                            }
                            result = true;
                        }
                    }
                }
            }
            
            // Commit transaction nếu mọi thứ thành công
            if (result) {
                conn.commit();
            } else {
                conn.rollback();
            }
            
        } catch (SQLException | IllegalArgumentException e) {
            // Rollback nếu có lỗi
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw e; // Ném lại exception để caller biết lỗi
        } finally {
            // Đặt lại auto-commit và đóng kết nối
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return result;
    }

    @Override
    public boolean updateMaintenance(Maintenance maintenance) throws SQLException {
        if (maintenance == null) {
            return false;
        }
        if (maintenance.getId() <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_ID_NULL);
        }
        if (maintenance.getTitle() == null || maintenance.getTitle().isEmpty()) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_NAME_EMPTY);
        }
        if (maintenance.getStartDateTime().isAfter(maintenance.getEndDateTime())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_INVALID);
        }
        if (maintenance.getEndDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_CANNOT_UPDATE_COMPLETED);
        }
        
        // Lấy thông tin bảo trì hiện tại để kiểm tra
        Maintenance existingMaintenance = getMaintenanceById(maintenance.getId());
        if (existingMaintenance == null) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_NOT_FOUND_BY_ID + maintenance.getId());
        }
        
        // Kiểm tra nếu bảo trì chưa bắt đầu (pending), thì start date phải là thời gian tương lai
        if (existingMaintenance.getStartDateTime().isAfter(LocalDateTime.now()) && 
            maintenance.getStartDateTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_START_DATE_IN_FUTURE);
        }
        
        // Kiểm tra nếu bảo trì đã bắt đầu, không cho phép thay đổi ngày bắt đầu thành ngày trong tương lai
        if (existingMaintenance.getStartDateTime().isBefore(LocalDateTime.now()) && 
            maintenance.getStartDateTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_CANNOT_CHANGE_START_DATE);
        }
        
        // Kiểm tra xung đột lịch bảo trì (bỏ qua chính nó)
        checkOverlappingMaintenancesForUpdate(maintenance);
        
        String sql = "UPDATE Maintenance SET title = ?, description = ?, start_datetime = ?, end_datetime = ? WHERE id = ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, maintenance.getTitle());
            stmt.setString(2, maintenance.getDescription());
            stmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            stmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            stmt.setInt(5, maintenance.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteMaintenance(Maintenance maintenance) throws SQLException {
        if (maintenance == null || maintenance.getId() <= 0) {
            return false;
        }
        return deleteMaintenanceById(maintenance.getId());
    }

    @Override
    public boolean deleteMaintenanceById(int id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_ID_NULL);
        }
        String sql = "DELETE FROM `maintenance` WHERE id = ? AND is_active=true";
        try (Connection conn = JdbcUtils.getConn();PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Kiểm tra xem có lịch bảo trì nào chồng chéo không
     */
    private void checkOverlappingMaintenances(Maintenance maintenance) throws SQLException {
        String checkOverlapSql = 
            "SELECT COUNT(*) FROM maintenance " +
            "WHERE is_active = true AND " +
            "(" +
            "   (start_datetime <= ? AND end_datetime >= ?) OR " + // Kết thúc mới >= bắt đầu hiện có
            "   (start_datetime <= ? AND end_datetime >= ?) OR " + // Bắt đầu mới <= kết thúc hiện có
            "   (start_datetime >= ? AND end_datetime <= ?)" +     // Lịch hiện có nằm trong lịch mới
            ")";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement checkStmt = conn.prepareStatement(checkOverlapSql)) {
            checkStmt.setTimestamp(1, Timestamp.valueOf(maintenance.getEndDateTime()));
            checkStmt.setTimestamp(2, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(3, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(4, Timestamp.valueOf(maintenance.getEndDateTime()));
            checkStmt.setTimestamp(5, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(6, Timestamp.valueOf(maintenance.getEndDateTime()));
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_OVERLAP);
            }
        }
    }

    /**
     * Kiểm tra xem có lịch bảo trì nào chồng chéo không (trừ chính nó)
     */
    private void checkOverlappingMaintenancesForUpdate(Maintenance maintenance) throws SQLException {
        // Loại trừ maintenance hiện tại ra khỏi kiểm tra
        String checkOverlapSql =
            "SELECT COUNT(*) FROM maintenance " +
            "WHERE is_active = true AND id != ? AND " +
            "(" +
            "   (? BETWEEN start_datetime AND end_datetime) OR " +
            "   (? BETWEEN start_datetime AND end_datetime) OR " +
            "   (start_datetime BETWEEN ? AND ?) OR " +
            "   (end_datetime BETWEEN ? AND ?)" +
            ")";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement checkStmt = conn.prepareStatement(checkOverlapSql)) {
            checkStmt.setInt(1, maintenance.getId());
            checkStmt.setTimestamp(2, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(3, Timestamp.valueOf(maintenance.getEndDateTime()));
            checkStmt.setTimestamp(4, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(5, Timestamp.valueOf(maintenance.getEndDateTime()));
            checkStmt.setTimestamp(6, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(7, Timestamp.valueOf(maintenance.getEndDateTime()));
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalArgumentException(ExceptionMessage.MAINTENANCE_OVERLAP);
            }
        }
    }

    /**
     * Kiểm tra xem có lịch bảo trì nào chồng chéo cho cùng thiết bị không
     */
    private void checkOverlappingMaintenancesForEquipment(Maintenance maintenance, List<EquipmentMaintenance> equipmentMaintenances) throws SQLException {
        if (equipmentMaintenances == null || equipmentMaintenances.isEmpty()) {
            return;
        }

        // Lấy danh sách ID thiết bị cho lịch bảo trì mới
        List<Integer> equipmentIds = equipmentMaintenances.stream()
                .map(EquipmentMaintenance::getEquipmentId)
                .collect(Collectors.toList());
        
        // Nếu không có thiết bị nào, không cần kiểm tra
        if (equipmentIds.isEmpty()) {
            return;
        }

        // Tạo placeholders cho câu truy vấn SQL IN clause
        String placeholders = String.join(",", Collections.nCopies(equipmentIds.size(), "?"));
        
        String checkOverlapSql = 
            "SELECT COUNT(*) FROM maintenance m " +
            "JOIN equipment_maintenance em ON m.id = em.maintenance_id " +
            "WHERE m.is_active = true AND em.equipment_id IN (" + placeholders + ") AND " +
            "(" +
            "   (? BETWEEN m.start_datetime AND m.end_datetime) OR " +
            "   (? BETWEEN m.start_datetime AND m.end_datetime) OR " +
            "   (m.start_datetime BETWEEN ? AND ?) OR " +
            "   (m.end_datetime BETWEEN ? AND ?)" +
            ")";
        
        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement checkStmt = conn.prepareStatement(checkOverlapSql)) {
            
            // Đặt các tham số thiết bị
            int paramIndex = 1;
            for (Integer equipmentId : equipmentIds) {
                checkStmt.setInt(paramIndex++, equipmentId);
            }
            
            // Đặt các tham số thời gian
            checkStmt.setTimestamp(paramIndex++, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(paramIndex++, Timestamp.valueOf(maintenance.getEndDateTime()));
            checkStmt.setTimestamp(paramIndex++, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(paramIndex++, Timestamp.valueOf(maintenance.getEndDateTime()));
            checkStmt.setTimestamp(paramIndex++, Timestamp.valueOf(maintenance.getStartDateTime()));
            checkStmt.setTimestamp(paramIndex++, Timestamp.valueOf(maintenance.getEndDateTime()));
            
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new IllegalArgumentException(ExceptionMessage.EQUIPMENT_MAINTENANCE_TIME_CONFLICT);
            }
        }
    }
}
