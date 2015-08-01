package com.diversey.servicio.logistica;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class UserItemAdapter extends ArrayAdapter<UserRecord> {
	private List<UserRecord> users;
	private Context context;
	public Typeface BebasNeueRegular;
	public Typeface BebasNeueLight;
	public Typeface BebasNeueBold;
	
	
	public UserItemAdapter(Context context, int textViewResourceId, List<UserRecord> users) {
		super(context, textViewResourceId, users);
		this.context = context;
		this.users = users;

		BebasNeueLight = Typeface.createFromAsset(context.getAssets(),
				"BebasNeueLight.ttf");

		BebasNeueRegular = Typeface.createFromAsset(context.getAssets(),
				"BebasNeueRegular.ttf");

		BebasNeueBold = Typeface.createFromAsset(context.getAssets(),
				"BebasNeueBold.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.listitems, null);
			v.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					return;
				}
			});
		}

		UserRecord user = users.get(position);
		Log.d("SugarContent", user.toString());
		if (user != null) {





			TextView title_site = (TextView) v.findViewById(R.id.title_site);
			TextView username = (TextView) v.findViewById(R.id.list_nombre);
			TextView razonSocial = (TextView) v.findViewById(R.id.list_nombre_empresa);

			TextView contacto = (TextView) v.findViewById(R.id.list_nombre_contacto);
			TextView title_contacto = (TextView) v.findViewById(R.id.title_contacto);



			TextView direccion = (TextView) v.findViewById(R.id.list_direccion_empresa);
			TextView title_direccion = (TextView) v.findViewById(R.id.title_direccion);


			ImageView state = (ImageView) v.findViewById(R.id.list_estado);
			ImageView logo = (ImageView)v.findViewById(R.id.list_avatar);

			TextView otId= (TextView) v.findViewById(R.id.list_numero_ot);
			TextView title_ot_id= (TextView) v.findViewById(R.id.title_ot_id);



			TextView title_fecha= (TextView) v.findViewById(R.id.title_fecha);


			TextView fechaAsignacion= (TextView) v.findViewById(R.id.list_fecha_asignacion);
			TextView fechaEjecucion= (TextView) v.findViewById(R.id.list_fecha_ejecucion);
			fechaAsignacion.setVisibility(View.GONE);
			
			//logo.setImageBitmap(AppParameters.getCacheBitmap(user.logo));

			Picasso.with(context)
					.load(AppParameters.logourl+user.logo)
					.placeholder(R.drawable.logo_empresa) // optional
					.error(R.drawable.logo_empresa)         // optional
					.into(logo);

			//Picasso.with(context).setIndicatorsEnabled(true);

			username.setTypeface(BebasNeueRegular);
			username.setText(user.nombre);

			title_site.setTypeface(BebasNeueBold);
			razonSocial.setText(user.razon_social);
			razonSocial.setTypeface(BebasNeueRegular);

			title_direccion.setTypeface(BebasNeueBold);
			direccion.setText(user.direccion);
			direccion.setTypeface(BebasNeueRegular);

			title_contacto.setTypeface(BebasNeueBold);
			contacto.setText(user.nombre_contacto);
			contacto.setTypeface(BebasNeueRegular);

			title_ot_id.setTypeface(BebasNeueBold);
			otId.setText(user.idot);
			otId.setTypeface(BebasNeueRegular);

			title_fecha.setTypeface(BebasNeueBold);
			fechaAsignacion.setText(user.fecha_creacion);
			fechaAsignacion.setTypeface(BebasNeueRegular);
			fechaEjecucion.setText(user.fecha_ejecucion);
			fechaEjecucion.setTypeface(BebasNeueRegular);

			if(state != null){
				int st = Integer.valueOf(user.tipo_orden_id);
				switch (st) {
				case AppParameters.OT_PENDIENTE:
					//state.setImageResource(R.drawable.estadopendientes);
					state.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
					break;
				case AppParameters.OT_EJECUCION:
					state.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_dark));
					break;
				case AppParameters.OT_REALIZADA:
					state.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
					break;
				default:
					state.setBackgroundColor(context.getResources().getColor(android.R.color.background_dark));
					break;
				}
			}
		}
		return v;
	}

	public void setList(ArrayList<UserRecord> u){
		this.users = u;
	}
}
