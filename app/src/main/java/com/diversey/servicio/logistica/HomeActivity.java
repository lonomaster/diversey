package com.diversey.servicio.logistica;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeActivity extends Activity {

    private String userName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userName = getIntent().getStringExtra("username");
        userId = getIntent().getStringExtra("userid");

        Button btn_sync = (Button)findViewById(R.id.btn_sync);

        btn_sync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            }

        });


    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.btn_sync:
                onBackPressed();
                //super.onBackPressed();

                break;


            case R.id.btn_pendientes:


                break;

            case R.id.btn_done:


                break;
            default:
                break;
        }
    }






    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
