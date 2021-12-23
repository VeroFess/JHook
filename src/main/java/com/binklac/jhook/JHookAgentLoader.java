package com.binklac.jhook;

import com.nqzero.permit.Permit;
import com.sun.tools.attach.VirtualMachine;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class JHookAgentLoader {
    private static Field InstrumentationField = null;

    protected static boolean OverWriteAllowAttachSelfFlag(){
        try {
            Method PrivateLookupIn = null;
            Method FindVarHandle   = null;
            Method ModifierSetter = null;

            Permit.godMode();

            Field AttachSelfEnabledFlag = Class.forName("sun.tools.attach.HotSpotVirtualMachine").getDeclaredField("ALLOW_ATTACH_SELF");

            for (Method MethodHandlesMethods : MethodHandles.class.getDeclaredMethods()) {
                if(MethodHandlesMethods.getName().equals("privateLookupIn")){
                    PrivateLookupIn = MethodHandlesMethods;
                    break;
                }
            }

            assert PrivateLookupIn != null;
            Object LookupClass = PrivateLookupIn.invoke(null, Field.class, MethodHandles.lookup());
            for (Method LookupMethods : LookupClass.getClass().getDeclaredMethods()) {
                if(LookupMethods.getName().equals("findVarHandle")){
                    FindVarHandle = LookupMethods;
                    break;
                }
            }

            assert FindVarHandle != null;
            Object ModifierVarHandle = FindVarHandle.invoke(LookupClass, Field.class, "modifiers", int.class);
            for (Method ModifierVarHandleMethods : ModifierVarHandle.getClass().getDeclaredMethods()) {
                if(ModifierVarHandleMethods.getName().equals("set")){
                    ModifierSetter = ModifierVarHandleMethods;
                }
            }

            assert ModifierSetter != null;
            Permit.setAccessible(ModifierSetter);
            ModifierSetter.invoke(null,ModifierVarHandle, AttachSelfEnabledFlag, (AttachSelfEnabledFlag.getModifiers() & ~Modifier.FINAL));

            Permit.setAccessible(AttachSelfEnabledFlag);
            AttachSelfEnabledFlag.set(null, Boolean.TRUE);

            return true;
        } catch (Exception  e) {
            return false;
        }
    }



    protected static Boolean InitializeJHookEnvironment() {
        if (InstrumentationField == null) {
            if(Utils.IsJvmAfter8() && !OverWriteAllowAttachSelfFlag()){
                return false;
            }

            try {
                VirtualMachine VMInstance = VirtualMachine.attach(String.valueOf(ManagementFactory.getRuntimeMXBean().getName().split("@")[0]));

                VMInstance.loadAgent(Utils.GetJHookJarPath());

                InstrumentationField = Class.forName(JHookAgent.class.getCanonicalName(), true, ClassLoader.getSystemClassLoader()).getDeclaredField("instrumentation");
                InstrumentationField.setAccessible(true);

                while(InstrumentationField.get(null) == null){
                    Thread.sleep(1);
                }

                return true;
            } catch (Exception ex) {
                return false;
            }
        } else {
            return true;
        }
    }
}
