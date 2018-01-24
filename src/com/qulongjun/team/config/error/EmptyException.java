package com.qulongjun.team.config.error;

/**
 * 空值异常
 * 出现时机：输入的内容无法映射到数据库中
 */
public class EmptyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EmptyException() {
        super("当前数据不存在！");
    }

    public EmptyException(String message) {
        super(message);
    }
}