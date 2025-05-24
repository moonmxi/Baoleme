package org.demo.baoleme.error;

public class ErrorString {

    public static String isNull(String str) {
        return str + "不能为空";
    }

    public static String isRepeat(String str) {
        return str + "不能重复";
    }

    public static String isNotExist(String str) {
        return str + "不存在";
    }

    public static String isNotPermit() {
        return "您没有该操作的权限";
    }

    public static String isNotFormat(String str) {
        return str + "的格式不规范，请您重新填写";
    }

    public static String StatusIsInvalid(String str) {
        return str + "状态无效，请检查";
    }
}
