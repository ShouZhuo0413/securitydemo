package com.ljh.securitydemo.common;

public class CommonResult<T> {
    //    状态码
    private long code;

    //    提示信息
    private String message;

    //    数据封装
    private T data;

    public CommonResult() {

    }

    public CommonResult(long code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    //    成功返回结果
    public static <T> CommonResult<T> success(T data) {
        return new CommonResult<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }


    public static <T> CommonResult<T> success(T data, String message) {
        return new CommonResult<T>(ResultCode.SUCCESS.getCode(), message, data);
    }

    //    失败返回结果
    public static <T> CommonResult<T> failed(ResultCode resultCode) {
        return new CommonResult<T>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    //    失败返回结果
    public static <T> CommonResult<T> failed(ResultCode resultCode, String message) {
        return new CommonResult<T>(resultCode.getCode(), message, null);
    }

    //    失败返回结果
    public static <T> CommonResult<T> failed(String message) {
        return new CommonResult<T>(ResultCode.FAILED.getCode(), message, null);
    }

    //    失败返回结果

    public static <T> CommonResult<T> failed() {
        return failed(ResultCode.FAILED);
    }

}
