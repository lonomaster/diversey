package com.diversey.servicio.logistica;

import android.content.Context;
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
import java.util.Collections;
import java.util.List;

public class UserItemAdapter extends ArrayAdapter<UserRecord> {
	private List<UserRecord> users;
	private Context context;
	
	
	public UserItemAdapter(Context context, int textViewResourceId, List<UserRecord> users) {
		super(context, textViewResourceId, users);
		this.context = context;
		this.users = users;
		Collections.reverse(this.users);
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
			TextView username = (TextView) v.findViewById(R.id.list_nombre);
			TextView razonSocial = (TextView) v.findViewById(R.id.list_nombre_empresa);
			TextView contacto = (TextView) v.findViewById(R.id.list_nombre_contacto);
			TextView direccion = (TextView) v.findViewById(R.id.list_direccion_empresa);
			ImageView state = (ImageView) v.findViewById(R.id.list_estado);
			ImageView logo = (ImageView)v.findViewById(R.id.list_avatar);

			TextView otId= (TextView) v.findViewById(R.id.list_numero_ot);
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

			razonSocial.setText(user.razon_social);
			username.setText(user.nombre);
			contacto.setText(user.nombre_contacto);
			direccion.setText(user.direccion);
			otId.setText(user.idot);
			fechaAsignacion.setText(user.fecha_creacion);
			fechaEjecucion.setText(user.fecha_ejecucion);
			
			if(state != null){
				int st = Integer.valueOf(user.tipo_orden_id);
				switch (st) {
				case AppParameters.OT_PENDIENTE:
					state.setImageResource(R.drawable.estadopendientes);
					break;
				case AppParameters.OT_EJECUCION:
					state.setImageResource(R.drawable.estadoenejec);
					break;
				case AppParameters.OT_REALIZADA:
					state.setImageResource(R.drawable.estadorecibidos);
					break;
				default:
					state.setImageResource(android.R.color.black);
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
