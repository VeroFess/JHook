package com.binklac.jhook;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class JHookDynamicallyLoader {
    private static final String JavaHome = System.getProperty("java.home");

    private static Boolean IsToolJarFound() {
        return (new File(JavaHome + "\\..\\lib\\tools.jar")).exists();
    }

    private static Boolean IsJavaToolLoaded() {
        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Boolean TryLoadSystemLibrary(){
        if (!Utils.IsJvmAfter8() && !IsJavaToolLoaded() && IsToolJarFound()) {
            try {
                Method AddUrl = URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { URL.class });
                AddUrl.setAccessible(true);
                AddUrl.invoke(ClassLoader.getSystemClassLoader(), (new File(JavaHome + "/../lib/tools.jar")).toURI().toURL());
            } catch (NoSuchMethodException | MalformedURLException | InvocationTargetException | IllegalAccessException e) {
                return false;
            }
        }

        return IsJavaToolLoaded();
    }
}
