package com.binklac.jhook;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class JHookTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        return JHook.Instance().GetModifyClass(className.replace("/", "."));
    }
}
