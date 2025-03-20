package com.hatecode.services;

import com.hatecode.pojo.Role;
import java.sql.SQLException;
import java.util.List;

public interface RoleService {
    List<Role> getRoles() throws SQLException;
    Role getRoleById(int id) throws SQLException;
    boolean addRole(Role role) throws SQLException;
    boolean updateRole(Role role) throws SQLException;
    boolean deleteRole(int id) throws SQLException;
}