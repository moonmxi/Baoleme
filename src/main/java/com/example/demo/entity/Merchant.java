package com.example.demo.entity;

import java.sql.Timestamp;

public class Merchant {

    public final static String DEFAULTPASSWORD = "000000";

    private Long id;
    private String username;
    private String password;
    private String phone;
    private Timestamp createdAt;

    // 无参构造方法（供MyBatis反射使用）
    public Merchant() {
    }

    public Merchant(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // 全参数构造方法：用于从数据库加载数据
    public Merchant(Long id, String username, String password, String phone, Timestamp createdAt) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.createdAt = createdAt;
    }

    public boolean isPasswordAccept(String password) {
        return this.password.equals(password);
    }

    public static boolean validateCredentials(String username, String password) {
        if (username == null || password == null) {
            return false;
        }
        // 用户名规则：3-20位字母、数字或下划线
        String usernameRegex = "^[a-zA-Z0-9_]{3,20}$";
        // 密码规则：至少6位，包含至少一个字母和一个数字
        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";

        boolean isUsernameValid = username.matches(usernameRegex);
        boolean isPasswordValid = password.matches(passwordRegex);

        return isUsernameValid && isPasswordValid;
    }

    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
