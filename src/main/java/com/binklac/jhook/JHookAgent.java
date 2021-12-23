package com.binklac.jhook;

import java.lang.instrument.Instrumentation;

public class JHookAgent {
    private static Instrumentation instrumentation = null;

    public static void agentmain (String agentArgs, Instrumentation inst) {
        instrumentation = inst;
    }
}
