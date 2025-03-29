package com.hatecode.services.interfaces;

import com.hatecode.pojo.Status;
import java.sql.SQLException;
import java.util.List;

public interface StatusService {
    List<Status> getStatuses() throws SQLException;
    Status getStatusById(int id) throws SQLException;
    boolean addStatus(Status status) throws SQLException;
    boolean updateStatus(Status status) throws SQLException;
    boolean deleteStatus(int id) throws SQLException;
}