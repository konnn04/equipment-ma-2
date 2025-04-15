package com.hatecode.services.impl;

import com.hatecode.utils.JdbcUtils;
import com.hatecode.pojo.MaintenanceRepairSuggestion;
import com.hatecode.services.interfaces.MaintenanceRepairSuggestionService;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MaintenanceRepairSuggestionImpl implements MaintenanceRepairSuggestionService {

    @Override
    public List<MaintenanceRepairSuggestion> getMaintenanceTypes() throws SQLException {
        List<MaintenanceRepairSuggestion> maintenanceRepairSuggestions = new ArrayList<>();

        try (Connection conn = JdbcUtils.getConn()) {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM Maintenance_Repair_Suggestion");

            while (rs.next()) {
                maintenanceRepairSuggestions.add(extractMaintenanceTypeFromResultSet(rs));
            }

            return maintenanceRepairSuggestions;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }

    }

    @Override
    public MaintenanceRepairSuggestion getMaintenanceTypeById(int id) throws SQLException {
        MaintenanceRepairSuggestion maintenanceRepairSuggestion = null;
        String sql = "SELECT * FROM Maintenance_Repair_Suggestion WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractMaintenanceTypeFromResultSet(rs);
            }
            return maintenanceRepairSuggestion;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    @Override
    public boolean addMaintenanceType(MaintenanceRepairSuggestion maintenanceRepairSuggestion) throws SQLException {
        if (maintenanceRepairSuggestion.getSuggestPrice() < 0) {
            throw new SQLException("Suggested price cannot be negative");
        }

        String sql = "INSERT INTO Maintenance_Repair_Suggestion (name, description, suggest_price) VALUES (?, ?, ?)";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setMaintenanceTypeStatementParameters(pstmt, maintenanceRepairSuggestion, false, 1);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    @Override
    public boolean updateMaintenanceType(MaintenanceRepairSuggestion maintenanceRepairSuggestion) throws SQLException {
        if (maintenanceRepairSuggestion.getSuggestPrice() < 0) {
            throw new SQLException("Suggested price cannot be negative");
        }
        
        String sql = "UPDATE Maintenance_Repair_Suggestion SET name = ?, description = ?, suggest_price = ? WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            setMaintenanceTypeStatementParameters(pstmt, maintenanceRepairSuggestion, true, 1);

            if (pstmt.executeUpdate() > 0)
                return true;
            else
                throw new SQLException("An unexpected error occurred while updating the database");
        }
        catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    @Override
    public boolean deleteMaintenanceType(int id) throws SQLException {
        String sql = "UPDATE Maintenance_Repair_Suggestion SET is_active = false WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() > 0)
                return true;
            else
                throw new SQLException("An unexpected error occurred while updating the database");
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }

    @Override
    public boolean hardDeleteMaintenanceType(int id) throws SQLException {
        String sql = "DELETE FROM Maintenance_Repair_Suggestion WHERE id = ?";

        try (Connection conn = JdbcUtils.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            if (pstmt.executeUpdate() > 0)
                return true;
            else
                throw new SQLException("An unexpected error occurred while updating the database");
        } catch (SQLException e) {
            throw e;
        } catch (Exception e) {
            throw new SQLException("An unexpected error occurred while accessing the database", e);
        }
    }



    /**
     * Extracts a MaintenanceRepairSuggestion object from a ResultSet.
     *
     * @param resultSet The ResultSet containing the data
     * @return A MaintenanceRepairSuggestion object populated with the data from the ResultSet
     * @throws SQLException If there's an error accessing the ResultSet
     */
    private static MaintenanceRepairSuggestion extractMaintenanceTypeFromResultSet(ResultSet resultSet) throws SQLException {
        MaintenanceRepairSuggestion maintenanceType = new MaintenanceRepairSuggestion();
        maintenanceType.setId(resultSet.getInt("id"));
        maintenanceType.setName(resultSet.getString("name"));
        maintenanceType.setDescription(resultSet.getString("description"));
        maintenanceType.setSuggestPrice(resultSet.getFloat("suggest_price"));

        Timestamp timestamp = resultSet.getTimestamp("created_date");
        if (timestamp != null) {
            maintenanceType.setcreatedAt(timestamp);
        }

        return maintenanceType;
    }

    /**
     * Sets parameters on a PreparedStatement for a MaintenanceRepairSuggestion object.
     *
     * @param preparedStatement The PreparedStatement to set parameters on
     * @param maintenanceType The MaintenanceRepairSuggestion object containing the data
     * @param includeId Whether to include the ID as a parameter (typically false for inserts, true for updates)
     * @param parameterOffset The offset to start setting parameters from (useful when statement has other parameters)
     * @return The next parameter index after the ones set by this method
     * @throws SQLException If there's an error setting the parameters
     */
    private static int setMaintenanceTypeStatementParameters(
            PreparedStatement preparedStatement,
            MaintenanceRepairSuggestion maintenanceType,
            boolean includeId,
            int parameterOffset) throws SQLException {

        int paramIndex = parameterOffset;

        // Set parameters in the order they appear in your SQL statement
        preparedStatement.setString(paramIndex++, maintenanceType.getName());
        preparedStatement.setString(paramIndex++, maintenanceType.getDescription());
        preparedStatement.setFloat(paramIndex++, maintenanceType.getSuggestPrice());

        // If including ID (for updates), add it as the last parameter
        if (includeId) {
            preparedStatement.setInt(paramIndex++, maintenanceType.getId());
        }

        return paramIndex;
    }

}
