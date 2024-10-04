package com.example.mvc;

public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUsername(int id) {
        User user = userRepository.findById(id);
        return user != null ? user.getName() : null;
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }
}
