/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.hatecode.services;

import java.util.List;

/**
 *
 * @author ADMIN
 */
public interface MailServices {
    void sendEmailNotify(String receivedUser, String subject, List<Integer> maintainanceIds, String username, String password);
}
