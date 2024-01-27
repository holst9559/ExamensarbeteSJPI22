package com.example.examensarbete.data;

import com.example.examensarbete.entities.User;
import com.example.examensarbete.repository.UserRepository;
import com.example.examensarbete.utils.UserDao;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jpa")
public class UserJPADataAccessService implements UserDao {

    private final UserRepository userRepository;

    public UserJPADataAccessService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> selectAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> selectUserById(Integer userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void insertUser(User user) {
        userRepository.save(user);

    }

    @Override
    public boolean existsUserWithEmail(String email) {
        return userRepository.existsUserByEmail(email);
    }

    @Override
    public boolean existsUserById(Integer userId) {
        return userRepository.existsById(userId);
    }

    @Override
    public void deleteUserById(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public void updateUser(User update) {
        userRepository.save(update);
    }

    @Override
    public Optional<User> selectUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
