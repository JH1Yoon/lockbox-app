package com.example.lockbox.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private Boolean isDelete = false;

    @Builder
    public User(String email, String password, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.userRole = userRole;
    }

    public void deleteUser() {
        this.isDelete = true;
    }

    public void recoverUser() {
        this.isDelete = false;
    }

    // 비밀번호 업데이트 메서드
    public void updatePassword(String password) {
        this.password = password;
    }
}
