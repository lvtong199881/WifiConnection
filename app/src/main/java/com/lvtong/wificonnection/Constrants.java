package com.lvtong.wificonnection;

/**
 * @author tong.lv
 * @date 2020/1/9
 */
public class Constrants {

    public static String SCAN_RESULT = "ScanResult";

    public static class RequestCode {

        public static int SECURITY = 1;
        public static int PERMISSION = 2;
    }

    public static class Security {

        public static String SECURITY = "security";

        public static int TYPE_NONE = 0;
        public static int TYPE_WEP = 1;
        public static int TYPE_WPA = 1;
        public static int TYPE_EAP = 1;
    }
}
