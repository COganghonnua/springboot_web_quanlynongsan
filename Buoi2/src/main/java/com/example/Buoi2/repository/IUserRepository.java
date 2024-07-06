package com.example.Buoi2.repository;

import com.example.Buoi2.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IUserRepository extends JpaRepository<User, Long> {
    // Tìm User bằng tên người dùng.
    @Query("SELECT u FROM User u WHERE u.username = ?1")
    User findByUsername(String username);

    // Tìm User bằng email.
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);

    // Thêm quyền cho người dùng.
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO user_role (user_id, role_id) VALUES (?1, ?2)", nativeQuery = true)
    void addRoleToUser(Long userId, Long roleId);

    // Lấy ID của User bằng Username
    @Query("SELECT u.id FROM User u WHERE u.username = ?1")
    Long getUserIdByUsername(String username);

    // Lấy danh sách quyền từ User Id
    @Query(value = "SELECT r.name FROM role r INNER JOIN user_role ur ON r.id = ur.role_id WHERE ur.user_id = ?1", nativeQuery = true)
    String[] getRoleOfUser(Long userId);
}