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

    @Column(nullable = false)
    private String name;  // 사용자 이름 추가

    @Column(nullable = false)
    private String phoneNumber;  // 사용자 전화번호 추가

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole userRole;

    @Column(nullable = false)
    private Boolean isDelete = false;

    @Builder
    public User(String email, String password, String name, String phoneNumber, UserRole userRole) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
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

    // 사용자 이름 업데이트 메서드
    public void updateName(String name) {
        this.name = name;
    }

    // 전화번호 업데이트 메서드
    public void updatePhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
