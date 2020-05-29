package ysm.dc.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.load.resource.bitmap.BitmapEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static ysm.dc.myapplication.MainActivity.server;

public class PhotoActivity  extends AppCompatActivity implements View.OnClickListener{
    private static final int REQUEST_CODE_CAMERA=1001;
    private static final int REQUEST_CAMERA=2001;

    private static final int REQUEST_CODE_PHOTOSHOP=1002;
    private static final int REQUEST_PHOTOSHOP=2002;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private Button TakePhoto;
    private Button SelectPhoto;
    private Button UploadPhoto;
    private TextView Path;
    private TextView Uri;

    private ImageView Pic;
    private Context context = PhotoActivity.this;
    private Bitmap bitmap ;
    private Uri mUri;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo);
        TakePhoto =  findViewById(R.id.TakePhoto);
        SelectPhoto = findViewById(R.id.SelectPhoto);
        UploadPhoto= findViewById(R.id.UploadPhoto);
        Path =  findViewById(R.id.TPath);
        Uri = findViewById(R.id.TUri);
        Pic =  findViewById(R.id.Pic);
        TakePhoto.setOnClickListener( this );
        SelectPhoto.setOnClickListener( this);
        UploadPhoto.setOnClickListener( this );
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.TakePhoto:
                if(ContextCompat.checkSelfPermission( PhotoActivity.this, Manifest.permission.CAMERA )!=
                        PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions( PhotoActivity.this,
                            new String[]{
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.CAMERA},REQUEST_CAMERA );
                }else{
                    TPhoto();
                }
                break;

            case R.id.SelectPhoto:
                if(ContextCompat.checkSelfPermission( PhotoActivity.this, Manifest.permission.CAMERA )!=
                        PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(PhotoActivity.this,new String[]{
                             Manifest.permission.WRITE_EXTERNAL_STORAGE,
                             Manifest.permission.READ_EXTERNAL_STORAGE,
                             Manifest.permission.CAMERA}, REQUEST_PHOTOSHOP);
                }else{
                    SPhotos();
                }
                break;

            case R.id.UploadPhoto:
                StudengUPhoto();
                //verifyStoragePermissions(this);
                Log.d( "PhotoActivity", "121111111111111111111111" );
                StudengUPhoto();
                break;

            default:
                break;
        }
    }

    public void verifyStoragePermissions(Activity activity) {
        try {
            Log.d( "PhotoActivity", "检测是否有写的权限..." );
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                Log.d( "PhotoActivity", "没有写的权限，去申请写的权限..." );
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //回调
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CAMERA && resultCode == RESULT_OK) {
            Bitmap bm = null;
            try {
                bm = getBitmapFormUri(mUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Pic.setImageBitmap(bm);
            System.out.printf( "成功" );
            //加载到页面上
        }
        else if(requestCode == REQUEST_CODE_PHOTOSHOP && resultCode == RESULT_OK) {
            if (data!=null) {
                Bitmap bm = null;
                Bundle bundle = data.getExtras();
                // 获取相机返回的数据，并转换为Bitmap图片格式，这是缩略图
                bm = (Bitmap) bundle.get("data");

                mUri = data.getData();//这里并不是绝对路径，需要根据Uri获取其绝对路径再上传
                try {
                    bm = getBitmapFormUri(mUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Pic.setImageBitmap(bm);
            }
            System.out.printf( "成功！" );
        }else {
            System.out.printf( "失败" );
        }
    }


    //权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult( requestCode, permissions, grantResults );
        switch (requestCode){
            case REQUEST_CAMERA :
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    TPhoto();
                }else{
                    Toast.makeText( this,"你拒绝了权限",Toast.LENGTH_SHORT ).show();
                }
            break;

            case REQUEST_PHOTOSHOP:
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    SPhotos();
                }else{
                    Toast.makeText( this,"你拒绝了权限",Toast.LENGTH_SHORT ).show();
                }
                break;
            default:
        }

    }

    private void TPhoto() {
        // 步骤一：创建存储照片的文件
        String path = getFilesDir() + File.separator + "images" + File.separator;
        File file = new File(path, "test.jpg");
        if(!file.getParentFile().exists())
            file.getParentFile().mkdirs();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //步骤二：Android 7.0及以上获取文件 Uri
            //com.rk.myfeaturesapp是自己App的包名fileprovider是死值
            mUri = FileProvider.getUriForFile(PhotoActivity.this, "com.rk.myfeaturesapp.fileprovider", file);
        } else {
            //步骤三：获取文件Uri
            mUri = android.net.Uri.fromFile(file);
        }
        //步骤四：调取系统拍照
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        startActivityForResult(intent,REQUEST_CODE_CAMERA);

    }

    private void SPhotos() {
        Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PHOTOSHOP);
    }
    //单纯上传照片用
    private  void StudengUPhoto( ){
        /**
         * 需求:
         * 1、指定图片在服务器的保存地址 pic_server_path
         * 2、图片的类型（学生个人 or 班级照片）pic_tors
         * 3、图片命名规范--单独指定字段 pic_name
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                UriPathUtils uriPathUtils = new UriPathUtils();
                imagePath= uriPathUtils.getRealPathFromUri(context,mUri);

                File file = null;

                try {
                    file  = new File( imagePath );
                    Log.d( "PhotoActivity", file.getName().toString() );
                }catch (Exception e){
                    Log.d( "PhotoActivity","111" +e.getMessage() );
                }



                RequestBody image = RequestBody.create( MediaType.parse("image/jpg"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType( MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), image)
                        .addFormDataPart( "studentId","123" )
                        .addFormDataPart( "file_name_InSFolder","1.jpg" )
                        //.addFormDataPart( "file_save_path_InServer",file_save_path_InServer )
                        //.addFormDataPart( "file_name_InSFolder",file_name_InSFolder )
                        .build();
                Log.d( "PhotoActivity", "111111111111111111" );

                Request request = new Request.Builder()
                        .url(server+"savePictures/")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //...
                        Log.d( "PhotoActivity","222!!!!!!!!!!!!!!!!!!失败222222" );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String result = response.body().string();
                            try{
                                JSONObject jsonObject= new JSONObject( result );
                                int res =jsonObject.getInt( "RESULT" );
                                Log.d( "PhotoActivity", "000"+result );
                                if(res==1){
                                    //上传成功
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText( PhotoActivity.this,"上传成功",Toast.LENGTH_SHORT ).show();

                                          /*  String url = "http://47.115.6.199/data/wwwroot/IFace/IFace_res/1.jpg";
                                            String updateTime = String.valueOf(System.currentTimeMillis());
                                            Glide.with(PhotoActivity.this).load(url)
                                                    .signature(new StringSignature(updateTime))
                                                    .into(Pic);*/

                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText( PhotoActivity.this,"上传失败",Toast.LENGTH_SHORT ).show();
                                        }
                                    });
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.d( "PhotoActivity", "000"+ e.getMessage() );
                            }
                        }
                    }
                });

            }
        }).start();

    }
   /* private void UPhoto() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                UriPathUtils uriPathUtils = new UriPathUtils();
                imagePath= uriPathUtils.getRealPathFromUri(context,mUri);

                //String imagePath= Environment.getExternalStorageDirectory()+"/DCIM/Camera/123.jpg";
                System.out.printf(imagePath );

                File file = new File( imagePath );

                System.out.printf(file.getName().toString());
                RequestBody image = RequestBody.create( MediaType.parse("image/jpg"), file);
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", imagePath, image)
                        //.addFormDataPart( "postId","3374" )
                        .build();

                System.out.printf( "111111111111111111" );
                Request request = new Request.Builder()
                        .url("http://10.34.15.176:8000/savePictures/")
                        .post(requestBody)
                        .build();
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //...
                        System.out.printf( "!!!!!!!!!!!!!!!!!!!失败" );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String result = response.body().string();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        }
                    }
                });

            }
        }).start();
    }*/

    /*//单纯上传照片用
    private  void StudengUPhoto( ){
        *//**
         * 需求:
         * 1、指定图片在服务器的保存地址 pic_server_path
         * 2、图片的类型（学生个人 or 班级照片）pic_tors
         * 3、图片命名规范--单独指定字段 pic_name
         *
         *//*
        Log.d( "PhotoActivity", "启动" );

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();
                UriPathUtils uriPathUtils = new UriPathUtils();
                imagePath= uriPathUtils.getRealPathFromUri(context,mUri);
                Log.d( "PhotoActivity", imagePath );
                File file = null;

                try {
                    file  = new File( imagePath );
                    Log.d( "PhotoActivity", file.getName().toString() );
                }catch (Exception e){
                    Log.d( "PhotoActivity","111" +e.getMessage() );
                }

                RequestBody image = RequestBody.create( MediaType.parse("image/jpg"), file);

                Log.d( "PhotoActivity image", image.toString());
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType( MultipartBody.FORM)
                        .addFormDataPart("file", file.getName(), image)
                        //.addFormDataPart( "file_name_InSFolder",file_name_InSFolder )
                        .build();
                Request request = new Request.Builder()
                        .url("http://10.34.45.105:8000/demotest/")
                        .post(requestBody)
                        .build();
                Log.d( "PhotoActivity","2222222222222222222222" );
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        //...
                        System.out.printf( "!!!!!!!!!!!!!!!!!!!失败" );
                        Log.d( "PhotoActivity","!!!!!!!!!!!!!!!!!!!失败" );
                        Log.d( "PhotoActivity",e.getMessage() );
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){
                            final String result = response.body().string();
                            Log.d( "PhotoActivity","---"+result );
                            try{
                                JSONObject jsonObject= new JSONObject( result );
                                int res =jsonObject.getInt( "RESULT" );
                                if(res==1){
                                    //上传成功
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText( PhotoActivity.this,"上传成功",Toast.LENGTH_SHORT ).show();
                                        }
                                    });
                                }else{
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText( PhotoActivity.this,"上传失败",Toast.LENGTH_SHORT ).show();
                                        }
                                    });
                                }
                            }catch (Exception e){
                                e.printStackTrace();
                                Log.d( "PhotoActivity", e.getMessage() );
                            }

                        }
                    }
                });

            }
        }).start();

    }*/

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();
                    //创建Request
                    Request request = new Request.Builder()
                            .url("https://baidu.com")//访问连接
                            .get()
                            .build();
                    //创建Call对象
                    Call call = client.newCall(request);
                    //通过execute()方法获得请求响应的Response对象
                    final Response response = call.execute();
                    if (response.isSuccessful()) {
                        //处理网络请求的响应，处理UI需要在UI线程中处理
                        //...
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.d( "PhotoActivity", response.body().toString() );

                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //json解析
    private void parseJSONWithJSONObject(String jsonData){
        try {
            JSONArray jsonArray = new JSONArray( jsonData );
            for (int i=0 ;i<jsonArray.length();i++){
                JSONObject jsonObject = jsonArray.getJSONObject( i );
                String falg=jsonObject.getString( "flag" );
                String id =jsonObject.getString( "id" );
                Log.d( "PhotoActivity","flag is"+falg );
                Log.d( "PhotoActivity","id is"+id );
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //json解析
    private void parseJSONWithJSONObject1(String jsonData){
        try {
            JSONObject jsonObject= new JSONObject( jsonData );
                String falg=jsonObject.getString( "flag" );
                String id =jsonObject.getString( "id" );
                Log.d( "PhotoActivity","flag is"+falg );
                Log.d( "PhotoActivity","id is"+id );

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void HandleResponse(final String result) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText( PhotoActivity.this,result,Toast.LENGTH_SHORT ).show();
            }
        });
    }


    public Bitmap getBitmapFormUri(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContentResolver().openInputStream(uri);

        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;//不加载到内存
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x800为标准
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }
    //进行二次压缩

    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options<=0)
                break;
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

}
