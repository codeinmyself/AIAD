package com.xmu.lxq.aiad.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xmu.lxq.aiad.Exception.ExceptionUtil;
import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.SudokuUtil.ActiveGrideView;
import com.xmu.lxq.aiad.SudokuUtil.DragBaseAdapter;
import com.xmu.lxq.aiad.util.OkHttpUtil;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by asus1 on 2017/12/18.
 */

public class SudokuActivity  extends AppCompatActivity {


    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SeekBar seekBar;
    private ImageButton start;
    private boolean isPlaying;

    private ActiveGrideView aGridview;
    public  static List<HashMap<String, String>> list;
    private DragBaseAdapter adapter;

    public static String[] img_text = { "u_1", "宫格1", "宫格2", "宫格3","u_2", "宫格4",
             "宫格5", "宫格6", "u_3"};

    static String default_img="/sdcard/1513955901.png";
    public static String[] imgs={"hh",default_img,default_img,default_img,"hh",default_img,default_img,default_img,"hh"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        makeActionOverflowMenuShown();
        setContentView(R.layout.activity_sudoku);
        initView();
    }

    private void makeActionOverflowMenuShown() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {

        }
    }
    /**
     * initView and set Listener
     */
    private void initView() {
        surfaceView=(SurfaceView)findViewById(R.id.surfaceView);
        start=(ImageButton)findViewById(R.id.video_start);
        seekBar=(SeekBar)findViewById(R.id.seekBar);
        player = new MediaPlayer();
        surfaceView.getHolder().setKeepScreenOn(true);

        aGridview=(ActiveGrideView) findViewById(R.id.gridview);
        aGridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View item, int arg2,
                                    long position) {

                Log.e(TAG,"arg2:"+arg2);
                Log.e(TAG, "bitmap:" + imgs[arg2]+"  "+"text:"+img_text[arg2]);

                if (imgs[arg2].equals(default_img)||doubleClick()||imgs[arg2].equals("hh")) {
                    DragBaseAdapter dba = (DragBaseAdapter) parent.getAdapter();
                    Map.Entry entry = dba.loopItem(dba.get(), (int) position);
                    Intent intent = new Intent(SudokuActivity.this, VideoActivity.class);
                    intent.putExtra("order", arg2 + "");
                    startActivityForResult(intent, 1);
                } else {
                    final String path = getFileNameNoEx(imgs[arg2])+".mp4";
                    Log.e(TAG,"path:"+path);
                    start.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            videoPlay(0,path);
                        }
                    });

                    surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
                        @Override
                        public void surfaceChanged(SurfaceHolder holder, int format, int width,
                                                   int height) {

                        }

                        @Override
                        public void surfaceCreated(SurfaceHolder holder) {
                           /* if (currentPosition > 0) {*/
                            // 创建SurfaceHolder的时候，如果存在上次播放的位置，则按照上次播放位置进行播放
                            // video_play(currentPosition,path);
                            /*    currentPosition = 0;
                            }*/
                        }

                        @Override
                        public void surfaceDestroyed(SurfaceHolder holder) {
                            // 销毁SurfaceHolder的时候记录当前的播放位置并停止播放
                            if (player != null && player.isPlaying()) {
                                /*currentPosition = player.getCurrentPosition();*/
                                player.stop();
                                player.release();
                                player=null;
                            }
                        }
                    });

                }
            }

        });
        initialAD();
        initialData();
        adapter = new DragBaseAdapter(this,list);
        aGridview.setAdapter(adapter);
    }

    /**
     * initial 6 ads(Template)
     */
    public void initialAD(){
        int count=0;
        for(int i=0;i<=8;i++){
            if(i==1||i==2||i==3|i==5||i==6||i==7){
                Log.e(TAG,"videosName[count]:"+ ProgressActivity.videosName[count]);
                Bitmap bitmap=getVideoThumbnail("/sdcard/"+ ProgressActivity.videosName[count]+".mp4");
                //Bitmap bitmap=getVideoThumbnail("/sdcard/"+ProgressActivity.videosName[count]+".3gp");

                saveBitmap(bitmap, ProgressActivity.videosName[count]+"");
                imgs[i]="/sdcard/"+ ProgressActivity.videosName[count]+".png";
                img_text[i]= ProgressActivity.videosName[count];
                count++;
            }
        }
    }

    /**
     * initial data
     */
    public void initialData(){
        if(list != null){
            if(list.size() > 0)list.clear();
        }else{
            //list = new ArrayList<HashMap<String,Integer>>();
            list = new ArrayList<HashMap<String,String>>();
        }
        for(int i=0;i<img_text.length;i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(img_text[i], imgs[i]);
            list.add(map);
        }
    }

    /**
     * uploadVideo by User
     */
    private void uploadVideo(){
        int count=1;
        String url = OkHttpUtil.base_url + "uploadVideo";
        for(int i=0;i<img_text.length;i++){
            if(img_text[i].equals("u_1")||img_text[i].equals("u_2")||img_text[i].equals("u_3")){
                OkHttpUtil.doFile(url, getFileNameNoEx(imgs[i])+".mp4"+"","u_"+(count++)+".mp4", new Callback() {
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
                        try {
                            JSONObject jsonObject = new JSONObject(tempResponse);
                            String returnCode = jsonObject.getString("code");
                            Log.i(TAG, "坎坎坷坷扩扩" + returnCode);
                            if ("200".equals(returnCode)) {
                                Log.i(TAG, "进来了" + returnCode);
                            }
                            Intent intent=new Intent(SudokuActivity.this,ResultActivity.class);
                            startActivity(intent);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
            }

        }
    }

    private long[] mHits = new long[2];

    /**
     * deal double click
     * @return
     */
    private boolean doubleClick() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();//获取手机开机时间
        if (mHits[mHits.length - 1] - mHits[0] < 500) {
            return true;
        }
        return false;
    }


    /**
     * video play
     * @param msec
     * @param path
     */
    protected void videoPlay(final int msec, final String path) {
            if(player==null){
                player = new MediaPlayer();
            }
            if(player.isPlaying()){
                player.stop();
                player.release();
                player=null;
                player=new MediaPlayer();
            }
            //设置音频流类型
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 设置播放的视频源
            try {
                player.setDataSource(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
            // 设置显示视频的SurfaceHolder
            player.setDisplay(surfaceView.getHolder());//这一步是关键，制定用于显示视频的SurfaceView对象（通过setDisplay（））
            player.prepareAsync();
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    player.start();

                    // 按照初始位置播放
                    player.seekTo(msec);
                    // 设置进度条的最大进度为视频流的最大播放时长
                    seekBar.setMax(player.getDuration());
                    // 开始线程，更新进度条的刻度
                    new Thread() {

                        @Override
                        public void run() {
                            try {
                                isPlaying = true;
                                while (isPlaying) {
                                    Log.e(TAG,"player:"+player);
                                    int current = player.getCurrentPosition();
                                    Log.e(TAG,"current:"+current);
                                    seekBar.setProgress(current);

                                    sleep(500);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    start.setEnabled(false);
                }
            });
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 在播放完毕被回调
                    start.setEnabled(true);
                    SudokuActivity.this.player.release();
                    SudokuActivity.this.player=null;
                }
            });
        }

    /**
     * mode of open file: a ,add img_text[]
     */
    public void submitRecord(){
            File file=new File("/sdcard/","DateRecording.txt");
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file,true);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(img_text[0]+" ");
                bufferedWriter.write(img_text[1]+" ");
                bufferedWriter.write(img_text[2]+" ");
                bufferedWriter.write(img_text[3]+" ");
                bufferedWriter.write(img_text[4]+" ");
                bufferedWriter.write(img_text[5]+" ");
                bufferedWriter.write(img_text[6]+" ");
                bufferedWriter.write(img_text[7]+" ");
                bufferedWriter.write(img_text[8]+"\r\n");
                bufferedWriter.close();
                outputStreamWriter.close();
            }catch (Exception e){
                e.printStackTrace();
            }
            String url= OkHttpUtil.base_url+"uploadTxt2";
            OkHttpUtil.doFile(url, "/sdcard/DateRecording.txt", "DateRecording.txt", new Callback() {
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
                    try {
                        JSONObject jsonObject = new JSONObject(tempResponse);
                        String returnCode = jsonObject.getString("code");
                        Log.i(TAG, "坎坎坷坷扩扩" + returnCode);
                        if ("200".equals(returnCode)) {
                            Log.i(TAG, "进来了" + returnCode);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
        }

    /**
     * reInitial img_text[] and images[] according to list(has changed)
     */
    public static void reInitial(){
        for(int i=0;i<img_text.length;i++){
            HashMap<String, String> map = new HashMap<String, String>();
            map=list.get(i);
            for(String key:map.keySet()){
                img_text[i]=key;
            }
            Log.e(TAG,"img_text[i]:"+img_text[i]);
            for(String value:map.values()){
                imgs[i]=value;
            }
            Log.e(TAG,"imgs[i]:"+imgs[i]);
        }
    }

    /**
     * deal activity result
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (data == null) {
                    return;
                }
                int order=Integer.parseInt((data.getStringExtra("order")).trim());
                String fileName=data.getStringExtra("fileName");
                String absolutePath=data.getStringExtra("absolutePath");
                Bitmap bitmap=getVideoThumbnail(absolutePath+"");
                Log.e(TAG,"fileName:"+fileName);
                Log.e(TAG,"绝对路径："+absolutePath);
                Log.e(TAG,"order:"+order+"");

                saveBitmap(bitmap,fileName);
                aGridview=(ActiveGrideView) findViewById(R.id.gridview);
                ImageView imageView=(ImageView)aGridview.getChildAt(order).findViewById(R.id.iv_item);
                imageView.setImageBitmap(bitmap);

                imgs[order]="/sdcard/"+fileName+".png";
                initialData();
                adapter = new DragBaseAdapter(this,list);
               adapter.notifyDataSetChanged();
                list=new ArrayList<>(adapter.get()) ;
                reInitial();
                aGridview.setAdapter(adapter);
               /* initdata();
                adapter = new DragBaseAdapter(this,list);
                aGridview.setAdapter(adapter);
                list=new ArrayList<>(adapter.get()) ;
                Log.e(TAG,"list:"+list);
                reInitial();*/
                break;
        }
    }

    /**
     * get thumbnail of video
     * @param filePath
     * @return
     */
    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;

        if (TextUtils.isEmpty(filePath)) {
            Log.e(TAG,"=======路径为null!");
            return null;
        }
        File file1 = new File(filePath);
        if (!file1.exists()) {
            Log.e(TAG,filePath+":文件不存在!");
            return null;
        }
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            Log.e(TAG,"filepath:"+filePath);
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime(10);
            Log.e(TAG,"bitmap:"+bitmap);
        }
        catch(IllegalArgumentException e) {
            e.printStackTrace();
        }
        catch (RuntimeException e) {
            e.printStackTrace();
        }
        finally {
            try {
                retriever.release();
            }
            catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    /**
     * saveBitmap
     */
    public void  saveBitmap(Bitmap bitmap,String fileName) { // 将在屏幕上绘制的图形保存到SD卡
        File file=new File("/sdcard/"+fileName+".png");
        if(!file.exists()){
            try {
                file.createNewFile();
            }
            catch(IOException e){
                Log.e(TAG,"文件创建失败"+ ExceptionUtil.getLineNumber(e));
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file); // 创建文件输出流（写文件）
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);// 将图片对象按PNG格式压缩（质量100%)，写入文件

            fos.flush(); // 刷新
            fos.close();// 关闭流
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * get currentTimeMills
     * @return
     */
    public String getTime(){
        long time=System.currentTimeMillis()/1000;//获取系统时间的10位的时间戳
        return String.valueOf(time);
    }

    /**
     * get fileName without ex
     * @param filename
     * @return
     */
     public static String getFileNameNoEx(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length()))) {
                return filename.substring(0, dot);
            }
        }
        return filename;
    }

    /**
     * onCreateOptionsMenu
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * onOptionsItemSelected
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            Log.e(TAG,"here 2017");
            for(int i=0;i<img_text.length;i++){
                if(imgs[i].equals(default_img)||imgs[i].equals("hh")){
                    Handler h = new Handler(Looper.getMainLooper());
                    h.post(new Runnable() {
                        public void run() {
                            Toast.makeText(SudokuActivity.this, "九宫格视频尚未玩成！",Toast.LENGTH_SHORT).show();
                        }
                    });
                    return true;
                }
            }
            submitRecord();

            uploadVideo();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
