package com.example.examensarbete.utils;

import com.example.examensarbete.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao{
    List<User> selectAllUsers();
    Optional<User> selectUserById(Integer userId);
    void insertUser(User user);
    boolean existsUserWithEmail(String email);
    boolean existsUserById(Integer userId);
    void deleteUserById(Integer userId);
    void updateUser(User update);
    Optional<User> selectUserByEmail(String email);
}