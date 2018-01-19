package com.xmu.lxq.aiad.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.util.OkHttpUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by asus1 on 2017/12/27.
 */

public class ProgressActivity extends Activity{

    public static String[] videosName=new String[6];
    static int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_progress);
        getVideosName();

        initialView();
    }

    /**
     * mode of open file:a ,to add videosName[]
     */
    public void recordVideosName(){
        File file=new File("/sdcard/","DateRecording.txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file,true);//追加方式打开
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
            bufferedWriter.write(videosName[0]+" "+videosName[1]+" "+videosName[2]+" "+videosName[3]+" "+videosName[4]+" "+videosName[5]+"\r\n");
            bufferedWriter.write("u_1 u_2 u_3"+"\r\n");
            bufferedWriter.close();
            outputStreamWriter.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * get : videosName[]
     */
    public void getVideosName(){
        String url= OkHttpUtil.base_url+"getVideosName";
        OkHttpUtil.doGet(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG,"getVideosName失败!");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String tempResponse =  response.body().string();
                Log.e(TAG,"tempResponse:"+tempResponse);
                if(response.isSuccessful()){
                    Log.e(TAG,"相应成功！！！！！");
                }
                try {
                    JSONObject jsonObject = new JSONObject(tempResponse);
                    String returnCode = jsonObject.getString("code");
                    Log.i(TAG, "坎坎坷坷扩扩" + returnCode);
                    if ("200".equals(returnCode)) {
                        Log.i(TAG, "进来了" + returnCode);
                        jsonObject=jsonObject.getJSONObject("detail");

                        videosName[0]=jsonObject.getString("0");
                        Log.e(TAG,"videosName[0]:"+videosName[0]);
                        videosName[1]=jsonObject.getString("1");
                        Log.e(TAG,"videosName[1]:"+videosName[1]);

                        videosName[2]=jsonObject.getString("2");
                        videosName[3]=jsonObject.getString("3");
                        videosName[4]=jsonObject.getString("4");
                        videosName[5]=jsonObject.getString("5");
                        //Thread.sleep(5000);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    getTemplateVideo();
                    recordVideosName();
                    /*try{
                        while(!isExists())
                            Thread.sleep(15000);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
*/
                }
            }
        });
    }

    static int num=0;

    /**
     * get 'Template' videos
     */
    public void getTemplateVideo(){

        try{
            for(int i=0;i<=5;i++){
                String url = OkHttpUtil.base_url + "downloadVideos/"+i;
                Log.e(TAG,"url:"+url);
                Log.e(TAG,"/sdcard/"+videosName[i]+".mp4");
                while(videosName[i]==null){
                    Log.e(TAG,"videosName还是null");
                }
                Log.e(TAG,"videosName不是null了==");

                OkHttpUtil.downFile(url, "/sdcard/", /*videosName[i]*/videosName[i] + ".mp4", new OkHttpUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess() {
                        count++;
                        Log.e(TAG,videosName[num++]+"下载视频成功！");

                    }

                    @Override
                    public void onDownloading(final int progress) {

                       // Log.e(TAG,"正在下载"+progress+"%");
                    }

                    @Override
                    public void onDownloadFailed() {
                        Log.e(TAG,"下载失败！");
                    }
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {

        }

    }

    /**
     * isExists about videos in sdcard:since the thread in async
     * @return
     */
    public boolean isExists(){
        for(int i=0;i<=5;i++){
            //File file=new File("/sdcard/"+videosName[i]+".mp4");
            File file=new File("/sdcard/"+videosName[i]+".3gp");
            if(!file.exists()){
                Log.e(TAG,videosName[i]+"不存在");
                return false;
            }
            Log.e(TAG,videosName[i]+"存在");
        }
        return true;
    }

    /**
     * initial view if isExists()==true
     */
    private void initialView(){
        // 进度条还有二级进度条的那种形式，这里就不演示了
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条

        dialog.setTitle("正在下载");
        dialog.setMax(100);
        dialog.setMessage("请等待");
        dialog.show();


        /*ThreadA t1 = new ThreadA("t1");

        synchronized (t1){
            t1.start();
            while(!isExists()){
                try{
                    t1.wait();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }*/


       new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Thread.sleep(25000);
                    dialog.cancel();
                    Intent intent=new Intent(ProgressActivity.this,SudokuActivity.class);
                    startActivity(intent);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
