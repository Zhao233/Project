package com.duckduckgogogo.services;

import com.duckduckgogogo.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserService extends JpaRepository<User, Long>{
    public User findByUsername(String username);

    public User findByEmail(String email);

    public User findById(long id);

    @Query("select user from User user where user.username like %?1% or user.firstName like %?1% or user.lastName like %?1% or user.email like %?1%")
    public Page<User> findAll(String q, Pageable pageable);
}
