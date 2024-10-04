package com.example.mvc;

public interface UserRepository {
    User findById(int id);
    void save(User user);
}
