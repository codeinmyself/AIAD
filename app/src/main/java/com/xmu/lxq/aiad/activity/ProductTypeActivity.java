package com.xmu.lxq.aiad.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.util.OkHttpUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by asus1 on 2017/12/26.
 */

public class ProductTypeActivity extends Activity{

    RadioGroup radioGroup;
    RadioButton radioButton1;
    RadioButton radioButton2;
    RadioButton radioButton3;
    RadioButton radioButton4;
    RadioButton radioButton5;
    Button confirmButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_type);
        initView();
        initialize();

    }
    /**
     * initialView
     */
    private void initView(){

        radioGroup=(RadioGroup)findViewById(R.id.radioGroup1);
        radioButton1=(RadioButton)radioGroup.getChildAt(0);
        radioButton2=(RadioButton)radioGroup.getChildAt(1);
        radioButton3=(RadioButton)radioGroup.getChildAt(2);
        radioButton4=(RadioButton)radioGroup.getChildAt(3);
        radioButton5=(RadioButton)radioGroup.getChildAt(4);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = (RadioButton) findViewById(checkedId);
                final String choose=String.valueOf(radioButton.getText());
                Log.i(TAG, String.valueOf(radioButton.getText()));
                Handler h = new Handler(Looper.getMainLooper());
                h.post(new Runnable() {
                    public void run() {
                        Toast.makeText(ProductTypeActivity.this, "已选择"+choose,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        confirmButton=(Button)findViewById(R.id.buttonConfirm1);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RadioButton button=(RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                String choose=String.valueOf(button.getText());
                showSingleChoiceDialog(choose);
            }
        });
    }

    /**
     * set Text of radioButtons
     */
    private void initialize(){
        Intent intent=getIntent();
        String type1=intent.getStringExtra("type1");
        String type2=intent.getStringExtra("type2");
        String type3=intent.getStringExtra("type3");
        String type4=intent.getStringExtra("type4");
        String type5=intent.getStringExtra("type5");
        radioButton1.setText(type1);
        radioButton2.setText(type2);
        radioButton3.setText(type3);
        radioButton4.setText(type4);
        radioButton5.setText(type5);
    }
    /**
     * dialog:choose time of ad
     * @param type
     */
    private void showSingleChoiceDialog(final String type) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setTitle("选取视频广告时长");

        /**
         * 设置内容区域为单选列表项
         */
        final String[] items={"30s","45s","60s"};
        builder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "You clicked "+items[i], Toast.LENGTH_SHORT).show();
                getDialogOfTime(type,items[i]);
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }


    /**
     * dialog:get time of ad
     * @param type
     * @param time
     */
    public void getDialogOfTime(final String type,final String time){
        AlertDialog.Builder dialog=new AlertDialog.Builder(ProductTypeActivity.this);
        dialog.setTitle("已选择:"+time);
        dialog.setMessage("下一步？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                chooseStyle(type,time);
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * dialog:show style of ad
     * @param type
     * @param time
     */
    private void chooseStyle(final String type,final String time) {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(false);
        builder.setTitle("选取广告风格");

        /**
         * 设置内容区域为单选列表项
         */
        final String[] items={"fresh","vintage","hip hop"};
        builder.setSingleChoiceItems(items, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "You clicked "+items[i], Toast.LENGTH_SHORT).show();
                getDialogOfStyle(type,time,items[i]);
            }
        });

        builder.setCancelable(true);
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    /**
     * dialog:get style of ad
     * @param type
     * @param time
     * @param style
     */
    public void  getDialogOfStyle(final String type,final String time,final String style){
        AlertDialog.Builder dialog=new AlertDialog.Builder(ProductTypeActivity.this);
        dialog.setTitle("已选择:"+style);
        dialog.setMessage("下一步？");
        dialog.setCancelable(false);
        dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file=new File("/sdcard/","DateRecording.txt");
                try {
                    file.createNewFile(); // 创建文件
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                try{
                    FileOutputStream fileOutputStream=new FileOutputStream(file);
                    OutputStreamWriter outputStreamWriter=new OutputStreamWriter(fileOutputStream);
                    BufferedWriter bufferedWriter=new BufferedWriter(outputStreamWriter);
                    bufferedWriter.write("654321"+"\r\n");
                    bufferedWriter.write("light"+"\r\n");
                    bufferedWriter.write(time+"\r\n");
                    bufferedWriter.write(style+"\r\n");
                    bufferedWriter.close();
                    outputStreamWriter.close();
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                }catch (IOException e){
                    e.printStackTrace();
                }
                submitDataRecoding();
            }
        });
        dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    /**
     * submit DataRecoding
     */
    public void submitDataRecoding(){
        String url= OkHttpUtil.base_url+"uploadTxt";
        OkHttpUtil.doFile(url, "/sdcard/DateRecording.txt", "DateRecording.txt", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"DateRecoding上传失败！！！！！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String tempResponse =  response.body().string();
                Log.e(TAG,"啦啦啦啦啦啦：：：:"+tempResponse);
                if(response.isSuccessful()){
                    Log.e(TAG,"DateRecoding上传成功！！！！！");
                }
                try {
                    JSONObject jsonObject = new JSONObject(tempResponse);
                    String returnCode = jsonObject.getString("code");
                    Log.i(TAG, "坎坎坷坷扩扩" + returnCode);
                    if ("200".equals(returnCode)) {
                        Log.i(TAG, "进来了" + returnCode);
                        Intent intent=new Intent(ProductTypeActivity.this,ProgressActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

    }
}
