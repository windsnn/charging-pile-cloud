package com.trick.backend.common.result;

import lombok.Data;

@Data
public class Result<T> {

    private Integer code;
    private String msg;
    private T data;

    // 成功
    public static <T> Result<T> success() {
        return build(ResultCode.SUCCESS, null);
    }

    public static <T> Result<T> success(T data) {
        return build(ResultCode.SUCCESS, data);
    }

    // 失败
    //默认500异常
    public static <T> Result<T> error(String msg) {
        return build(ResultCode.ERROR.getCode(), msg, null);
    }

    //自定义code
    public static <T> Result<T> error(Integer code, String msg) {
        return build(code, msg, null);
    }

    //自定义枚举
    public static <T> Result<T> error(ResultCode codeEnum) {
        return build(codeEnum, null);
    }

    // 构建统一结构
    private static <T> Result<T> build(ResultCode codeEnum, T data) {
        return build(codeEnum.getCode(), codeEnum.getMsg(), data);
    }

    private static <T> Result<T> build(Integer code, String msg, T data) {
        Result<T> r = new Result<>();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
}
