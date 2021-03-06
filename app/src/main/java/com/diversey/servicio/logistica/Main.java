package com.diversey.servicio.logistica;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.orm.StringUtil;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;

import dmax.dialog.SpotsDialog;


public class Main extends Activity {

    public static final int CASO_CAFE = 100;
    public static final int CASO_PLAGAS = 200;
    public static final int CASO_HIGIENE = 300;
    public static final int CASO_4 = 400;
    private static final int FINISH_ALERT = 0;
    private static int count_ot_local = 0;
    final Messenger mMessenger = new Messenger(new IncomingHandler());
    public boolean is_on_map = false;
    public Typeface BebasNeueRegular;
    public Typeface BebasNeueLight;
    public Typeface BebasNeueBold;
    public Boolean home = true;
    public JSONObject ot;
    Timer timer = null;
    Messenger mService = null;
    boolean mIsBound;
    private ProcessData processData;
    private LocationManager locationManager;
    private ListView otList;
    private LinearLayout listLayout;
    private GeoPoint geoPoint_location = new GeoPoint(0, 0);
    private String userId;
    private AlertDialog dialogL;
    private AlertDialog dialogR;
    private AlertDialog dialogG;
    private AlertDialog dialogS;
    private LinearLayout layoutList;
    private ListView listView1;
    private List<UserRecord> usersNow;
    private UserRecord u;
    private int contadorRefresh = 0, sync_count = 0, done_count = 0, pend_count = 0;
    private ArrayList<String> lastOTid = new ArrayList<String>();

