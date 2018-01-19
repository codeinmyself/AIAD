package com.xmu.lxq.aiad.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.service.AppContext;
import com.xmu.lxq.aiad.util.OkHttpUtil;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by lxq on 2017/12/7.
 */

public class LoginActivity extends Activity{

    private EditText  et_telephone=null;
    private EditText  et_password=null;
    private TextView attempts;
    private Button login;
    private Button register;
    //3次登录机会
    private int LOGIN_CHANCES = 3;
    long errorTime;

    //还剩几次登录机会的标志，初始值就是LOGIN_CHANCES
    private int counter = LOGIN_CHANCES;
    //多次认证失败时需要等待的时间
    private long WAIT_TIME = 30000L;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);
        initView();
    }


    /**
     * initView
     */
    public void initView(){
        et_telephone = (EditText)findViewById(R.id.editText1);
        et_password = (EditText)findViewById(R.id.editText2);
        /*attempts = (TextView)findViewById(R.id.textView5);
        attempts.setText(Integer.toString(counter));*/
        login = (Button)findViewById(R.id.button1);
        register=(Button)findViewById(R.id.button2);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sp = getSharedPreferences("data", MODE_PRIVATE);

                //输入错误时的时间,如果为空的话就取0L
                errorTime = sp.getLong("errorTime", 0L);
                //获取当前时间
                long recentTime = System.currentTimeMillis();
                //如果当前时间与出错时间相差超过30s
                if(recentTime - errorTime > WAIT_TIME) {
                    if(matchLoginMsg(et_telephone.getText().toString().trim(),et_password.getText().toString().trim())) {
                        doLogin(et_telephone.getText().toString().trim(), et_password.getText().toString().trim());
                    }

                }
                else{
                    long remainTime=errorTime+WAIT_TIME-recentTime;
                    //Toast提醒
                    Toast.makeText(LoginActivity.this, "登录界面锁定中，请等待！剩余"+remainTime/1000+"s",Toast.LENGTH_SHORT).show();
                }


            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toRegister();
            }
        });
    }

    /**
     * toRegister
     */
    public void toRegister(){
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    /**
     * matchLoginMsg
     * @param telephone
     * @param password
     * @return
     */
    public boolean matchLoginMsg(String telephone,String password){
        if("".equals(telephone))
        {
            Toast.makeText(LoginActivity.this, "账号不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        if("".equals(password))
        {
            Toast.makeText(LoginActivity.this, "密码不能为空",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * doLogin
     * @param telephone
     * @param password
     */
    private void doLogin(final String telephone, String password)
    {
        // 使用Map封装请求参数
        HashMap<String, String> map = new HashMap<>();
        map.put("telephone", telephone);
        map.put("password", password);

        String url = OkHttpUtil.base_url + "login"; //POST方式
        try {
            // 发送请求
            OkHttpUtil.doPost(url, map, new Callback() {
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
                            Log.i(TAG,"进来了"+returnCode);

                            /*SharePreferenceUtil sharePreferenceUtil=new SharePreferenceUtil(LoginActivity.this);
                            sharePreferenceUtil.setStateLogin();*/
                            AppContext appContext=new AppContext();
                            appContext.setIsLogin(true);
                            /*setContentView(R.layout.activity_main);
                            TextView textView=(TextView)findViewById(R.id.account_text);
                            textView.setText(telephone);

                            Button button=(Button)findViewById(R.id.button1);
                            button.setVisibility(View.INVISIBLE);*/

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("telephone",telephone);
                            startActivity(intent);
                            finish();
                        }else{

                            if(counter==1){
                                counter=LOGIN_CHANCES;
                                Handler h = new Handler(Looper.getMainLooper());
                                h.post(new Runnable() {
                                    public void run() {
                                        et_password.setText("");
                                        Toast.makeText(LoginActivity.this, "连续" + LOGIN_CHANCES + "次认证失败，请您" + WAIT_TIME / 1000 +"秒后再登陆！", Toast.LENGTH_LONG).show();

                                    }
                                });
                                //Toast提醒
                                errorTime = System.currentTimeMillis();
                                SharedPreferences sp1 = getSharedPreferences("data", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp1.edit();
                                editor.putLong("errorTime", errorTime);
                                editor.commit();
                            }else {
                                counter--;
                                Handler h = new Handler(Looper.getMainLooper());
                                h.post(new Runnable() {
                                    public void run() {
                                        et_password.setText("");
                                        Toast.makeText(LoginActivity.this, "用户名或密码错误，请重新输入!剩余"+counter+"机会",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }


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
}
