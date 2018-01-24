package com.qulongjun.team.config.error;

/**
 * 其他类型异常
 * 出现时机：当出现其他错误时调用
 */
public class OtherException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OtherException() {
        super("其他类型错误");
    }

    public OtherException(String message) {
        super(message);
    }
}