package com.xmu.lxq.aiad.Exception;

/**
 * Created by asus1 on 2017/12/22.
 */

public class ExceptionUtil {

    /**
     *得到Exception所在代码的行数
     *如果没有行信息,返回-1
     */
    public static int getLineNumber(Exception e){
        StackTraceElement[] trace =e.getStackTrace();
        if(trace==null||trace.length==0) return -1; //
        return trace[0].getLineNumber();
    }
}
