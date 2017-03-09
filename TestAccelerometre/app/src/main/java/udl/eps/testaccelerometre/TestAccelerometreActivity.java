package udl.eps.testaccelerometre;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.conn.ClientConnectionManager;

import java.sql.Connection;

public class TestAccelerometreActivity extends Activity {
  private boolean color = false;
  private TextView view, view2;
    private String text="";

  
/** Called when the activity is first created. */

  @Override
  public void onCreate(Bundle savedInstanceState) {


    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
      view = (TextView) findViewById(R.id.textView);
      view.setBackgroundColor(Color.GREEN);
      view2 = (TextView)findViewById(R.id.textView2);
      view2.setBackgroundColor(Color.YELLOW);

      ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo networkInfo = cm.getActiveNetworkInfo();
      if(networkInfo != null && networkInfo.isConnected()){
          view.setText(networkInfo.toString());
      }else{
          view.setText(R.string.notconnected);
      }

        Button b = (Button)findViewById(R.id.refreshbutton);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateinfo();
            }
        });

        updateinfo();

  }

  private void updateinfo(){
      netinfo ni = new netinfo();
      ni.execute();
      view2.append("\n"+text);
  }


  /*@Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {
    // Do something here if sensorAcc accuracy changes.
  }*/

  @Override
  protected void onPause() {
    super.onPause();
  }


    @Override
    protected void onResume(){
        super.onResume();
        updateinfo();
    }

    @Override
    protected void onStart(){
        super.onStart();
        updateinfo();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }



    private class netinfo extends AsyncTask<String,Void,String>{

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
            text = result;
        }
    }
    
}

