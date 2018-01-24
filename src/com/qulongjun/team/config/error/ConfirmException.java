package com.qulongjun.team.config.error;

/**
 * 确认异常
 * 出现时机：尚未通过邮箱验证
 */
public class ConfirmException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ConfirmException() {
        super("尚未确认邮箱验证！");
    }

    public ConfirmException(String message) {
        super(message);
    }
}