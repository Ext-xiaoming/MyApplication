package ysm.dc.myapplication;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class NetWork1 {

    public NetWork1(){



    }




     public  HttpURLConnection communicate(){
         System.out.printf( "!!!!!!!33333333333333333!!!!!!!!!!!!!!" );

         HttpURLConnection httpURLConnection=null;
        try {
            //1,找水源--创建URL
//            URL url = new URL("https://www.baidu.com/");//放网站
            URL url = new URL("http://10.34.15.176:8000/postIfaceCheck/");//放网站
//            URL url = new URL("http://10.0.2.2:8080/courseManagementServer_war_exploded/server");//放网站
            //2,开水闸--openConnection
            httpURLConnection = (HttpURLConnection) url.openConnection();

            /*httpURLConnection.addRequestProperty("pictureId","countTheNumberOfHomework");
            httpURLConnection.addRequestProperty("pictureId","countTheNumberOfHomework");
            httpURLConnection.addRequestProperty("pictureId","countTheNumberOfHomework");*/
            System.out.printf( "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!" );
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return httpURLConnection;
    }


}
