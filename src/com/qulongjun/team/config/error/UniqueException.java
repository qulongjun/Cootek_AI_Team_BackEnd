package com.qulongjun.team.config.error;

/**
 * 数据重复异常
 * 出现时机：当违反唯一性约束时出现
 */
public class UniqueException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UniqueException() {
        super("当前记录已经存在");
    }

    public UniqueException(String message) {
        super(message);
    }
}