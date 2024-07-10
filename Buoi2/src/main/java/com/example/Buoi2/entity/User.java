package com.example.Buoi2.entity;

import com.example.Buoi2.validator.ValidUsername;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    @NotBlank(message = "Ten dang nhap khong duoc de trong")
    @Size(max = 50, message = "Ten dang nhap phai it hon 50 ki tu")
    @ValidUsername
    private String username;

    @Column(name = "password", length = 250, nullable = true)
    private String password;

    @Column(name = "email", length = 50)
    @Size(max = 50, message = "Email phai it hon 50 ky tu")
    private String email;

    @Column(name = "name", length = 50, nullable = false)
    @Size(max = 50, message = "Ten cua ban phai it hon 50 ky tu")
    private String name;

    @Column(name = "address", length = 255, nullable = true)
    private String address;

    @Column(name = "birthdate")
    @Past(message = "Ngay sinh phai la mot ngay trong qua khu")
    private LocalDate birthdate;

    private String googleId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private List<Role> roles;


}
