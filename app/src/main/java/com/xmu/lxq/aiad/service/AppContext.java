package com.xmu.lxq.aiad.service;

import android.app.Application;

/**
 * Created by asus1 on 2017/12/21.
 */

public class AppContext extends Application{
    public  static boolean isLogin=false;

   /* private  AppContext instance;

    public  AppContext getInstance() {
        return instance;
    }*/
   /* @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }*/
    public boolean isLogin(){
        return isLogin;
    }
    public void setIsLogin(boolean flag){
        isLogin = flag;
    }
}
