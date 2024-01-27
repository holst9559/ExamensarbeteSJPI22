package com.example.examensarbete.utils;

import com.example.examensarbete.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {
    List<User> selectAllCustomers();
    Optional<User> selectCustomerById(Integer userId);
    void insertCustomer(User user);
    boolean existsCustomerWithEmail(String email);
    boolean existsCustomerById(Integer userId);
    void deleteCustomerById(Integer userId);
    void updateCustomer(User user);
    Optional<User> selectUserByEmail(String email);
}