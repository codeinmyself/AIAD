package com.xmu.lxq.aiad.util;


import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class OkHttpUtil{

    //public  static String base_url="http://10.0.2.2:8080/";
    //public  static String base_url="http://117.28.251.163:8080/";
    public  static String base_url="http://192.168.0.29:81/";


    private static OkHttpClient singleton;
    //非常有必要，要不此类还是可以被new，但是无法避免反射
    private OkHttpUtil(){

    }

    /**
     * single instance
     * @return
     */
    private static OkHttpClient getInstance() {
        if (singleton == null)
        {
            synchronized (OkHttpUtil.class)
            {
                if (singleton == null)
                {
                    //singleton = new OkHttpClient();
                    singleton = new OkHttpClient.Builder()
                            .connectTimeout(20, TimeUnit.SECONDS)
                            .readTimeout(20, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return singleton;
    }

    /**
     * Get请求
     * @param address
     * @param callback
     */
    public static void doGet(String address,okhttp3.Callback callback){
        Request request=new Request.Builder()
                .url(address)
                .build();
        getInstance().newCall(request).enqueue(callback);
    }

    public static String doGetSyn(String address){
        Request request=new Request.Builder()
                .url(address)
                .build();
        Call call = getInstance().newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Post请求发送键值对数据
     * @param address
     * @param mapParams
     * @param callback
     */
    public static void doPost(String address, Map<String,String> mapParams,okhttp3.Callback callback){

        FormBody.Builder builder=new FormBody.Builder();
        for(String key:mapParams.keySet()){
            builder.add(key,mapParams.get(key));
        }
        Request request=new Request.Builder()
                .url(address)
                .post(builder.build())
                .build();
        Call call=getInstance().newCall(request);
        call.enqueue(callback);

    }

    /**
     * Post请求发送JSON数据
     * @param url
     * @param jsonParams
     * @param callback
     */
    public static void doPost(String url, String jsonParams, Callback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , jsonParams);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }

    public static void doPost(String url, Object object, Callback callback) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8")
                , new Gson().toJson(object));
        Log.i(TAG,"传入的json串："+object.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }


    /**
     * 上传文件--异步
     * @param url
     * @param pathName
     * @param fileName
     * @param callback
     */
    public static void doFile(String url, String pathName, String fileName, Callback callback) {
        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        //创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MEDIA_TYPE.type(), fileName,
                        RequestBody.create(MEDIA_TYPE, new File(pathName)));
        //发出请求参数
        Request request = new Request.Builder()
               // .header("Authorization", "Client-ID " + "9199fdef135c122")
                .url(url)
                .post(builder.build())
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(callback);
    }


    /**
     * 上传文件--同步
     * @param url
     * @param pathName
     * @param fileName
     */
    public static String doFileSynchronize(String url, String pathName, String fileName) {
        //判断文件类型
        MediaType MEDIA_TYPE = MediaType.parse(judgeType(pathName));
        //创建文件参数
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(MEDIA_TYPE.type(), fileName,
                        RequestBody.create(MEDIA_TYPE, new File(pathName)));
        //发出请求参数
        Request request = new Request.Builder()
                // .header("Authorization", "Client-ID " + "9199fdef135c122")
                .url(url)
                .post(builder.build())
                .build();
        Call call = getInstance().newCall(request);
        try {
            Response response = call.execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



    /**
     * 根据文件路径判断MediaType
     * @param path
     * @return
     */
    private static String judgeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    /*public static boolean downFileSynchronized(String url, final String fileDir, final String fileName){
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        InputStream is = null;
        byte[] buf = new byte[2048];
        int len = 0;
        FileOutputStream fos = null;
        Response response;
        try {
            response= call.execute();
            is = response.body().byteStream();
            while ((len = is.read(buf)) != -1) {
                fos.write(buf, 0, len);
            }
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try{
                if(is!=null)
                    is.close();
                if(fos!=null)
                    fos.close();
            }catch (IOException e){

            }
        }
        return true;
    }*/
    /**
     * 下载文件
     * @param url
     * @param fileDir
     * @param fileName
     */
    public static void downFile(String url, final String fileDir, final String fileName,final OnDownloadListener listener) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        Call call = getInstance().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                // 下载失败
                listener.onDownloadFailed();
                Log.e(TAG,"下载失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File(fileDir, fileName);
                    fos = new FileOutputStream(file);
                    //---增加的代码---
                    //计算进度
                    long totalSize = response.body().contentLength();
                    long sum = 0;
                    while ((len = is.read(buf)) != -1) {
                        sum += len;
                        //progress就是进度值
                        int progress = (int) (sum * 1.0f/totalSize * 100);
                        listener.onDownloading(progress);
                        fos.write(buf, 0, len);
                    }
                    listener.onDownloadSuccess();
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (is != null) is.close();
                    if (fos != null) fos.close();
                }
            }
        });
    }

    public interface OnDownloadListener {
        /**
         * 下载成功
         */
        void onDownloadSuccess();

        /**
         * @param progress
         * 下载进度
         */
        void onDownloading(int progress);

        /**
         * 下载失败
         */
        void onDownloadFailed();
    }
}