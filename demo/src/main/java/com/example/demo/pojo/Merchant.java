package com.example.demo.pojo;

public class Merchant {

    public final static String DEFAULTPASSWORD = "000000";

    String username;
    String password;

    public Merchant(String username, String password) {
        this.username = username;
        this.password = password;
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

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
