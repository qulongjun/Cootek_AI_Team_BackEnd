package com.qulongjun.team.utils;

import com.jfinal.core.Controller;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;
import sun.misc.BASE64Encoder;
import sun.misc.Regexp;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by qulongjun on 2016/10/26.
 */
public class ParaUtils extends Controller {

    public final static DateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    /**
     * 拆分请求中的搜索条件
     *
     * @param condition
     * @return
     */
    public static Map getSplitCondition(String condition) {
        String[] conditionArr = condition.split("&");
        Map paraMap = new HashMap<>();
        for (int i = 0; i < conditionArr.length; i++) {
            String temp = conditionArr[i];
            String[] kv = temp.split("=");
            if (kv.length == 2) paraMap.put(kv[0], convertRequestParam(kv[1]));
        }
        return paraMap;
    }

    /**
     * 过滤中文字符,防止乱码
     *
     * @param param
     * @return
     */
    public static String convertRequestParam(String param) {
        if (param != null) {
            try {
                return URLDecoder.decode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                System.out.println("request convert to UTF-8 error ");
            }
        }
        return "";
    }

    /**
     * 利用MD5进行加密
     *
     * @param str 待加密的字符串
     * @return 加密后的字符串
     * @throws NoSuchAlgorithmException     没有这种产生消息摘要的算法
     * @throws UnsupportedEncodingException
     */
    public static String EncoderByMd5(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //确定计算方法
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        BASE64Encoder base64en = new BASE64Encoder();
        //加密后的字符串
        String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
        return newstr;
    }


    /**
     * 判断用户密码是否正确
     *
     * @param newpasswd 用户输入的密码
     * @param oldpasswd 数据库中存储的密码－－用户密码的摘要
     * @return
     * @throws NoSuchAlgorithmException
     * @throws UnsupportedEncodingException
     */
    public static boolean checkpassword(String newpasswd, String oldpasswd) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        if (EncoderByMd5(newpasswd).equals(oldpasswd))
            return true;
        else
            return false;
    }


    /**
     * 判断key是否在数组arrs中
     *
     * @param arrs
     * @param key
     * @return
     */
    public static Boolean isInArray(String[] arrs, String key) {
        for (int i = 0; arrs != null && i < arrs.length; i++) {
            if (arrs[i].equals(key)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取系统配置的rowCount信息
     *
     * @return
     */
    public static int getRowCount() {
//        Prop setting = PropKit.use("setting.properties");
//        int rowCount = setting.getInt("rowCount");
//        return rowCount;
        return 8;
    }

    public static String IPConvert(String ip) {
        if (isIP(ip)) {
            String[] ipList = ip.split("\\.");
            String str = "";
            if (ipList.length == 4) {
                str = ipList[0] + "." + ipList[1] + ".**.**";
            }
            if (ipList.length == 6) {
                str = ipList[0] + "." + ipList[1] + "." + ipList[2] + ".**.**.**";
            }
            return str != "" ? str : "佚名";
        } else
            return "佚名";
    }


    public static boolean isIP(String addr) {
        if (addr.length() < 7 || addr.length() > 15 || "".equals(addr)) {
            return false;
        }
        /**
         * 判断IP格式和范围
         */
        String rexp = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";

        Pattern pat = Pattern.compile(rexp);

        Matcher mat = pat.matcher(addr);

        boolean ipAddress = mat.find();

        return ipAddress;
    }

    /**
     * 获取当前网络ip
     *
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
            if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                //根据网卡取本机配置的IP
                InetAddress inet = null;
                try {
                    inet = InetAddress.getLocalHost();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                ipAddress = inet.getHostAddress();
            }
        }
        //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > 15) { //"***.***.***.***".length() = 15
            if (ipAddress.indexOf(",") > 0) {
                ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
            }
        }
        return ipAddress;
    }
}
