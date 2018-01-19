package com.xmu.lxq.aiad.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.model.User;
import com.xmu.lxq.aiad.util.OkHttpUtil;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by asus1 on 2017/12/16.
 */

public class RegisterActivity extends Activity{

    private EditText telephoneText;
    private EditText passwordText;
    private EditText checkPasswordText;

    private Button submitButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_register);
        initView();
    }

    /**
     * initial view
     */
    public void initView(){
        telephoneText=(EditText)findViewById(R.id.resetpwd_edit_name);
        passwordText=(EditText)findViewById(R.id.resetpwd_edit_pwd_old);
        checkPasswordText=(EditText)findViewById(R.id.resetpwd_edit_pwd_new);

        submitButton=(Button)findViewById(R.id.register_btn_sure);
        cancelButton=(Button)findViewById(R.id.register_btn_cancel);

        submitButton.setOnClickListener(m_register_Listener);
        cancelButton.setOnClickListener(m_register_Listener);
    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.register_btn_sure:                       //确认按钮的监听事件
                    registerCheck();
                    break;
                case R.id.register_btn_cancel:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(RegisterActivity.this,LoginActivity.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };

    /**
     * registerCheck
     */
    public void registerCheck() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String telephone = telephoneText.getText().toString().trim();
            String userPwd = passwordText.getText().toString().trim();
            String userPwdCheck = passwordText.getText().toString().trim();
            /*//检查用户是否存在
            int count=mUserDataManager.findUserByName(userName);
            //用户已经存在时返回，给出提示文字
            if(count>0){
                Toast.makeText(this, getString(R.string.name_already_exist, userName),Toast.LENGTH_SHORT).show();
                return ;
            }*/
            if(userPwd.equals(userPwdCheck)==false){     //两次密码输入不一样
                Toast.makeText(this, "两次密码输入不一样",Toast.LENGTH_SHORT).show();
                return ;
            } else {
                User mUser = new User(Long.parseLong(telephone), userPwd);
                doRegister(mUser);
            }
        }
    }

    /**
     * doRegister
     * @param user
     */
    private void doRegister(User user)
    {
        String url = OkHttpUtil.base_url + "register"; //POST方式
        try {
            // 发送请求
            OkHttpUtil.doPost(url,user, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG,"相应失败！！！！！");

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String tempResponse =  response.body().string();
                    Log.e(TAG,"啦啦啦啦啦啦：：：:"+tempResponse);
                    if(response.isSuccessful()){
                        Log.e(TAG,"相应成功！！！！！");
                    }
                    try{
                        JSONObject jsonObject=new JSONObject(tempResponse);
                        String returnCode=jsonObject.getString("code");
                        Log.i(TAG,"坎坎坷坷扩扩"+returnCode);
                        if("200".equals(returnCode)){
                            Log.i(TAG,"注册成功!"+returnCode);
                            Handler h = new Handler(Looper.getMainLooper());
                            h.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "注册成功!",Toast.LENGTH_SHORT).show();
                                }
                            });

                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }else{

                            Handler h = new Handler(Looper.getMainLooper());
                            h.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(RegisterActivity.this, "注册失败!",Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });  //POST方式
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * isUserNameAndPwdValid
     * @return
     */
    public boolean isUserNameAndPwdValid() {
        if (telephoneText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }else if(checkPasswordText.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_check_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * onBackPressed
     */
    @Override
    public void onBackPressed() {
        /*Intent intent = new Intent(this, SignOutActivity.class);
        startActivityForResult(intent, 1);*/

    }
}
