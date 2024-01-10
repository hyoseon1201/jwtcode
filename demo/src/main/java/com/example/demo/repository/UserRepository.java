package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, String> {

    @EntityGraph(attributePaths = "userRoleList")
    @Query("select a from User a where a.username = :username")
    User getWithRoles(@Param("username") String username);
}
