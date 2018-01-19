package com.xmu.lxq.aiad.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xmu.lxq.aiad.R;
import com.xmu.lxq.aiad.service.AppContext;
import com.xmu.lxq.aiad.util.OkHttpUtil;
import com.xmu.lxq.aiad.util.SharePreferenceUtil;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private ImageView picture;
    private TextView textView_account;
    private Button button_to_login;
    private ImageButton icon_image;
    private Uri imageUri;

    // 裁剪后的文件名称
    public static final String IMAGE_FILE_NAME_TEMP = "faceImage_temp.jpg";
    private File cropFile = new File(Environment.getExternalStorageDirectory(), IMAGE_FILE_NAME_TEMP);
    private Uri imageCropUri = Uri.fromFile(cropFile);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
       /* FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View view=navigationView.getHeaderView(0);
        icon_image=(ImageButton)view.findViewById(R.id.icon_image) ;
        //SharePreferenceUtil sharePreferenceUtil=new SharePreferenceUtil(MainActivity.this);
        AppContext appContext=new AppContext();

        if(appContext.isLogin){
            String path= Environment.getExternalStorageDirectory()+"/aiad/icon_bitmap/"+ "myicon.jpg";
            try
            {
                File file = new File(path);
                if(file.exists())
                {
                    icon_image.setImageBitmap(getDiskBitmap(path));
                }else{
                    icon_image.setImageResource(R.drawable.ic_launcher_background);
                }
            } catch (Exception e)
            {
                // TODO: handle exception
            }

        }

        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        if (bundle!=null){
            String telephone=bundle.getString("telephone");
            if(telephone!=null){
                Log.e(TAG,"传回来了了！");
                textView_account=(TextView)view.findViewById(R.id.account_text);
                textView_account.setText(telephone);
                textView_account.setVisibility(View.VISIBLE);


                button_to_login=(Button)view.findViewById(R.id.button1);
                button_to_login.setVisibility(View.INVISIBLE);
            }
        }

        Button takePhoto=(Button)findViewById(R.id.take_photo);
        Button chooseFromAlbum=(Button)findViewById(R.id.choose_from_album);
        picture=(ImageView)findViewById(R.id.picture);
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"进入拍照！！！！！");
                /*File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(MainActivity.this, "com.aiad.camera.fileprovider", outputImage);

                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                //启动相机
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivityForResult(intent,TAKE_PICTURE);*/

                Intent openCameraIntent = new Intent(
                        MediaStore.ACTION_IMAGE_CAPTURE);
                imageUri = Uri.fromFile(new File(Environment
                        .getExternalStorageDirectory(), "image.jpg"));
                // 指定照片保存路径（SD卡），image.jpg为一个临时文件，每次拍照后这个图片都会被替换
                openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(openCameraIntent, TAKE_PICTURE);
            }
        });
        chooseFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG,"进入相册！！！！！");
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }else{
                    openAlbum();
                }
            }
        });

        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskReads().detectDiskWrites().detectNetwork()
                .penaltyLog().build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
                .penaltyLog().penaltyDeath().build());
    }

    @Override
    public void onBackPressed() {
        /*DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
        moveTaskToBack(false);
    }

    /**
     * 从本地获取图片
     * @param pathString 文件路径
     * @return 图片
     */
    public Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e)
        {
            // TODO: handle exception
        }
        return bitmap;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }*/

  /*  @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.upload) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    /**
     * onNavigationItemSelected
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.personal_info) {

            AppContext appContext=new AppContext();
            if(appContext.isLogin){
                Intent intent=new Intent(MainActivity.this,PersonalInfo.class);
                startActivity(intent);
            }else{
                Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
            }

        } else if (id == R.id.nav_gallery) {

            AppContext appContext=new AppContext();
            if(appContext.isLogin){

            }else{
                Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_slideshow) {

            AppContext appContext=new AppContext();
            if(appContext.isLogin){

            }else{
                Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_manage) {
            AppContext appContext=new AppContext();
            if(appContext.isLogin){

            }else{
                Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.nav_share) {
            AppContext appContext=new AppContext();
            if(appContext.isLogin){

            }else{
                Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.logout) {
            AppContext appContext=new AppContext();
            appContext.setIsLogin(true);
            //SharePreferenceUtil sharePreferenceUtil=new SharePreferenceUtil(MainActivity.this);
            if(appContext.isLogin){
                Log.e(TAG,"要退出账号了！");
                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                View view=navigationView.getHeaderView(0);
                textView_account=(TextView)view.findViewById(R.id.account_text);
                textView_account.setVisibility(View.INVISIBLE);

                button_to_login=(Button)view.findViewById(R.id.button1);
                button_to_login.setText("点击登录");
                button_to_login.setVisibility(View.VISIBLE);
               // sharePreferenceUtil.setStateLogout();
                appContext.setIsLogin(false);
            }else{
                Toast.makeText(MainActivity.this, "并未登录！", Toast.LENGTH_SHORT).show();
            }


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * onResume
     */
    @Override
    public void onResume(){
        super.onResume();
        SharePreferenceUtil sharePreferenceUtil=new SharePreferenceUtil(MainActivity.this);
        if(sharePreferenceUtil.getState()){
            String path= Environment.getExternalStorageDirectory()+"/aiad/icon_bitmap/"+ "myicon.jpg";
            try
            {
                File file = new File(path);
                if(file.exists())
                {
                    icon_image.setImageBitmap(getDiskBitmap(path));
                }
            } catch (Exception e)
            {
                // TODO: handle exception
            }

        }

    }

    /**
     * button1_click
     * @param view
     */
    public void button1_click(View view){
        Intent intent=new Intent();
        intent.setClass(MainActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    /**
     * icon_image_click
     * @param view
     */
    public void icon_image_click(View view){

        //SharePreferenceUtil sharePreferenceUtil=new SharePreferenceUtil(MainActivity.this);
        AppContext appContext=new AppContext();

        Log.e(TAG,appContext.isLogin+"");
        if(appContext.isLogin){
            Intent intent=new Intent(MainActivity.this,IconActivity.class);
            startActivity(intent);
        }else{
            Toast.makeText(MainActivity.this, "请先登录！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * openAlbum
     */
    private void openAlbum(){
        Intent intent=new Intent( Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent,CHOOSE_PICTURE);
    }

    /**
     * onRequestPermissionsResult
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    openAlbum();
                }else{
                    Toast.makeText(this,"没有权限！",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    /**
     * 裁剪图片方法实现
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "The uri is not exist.");
        }
        imageUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 100);
        intent.putExtra("aspectY", 100);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 450);
        intent.putExtra("outputY", 450);
        //intent.putExtra("return-data", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageCropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }

    /**
     * onActivityResult
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            switch (requestCode) {
                case CROP_SMALL_PICTURE:
                    setImageToView(data);
                    showNextDialog();
                    break;
                case TAKE_PICTURE:
                    startPhotoZoom(imageUri);
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData());
                       /* if (Build.VERSION.SDK_INT >= 19) {
                            handleImageOnKitKat(data);
                            showNextDialog();
                        } else {
                            handleImageBeforeKitKat(data);
                        }*/
                        /*setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    showNextDialog();*/
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 保存裁剪之后的图片数据
     * @param data
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageCropUri));
            Log.d("size",bitmap.getByteCount()+"");
            picture.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
       /* if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            //photo = ImageUtil.toRoundBitmap(photo); // 这个时候的图片已经被处理成圆形的了
            picture.setImageBitmap(photo);
            saveBitmapAsPNG(photo);

        }*/
    }

    /**
     * saveBitmapAsPNG
     * @param bitmap
     */
    private void saveBitmapAsPNG(Bitmap bitmap){
        File file=new File("/sdcard/product.jpg");
        try {
            BufferedOutputStream bout=new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bout);
            bout.flush();
            bout.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * showNextDialog
     */
    public void showNextDialog(){

        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("上传图片");
        builder.setMessage("下一步？");
        builder.setCancelable(false);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                /*picture.setDrawingCacheEnabled(true);
                Bitmap bitmap=picture.getDrawingCache();
                picture.setDrawingCacheEnabled(false);*/
                uploadPic();

            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        //创建对话框
        AlertDialog dialog = builder.create();
        Window dialogWindow=dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity( Gravity.CENTER_HORIZONTAL|Gravity.TOP);

        lp.y = 10; // 新位置Y坐标
        lp.width = 20; // 宽度
        lp.height = 20; // 高度
        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);

        dialog.show();
    }

    /**
     * uploadPic
     */
    private void uploadPic(){
        File img=new File("/sdcard/product.jpg");
        String url = OkHttpUtil.base_url + "uploadProductImage";
       /* OkHttpUtil.doFile(url, "/sdcard/product.jpg", img.getName(), new Callback() {
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

                        jsonObject=jsonObject.getJSONObject("detail");
                        String type[]=new String[5];
                        type[0]=jsonObject.getString("type1");
                        type[1]=jsonObject.getString("type2");
                        type[2]=jsonObject.getString("type3");
                        type[3]=jsonObject.getString("type4");
                        type[4]=jsonObject.getString("type5");
                        Log.e(TAG,"type[0]:"+type[0]);
                        Intent intent = new Intent(MainActivity.this, ProductTypeActivity.class);
                        intent.putExtra("type1", type[0]);
                        intent.putExtra("type2", type[1]);
                        intent.putExtra("type3", type[2]);
                        intent.putExtra("type4", type[3]);
                        intent.putExtra("type5", type[4]);
                        startActivity(intent);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });*/
       String tempResponse= OkHttpUtil.doFileSynchronize(url, "/sdcard/product.jpg", img.getName()) ;
        Log.e(TAG,"啦啦啦啦啦啦：：：:"+tempResponse);

        try {
            JSONObject jsonObject = new JSONObject(tempResponse);
            String returnCode = jsonObject.getString("code");
            Log.i(TAG, "坎坎坷坷扩扩" + returnCode);
            if ("200".equals(returnCode)) {
                Log.i(TAG, "进来了" + returnCode);

                jsonObject=jsonObject.getJSONObject("detail");
                String type[]=new String[5];
                type[0]=jsonObject.getString("type1");
                type[1]=jsonObject.getString("type2");
                type[2]=jsonObject.getString("type3");
                type[3]=jsonObject.getString("type4");
                type[4]=jsonObject.getString("type5");
                Log.e(TAG,"type[0]:"+type[0]);
                Intent intent = new Intent(MainActivity.this, ProductTypeActivity.class);
                intent.putExtra("type1", type[0]);
                intent.putExtra("type2", type[1]);
                intent.putExtra("type3", type[2]);
                intent.putExtra("type4", type[3]);
                intent.putExtra("type5", type[4]);
                startActivity(intent);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
   /* @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            //如果是document类型的uri，则通过document id 处理
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
                String id = docId.split(":")[1];//解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" +id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath = getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            //如果是content类型的uri则使用普通的方式处理
            imagePath = getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            //如果是file类型的URI则直接获取图片路径即可
            imagePath = uri.getPath();
        }
        displayImage(imagePath);//根据图片路径显示图片
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri,null);
        displayImage(imagePath);
    }*/
   /* private String getImagePath(Uri uri, String selection) {
        String path = null;
        //通过URI和selection来获取真是的图片路径
        Cursor cursor = getContentResolver().query(uri,null,selection,null,null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }*/
    /**
     * 在这里使用的是bitmap
     * @param imagePath
     */
    /*private void displayImage(String imagePath) {
        if (imagePath != null){
            //将照片显示出来
            Bitmap bitmap=BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);
        }else{
            Toast.makeText(getApplicationContext(),"failed to get image!",Toast.LENGTH_SHORT).show();
        }
    }*/

}
