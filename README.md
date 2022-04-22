# JHook
A tool that can dynamically modify Java classes at runtime.

# Demo

Tested on Java 1.8 - Java 17, **just support JDK**

```
package com.binklac.jhook.test;

import com.binklac.jhook.JHook;
import com.binklac.jhook.Utils;
import javassist.*;

import java.io.IOException;

class JHookTestPrinter {
    public static Boolean PrintAndGetResult(){
        System.out.println("[X] The function has not been modified. ");
        return false;
    }
}

public class JHookTest {
    public static void main(String[] args) throws NotFoundException, CannotCompileException, IOException {
        System.out.println("[#] JHook operating environment check.");

        ClassPool defaultClassPool = ClassPool.getDefault();

        defaultClassPool.appendClassPath(Utils.GetJHookJarPath());

        CtClass jndiLookupClass = defaultClassPool.get("com.binklac.jhook.test.JHookTestPrinter");
        CtMethod lookup = jndiLookupClass.getDeclaredMethod("PrintAndGetResult");
        lookup.setBody("{System.out.println(\"[#] The function was successfully modified.\"); return Boolean.TRUE;}");

        if (!JHook.Instance().ModifyClass("com.binklac.jhook.test.JHookTestPrinter", jndiLookupClass.toBytecode())) {
            System.out.println("[X] The class cannot be modified, you need to install jdk or upgrade to a version after Java SE 9 to use the program normally.!");
        }

        if (JHookTestPrinter.PrintAndGetResult()) {
            System.out.println("[#] JHook is running normally and your environment supports JHook.");
        } else {
            System.out.println("[X] JHook cannot run normally and the test fails.");
        }
    }
}
```

