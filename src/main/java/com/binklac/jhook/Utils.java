package com.binklac.jhook;

import java.io.File;

public class Utils {
    public static String GetJHookJarPath() {
        String RawPath = JHook.class.getProtectionDomain().getCodeSource().getLocation().toString();

        int endIndex = RawPath.indexOf('%') == -1 ? RawPath.length() : RawPath.indexOf('%');

        if(System.getProperty("os.name").toLowerCase().contains("linux")){
            return (new File(RawPath.substring(RawPath.indexOf(':'), endIndex))).getAbsolutePath();
        } else {
            return (new File(RawPath.substring(RawPath.indexOf(':') + 1, endIndex))).getAbsolutePath();
        }
    }

    public static Boolean IsJvmAfter8() {
        try {
            String version = System.getProperty("java.version");

            if(version.startsWith("1.")) {
                version = version.substring(2, 3);
            } else {
                int dot = version.indexOf(".");
                if(dot != -1) {
                    version = version.substring(0, dot);
                }
            }

            return Integer.parseInt(version) > 8;
        } catch (Exception e) {
            return false;
        }
    }
}
