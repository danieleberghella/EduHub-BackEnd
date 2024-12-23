package com.berghella.daniele.edu_hub.service;

import com.berghella.daniele.edu_hub.dao.UserDAO;
import com.berghella.daniele.edu_hub.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserService {
    private UserDAO userDAO = new UserDAO();

    public void createUser(User user){
        userDAO.createUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public Optional<User> getUserById(UUID id) {
        return userDAO.getUserById(id);
    }

    public User updateUserById(User userUpdate, UUID oldUserId) {
        return userDAO.updateUserById(userUpdate, oldUserId);
    }

    public boolean deleteUserById(UUID id) {
        return userDAO.deleteUserById(id);
    }
}
