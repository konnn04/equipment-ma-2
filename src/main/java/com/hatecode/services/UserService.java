package com.hatecode.services;

import com.hatecode.pojo.Image;
import com.hatecode.pojo.User;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    List<User> getUsers(String kw,int roleId) throws SQLException;
    User getUserById(int id) throws SQLException;
    User getUserByUsername(String username) throws SQLException;
    boolean addUser(User user, Image image) throws SQLException;
    boolean updateUser(User user) throws SQLException;
    boolean updateUser(User user, String password, Image newImage) throws SQLException;
    boolean deleteUser(int id) throws SQLException;
    User authenticateUser(String username, String password) throws SQLException;
    String getUserImage(User user);
    String uploadUserImage(File imageFile);
    boolean deleteUserImage(String publicId);
    int getCount();
}