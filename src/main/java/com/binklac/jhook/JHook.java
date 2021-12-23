package com.binklac.jhook;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Field;
import java.util.HashMap;

public class JHook {
    private static JHook SelfInstance = null;
    private static Class<?> GlobalInstance = null;
    private static Class<?> GlobalAgent = null;
    private static final JHookTransformer HookTransformer = new JHookTransformer();
    private static final HashMap<String, byte[]> ModifyMap = new HashMap<>();

    private JHook() {
    }

    public synchronized static JHook Instance() {
        try {
            SelfInstance = new JHook();

            if (SelfInstance.getClass().getClassLoader().equals(ClassLoader.getSystemClassLoader())) {
                Boolean LoadSystemLibraryResult = JHookDynamicallyLoader.TryLoadSystemLibrary();
                Boolean InitializeJHookEnvironmentResult = JHookAgentLoader.InitializeJHookEnvironment();

                if (!(LoadSystemLibraryResult && InitializeJHookEnvironmentResult)) {
                    SelfInstance = null;
                }

                GlobalInstance = SelfInstance.getClass();
            } else {
                GlobalInstance = Class.forName(JHook.class.getCanonicalName(), false, ClassLoader.getSystemClassLoader());
            }
        } catch(ClassNotFoundException e) {
            try {
                Boolean LoadSystemLibraryResult = JHookDynamicallyLoader.TryLoadSystemLibrary();
                Boolean InitializeJHookEnvironmentResult = JHookAgentLoader.InitializeJHookEnvironment();

                if (!(LoadSystemLibraryResult && InitializeJHookEnvironmentResult)) {
                    SelfInstance = null;
                }

                GlobalInstance = Class.forName(JHook.class.getCanonicalName(), false, ClassLoader.getSystemClassLoader());
            } catch (ClassNotFoundException ex) {
                SelfInstance = null;
            }
        }

        return SelfInstance;
    }

    protected byte[] GetModifyClass(String Name) {
        try {
            Field GlobalModifyMap = GlobalInstance.getDeclaredField("ModifyMap");
            GlobalModifyMap.setAccessible(true);
            HashMap<String, byte[]> GModifyMap = (HashMap<String, byte[]>) GlobalModifyMap.get(null);
            return GModifyMap.remove(Name);
        } catch (Exception e) {
            return null;
        }
    }

    public Boolean ModifyClass(String ClassName, byte[] NewClass) {
        try {
            Class<?> Clazz = Class.forName(ClassName);

            if(GlobalAgent == null) {
                GlobalAgent = Class.forName(JHookAgent.class.getCanonicalName(), false, ClassLoader.getSystemClassLoader());
            }

            Field InstrumentationField = GlobalAgent.getDeclaredField("instrumentation");
            InstrumentationField.setAccessible(true);
            Instrumentation instrumentation = (Instrumentation)InstrumentationField.get(null);

            Field GlobalModifyMap = GlobalInstance.getDeclaredField("ModifyMap");
            GlobalModifyMap.setAccessible(true);
            HashMap<String, byte[]> _ModifyMap = (HashMap<String, byte[]>) GlobalModifyMap.get(null);

            if (instrumentation != null && instrumentation.isModifiableClass(Clazz)) {
                _ModifyMap.put(Clazz.getCanonicalName(), NewClass);
                instrumentation.addTransformer(HookTransformer, true);
                instrumentation.retransformClasses(Clazz);
                while (_ModifyMap.get(Clazz.getCanonicalName()) != null) {
                    Thread.sleep(1);
                }
                instrumentation.removeTransformer(HookTransformer);
                return true;
            }

            return false;
        } catch (UnmodifiableClassException | InterruptedException | ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
