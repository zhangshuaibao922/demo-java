package com.cardo.demojava.contant;


public enum Code {

    SUCCESS(200, "success"),
    // 定义错误枚举项
    NONE(400, "没有找到对应内容"),
    ADD_FAIL(401,"新增失败"),
    UPDATE_FAIL(402,"修改失败"),
    DELETE_FAIL(403,"删除失败"),
    PASSWORD_FAIL(405,"账号或密码不正确"),
    FILE_UPLOAD_FAIL(406,"文件上传失败")
    ;


    // 错误代码
    private final int code;

    // 错误消息
    private final String message;

    // 构造方法
    Code(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // 获取错误代码
    public int getCode() {
        return code;
    }

    // 获取错误消息
    public String getMessage() {
        return message;
    }

}
