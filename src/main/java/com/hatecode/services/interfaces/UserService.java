package com.hatecode.services.interfaces;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.User;
import java.io.File;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    List<User> getUsers(String kw,int roleId) throws SQLException;
    User getUserById(int id) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    boolean addUser(User user) throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean updateUser(User user, Image newImage) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    boolean authenticateUser(String username, String password) throws SQLException;
    String getUserImage(User user);
    String uploadUserImage(File imageFile);
    boolean deleteUserImage(String publicId);
//    Features
//    Get
}