package com.example.springjdbcproject.dao;
import com.example.springjdbcproject.model.User;
import java.util.List;

public interface ContactDao {
    List<User> getAllContacts();
    User getContactById(Long id);
    User addContact(User user);
    void updatePhoneNumber(Long id, String phoneNumber);
    void updateEmail(Long id, String email);
    void deleteContact(Long id);}