    public Button btn_done;
    Button btn_sync;
    public Button btn_pendientes;
    public LinearLayout homelayout;
    public RelativeLayout homerelative;

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = new Messenger(service);
            Log.i("Service", "Attached.");
            try {
                Message msg = Message.obtain(null, OTService.MSG_REGISTER_CLIENT);
                msg.replyTo = mMessenger;
                mService.send(msg);
            } catch (RemoteException e) {
                // In this case the service has crashed before we could even do anything with it
            }
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been unexpectedly disconnected - process crashed.
            mService = null;
            Log.i("Service", "Disconnected.");
        }
    };

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppParameters.createDirIfNotExists(AppParameters.applicationPath);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.main);

        BebasNeueLight = Typeface.createFromAsset(Main.this.getAssets(),
                "BebasNeueLight.ttf");

        BebasNeueRegular = Typeface.createFromAsset(Main.this.getAssets(),
                "BebasNeueRegular.ttf");

        BebasNeueBold = Typeface.createFromAsset(Main.this.getAssets(),
                "BebasNeueBold.ttf");


		/*
        *  <Button
                        android:id="@+id/showlist_todos"

                        android:id="@+id/showlist_pendientes"

                        android:id="@+id/showlist_realizados"

		/*styling*/
        Button botTodos = (Button) findViewById(R.id.showlist_todos);
        botTodos.setTypeface(BebasNeueBold);
        botTodos.setBackgroundColor(getResources().getColor(R.color.gray_light));

        Button botPendientes = (Button) findViewById(R.id.showlist_pendientes);
        botPendientes.setTypeface(BebasNeueRegular);
        botPendientes.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

        Button botRealizados = (Button) findViewById(R.id.showlist_realizados);
        botRealizados.setTypeface(BebasNeueRegular);
        botRealizados.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));


        btn_pendientes = (Button) findViewById(R.id.btn_pendientes);
        btn_done = (Button) findViewById(R.id.btn_done);
        btn_sync = (Button) findViewById(R.id.btn_sync);


        btn_sync.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showListRefresh();
                return;
            }
        });

        homelayout = (LinearLayout) findViewById(R.id.homelayout);
        homerelative = (RelativeLayout) findViewById(R.id.homerelative);

        btn_pendientes.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                homelayout.setVisibility(View.VISIBLE);
                homerelative.setVisibility(View.INVISIBLE);
                home = false;
                showListPendiente();
                SharedPreferences prefe = getSharedPreferences("datosDiversey", Login.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefe.edit();
                editor.putString("actual", "pendientes");
                editor.commit();

            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                homelayout.setVisibility(View.VISIBLE);
                homerelative.setVisibility(View.INVISIBLE);
                home = false;
                showListRealizadas();
                SharedPreferences prefe = getSharedPreferences("datosDiversey", Login.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefe.edit();
                editor.putString("actual", "realizadas");
                editor.commit();
            }
        });

        TextView title_company_name = (TextView) findViewById(R.id.title_company_name);
        title_company_name.setTypeface(BebasNeueLight);

        SharedPreferences prefe = getSharedPreferences("datosDiversey", Login.MODE_PRIVATE);

        TextView login = (TextView) findViewById(R.id.login);
        login.setText(prefe.getString("usernameDiversey", ""));
        login.setTypeface(BebasNeueRegular);

        TextView version = (TextView) findViewById(R.id.version);

        int versionCode = BuildConfig.VERSION_CODE;

        version.setText("Version " + String.valueOf(versionCode));

		/* END styling*/

        dialogL = new SpotsDialog(Main.this, R.style.load);
        dialogG = new SpotsDialog(Main.this, R.style.get);
        dialogR = new SpotsDialog(Main.this, R.style.refresh);
        dialogS = new SpotsDialog(Main.this, R.style.sync);
        u.deleteAll(UserRecord.class, "status = ?", "false");

        layoutList = (LinearLayout) findViewById(R.id.list_layout);

        layoutList.setVisibility(View.VISIBLE);

        Intent i = getIntent();
        userId = i.getStringExtra("userid");

        TextView title_user= (TextView) findViewById(R.id.title_user);
        title_user.setText(i.getStringExtra("username"));

		/*Mapa*/
        if (isNetworkAvailable()) {
            Criteria crtr = new Criteria();
            crtr.setPowerRequirement(Criteria.ACCURACY_FINE);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            MyLocationListener locationListener = new MyLocationListener();

            locationManager.requestLocationUpdates(locationManager.getBestProvider(crtr, false), 0, 0, locationListener);
            Location loc1 = locationManager.getLastKnownLocation(locationManager.getBestProvider(crtr, false));

            if (loc1 != null)
                geoPoint_location = new GeoPoint((int) (loc1.getLatitude() * 1000000), (int) (loc1.getLongitude() * 1000000));
        }
        StartMain sm = new StartMain(this);
        sm.execute("");
    }

    @Override
    public void onResume() {
        TextView counterOTlocal = (TextView) findViewById(R.id.ot_local);

        List<UserRecord> OTsLocal = UserRecord.find(UserRecord.class, "status = ?", "true");

        count_ot_local = OTsLocal.size();

        if (count_ot_local > 0) {
            counterOTlocal.setVisibility(View.VISIBLE);
            counterOTlocal.setText(Integer.toString(count_ot_local));
        } else {
            counterOTlocal.setVisibility(View.GONE);
        }
        List<UserRecord> ots = Select.from(UserRecord.class)
                .list();
        usersNow = new ArrayList<UserRecord>(ots);
	/*ArrayAdapter<UserRecord> adapter1 = new UserItemAdapter(Main.this,R.layout.listitems, ots);
	listView1 = (ListView)findViewById(R.id.mylist1);
	listView1.setAdapter(adapter1);
	adapter1.notifyDataSetChanged();

	listView1.invalidate();

		setButtons();
	Button botTodos = (Button)findViewById(R.id.showlist_todos);
	botTodos.setTypeface(BebasNeueBold);
	botTodos.setBackgroundColor(getResources().getColor(R.color.gray_light));*/

        SharedPreferences prefe = getSharedPreferences("datosDiversey", Login.MODE_PRIVATE);
        if (prefe.getString("actual", "").equalsIgnoreCase("pendientes")) {
            showListPendiente();
        } else if(prefe.getString("actual", "").equalsIgnoreCase("pendientes")) {
            showListRealizadas();
        }else{
            home = true;
            homerelative.setVisibility(View.VISIBLE);
            homelayout.setVisibility(View.INVISIBLE);
        }

        //contadores

        List<UserRecord> OTsPend = UserRecord.find(UserRecord.class, "tipoordenid  = ?", "2");
        List<UserRecord> OTsdone = UserRecord.find(UserRecord.class, "tipoordenid  = ?", "4");

        pend_count = OTsPend.size();
        done_count = OTsdone.size();

        Log.i("Numero ots realizas", " : " + OTsPend.size() + "  count  : " + done_count);

        TextView textview_pend_count = (TextView) findViewById(R.id.textview_pend_count);
        textview_pend_count.setText(pend_count + "");

        TextView textview_done_count = (TextView) findViewById(R.id.textview_done_count);
        textview_done_count.setText(done_count + "");

        TextView textview_sync_count = (TextView) findViewById(R.id.textview_sync_count);

        textview_sync_count.setText(count_ot_local + "");

        super.onResume();
    }

    private void setButtons() {

        Button botTodos = (Button) findViewById(R.id.showlist_todos);
        botTodos.setTypeface(BebasNeueRegular);
        botTodos.setBackgroundColor(getResources().getColor(R.color.gray_dark));

        Button botPendientes = (Button) findViewById(R.id.showlist_pendientes);
        botPendientes.setTypeface(BebasNeueRegular);
        botPendientes.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));

        Button botRealizados = (Button) findViewById(R.id.showlist_realizados);
        botRealizados.setTypeface(BebasNeueRegular);
        botRealizados.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));

    }

    private void centerToPosition() {

        Criteria crtr = new Criteria();
        crtr.setPowerRequirement(Criteria.ACCURACY_FINE);
        String bestProvider = locationManager.getBestProvider(crtr, false);
        Location loc = null;
        if (bestProvider != null)
            loc = locationManager.getLastKnownLocation(bestProvider);
        if (loc == null) return;
        geoPoint_location = new GeoPoint((int) (loc.getLatitude() * 1000000), (int) (loc.getLongitude() * 1000000));

    }

    public void toggleClick(View view) {
        ToggleButton b = (ToggleButton) view;
        ActionBar a = getActionBar();
        String s = (String) b.getText();
        a.setSubtitle(s);
    }

    public void onClick(View v) {
        centerToPosition();

        int id = v.getId();
        switch (id) {
            case R.id.list_avatar:
            case R.id.list_showcase:
                listLayout = null;
                if (id == R.id.list_showcase)
                    listLayout = (LinearLayout) v.getParent().getParent().getParent(); // recordar referencias dentro del archivo xml
                if (id == R.id.list_avatar)
                    listLayout = (LinearLayout) v.getParent().getParent(); // recordar referencias dentro del archivo xml

                TextView inputName = (TextView) listLayout.findViewById(R.id.list_nombre);
                ListView lv = (ListView) findViewById(R.id.mylist1);
                int position = lv.getPositionForView(inputName);

                OpenOt newOt = new OpenOt(v.getContext(), usersNow.get(position), v);
                newOt.execute("");

                break;
        }
    }

    public void showListPendiente() {

        setButtons();

        int tipoOt = 2;

        List<UserRecord> users1 = Select.from(UserRecord.class)
                .list();
        Log.d("SugarSizeNormal", String.valueOf(users1.size()));//processData.getData();
        List<UserRecord> users2 = new ArrayList<UserRecord>();
        ArrayAdapter<UserRecord> adapter = null;
        usersNow.clear();


        for (UserRecord user : users1) {
            //				Log.i("User (orden):",user.tipo_orden_id + Integer.toString(tipoOt));
            if (Integer.parseInt(user.tipoordenid) == tipoOt) {
                users2.add(user);
            }
        }
        adapter = new UserItemAdapter(this, R.layout.listitems, users2);
        usersNow = new ArrayList<UserRecord>(users2);


        otList = (ListView) findViewById(R.id.mylist1);
        otList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        otList.invalidate();

    }

    public void showListRefresh() {

        setButtons();

        Log.i("Refrescando", "...");
        // if(contadorRefresh == 50){
        VerificarInternet.Internet test = new VerificarInternet.Internet();
        if (test.conexion(getApplicationContext()) == false) {
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Sin conexión a internet");
            alertDialog.setMessage("Para sincronizar necesita de una conexión a internet");
            alertDialog.setButton("Aceptar", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
            alertDialog.show();
        } else {
            dialogR.show();
            List<UserRecord> OTsLocal = UserRecord.find(UserRecord.class, "status = ?", "true");
            int count = 0;
            for (int i = 0; i < OTsLocal.size(); i++) {

                Log.d("OTLocal", OTsLocal.get(i).idot);
                Log.d("OTLocal", OTsLocal.get(i).tipoordenid);
                Log.d("OTLocal", OTsLocal.get(i).json_imagenes);
                Log.d("OTLocal", OTsLocal.get(i).json_imagenes_qr);

                try {
                    ot = new JSONObject(OTsLocal.get(i).allJsonOT);
                    JSONArray json_imagenes = new JSONArray(OTsLocal.get(i).json_imagenes);
                    JSONArray json_imagenes_qr = new JSONArray(OTsLocal.get(i).json_imagenes_qr);

                    ot.put("json_imagenes", json_imagenes);
                    ot.put("json_imagenes_qr", json_imagenes_qr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (AppParameters.postData(ot, "actualizar")) {
                    UserRecord u = OTsLocal.get(i);
                    u.status = "false";
                    u.save();
                    count++;
                } else {
                    UserRecord u = OTsLocal.get(i);
                    u.status = "false";
                    u.save();
                    count++;
                }


            }

            dialogR.dismiss();


            RefreshOts rfot = new RefreshOts(Main.this);
            rfot.execute("");

        }

        TextView counterOTlocal = (TextView) findViewById(R.id.ot_local);

        List<UserRecord> OTsLocal = UserRecord.find(UserRecord.class, "status = ?", "true");

        count_ot_local = OTsLocal.size();

        if (count_ot_local > 0) {
            counterOTlocal.setVisibility(View.VISIBLE);
            counterOTlocal.setText(Integer.toString(count_ot_local));
        } else {
            counterOTlocal.setVisibility(View.GONE);
        }


    }

    public void showListRealizadas() {

        setButtons();

        int tipoOt = 4;

        List<UserRecord> users1 = Select.from(UserRecord.class)
                .list();
        Log.d("SugarSizeNormal", String.valueOf(users1.size()));//processData.getData();
        List<UserRecord> users2 = new ArrayList<UserRecord>();
        ArrayAdapter<UserRecord> adapter = null;
        usersNow.clear();

        for (UserRecord user : users1) {
            //				Log.i("User (orden):",user.tipo_orden_id + Integer.toString(tipoOt));
            if (Integer.parseInt(user.tipoordenid) == tipoOt) {
                users2.add(user);
            }
        }
        adapter = new UserItemAdapter(this, R.layout.listitems, users2);
        usersNow = new ArrayList<UserRecord>(users2);


        otList = (ListView) findViewById(R.id.mylist1);
        otList.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        otList.invalidate();

    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }
        return (networkInfo != null);
    }

    @Override
    public void onBackPressed() {
        if (home) showDialog(FINISH_ALERT);
        else {
            home = true;
            homelayout.setVisibility(View.INVISIBLE);
            homerelative.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case FINISH_ALERT:
                // Create out AlterDialog
                Builder builder = new AlertDialog.Builder(this);
                Drawable icon = getResources().getDrawable(R.drawable.logo_app);
                Bitmap d = ((BitmapDrawable) icon).getBitmap();
                Bitmap bitmapOrig = Bitmap.createScaledBitmap(d, 30, 30, false);
                icon = new BitmapDrawable(bitmapOrig);
                builder.setIcon(icon);
                builder.setTitle("Servicio Logística Diversey");
                builder.setMessage("Deseas terminar esta aplicación?");
                builder.setCancelable(true);
                builder.setPositiveButton("SALIR", new OkOnClickListener());
                builder.setNegativeButton("CANCELAR", new CancelOnClickListener());

                AlertDialog dialog = builder.create();
                dialog.show();
        }
        return super.onCreateDialog(id);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        sendMessageToService(OTService.DO_REFRESH_OTS, 1);
        if (data != null) {
            if (resultCode == Main.CASO_CAFE || resultCode == Main.CASO_HIGIENE) {
                processOTData(data.getExtras());
                Button b = (Button) findViewById(R.id.showlist_todos);
                b.performClick();
            } else if (resultCode == Main.CASO_PLAGAS) {

            }
        }
        return;

    }

    private void processOTData(Bundle bl) {
        JSONObject datos1 = null;

        try {
            datos1 = new JSONObject(bl.getString("json-datos"));

            if (datos1 != null) {
                String st = datos1.getString("tipo_orden_id");
                String otid = datos1.getString("orden_trabajo_id");

                processData.setData(otid, st);
                //sendMessageToService(OTService.MSG_SET_OT_UPDATE,datos1.toString());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //	        outState.putString("textStatus", textStatus.getText().toString());
        //	        outState.putString("textIntValue", textIntValue.getText().toString());
        //	        outState.putString("textStrValue", textStrValue.getText().toString());
    }

    @SuppressWarnings("unused")
    private void restoreMe(Bundle state) {
        if (state != null) {
            Log.i("Service", "textStatus");
            Log.i("Service", "textIntValue");
            Log.i("Service", "textStrValue");
        }
    }

    @SuppressWarnings("unused")
    private void CheckIfServiceIsRunning() {
        //If the service is running when the activity starts, we want to automatically bind to it.
        if (OTService.isRunning()) {
            doBindService();
        }
    }

    @SuppressWarnings("unchecked")
    private void sendMessageToService(int tipo, Object val) {
        if (mIsBound) {
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, tipo);
                    Bundle bl = new Bundle();
                    if (tipo == OTService.MSG_SET_OT_LIST) {
                        bl.putStringArrayList("arr", (ArrayList<String>) val);
                    } else if (tipo == OTService.MSG_SET_OT_UPDATE) {
                        bl.putString("ot", (String) val);
                    } else if (tipo == OTService.MSG_SET_HEADER_STRING) {
                        bl.putString("str", (String) val);
                    } else if (tipo == OTService.DO_REFRESH_OTS) {
                        bl.putInt("status", (Integer) val);
                    }

                    msg.setData(bl);

                    msg.replyTo = mMessenger;
                    mService.send(msg);
                    Log.i("Main snd:", msg.toString());
                } catch (RemoteException e) {
                }
            }
        }
    }

    void doBindService() {
        bindService(new Intent(this, OTService.class), mConnection, Context.BIND_AUTO_CREATE);
        mIsBound = true;
        Log.i("Service", "Binding.");
    }

    void doUnbindService() {
        if (mIsBound) {
            // If we have received the service, and hence registered with it, then now is the time to unregister.
            if (mService != null) {
                try {
                    Message msg = Message.obtain(null, OTService.MSG_UNREGISTER_CLIENT);
                    msg.replyTo = mMessenger;
                    mService.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has crashed.
                }
            }
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
            Log.i("Service", "Unbinding.");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dialogG.dismiss();
        dialogL.dismiss();
        dialogR.dismiss();
        dialogS.dismiss();
        try {
            doUnbindService();
            stopService(new Intent(Main.this, OTService.class));
        } catch (Throwable t) {
            Log.e("MainActivity", "Failed to unbind from the service", t);
        }
    }

    protected void onStop() {
        super.onStop();

        dialogL.dismiss();
        dialogR.dismiss();
        dialogG.dismiss();
        dialogS.dismiss();

    }

    void sendOtsMessage() {

        Log.i("Enviando msg a servicio", lastOTid.toString() + "|" + userId);
        sendMessageToService(OTService.MSG_SET_OT_LIST, lastOTid);
        sendMessageToService(OTService.MSG_SET_HEADER_STRING, userId);
        return;
    }

    private class StartMain extends AsyncTask<String, String, Boolean> {

        private ProgressDialog progDailog;
        private Context ctx;
        private List<UserRecord> users;

        public StartMain(Context c) {
            ctx = c;
        }

        protected void onPreExecute() {
			/*progDailog.setTitle("Descargando datos");
			progDailog.setMessage("por favor, espera...");
			progDailog.setIndeterminate(false);
			progDailog.setCancelable(false);
			progDailog.setMax(1);
			progDailog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			progDailog.show();*/
            dialogL.show();
            //startService(new Intent(Main.this, OTService.class));
        }

        protected Boolean doInBackground(String... urls) {
            try {
                if (isNetworkAvailable()) {
                    processData = new ProcessData(AppParameters.url + userId);
                } else {
                    processData = new ProcessData();
                }
                //users = processData.getData();

                users = Select.from(UserRecord.class).list();
                Log.d("SugarSizeStart", String.valueOf(users.size()));
                usersNow = new ArrayList<UserRecord>(users);

                int total = usersNow.size();
                int avance = 0;
                //progDailog.setMax(total);
                for (UserRecord user : users) {
                    //progDailog.setProgress(avance);
                    avance++;
                }

                lastOTid.clear();
                for (UserRecord user : users) {
                    lastOTid.add(user.idot);

                    Log.d("SugarLastID", "" + user.toString());
                }
            } catch (Exception e) {

            }
            doBindService();
            return true;
        }

        protected void onPostExecute(Boolean result) {
            ArrayAdapter<UserRecord> adapter1 = new UserItemAdapter(ctx, R.layout.listitems, users);
            listView1 = (ListView) findViewById(R.id.mylist1);
            listView1.setAdapter(adapter1);
            adapter1.notifyDataSetChanged();

            listView1.invalidate();

            try {
                dialogL.dismiss();

            } catch (Exception e) {

            }

            List<UserRecord> OTsLocal = UserRecord.find(UserRecord.class, "status = ?", "true");
            List<UserRecord> OTsPend = UserRecord.find(UserRecord.class, "tipoordenid  = ?", "2");
            List<UserRecord> OTsdone = UserRecord.find(UserRecord.class, "tipoordenid  = ?", "4");

            count_ot_local = OTsLocal.size();
            pend_count = OTsPend.size();
            done_count = OTsdone.size();

            Log.i("Numero ots realizas", " : " + OTsPend.size() + "  count  : " + done_count);

            TextView textview_pend_count = (TextView) findViewById(R.id.textview_pend_count);
            textview_pend_count.setText(pend_count + "");

            TextView textview_done_count = (TextView) findViewById(R.id.textview_done_count);
            textview_done_count.setText(done_count + "");

            TextView textview_sync_count = (TextView) findViewById(R.id.textview_sync_count);
            textview_sync_count.setText(count_ot_local + "");


			/*if(isNetworkAvailable()){
				final Handler handler = new Handler();
				handler.postDelayed(new Runnable() {public void run() {sendOtsMessage();}}, 250);
			}
			else{
				timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask(){ public void run() {sendOtsMessage();}}, 250, 1000);
			}*/

        }
    }

    private class RefreshOts extends AsyncTask<String, String, Boolean> {

        private Context ctx;

        public RefreshOts(Context c) {
            ctx = c;
            Log.i("String Refresh", c.toString());

        }

        protected void onPreExecute() {
            dialogR.show();
            sendMessageToService(OTService.DO_REFRESH_OTS, 0);
        }

        protected Boolean doInBackground(String... urls) {
            if (isNetworkAvailable()) {
                u.deleteAll(UserRecord.class, "status = ?", "false");
                String readDb = processData.readDbFeed(AppParameters.url + userId);
                if (processData.getJson(readDb)) {
                    Log.i("Descargando OTs", "...");

                    usersNow.clear();
                    //usersNow = processData.getData();
                    usersNow = Select.from(UserRecord.class)
                            .list();
                    Log.d("SugarSizeRefresh", String.valueOf(usersNow.size()));

                    usersNow = new ArrayList<UserRecord>(usersNow);
                    lastOTid.clear();


                    int total = usersNow.size();
                    int avance = 0;

                    for (UserRecord user : usersNow) {

                        lastOTid.add(user.idot);

                    }
                }
            } else {
                Toast.makeText(Main.this, "Error en la descarga", Toast.LENGTH_LONG).show();
            }
            return true;
        }

        protected void onPostExecute(Boolean result) {
            sendMessageToService(OTService.DO_REFRESH_OTS, 1);
            try {
                dialogR.dismiss();
            } catch (Exception e) {
                // nothing
            }

            List<UserRecord> OTsLocal = UserRecord.find(UserRecord.class, "status = ?", "true");
            List<UserRecord> OTsPend = UserRecord.find(UserRecord.class, "tipoordenid  = ?", "2");
            List<UserRecord> OTsdone = UserRecord.find(UserRecord.class, "tipoordenid  = ?", "4");

            count_ot_local = OTsLocal.size();
            pend_count = OTsPend.size();
            done_count = OTsdone.size();

            Log.i("Numero ots realizas", " : " + OTsPend.size() + "  count  : " + done_count);

            TextView textview_pend_count = (TextView) findViewById(R.id.textview_pend_count);
            textview_pend_count.setText(pend_count + "");

            TextView textview_done_count = (TextView) findViewById(R.id.textview_done_count);
            textview_done_count.setText(done_count + "");

            TextView textview_sync_count = (TextView) findViewById(R.id.textview_sync_count);
            textview_sync_count.setText(count_ot_local + "");


            //otList.invalidate();

            Log.i("lastOTid", lastOTid.toString());

            //Button b = (Button) findViewById(R.id.showlist_todos);
            //b.performClick();
        }
    }

    private class OpenOt extends AsyncTask<String, String, Boolean> {

        //private ProgressDialog progDailog;
        private Context ctx;
        private Intent nextScreen;
        private UserRecord user;
        private View v;
        private int req_code;

        public OpenOt(Context c, UserRecord val, View v) {
            ctx = c;
            user = val;
            dialogG.show();
            this.v = v;
        }

        protected void onPreExecute() {

            v.setClickable(false);

        }

        protected Boolean doInBackground(String... userid) {

            nextScreen = new Intent(ctx, otDiversey.class);
            req_code = Main.CASO_HIGIENE;

            nextScreen.putExtra("fecha_realizacion", user.fecha_ejecucion);

            nextScreen.putExtra("json_maquinas", user.json_maq);
            nextScreen.putExtra("json_maquinas_all", user.json_maq_all);

            nextScreen.putExtra("tipo_ot", user.tipoordenid);
            nextScreen.putExtra("tipo_mantencion", user.tipo_mantencion);
            nextScreen.putExtra("tipo_servicio", user.tipo_servicio);

            nextScreen.putExtra("descripcion", user.descripcion);//falla informada
            nextScreen.putExtra("diagnostico", user.diagnostico);
            nextScreen.putExtra("obs_general", user.obs_general);
            nextScreen.putExtra("horas_hombre", user.horas_hombre);
            nextScreen.putExtra("obs_finales", user.obs_finales);

            nextScreen.putExtra("idOT", user.idot);
            nextScreen.putExtra("nombreOT", user.nombre);
            nextScreen.putExtra("id_tecnico", userId);
            nextScreen.putExtra("cliente", user.razon_social);

            nextScreen.putExtra("rut", user.rut);
            nextScreen.putExtra("direccion", user.direccion);

            nextScreen.putExtra("firma_nombre", user.firma_nombre);
            nextScreen.putExtra("firma_mail", user.firma_mail);
            nextScreen.putExtra("firma_rut", user.firma_rut);

            nextScreen.putExtra("correo_contacto", user.correo_contacto);
            nextScreen.putExtra("descripcion", user.descripcion);

            nextScreen.putExtra("modelo_maquina", user.modelo_maquina);
            nextScreen.putExtra("nro_serie", user.nro_serie);
            nextScreen.putExtra("cantidad", user.cantidad);
            nextScreen.putExtra("codigo", user.codigo);
            nextScreen.putExtra("comentarios", user.comentarios);

            nextScreen.putExtra("latitud", user.latitud);
            nextScreen.putExtra("longitud", user.longitud);

            nextScreen.putExtra("partes_usadas", user.partes_usadas);

            float tec_lon = (float) ((float) (geoPoint_location.getLongitudeE6()) / 1E6);
            float tec_lat = (float) ((float) (geoPoint_location.getLatitudeE6()) / 1E6);
            nextScreen.putExtra("longitud_tecnico", Float.toString(tec_lon));
            nextScreen.putExtra("latitud_tecnico", Float.toString(tec_lat));
            dumpIntent(nextScreen);
            return true;
        }

        protected void onPostExecute(Boolean result) {
            sendMessageToService(OTService.DO_REFRESH_OTS, 0);
            dumpIntent(nextScreen);
            v.setClickable(true);
            startActivityForResult(nextScreen, req_code);
            dialogG.dismiss();
        }
    }

    public static void dumpIntent(Intent i) {

        Bundle bundle = i.getExtras();
        if (bundle != null) {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();
            Log.e("key intent", "Dumping Intent start");
            while (it.hasNext()) {
                String key = it.next();
                Log.e("key intent", "[" + key + "=" + bundle.get(key) + "]");
            }
            Log.e("key intent", "Dumping Intent end");
        }
    }

    private final class CancelOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getApplicationContext(), " El servicio continuará funcionando ",
                    Toast.LENGTH_LONG).show();
        }
    }

    private final class OkOnClickListener implements
            DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which) {
            try {
                doUnbindService();
                stopService(new Intent(Main.this, OTService.class));
            } catch (Throwable t) {
                Log.e("MainActivity", "Failed to unbind from the service", t);
            }
            finish();
        }
    }

    @SuppressLint("HandlerLeak")
    class IncomingHandler extends Handler {

        public void handleMessage(Message msg) {
            Button b = (Button) findViewById(R.id.refresh);
            switch (msg.what) {
                case OTService.REFRESH_OTS:
                    //b.setClickable(true);
                    //b.performClick();
                    //b.setClickable(false);
                    break;
                case OTService.COUNT_UPLOADS:
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

}
