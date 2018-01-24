package com.qulongjun.team.config.error;

/**
 * 上传异常
 * 出现时机：上传文件时异常
 */
public class UploadException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UploadException() {
        super("上传文件异常！");
    }

    public UploadException(String message) {
        super(message);
    }
}