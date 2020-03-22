package com.copsec.monitor.web.utils;

import com.esotericsoftware.reflectasm.MethodAccess;

public class CopsecReflectionUtils {
    public static Object getInvoke(Object object, String method, Object... args) {
        MethodAccess methodAccess = MethodAccess.get(object.getClass());
        return methodAccess.invoke(object, method, args);
    }
}
