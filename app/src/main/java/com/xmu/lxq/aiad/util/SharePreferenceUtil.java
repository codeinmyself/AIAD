package com.xmu.lxq.aiad.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by asus1 on 2017/12/20.
 */

public class SharePreferenceUtil {
    private   Context context;

    public SharePreferenceUtil(Context context)
    {
        this.context = context;
    }

    /****true为已经登录过****/
    public  void setStateLogin()
    {
        SharedPreferences sp = context.getSharedPreferences("save.himi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", true);
        editor.commit();
    }
    /****true为已经登出****/
    public  void setStateLogout()
    {
        SharedPreferences sp = context.getSharedPreferences("save.himi", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("isLogin", false);
        editor.commit();
    }
    /***获取状态***/
    public  boolean getState()
    {
        SharedPreferences sp = context.getSharedPreferences("save.himi", Context.MODE_PRIVATE);
        boolean b = sp.getBoolean("isLogin", false);
        return b;
    }
}
