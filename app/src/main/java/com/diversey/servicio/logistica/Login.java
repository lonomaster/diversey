package com.diversey.servicio.logistica;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;


public class Login extends Activity{

	private List<String> list = new ArrayList<String>();
	private String userid = "";
	private AlertDialog dialogC;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login);

		dialogC = new SpotsDialog(Login.this, R.style.connect);

		/*Intent nextScreen = new Intent(Login.this, Main.class);

			nextScreen.putExtra("username","user@gmail.com");
			nextScreen.putExtra("userid","1");
		startActivity(nextScreen);
		finish();
*/

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);


		Button next = (Button)findViewById(R.id.login_continue);
		next.setText("Espere...");
		next.setClickable(false);
		Spinner spinUsers = (Spinner) findViewById(R.id.login_users);
		spinUsers.setVisibility(View.GONE);

		StartLogin sl = new StartLogin(this);
		sl.execute("");
	}

	@Override
	public void onBackPressed() {
		finish();
		//super.onBackPressed();

	}

	private void openMain(View v) { 
		// TODO Auto-generated method stub 
		//final ProgressDialog progDailog = ProgressDialog.show(this, "Conectandose...", "Por favor, espere...", true);
		//progDailog.setProgress(ProgressDialog.STYLE_SPINNER);

dialogC.show();

		Spinner spinUsers = (Spinner) findViewById(R.id.login_users);
		String username = spinUsers.getSelectedItem().toString();
		userid = ProcessData.getUserId(username);

		final Context context = v.getContext();
		final String uname = username;
		final String uid = userid;

		new Thread() { 
			public void run() { 
				Intent nextScreen = new Intent(context, Main.class);
				try {
					Thread.sleep(2000);
					nextScreen.putExtra("username",uname);
					nextScreen.putExtra("userid",uid);
				} catch (Exception e) {;} 
				startActivity(nextScreen);
				finish(); 
				//progDailog.dismiss();
				dialogC.dismiss();
			} 
		}.start(); 

	} 

	private class StartLogin extends AsyncTask <String, String, Boolean> {

		private ProgressDialog progDailog;
		private Context ctx;

		public StartLogin(Context c){
			ctx = c;

		}

		protected void onPreExecute() {
			dialogC.show();
		}

		protected Boolean doInBackground(String... urls) {
			AccountManager am = AccountManager.get(ctx); // "this" references the current Context
			Account[] accounts = am.getAccountsByType("com.google");

			for(Account acc: accounts){
				Log.i("Users:",acc.name);
				if(ProcessData.validUser(acc.name)){
					list.add(acc.name);
				}
			}
			return true;
		}

		protected void onPostExecute(Boolean result) {
			try {
				dialogC.dismiss();
		    } catch (Exception e) {
		        // nothing
		    }
			if(list.size()>0){

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ctx,R.layout.item_spinner, list);
				dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				Spinner spinUsers = (Spinner) findViewById(R.id.login_users);
				spinUsers.setAdapter(dataAdapter);

				spinUsers.setVisibility(View.VISIBLE);		

				Button next = (Button)findViewById(R.id.login_continue);
				next.setText("Continuar");
				next.setClickable(true);
				next.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						Button b = (Button)v;
						b.setText("Espere...");
						b.setClickable(false);
						openMain(v);
					}
				});
			}
			else{
				Button next = (Button)findViewById(R.id.login_continue);
				next.setClickable(true);
				next.setText("Salir");
				next.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						finish();
					}
				});
				TextView textLogin = (TextView)findViewById(R.id.login_title);
				Spinner spinUsers = (Spinner) findViewById(R.id.login_users);
				spinUsers.setVisibility(View.GONE);
				textLogin.setText("No existen usuarios validos, o no hay conexión a internet.");
			}


		}
	}

}
