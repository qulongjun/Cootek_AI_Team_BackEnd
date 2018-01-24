package com.qulongjun.team.config.error;

/**
 * 验证异常
 * 出现时机：输入数据与数据库中数据不一致
 */
public class ValidateException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidateException() {
        super("信息不一致");
    }

    public ValidateException(String message) {
        super(message);
    }
}