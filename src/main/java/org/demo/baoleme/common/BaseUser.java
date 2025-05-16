package org.demo.baoleme.common;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseUser {

    @TableId(type = IdType.AUTO)
    protected Long id;

    protected String username;

    protected String password;

    protected String phone;

    protected LocalDateTime createdAt;

    /**
     * 验证密码是否匹配
     * @param password
     * @return
     */
    public boolean isPasswordAccept(String password) {
        return this.password.equals(password);
    }

    /**
     * 验证用户名与密码是否合法
     * @param username
     * @param password
     * @return
     */
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
}
