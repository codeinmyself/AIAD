package com.xmu.lxq.aiad.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.util.OkHttpUtil;

import org.json.JSONObject;

import static android.content.ContentValues.TAG;

/**
 * Created by asus1 on 2017/12/26.
 */

public class ResultActivity extends Activity {

    private SurfaceView surfaceView;
    private MediaPlayer player;
    private SeekBar seekBar;
    private ImageButton start;
    private boolean isPlaying;

    static int flag = 0;
    ProgressDialog dialog=null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_result);
        initView();
    }

    public boolean isFinished() {
        String url = OkHttpUtil.base_url + "isFinished";
        Log.e(TAG, "url:" + url);
        String response = OkHttpUtil.doGetSyn(url);

        Log.e(TAG, "啦啦啦啦啦啦：：：:" + response);
        try {
            JSONObject jsonObject = new JSONObject(response);
            String returnCode = jsonObject.getString("code");
            Log.i(TAG, "坎坎坷坷扩扩" + returnCode);
            if ("200".equals(returnCode)) {
                Log.i(TAG, "进来了" + returnCode);
                return true;
            }
        } catch (Exception e) {

        }
        return false;
    }

    /**
     * downloadVideo
     */
    private void downloadVideo() {

        String url = OkHttpUtil.base_url + "downloadVideo";
        Log.e(TAG, "url:" + url);
        OkHttpUtil.downFile(url, "/sdcard/", "merge1.mp4", new OkHttpUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess() {
                Log.e(TAG, "下载视频成功！");
                flag = 1;
                dialog.cancel();
            }

            @Override
            public void onDownloading(int progress) {
                Log.e(TAG, "正在下载" + progress + "%");
            }

            @Override
            public void onDownloadFailed() {
                Log.e(TAG, "下载失败！");
            }
        });
    }

    /**
     * initView
     */
    private void initView() {

        surfaceView = (SurfaceView) findViewById(R.id.surfaceView1);
        start = (ImageButton) findViewById(R.id.video_start1);
        seekBar = (SeekBar) findViewById(R.id.seekBar1);
        player = new MediaPlayer();
        surfaceView.getHolder().setKeepScreenOn(true);

        dialog = new ProgressDialog(this);

        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置水平进度条
        dialog.setCancelable(true);// 设置是否可以通过点击Back键取消
        dialog.setCanceledOnTouchOutside(false);// 设置在点击Dialog外是否取消Dialog进度条

        dialog.setTitle("正在下载");
        dialog.setMax(100);
        dialog.setMessage("请等待");
        dialog.show();


        final String path = "/sdcard/merge1.mp4";

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoPlay(0, path);
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
                    player = null;
                }
            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isFinished()) {
                    try {
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e(TAG, "wait");
                }
            }
        }).start();
        downloadVideo();
    }

    /**
     * play video
     *
     * @param msec
     * @param path
     */
    protected void videoPlay(final int msec, final String path) {
        if (player == null) {
            player = new MediaPlayer();
        }
        if (player.isPlaying()) {
            player.stop();
            player.release();
            player = null;
            player = new MediaPlayer();
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
                                Log.e(TAG, "player:" + player);
                                int current = player.getCurrentPosition();
                                Log.e(TAG, "current:" + current);
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
                ResultActivity.this.player.release();
                ResultActivity.this.player = null;
            }
        });
    }



    class DownloadTask extends AsyncTask<Void,Integer,Boolean> {

        protected ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params){
            try{
                while(true){
                    int downloadPercent = 1;//doDownload();//假装有`方法。
                    publishProgress(downloadPercent);
                    if(downloadPercent >= 100){
                        break;
                    }
                }
            }catch(Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            //更新下载进度
            progressDialog.setMessage("Download" + values[0] + "%");
        }

        @Override
        protected void onPostExecute(Boolean result){
            progressDialog.dismiss();
            if(result){
                Toast.makeText(ResultActivity.this,"Download succeeded",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(ResultActivity.this,"Download failed",Toast.LENGTH_SHORT).show();
            }
        }
    }
}
