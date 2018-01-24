package com.qulongjun.team.config.error;

/**
 * 数据不一致异常
 * 出现时机：当违反一致性约束时出现
 */
public class EqualException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EqualException() {
        super("两次记录不一致");
    }

    public EqualException(String message) {
        super(message);
    }
}