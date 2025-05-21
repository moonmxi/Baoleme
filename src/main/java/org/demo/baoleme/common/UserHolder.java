package org.demo.baoleme.common;

public class UserHolder {

    private static final ThreadLocal<Long> idHolder = new ThreadLocal<>();
    private static final ThreadLocal<String> roleHolder = new ThreadLocal<>();

    public static void set(Long id, String role) {
        idHolder.set(id);
        roleHolder.set(role);
    }

    public static Long getId() {
        return idHolder.get();
    }

    public static String getRole() {
        return roleHolder.get();
    }

    public static void clear() {
        idHolder.remove();
        roleHolder.remove();
    }
}