package com.qulongjun.team.controller;

import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;
import com.qulongjun.team.config.error.UploadException;
import com.qulongjun.team.utils.RenderUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by qulongjun on 2018/1/24.
 */
public class UploadController extends Controller {
    /**
     * Feedback
     */
    public void feedback() {
        try {
            UploadFile uploadFile = getFile();
            String newName = UUID.randomUUID().toString().replaceAll("-", "") + "." + uploadFile.getFileName().substring(uploadFile.getFileName().lastIndexOf(".") + 1);
            File f = uploadFile.getFile();
            File newFile = new File(PathKit.getWebRootPath() + "/upload/feedback/" + newName);
            File fileParent = newFile.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            Boolean back = f.renameTo(newFile);
            if (!back) {
                throw new UploadException("上传截图报错！");
            }
            Map result = new HashMap();
            result.put("name", uploadFile.getFileName());
            result.put("path", "/upload/feedback/" + newName);
            renderJson(result);
        } catch (Exception e) {
            renderError(500);
        }
    }
}
