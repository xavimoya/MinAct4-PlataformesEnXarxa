package xavi.minact4downloads;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String myWebPage = "http://www.udl.es/ca/";
    private final String myImage = "http://www.mayoff.com/5-01cablecarDCP01934.jpg";
    Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button downIMG = (Button) findViewById(R.id.buttonDownloadImage);
        Button downURL = (Button) findViewById(R.id.buttonDownloadURL);
        downIMG.setOnClickListener(this);
        downURL.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDownloadImage:
                downloadImage();
                break;
            case R.id.buttonDownloadURL:
                downloadURL();
                break;
        }
    }

    private void downloadURL(){
        DownURL du = new DownURL();
        du.execute();
    }

    private void downloadImage(){
        DownloadIMG Dimg = new DownloadIMG();
        Dimg.execute();

    }



    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onResume(){
        super.onResume();
        netinfo ni = new netinfo();
        ni.execute();
    }

    @Override
    protected void onStart(){
        super.onStart();
        netinfo ni = new netinfo();
        ni.execute();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }



    private class netinfo extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            String result="";
            if (networkInfo != null && networkInfo.isConnected()){
                boolean wifiok = networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
                boolean mobileok = networkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
                if (wifiok) result = getString(R.string.wificonnected);
                else if (mobileok) result = getString(R.string.mobileconnected);
            }
            else result = getString(R.string.noconnection);
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            showToast(result);
        }
    }

    private class DownloadIMG extends AsyncTask<String,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(String... params) {
            return  DownloadImageFromPath(myImage);

        }

        public Bitmap DownloadImageFromPath(String path){
            InputStream in =null;
            Bitmap bmp=null;
            int responseCode = -1;
            try{

                URL url = new URL(path);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setDoInput(true);
                con.connect();
                responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    //download
                    in = con.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                }

            }
            catch(Exception ex){
                Log.e("Exception",ex.toString());
            }
            return bmp;
        }

        @Override
        public void onPostExecute(Bitmap result){
            ImageView iv = (ImageView)findViewById(R.id.image1);
            iv.setImageBitmap(result);


        }
    }

    private class DownURL extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params){
            return downurl(myWebPage);
        }


        private String downurl(String webpage){

            InputStream is = null;


            int len = 5000;

            try{
                URL url = new URL(webpage);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.connect();
                int response = connection.getResponseCode();

                is = connection.getInputStream();

                //read
                Reader reader = null;
                reader = new InputStreamReader(is,"UTF-8");
                BufferedReader br = new BufferedReader(reader);
                String content="",line="";
                while((line = br.readLine()) != null){
                    content = content + "\n"+line;
                }
               /* char [] buff = new char[len];
                reader.read(buff);
                String content = new String(buff);*/
                return content;




            } catch (Exception e) {
                Log.e("Exception",e.toString());
                return "basura";
            } finally {
                if(is!=null) try {
                    is.close();
                } catch (Exception e) {
                    Log.e("Exception",e.toString());
                }
            }
        }

        @Override
        public void onPostExecute(String result){
            ((TextView)findViewById(R.id.text1)).setText(result);
        }
    }

    private void showToast(String text){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();
    }
}
