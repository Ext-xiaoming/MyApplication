package ysm.dc.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    TextView textView;
    Response response;
    public static final String server="http://10.34.45.105:8000/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sendRequest = (Button)findViewById(R.id.send_request);
        textView = (TextView)findViewById(R.id.request_text);
        Button uplode = (Button)findViewById(R.id.uplode);
        sendRequest.setOnClickListener(this);//设置点击事件
        uplode.setOnClickListener( this );
    }

    public void onClick(View v) {
        if(v.getId() == R.id.send_request){
            sendRequestWithOkHttp();
        }
        if(v.getId() == R.id.uplode){
            Intent intent = new Intent(MainActivity.this, PhotoActivity.class);
            startActivity(intent);
        }
    }

    private void sendRequestWithOkHttp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //创建OkHttpClient对象
                    OkHttpClient client = new OkHttpClient();
                    //创建Request
                    Request request = new Request.Builder()
                            .url("https://10.34.45.105:8000/index/")//访问连接
                            .get()
                            .build();
                    //创建Call对象
                    Call call = client.newCall(request);
                    //通过execute()方法获得请求响应的Response对象
                    response = call.execute();
                    if (response.isSuccessful()) {
                        //处理网络请求的响应，处理UI需要在UI线程中处理
                        //...
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(response.body().toString());
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void PostRequest(){
        System.out.printf( "111111111weweq111111" );
        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();

                String imagePath=Environment.getExternalStorageDirectory()+"/DCIM/Camera/123.jpg";
                System.out.printf(imagePath );
                File file = new File( imagePath );
                System.out.printf(file.toString() );
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
                                    textView.setText(result);
                                }
                            });
                        }
                    }
                });

            }
        }).start();
    }

}


/*
*  OkHttpClient client = new OkHttpClient();
    FormBody body = new FormBody.Builder()
            .add(key,value)
            .build();
    Request request = new Request.Builder()
            .url(url)
            .post(body)
            .build();
    Call call = client.newCall(request);
    call.enqueue(new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            //...
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            if(response.isSuccessful()){
                String result = response.body().string();
                //处理UI需要切换到UI线程处理
            }
        }
    });*/