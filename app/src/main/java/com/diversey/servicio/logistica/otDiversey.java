package com.diversey.servicio.logistica;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.util.ContentConverter;
import jp.sourceforge.qrcode.util.DebugCanvas;

public class otDiversey extends Activity implements OnTouchListener{

	public final static int NUMBER_PICTURES = 3;
	static final int REQUEST_TAKE_PHOTO = 1;
	static final int REQUEST_TAKE_QR = 2;
	public SharedPreferences sp;
	public int flag_pictures = 1;
	//Datos de la OT
	public String string_qr;
	public String nombre_imagen;
	public String fecha_inicio_ejecucion;
	public String string_base64_firma;
	public View v_temp;
	public String tipo_mantencion;
	public String tipo_servicio;
	public String diagnostico;
	public String obsGeneral;
	public String tipoFacturacion;
	public String obsFinales;
	public String horasHombre="";
	public String minutosHombre="";
	public int minutosTrabajados;
	public Button activarBuscar;
	public String rut_receptor="";
	public String nombre_receptor="";
	public String mail_receptor="";
	public String horas_hombre="";
	public String nombreCodigofinal = "";
	public int num_maquinas;
	public String horometro = "";
	public String horasFact = "";
	public String Nombre_de_maquinas="";
	public  UserRecord u;
	public JSONArray json_partes_usadas;
	public String json_maq_back_string;
	public JSONArray json_maquinas = new JSONArray();
	public JSONArray jsonArray_maquinas_back;
	public JSONArray json_imagenes = new JSONArray();
	public JSONArray json_imagenes_qr = new JSONArray();
	public JSONObject json_images_qr = new JSONObject();
	public JSONObject id_maquina;

	public JSONArray series;
	public ArrayList<String> arrayList;
	public String idSerieSelected = "";
	public int idSelected = 0;

	//referencias
	public ImageView imageViewImagen1;
	public boolean listo = false;
	//Datos maquinas
	boolean pulsarBotonEditarPieza = false;
	List<String> list_piezas = new ArrayList<String>();
	ProgressDialog progDailogGenera;
	ProgressBar progresoMapa;
	//public JSONObject json_imagenes = new JSONObject();
	Bitmap bitmap_scaled;
	String mCurrentPhotoPath;
	private Intent diverseyIntent;
	private View headerLayout;
	//public JSONObject json_piezas;
	private View mantenedorLayout;
	private String idOT;
	private JSONObject datosOT;
	private String comentarioGeneral;
	private EditText comentarioGeneralEt;
	private String latitudTecnico,longitudTecnico,idTecnico,descripcion,latitud,longitud;
	private DrawingView dragFirma = null;
	private String pathFirma = "";

	TextView textViewMod;
	TextView textViewID;
	TextView textVieCodigo;
	TextView textVieDescripcion;

	public Typeface fontRegular;
	public Typeface fontLight;
	public Typeface fontBold;


	public static void dumpIntent(Intent i){

	    Bundle bundle = i.getExtras();
	    if (bundle != null) {
	        Set<String> keys = bundle.keySet();
	        Iterator<String> it = keys.iterator();
	        Log.e("key intent","Dumping Intent start");
	        while (it.hasNext()) {
	            String key = it.next();
	            Log.e("key intent","[" + key + "=" + bundle.get(key)+"]");
	        }
	        Log.e("key intent","Dumping Intent end");
	    }
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.orden_trabajo);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		sp = PreferenceManager.getDefaultSharedPreferences(this); //instancia en onCreate
		diverseyIntent = getIntent();

		sp.edit().putString("screen_state", "PRE_OT");

		Log.i("Recibidos:", diverseyIntent.toString());
		Log.i("ENTRA AL ONCREATE:","ONCREATE !!!!!!please!!");

		fontLight = Typeface.createFromAsset(otDiversey.this.getAssets(),
				"BebasNeueLight.ttf");

		fontRegular = Typeface.createFromAsset(otDiversey.this.getAssets(),
				"BebasNeueRegular.ttf");

		fontBold = Typeface.createFromAsset(otDiversey.this.getAssets(),
				"BebasNeueBold.ttf");


		/*styling*/

		//Mapa

		TextView title_mapa_ubicacion = (TextView)findViewById(R.id.title_mapa_ubicacion);
		title_mapa_ubicacion.setTypeface(fontBold);

		TextView title_falla_informada = (TextView)findViewById(R.id.title_falla_informada);
		title_falla_informada.setTypeface(fontBold);

		TextView orden_trabajo_descripcion_caso = (TextView)findViewById(R.id.orden_trabajo_descripcion_caso);
		orden_trabajo_descripcion_caso.setTypeface(fontRegular);

		//Control tecnico
		TextView titulo_insumos_repuestos = (TextView)findViewById(R.id.titulo_insumos_repuestos);
		titulo_insumos_repuestos.setTypeface(fontBold);

		TextView title_diagnostico = (TextView)findViewById(R.id.title_diagnostico);
		title_diagnostico.setTypeface(fontBold);

		TextView title_obs_general = (TextView)findViewById(R.id.title_obs_general);
		title_obs_general.setTypeface(fontBold);

		TextView title_maquinas = (TextView)findViewById(R.id.title_maquinas);
		title_maquinas.setTypeface(fontBold);

		TextView title_detalle = (TextView)findViewById(R.id.title_detalle);
		title_detalle.setTypeface(fontBold);

		TextView title_accesorios = (TextView)findViewById(R.id.title_accesorios);
		title_accesorios.setTypeface(fontBold);

		EditText asdasd_editText = (EditText)findViewById(R.id.asdasd_editText);
		asdasd_editText.setTypeface(fontRegular);

		EditText insumos_numero_editText = (EditText)findViewById(R.id.insumos_numero_editText);
		insumos_numero_editText.setTypeface(fontBold);

		EditText dasdas_editText = (EditText)findViewById(R.id.dasdas_editText);
		dasdas_editText.setTypeface(fontRegular);

		TextView title_trabajo_realizado = (TextView)findViewById(R.id.title_trabajo_realizado);
		title_trabajo_realizado.setTypeface(fontBold);

		//Tipo reparacion

		TextView title_tipo = (TextView)findViewById(R.id.title_tipo);
		title_tipo.setTypeface(fontBold);

		TextView title_hora_hombre = (TextView)findViewById(R.id.title_hora_hombre);
		title_hora_hombre.setTypeface(fontBold);

		EditText horas_hombre_editText_hh = (EditText)findViewById(R.id.horas_hombre_editText_hh);
		horas_hombre_editText_hh.setTypeface(fontRegular);

		EditText minutos_hombre_editText_mm = (EditText)findViewById(R.id.minutos_hombre_editText_mm);
		minutos_hombre_editText_mm.setTypeface(fontRegular);

		TextView title_obs_final = (TextView)findViewById(R.id.title_obs_final);
		title_obs_final.setTypeface(fontBold);

		TextView obs_finales_editText = (TextView)findViewById(R.id.obs_finales_editText);
		obs_finales_editText.setTypeface(fontRegular);


		TextView textView3 = (TextView)findViewById(R.id.textView3);
		textView3.setTypeface(fontBold);

		TextView textView2 = (TextView)findViewById(R.id.textView2);
		textView2.setTypeface(fontBold);

		TextView title_minutos = (TextView)findViewById(R.id.title_minutos);
		title_minutos.setTypeface(fontBold);

		TextView horas_hombre_textview = (TextView)findViewById(R.id.horas_hombre_textview);
		horas_hombre_textview.setTypeface(fontRegular);

		TextView minutos_hombre_textview = (TextView)findViewById(R.id.minutos_hombre_textview);
		minutos_hombre_textview.setTypeface(fontRegular);

		TextView title_obs_finales = (TextView)findViewById(R.id.title_obs_finales);
		title_obs_finales.setTypeface(fontBold);

		TextView obs_finales_textview = (TextView)findViewById(R.id.obs_finales_textview);
		obs_finales_textview.setTypeface(fontRegular);


		//firma

		TextView title_firma = (TextView)findViewById(R.id.title_firma);
		title_firma.setTypeface(fontBold);

		TextView title_name = (TextView)findViewById(R.id.title_name);
		title_name.setTypeface(fontBold);

		EditText firma_nombre_editText = (EditText)findViewById(R.id.firma_nombre_editText);
		firma_nombre_editText.setTypeface(fontRegular);

		EditText firma_rut_editText = (EditText)findViewById(R.id.firma_rut_editText);
		firma_rut_editText.setTypeface(fontRegular);

		EditText firma_mail_editText = (EditText)findViewById(R.id.firma_mail_editText);
		firma_mail_editText.setTypeface(fontRegular);

		TextView firma_nombre_TextView = (TextView)findViewById(R.id.firma_nombre_TextView);
		firma_nombre_TextView.setTypeface(fontRegular);

		TextView title_rut = (TextView)findViewById(R.id.title_rut);
		title_rut.setTypeface(fontRegular);

		TextView firma_rut_TextView = (TextView)findViewById(R.id.firma_rut_TextView);
		firma_rut_TextView.setTypeface(fontRegular);

		TextView title_mail = (TextView)findViewById(R.id.title_mail);
		title_mail.setTypeface(fontBold);

		TextView firma_mail_TextView = (TextView)findViewById(R.id.firma_mail_TextView);
		firma_mail_TextView.setTypeface(fontRegular);

		Button orden_trabajo_generar_reporte = (Button)findViewById(R.id.orden_trabajo_generar_reporte);
		orden_trabajo_generar_reporte.setTypeface(fontBold);


		//Falla informada

		TextView orden_trabajo_descripcion_falla = (TextView)findViewById(R.id.orden_trabajo_descripcion_falla);
		orden_trabajo_descripcion_falla.setTypeface(fontRegular);

		//detalle

		TextView title_actividad = (TextView)findViewById(R.id.title_actividad);
		title_actividad.setTypeface(fontBold);

		TextView actividad_TextView = (TextView)findViewById(R.id.actividad_TextView);
		actividad_TextView.setTypeface(fontRegular);

		TextView title_tipo_Servicio = (TextView)findViewById(R.id.title_tipo_Servicio);
		title_tipo_Servicio.setTypeface(fontBold);

		TextView tipo_servicio_TextView = (TextView)findViewById(R.id.tipo_servicio_TextView);
		tipo_servicio_TextView.setTypeface(fontRegular);

		TextView title_detalle_big = (TextView)findViewById(R.id.title_detalle_big);
		title_detalle_big.setTypeface(fontBold);

		Button orden_trabajo_continuar = (Button)findViewById(R.id.orden_trabajo_continuar);
		orden_trabajo_continuar.setTypeface(fontBold);



		//Mantenedor

		//TextView title_mantencion = (TextView)findViewById(R.id.title_mantencion);
		//title_mantencion.setTypeface(fontBold);

		//TextView title_servicio2 = (TextView)findViewById(R.id.title_servicio2);
		//title_servicio2.setTypeface(fontBold);

		Spinner orden_trabajo_tipo_mantencion = (Spinner)findViewById(R.id.orden_trabajo_tipo_mantencion);
		Spinner orden_trabajo_tipo_servicio = (Spinner)findViewById(R.id.orden_trabajo_tipo_servicio);

		/*END styling*/

		imageViewImagen1 = (ImageView) findViewById(R.id.orden_trabajo_imageview_img1);
		ImageView staticMap = (ImageView) findViewById(R.id.orden_trabajo_mapa_empresa);
		staticMap.requestFocus();
		String lat = diverseyIntent.getStringExtra("latitud");
		String lon = diverseyIntent.getStringExtra("longitud");
		progresoMapa = (ProgressBar)findViewById(R.id.cargadorMapa);
		/*try {
			json_maquinas_back = new JSONObject(diverseyIntent.getStringExtra("json_maquinas").toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		latitud = diverseyIntent.getStringExtra("latitud");
		longitud = diverseyIntent.getStringExtra("longitud");
		if(isNetworkAvailable()){
			String url = "http://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+"&zoom=15&size=360x198&scale=2&maptype=roadmap&markers=icon:"+AppParameters.pin+"%7C"+lat+","+lon+"&sensor=true";


			Picasso.with(otDiversey.this).load(url).skipMemoryCache().into(staticMap, new Callback() {

				public void onSuccess() {
					// TODO Auto-generated method stub
					progresoMapa.setVisibility(View.GONE);
				}

				public void onError() {
					// TODO Auto-generated method stub
				}
			});

		}
		else{
			staticMap.setBackgroundColor(Color.GRAY);
		}

		json_maq_back_string = diverseyIntent.getStringExtra("json_maquinas");

		//PicassoTools.clearCache(Picasso.with(otDiversey.this));
		try {
			Log.i("Crear Json maq OT", json_maq_back_string);
			jsonArray_maquinas_back = new JSONArray(json_maq_back_string);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		idOT = diverseyIntent.getStringExtra("idOT");
		longitudTecnico = diverseyIntent.getStringExtra("longitud_tecnico");
		latitudTecnico = diverseyIntent.getStringExtra("latitud_tecnico");
		descripcion = diverseyIntent.getStringExtra("descripcion");

		datosOT = new JSONObject();

		TextView descripcionTvw = (TextView)findViewById(R.id.orden_trabajo_descripcion_caso);
		descripcionTvw.setText(descripcion);

		String tipoOT =  diverseyIntent.getStringExtra("tipo_ot");

		Log.d("LogOtEstado",tipoOT);

		if(tipoOT.equals("2")){//2 pendiente
			staticMap.requestFocus();
			completeMantenedor(R.id.layout_orden_trabajo_detalle);

			staticMap.requestFocus();

			LinearLayout controlTecnico = (LinearLayout)findViewById(R.id.orden_trabajo_control_tecnico);
			controlTecnico.setVisibility(View.GONE);

			LinearLayout tipoReparacion = (LinearLayout)findViewById(R.id.orden_trabajo_tipo_reparacion);
			tipoReparacion.setVisibility(View.GONE);

			LinearLayout firmaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_firma_layout);
			firmaLayout.setVisibility(View.GONE);

			LinearLayout resumenLayout = (LinearLayout)findViewById(R.id.orden_trabajo_vista_resumen);
			resumenLayout.setVisibility(View.GONE);

			num_maquinas = jsonArray_maquinas_back.length();
			Log.i("NUMERO MAQ", "N maq : "+ Integer.toString(num_maquinas));
			for(int i=0; i < num_maquinas; i++){

				JSONObject maquina_i;
				String nombre_modelo = "";
				String id_modelo = "";
				String codigo_maquina= "";
				String serie = "";
				String series = "";
				String descripcion = "";

				try {
					maquina_i = (JSONObject) jsonArray_maquinas_back.get(i);
					nombre_modelo = maquina_i.getString("nombre");
					if(i==0){
						Nombre_de_maquinas = nombre_modelo;
					}else{
						Nombre_de_maquinas = Nombre_de_maquinas+", "+nombre_modelo;
					}
					id_modelo  = maquina_i.getString("id");
					codigo_maquina = maquina_i.getString("codigo");
					serie = maquina_i.getString("serie");
					series = maquina_i.getString("series");
					descripcion= maquina_i.getString("descripcion");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				completeMaquinas(nombre_modelo,id_modelo,codigo_maquina,serie,series,descripcion);
				//completeMaquinas(nombre_modelo,id_modelo,codigo_maquina);

			}

			LinearLayout layoutMaquinas = (LinearLayout)findViewById(R.id.orden_trabajo_maquinas);
			layoutMaquinas.setVisibility(View.GONE);

			LinearLayout layoutDetalle = (LinearLayout)findViewById(R.id.orden_trabajo_detalle2);
			layoutDetalle.setVisibility(View.GONE);

			dragFirma = (DrawingView)findViewById(R.id.orden_trabajo_firma);
			staticMap.requestFocus();
		}


		else if(tipoOT.equals("4")){//4 realizada
			completeRealizado(R.id.layout_orden_trabajo_detalle);

			LinearLayout higieneComentarios = (LinearLayout)findViewById(R.id.orden_trabajo_comentarios);
			higieneComentarios.setVisibility(View.GONE);

			LinearLayout controlTecnico = (LinearLayout)findViewById(R.id.orden_trabajo_control_tecnico);
			controlTecnico.setVisibility(View.GONE);

			LinearLayout tipoReparacion = (LinearLayout)findViewById(R.id.orden_trabajo_tipo_reparacion);
			tipoReparacion.setVisibility(View.GONE);

			LinearLayout firmaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_firma_layout);
			firmaLayout.setVisibility(View.GONE);

			LinearLayout layoutMaquinas = (LinearLayout)findViewById(R.id.orden_trabajo_maquinas);
			layoutMaquinas.setVisibility(View.GONE);

			LinearLayout layoutDetalle = (LinearLayout)findViewById(R.id.orden_trabajo_detalle);
			layoutDetalle.setVisibility(View.GONE);

			RelativeLayout mapaLayout = (RelativeLayout)findViewById(R.id.orden_trabajo_mapa_layout);
			mapaLayout.setVisibility(View.GONE);

			LinearLayout fallaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_falla_layout);
			fallaLayout.setVisibility(View.GONE);

			LinearLayout detalleLayout = (LinearLayout)findViewById(R.id.orden_trabajo_detalle);
			detalleLayout.setVisibility(View.GONE);

			LinearLayout detalle2Layout = (LinearLayout)findViewById(R.id.orden_trabajo_detalle2);
			detalle2Layout.setVisibility(View.GONE);

			LinearLayout comentLayout = (LinearLayout)findViewById(R.id.orden_trabajo_comentarios);
			comentLayout.setVisibility(View.GONE);

			LinearLayout vistaResumenLayout = (LinearLayout)findViewById(R.id.orden_trabajo_vista_resumen);
			vistaResumenLayout.setVisibility(View.VISIBLE);

			//********************completar datos
			//referencias
			TextView textViewActividadServicio = (TextView)findViewById(R.id.actividad_TextView);
			TextView textViewTipoServicio = (TextView)findViewById(R.id.tipo_servicio_TextView);
			TextView textViewDescFalla = (TextView)findViewById(R.id.orden_trabajo_descripcion_falla);
			TextView textViewDiagnostico = (TextView)findViewById(R.id.diagnostico_TextView);
			TextView textViewObsGeneral = (TextView)findViewById(R.id.observacion_gral_TextView);
			TextView textViewHorasHombre = (TextView)findViewById(R.id.horas_hombre_textview);
			TextView textViewMinutosHombre = (TextView)findViewById(R.id.minutos_hombre_textview);
			TextView textViewObsFinales = (TextView)findViewById(R.id.obs_finales_textview);
			TextView textViewFirmaNombre = (TextView)findViewById(R.id.firma_nombre_TextView);
			TextView textViewFirmaMail = (TextView)findViewById(R.id.firma_mail_TextView);
			TextView textViewFirmaRut = (TextView)findViewById(R.id.firma_rut_TextView);

			//completar datos
			textViewActividadServicio.setText(diverseyIntent.getStringExtra("tipo_mantencion").toString());
			textViewTipoServicio.setText(diverseyIntent.getStringExtra("tipo_servicio").toString());
			textViewDescFalla.setText(diverseyIntent.getStringExtra("descripcion").toString());
			textViewDiagnostico.setText(diverseyIntent.getStringExtra("diagnostico").toString());
			textViewObsGeneral.setText(diverseyIntent.getStringExtra("obs_general").toString());

			// transformar los minutos
			int datos_tiempo_obtenido = Integer.parseInt(diverseyIntent.getStringExtra("horas_hombre").toString());
			int minuto = datos_tiempo_obtenido%60 ;
			int hora = datos_tiempo_obtenido/60;
			textViewHorasHombre.setText(Integer.toString(hora));
			textViewMinutosHombre.setText(Integer.toString(minuto));
			textViewObsFinales.setText(diverseyIntent.getStringExtra("obs_finales").toString());
			textViewFirmaNombre.setText(diverseyIntent.getStringExtra("firma_nombre").toString());
			textViewFirmaMail.setText(diverseyIntent.getStringExtra("firma_mail").toString());
			textViewFirmaRut.setText(diverseyIntent.getStringExtra("firma_rut").toString());

			//completar maquinas
			try {
				json_partes_usadas = new JSONArray(diverseyIntent.getStringExtra("partes_usadas"));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int numItems = json_partes_usadas.length();

			for (int i=0;i<numItems;i++){

				String nombre_maquina_temp="";
				String id_maquina_temp="";
				String string_piezas_temp="";
				String string_qr_temp="";

				JSONObject json_maq_temp  = new JSONObject();
				JSONArray json_array_piezas = new JSONArray();
				try {
					json_maq_temp = (JSONObject) json_partes_usadas.get(i);

					nombre_maquina_temp=json_maq_temp.getString("modelo_maquina");

					id_maquina_temp = json_maq_temp.getString("id_maquina");
					if(i==0){
						Nombre_de_maquinas = nombre_maquina_temp;
					}else{
						Nombre_de_maquinas = Nombre_de_maquinas+", "+nombre_maquina_temp;
					}
					string_piezas_temp = json_maq_temp.getString("detalle");

					string_qr_temp=json_maq_temp.getString("string_qr");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					json_array_piezas = new JSONArray(string_piezas_temp);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				completeMaquinasResumen(nombre_maquina_temp, id_maquina_temp, string_qr_temp, json_array_piezas);
			}

		}
		completeHeader(R.id.layout_orden_trabajo_header);
	}

	private void completeHeader(int layoutRootId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		headerLayout = inflater.inflate(R.layout.orden_trabajo_header,(ViewGroup) findViewById(layoutRootId));
		headerLayout.setVisibility(View.VISIBLE);

		String nombreSucursal = diverseyIntent.getStringExtra("idOT") + ". " +diverseyIntent.getStringExtra("nombreOT");
		String cliente = diverseyIntent.getStringExtra("cliente");
		String fecha_realizacion = diverseyIntent.getStringExtra("fecha_realizacion");
		String repLegal = diverseyIntent.getStringExtra("nombre_contacto");
		String rut = diverseyIntent.getStringExtra("rut");
		String direccion = diverseyIntent.getStringExtra("direccion");

		TextView nomSucTvw = (TextView)headerLayout.findViewById(R.id.orden_trabajo_nombre_sucursal);
		TextView clienteTvw = (TextView)headerLayout.findViewById(R.id.orden_trabajo_cliente_caso);
		TextView fechaRealizTvw = (TextView)headerLayout.findViewById(R.id.orden_trabajo_fecha_realizacion_head);
		TextView repLegalTvw = (TextView)headerLayout.findViewById(R.id.orden_trabajo_representante_legal);
		TextView nombreMaquina = (TextView)headerLayout.findViewById(R.id.orden_trabajo_maquina_head);

		TextView title_site = (TextView)headerLayout.findViewById(R.id.title_site);
		TextView title_fecha = (TextView)headerLayout.findViewById(R.id.title_fecha);
		TextView title_maquina = (TextView)headerLayout.findViewById(R.id.title_maquina);

		title_site.setTypeface(fontBold);
		title_fecha.setTypeface(fontBold);
		title_maquina.setTypeface(fontBold);

		nomSucTvw.setText(nombreSucursal);
		nomSucTvw.setTypeface(fontBold);
		clienteTvw.setText(cliente);
		clienteTvw.setTypeface(fontRegular);
		fechaRealizTvw.setText(fecha_realizacion);
		fechaRealizTvw.setTypeface(fontRegular);
		repLegalTvw.setText(repLegal);
		repLegalTvw.setTypeface(fontRegular);
		nombreMaquina.setText(Nombre_de_maquinas);
		nombreMaquina.setTypeface(fontRegular);
		//rutTvw.setText(rut);
		//direccionTvw.setText(direccion);
	}

	private void completeMantenedor(int layoutRootId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mantenedorLayout = inflater.inflate(R.layout.orden_trabajo_mantenedor,(ViewGroup) findViewById(layoutRootId));
		mantenedorLayout.setVisibility(View.VISIBLE);

		String modelo_maquina = diverseyIntent.getStringExtra("modelo_maquina");
		String nro_serie = diverseyIntent.getStringExtra("nro_serie");
		String cantidad = diverseyIntent.getStringExtra("cantidad");
		String codigo = diverseyIntent.getStringExtra("codigo");

		String tipo_mantencion = diverseyIntent.getStringExtra("tipo_mantencion");
		String tipo_servicio = diverseyIntent.getStringExtra("tipo_servicio");

		//TextView tipoServicioTvw = (TextView)mantenedorLayout.findViewById(R.id.actividad_servicio_textview);
		//TextView tipoMantencionTvw = (TextView)mantenedorLayout.findViewById(R.id.tipo_facturacion_textview);

		TextView modelomaquinaTvw = (TextView)mantenedorLayout.findViewById(R.id.orden_trabajo_maquina_modelo);
		TextView nroSerieTvw = (TextView)mantenedorLayout.findViewById(R.id.orden_trabajo_maquina_serie);
		TextView cantidadTvw = (TextView)mantenedorLayout.findViewById(R.id.orden_trabajo_cantidad);
		TextView codigoTvw = (TextView)mantenedorLayout.findViewById(R.id.orden_trabajo_codigo);

		//tipoServicioTvw.setText(tipo_servicio);
		//tipoMantencionTvw.setText(tipo_mantencion);

		modelomaquinaTvw.setText(modelo_maquina);
		nroSerieTvw.setText(nro_serie);
		cantidadTvw.setText(cantidad);
		codigoTvw.setText(codigo);

		List<String> list = new ArrayList<String>();
		list.add("Correctivo");
		list.add("Preventivo");
		list.add("Garantia");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(inflater.getContext(),R.layout.item_spinner, list);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		TextView title_mantencion = (TextView)mantenedorLayout.findViewById(R.id.title_mantencion);
		title_mantencion.setTypeface(fontBold);

		TextView title_servicio2 = (TextView)mantenedorLayout.findViewById(R.id.title_servicio2);
		title_servicio2.setTypeface(fontBold);

		Spinner spinServicio = (Spinner) findViewById(R.id.orden_trabajo_tipo_mantencion);
		spinServicio.setAdapter(dataAdapter);


		List<String> list1 = new ArrayList<String>();
		list1.add("Facturado");
		list1.add("Garantia");

		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(inflater.getContext(),R.layout.item_spinner, list1);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		Spinner spinTipoServicio = (Spinner) findViewById(R.id.orden_trabajo_tipo_servicio);
		spinTipoServicio.setAdapter(dataAdapter1);

	}

	private void completeRealizado(int layoutRootId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mantenedorLayout = inflater.inflate(R.layout.orden_trabajo_realizado,(ViewGroup) findViewById(layoutRootId));
		mantenedorLayout.setVisibility(View.VISIBLE);

		TextView comentarioRealizadoView = (TextView)mantenedorLayout.findViewById(R.id.orden_trabajo_comentario_realizado);

		String comentarios = diverseyIntent.getStringExtra("comentarios");

		comentarioRealizadoView.setText(comentarios);
	}

	public void completeMaquinas (String nombre_modelo, String id_modelo, String codigo_maquina, String serie_maquina,String series_maquina, String descripcion_maquina){

		ViewGroup layout = (ViewGroup) findViewById(R.id.orden_trabajo_contenedor_maquinas);//layout padre de los productos
		LayoutInflater inflater = LayoutInflater.from(this);//layout de tipo inflater

		int id = R.layout.orden_trabajo_maquina_dinamica;//layout del producto a agregar

		final LinearLayout mylinear = (LinearLayout) inflater.inflate(id, null, false);
		//RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mylinear.setLayoutParams(params);
		//mylinear.setId(numeroID);

		//Button boton =(Button) mylinear.findViewById(R.id.btn_agregar_lote);
		//boton.setId(idBtn + 10);

		//Log.i("Id boton",Integer.toString(boton.getId()));


		TextView title_modelo_maquina = (TextView) mylinear.findViewById(R.id.title_modelo_maquina);
		title_modelo_maquina.setTypeface(fontBold);

		TextView title_codigo = (TextView) mylinear.findViewById(R.id.title_codigo);
		title_codigo.setTypeface(fontBold);

		TextView title_serie = (TextView) mylinear.findViewById(R.id.title_serie);
		title_serie.setTypeface(fontBold);

		TextView title_descripcion = (TextView) mylinear.findViewById(R.id.title_descripcion);
		title_descripcion.setTypeface(fontBold);

		textViewMod = (TextView) mylinear.findViewById(R.id.textview_modelo_maquina);
		textViewMod.setText(nombre_modelo);
		textViewMod.setTypeface(fontRegular);

		textViewID = (TextView) mylinear.findViewById(R.id.id_maquina_hidden);
		textViewID.setText(id_modelo);

		textVieCodigo = (TextView)mylinear.findViewById(R.id.textview_campos_codigo);
		textVieCodigo.setText(codigo_maquina);
		textVieCodigo.setTypeface(fontRegular);

		try {
			series = new JSONArray(series_maquina);
			arrayList = new ArrayList<String>();

			for(int i=0; i<series.length();i++){
				JSONObject obj = series.getJSONObject(i);
				arrayList.add(obj.getString("serie"));
				Log.d("SerieJSON", obj.getString("serie") + " = " + serie_maquina + " ---->" + obj.getString("id"));
				if(serie_maquina.equals(obj.getString("serie"))){idSelected = i; idSerieSelected = obj.getString("serie");
				}
				Log.d("SerieJSON", idSerieSelected);
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(inflater.getContext(),R.layout.item_spinner_maquinas, arrayList);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			final Spinner spinSerieMaquina = (Spinner) mylinear.findViewById(R.id.spinner_serie);
			spinSerieMaquina.setAdapter(dataAdapter);
			spinSerieMaquina.setSelection(idSelected);
			spinSerieMaquina.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
					try {
						JSONObject o = series.getJSONObject(i);
						idSerieSelected = o.getString("serie");
						textViewMod = (TextView) mylinear.findViewById(R.id.textview_modelo_maquina);
						textViewMod.setText(o.getString("nombre"));
						textVieDescripcion = (TextView)mylinear.findViewById(R.id.textview_descripcion);
						textVieDescripcion.setText(o.getString("descripcion"));
						textViewID = (TextView) mylinear.findViewById(R.id.id_maquina_hidden);
						textViewID.setText(o.getString("id"));
						textVieCodigo = (TextView)mylinear.findViewById(R.id.textview_campos_codigo);
						textVieCodigo.setText(o.getString("codigo"));
						Log.d("SeriesSelected", idSerieSelected);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				public void onNothingSelected(AdapterView<?> adapterView) {
					return;
				}
			});


		} catch (JSONException e) {
			e.printStackTrace();
		}

		TextView textview_descripcion = (TextView)mylinear.findViewById(R.id.textview_descripcion);
		textview_descripcion.setText(descripcion_maquina);
		textview_descripcion.setTypeface(fontRegular);

		TextView title_captura_imagen = (TextView)mylinear.findViewById(R.id.title_captura_imagen);
		title_captura_imagen.setTypeface(fontBold);

		Button tomar_imagen = (Button)mylinear.findViewById(R.id.tomar_imagen);
		tomar_imagen.setTypeface(fontBold);

		TextView title_qr = (TextView)mylinear.findViewById(R.id.title_qr);
		title_qr.setTypeface(fontBold);

		Button tomar_qr = (Button)mylinear.findViewById(R.id.tomar_qr);
		tomar_qr.setTypeface(fontBold);

		TextView textView_string_qr = (TextView)mylinear.findViewById(R.id.textView_string_qr);
		textView_string_qr.setTypeface(fontRegular);

		//agregar pieza
		Button button1 = (Button)mylinear.findViewById(R.id.button1);
		button1.setTypeface(fontBold);

		layout.addView(mylinear);
	}

	public void completeMaquinasResumen (String nombre_modelo, String id_modelo, String string_qr, JSONArray partes_usadas){

		ViewGroup layout = (ViewGroup) findViewById(R.id.orden_trabajo_contenedor_maquinas_resumen);//layout padre de los productos
		LayoutInflater inflater = LayoutInflater.from(this);//layout de tipo inflater

		int id = R.layout.orden_trabajo_maquinas_resumen;//layout del producto a agregar

		LinearLayout mylinear = (LinearLayout) inflater.inflate(id, null, false);
		//RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(id, null, false);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		mylinear.setLayoutParams(params);

		TextView textViewMod = (TextView) mylinear.findViewById(R.id.textview_modelo_maquina_resumen);
		textViewMod.setText(nombre_modelo);



		//Agregar las piezas de la maquina

		int numItemsPartes = partes_usadas.length();

		for(int i=0; i < numItemsPartes; i++){

			JSONObject json_pieza_temp= new JSONObject();
			LinearLayout linearContenedor_piezas= (LinearLayout) mylinear.findViewById(R.id.orden_trabajo_piezas_dinamicas_resumen);

			int id2 = R.layout.orden_trabajo_pieza_parte_resumen;//layout del producto a agregar

			LinearLayout mylinearPieza = (LinearLayout) inflater.inflate(id2, null, false);

			LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			params2.topMargin = 15;
			params2.gravity = Gravity.RIGHT;
			//params.height = 50;
			mylinear.setLayoutParams(params2);
			//spinner


			//int id_maq = Integer.parseInt(textViewIdMaq.getText().toString());
			try {
				json_pieza_temp = partes_usadas.getJSONObject(i);

				TextView textViewNombrePieza = (TextView)mylinearPieza.findViewById(R.id.textViewNombrePieza);
				textViewNombrePieza.setText(json_pieza_temp.getString("nombre_parte"));

				TextView textViewcodigo = (TextView)mylinearPieza.findViewById(R.id.textViewCodigo);
				textViewcodigo.setText(json_pieza_temp.getString("codigo_parte"));

				TextView textViewCantidad = (TextView)mylinearPieza.findViewById(R.id.textViewCantidad);
				textViewCantidad.setText(json_pieza_temp.getString("cantidad_usada"));

				TextView textViewPrecio = (TextView)mylinearPieza.findViewById(R.id.textViewPrecio);
				textViewPrecio.setText(json_pieza_temp.getString("precio_parte"));

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


			//se agrega la pieza al contenedor de piezas de la maquina

			linearContenedor_piezas.addView(mylinearPieza);

		}

		/*TextView textViewID = (TextView) mylinear.findViewById(R.id.id_maquina_hidden);
		textViewID.setText(id_modelo);*/

		layout.addView(mylinear);
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onClick(View v){
		switch(v.getId()){
		case R.id.button_back_listmap:
			super.onBackPressed();
			break;
		case R.id.orden_volver:
			super.onBackPressed();
			break;
		case R.id.orden_trabajo_generar_reporte:
			//comentarioGeneral = comentarioGeneralEt.getText().toString();

			PreguntaAccion("Aviso","�Esta Seguro que desea generar el reporte?");

			//setResult(Main.CASO_HIGIENE);

			//finishActivity(Main.CASO_HIGIENE);
			//finish();
			break;


			case R.id.orden_trabajo_continuar:


			sp.edit().putString("screen_state", "WORK_OT").commit();

			SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
			fecha_inicio_ejecucion = s.format(new Date());

			LinearLayout controlTecnico = (LinearLayout)findViewById(R.id.orden_trabajo_control_tecnico);
			controlTecnico.setVisibility(View.VISIBLE);

			LinearLayout tipoReparacion = (LinearLayout)findViewById(R.id.orden_trabajo_tipo_reparacion);
			tipoReparacion.setVisibility(View.VISIBLE);

			LinearLayout firmaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_firma_layout);
			firmaLayout.setVisibility(View.VISIBLE);

			LinearLayout layoutMaquinas = (LinearLayout)findViewById(R.id.orden_trabajo_maquinas);
			layoutMaquinas.setVisibility(View.VISIBLE);

			LinearLayout layoutDetalle = (LinearLayout)findViewById(R.id.orden_trabajo_detalle);
			layoutDetalle.setVisibility(View.VISIBLE);



			RelativeLayout mapaLayout = (RelativeLayout)findViewById(R.id.orden_trabajo_mapa_layout);
			mapaLayout.setVisibility(View.GONE);

			LinearLayout fallaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_falla_layout);
			fallaLayout.setVisibility(View.GONE);

			LinearLayout detalleLayout = (LinearLayout)findViewById(R.id.orden_trabajo_detalle);
			detalleLayout.setVisibility(View.GONE);

			LinearLayout comentLayout = (LinearLayout)findViewById(R.id.orden_trabajo_comentarios);
			comentLayout.setVisibility(View.GONE);

			break;





		case R.id.orden_trabajo_borrar_firma:
			if(dragFirma!=null)
				dragFirma.clearScreen();
			break;
		case R.id.tomar_imagen:
			//open();
			dispatchTakePictureIntent();
			v_temp = v;
			Log.i("Intent camra","dispatchTakePictureIntent()");
			break;

		case R.id.tomar_qr:
			v_temp = v;
			dispatchTakeQrIntent();
			break;
		}
	}

	public void onClickMapsIntent(View v){

		/*

		 * The Possible Query params options are the following:
		 *
		 * Show map at location: geo:latitude,longitude
		 * Show zoomed map at location: geo:latitude,longitude?z=zoom
		 * Show map at locaiton with point: geo:0,0?q=my+street+address
		 * Show map of businesses in area: geo:0,0?q=business+near+city
		 *
		 */

		//Dir viña
		//		double latitude = -33.022298;
		//		double longitude = -71.545741;
		String zoom = "?z=16";
		String business=",?q=business+near+city";

		//String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
		//String uriString = "http://maps.google.com/maps?saddr=20.344,34.34&daddr=20.5666,45.345";
		//String uriString2= "http://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&maptype=satellite&sensor=true_or_false";

		//"http://maps.google.com/maps?q="-33.022298,-71.545741(labelLocation)&iwloc=A&hl=es

		String uriString4 = "http://maps.google.com/maps?q=-33.022298,-71.545741(labelLocation)&iwloc=A&hl=es";

		String label = "Cinnamon & Toast";
		String uriBegin = "geo:12,34";
		String query = "12,34(" + " "+label + ")";
		String encodedQuery = Uri.encode( query  );
		String uriString = uriBegin + "?q=" + encodedQuery;
		Uri uri = Uri.parse( uriString );
		Uri uri4 = Uri.parse( uriString4 );
		//pruebas de uri
		//String geoUriString="geo:"+latitude+","+longitude+"?q=("")@"+latitude+","+longitude;

		String uriString3 = "geo:"+ latitud + "," + longitud + zoom;

		String uriString2 ="geo:"+ latitud + "," + longitud + "?q="+latitud + "," + longitud +"(Empresa objetivo)";
		Uri uri2 = Uri.parse(uriString2);

		Log.i("MAPS", "URISTRING = "+uriString2 );
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri2);
		startActivity(intent);
	}

	public void agregarPiezas(View v){

		int i; //indice maquinas
		int x;//indice partes

		final List<String> list12 = new ArrayList<String>();
		final List<String> listCodigo = new ArrayList<String>();

		LayoutInflater inflater = LayoutInflater.from(this);//layout de tipo inflater

		LinearLayout contenedorMaquina = (LinearLayout) v.getParent();//linearlayout de la maquina

		LinearLayout contenedorPiezas = (LinearLayout) contenedorMaquina.findViewById(R.id.orden_trabajo_piezas_dinamicas);//del contenedor de maquinas, se obtiene el contendor de piezas

		int id = R.layout.orden_trabajo_pieza_parte_item;//layout del producto a agregar

		LinearLayout mylinear = (LinearLayout) inflater.inflate(id, null, false);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.topMargin = 15;
		params.gravity = Gravity.RIGHT;
		//params.height = 50;
		mylinear.setLayoutParams(params);
		//spinner

		TextView textViewIdMaq = (TextView)contenedorMaquina.findViewById(R.id.id_maquina_hidden);

		//int id_maq = Integer.parseInt(textViewIdMaq.getText().toString());
		String id_maq = textViewIdMaq.getText().toString();

		for(i = 0; i < jsonArray_maquinas_back.length(); i++){
			try {
				String value = jsonArray_maquinas_back.getJSONObject(i).getString("id");
				if(id_maq.equals(value)){
					JSONArray json_array_partes = jsonArray_maquinas_back.getJSONObject(i).getJSONArray("json_piezas");
					if(json_array_partes.length()== 0){
						list12.add("vacio");
						listCodigo.add("vacio");
					}
					Log.i("LIST ADD",json_array_partes.toString());

					for(x = 0; x < json_array_partes.length(); x++){
						String nombre_parte = json_array_partes.getJSONObject(x).getString("nombre");
						String codigo_parte = json_array_partes.getJSONObject(x).getString("codigo");
						Log.i("LIST ADD",nombre_parte);
						list12.add(nombre_parte);
						listCodigo.add(codigo_parte);
					}
					break;
				}
				else Log.i("LIST ADD","No id "+value+"="+id_maq);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(inflater.getContext(),R.layout.item_spinner_piezas, list12);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(inflater.getContext(),R.layout.item_spinner_piezas, listCodigo);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final Spinner spinNombrePieza = (Spinner) mylinear.findViewById(R.id.orden_trabajo_spinner_nombre);
		spinNombrePieza.setAdapter(dataAdapter1);

		final TextView textoPrecio = (TextView)mylinear.findViewById(R.id.textViewPrecio);
		final AutoCompleteTextView spinCodigoPieza = (AutoCompleteTextView) mylinear.findViewById(R.id.orden_trabajo_autoCompletado_codigo);
		spinCodigoPieza.setAdapter(dataAdapter2);
		spinCodigoPieza.setEnabled(false);

		activarBuscar = (Button)mylinear.findViewById(R.id.orden_trabajo_boton_busqueda);

		spinCodigoPieza.setOnFocusChangeListener(new OnFocusChangeListener() {

			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if(!(hasFocus)){

					if((!(listCodigo.contains(spinCodigoPieza.getText().toString()))) && (!list12.contains("vacio"))){
						//Toast.makeText(getBaseContext(), "No se encuentra el codigo ", Toast.LENGTH_LONG).show();
						MensajeCodigoBlanco();
						activarBuscar.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_busqueda_active));
						spinCodigoPieza.setFocusable(true);
						spinCodigoPieza.setEnabled(true);
					}
				}
			}
		});


		activarBuscar.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_busqueda_inactive));
		activarBuscar.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if(pulsarBotonEditarPieza){
					spinCodigoPieza.setEnabled(false);
					activarBuscar.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_busqueda_inactive));
					pulsarBotonEditarPieza = false;

				}else{
					spinCodigoPieza.setEnabled(true);
					activarBuscar.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_busqueda_active));
					pulsarBotonEditarPieza = true;
					spinCodigoPieza.setText("");
				}


			}
		});

		if(list12.contains("vacio")){
			activarBuscar.setVisibility(View.GONE);
		}else{
			activarBuscar.setVisibility(View.VISIBLE);
		}


		spinCodigoPieza.setOnItemClickListener(new OnItemClickListener() {


     public void onItemClick(AdapterView<?> arg0, View parentView, int arg2, long arg3) {

    	 Toast.makeText(getBaseContext(), arg0.getItemAtPosition(arg2)+" y position "+ Integer.toString(arg2), Toast.LENGTH_LONG).show();
    	 nombreCodigofinal = arg0.getItemAtPosition(arg2).toString();
		spinCodigoPieza.setEnabled(false);
		activarBuscar.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_busqueda_inactive));
		Log.i("Codigo seleccionado", nombreCodigofinal);
		String nombre_seleccionado = "";
		String precio_seleccionado = "";
		String id_seleccionado = "";
		int posicionListaCodigo = 0;
		for(int i=0;i<listCodigo.size();i++){
			if(listCodigo.get(i).toString().equals(nombreCodigofinal)){
				posicionListaCodigo = i;
			}
		}


		for(int i=0; i < num_maquinas; i++){
			try {
				JSONArray json_array_partes_temp = jsonArray_maquinas_back.getJSONObject(i).getJSONArray("json_piezas");
				Log.i("json_array_partes_temp .TOSTRING", json_array_partes_temp.toString());
				int num_part = json_array_partes_temp.length();
				Log.i("NUM MAQ:NUM PART",Integer.toString(num_maquinas)+ " : " + Integer.toString(num_part));

				for(int x=0; x < num_part; x++){

					String nombre_codigo = "";
					nombre_codigo = (String)json_array_partes_temp.getJSONObject(x).getString("codigo").toString();
					nombre_codigo= nombre_codigo.toLowerCase() + " ";
					Log.i("FOR x"," pieza_seleccionada : " + nombreCodigofinal+"/" + "  " + "nompre_pieza : " + nombre_codigo+"/" );

					if(nombre_codigo.equalsIgnoreCase(nombreCodigofinal)){
						nombre_seleccionado = json_array_partes_temp.getJSONObject(x).getString("nombre");
						Log.i("CODIGO SELECCIONADO", nombre_seleccionado+ "null");
						precio_seleccionado = json_array_partes_temp.getJSONObject(x).getString("precio_unitario");
						id_seleccionado = json_array_partes_temp.getJSONObject(x).getString("id");
					}

				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		//LinearLayout piezaly =  (LinearLayout)parentView.getParent().getParent();
		//LinearLayout piezalyy =  (LinearLayout)parentView.getParent().getParent().getParent();
		/*
		AutoCompleteTextView spinerviewCodigo = (AutoCompleteTextView)piezaly.findViewById(R.id.orden_trabajo_autoCompletado_codigo);
		TextView textviewPrecio = (TextView)piezaly.findViewById(R.id.textViewPrecio);
		Spinner spinerNombre = (Spinner)piezaly.findViewById(R.id.orden_trabajo_spinner_nombre);
		TextView textviewIdPieza = (TextView)piezalyy.findViewById(R.id.id_pieza_hidden);
		*/
		spinNombrePieza.setSelection(posicionListaCodigo);
		textoPrecio.setText(precio_seleccionado);
		//textviewIdPieza.setText(id_seleccionado);
		//spinerviewCodigo.setText(codigo_seleccionado);

		//textviewCodigo.setText(codigo_seleccionado);
		//textviewPrecio.setText(precio_seleccionado);

			            }

			        });


		spinNombrePieza.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parentView, View selectedItemView,
					int position, long id) {
				//Toast.makeText(parentView.getContext(), "Has seleccionado " +
					//	parentView.getItemAtPosition(position).toString(), Toast.LENGTH_LONG).show();

				//String pieza_seleccionada = parentView.getItemAtPosition(position).toString();
				String pieza_seleccionada = "";
				pieza_seleccionada = (String)((TextView)selectedItemView).getText().toString();
				pieza_seleccionada = pieza_seleccionada.toLowerCase() + " ";

				String codigo_seleccionado = "";
				String precio_seleccionado = "";
				String id_seleccionado = "";


				for(int i=0; i < num_maquinas; i++){
					try {
						JSONArray json_array_partes_temp = jsonArray_maquinas_back.getJSONObject(i).getJSONArray("json_piezas");
						Log.i("json_array_partes_temp .TOSTRING", json_array_partes_temp.toString());
						int num_part = json_array_partes_temp.length();
						Log.i("NUM MAQ:NUM PART",Integer.toString(num_maquinas)+ " : " + Integer.toString(num_part));

						for(int x=0; x < num_part; x++){

							String nombre_pieza = "";
							nombre_pieza = (String)json_array_partes_temp.getJSONObject(x).getString("nombre").toString();
							nombre_pieza= nombre_pieza.toLowerCase() + " ";
							Log.i("FOR x"," pieza_seleccionada : " + pieza_seleccionada+"/" + "  " + "nompre_pieza : " + nombre_pieza+"/" );

							if(nombre_pieza.equalsIgnoreCase(pieza_seleccionada)){
								codigo_seleccionado = json_array_partes_temp.getJSONObject(x).getString("codigo");
								Log.i("CODIGO SELECCIONADO", codigo_seleccionado+ "null");
								precio_seleccionado = json_array_partes_temp.getJSONObject(x).getString("precio_unitario");
								id_seleccionado = json_array_partes_temp.getJSONObject(x).getString("id");
							}

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}


				LinearLayout piezaly =  (LinearLayout)parentView.getParent().getParent();
				LinearLayout piezalyy =  (LinearLayout)parentView.getParent().getParent().getParent();
				AutoCompleteTextView spinerviewCodigo = (AutoCompleteTextView)piezaly.findViewById(R.id.orden_trabajo_autoCompletado_codigo);
				TextView textviewPrecio = (TextView)piezaly.findViewById(R.id.textViewPrecio);

				TextView textviewIdPieza = (TextView)piezalyy.findViewById(R.id.id_pieza_hidden);

				textviewIdPieza.setText(id_seleccionado);
				spinerviewCodigo.setText(codigo_seleccionado);

				//textviewCodigo.setText(codigo_seleccionado);
				textviewPrecio.setText(precio_seleccionado);

			}

			public void onNothingSelected(AdapterView<?> parentView) {

			}
		});

		//spiner fin
		contenedorPiezas.addView(mylinear);
		contenedorPiezas.requestFocus();
	}

	public void eliminarPiezas(View v){

		ViewParent parent1 = (ViewGroup) v.getParent().getParent();//pieza a borrar
		ViewParent parent2 = (ViewGroup)parent1.getParent();//contedor de piezas
		((ViewGroup) parent2).removeView((View) parent1);

	}

	//camara
	public void open(){
		Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, 0);
	}

	//Devuelve el string del QR entregado en un bitmap
	public String decodeQR(Bitmap image) {
		QRCodeDecoder decoder = new QRCodeDecoder();
		DebugCanvas canvas = new QRCanvas();
		QRCodeDecoder.setCanvas(canvas);
		String qrResultString = null;
		try {
			qrResultString = new String(decoder.decode(new QRBitmap(image)));
		} catch (Exception e) {
			//canvas.println("Error: " + e.getMessage());

		}
		boolean match_ok = false;
		if(qrResultString!=null){
			Log.i("QR CODE",qrResultString);
			match_ok = qrResultString.matches(".*[a-zA-Z0-9].$");
		}
		if(match_ok){
			//if(qrResultString != null){
			qrResultString = ContentConverter.convert(qrResultString).toString();
			//return true;
			return qrResultString;
		}
		else
			return "No se reconoce QR";
	}

	private void dispatchTakeQrIntent(){
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_QR);
			}
		}

	}

	private void dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				// Error occurred while creating the File

			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
						Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		//referencia al contenedor que llamo al intent de la camara dentro de la maquina
		LinearLayout linear_contenedor_imagenes = (LinearLayout) v_temp.getParent().getParent();
		LinearLayout linear_contenedor_maquina = (LinearLayout) v_temp.getParent().getParent().getParent();

		TextView textViewHidden = (TextView)linear_contenedor_imagenes.findViewById(R.id.flag_hidden_cont);
		TextView textViewHiddenID = (TextView)linear_contenedor_maquina.findViewById(R.id.id_maquina_hidden);

		String id_maquina = textViewHiddenID.getText().toString();
		Log.i("id_maquina seleccionada", " :" + id_maquina);
		/*//nombre de la maquina de la vista
		LinearLayout linear_contenedor_principal = (LinearLayout) v_temp.getParent().getParent().getParent();
		TextView textViewNombreMaquina = (TextView)linear_contenedor_imagenes.findViewById(R.id.textview_modelo_maquina);
		String NombreMaquina = textViewNombreMaquina.getText().toString();
		 */
		String NombreMaquina = "NOMBRE_MAQ";
		//obtengo el flag de la vista dinamica
		if(textViewHidden!=null){
			flag_pictures = Integer.parseInt(textViewHidden.getText().toString());
		}

        OutputStream fOut = null;
		Bitmap bitmap_desdeSD = BitmapFactory.decodeFile(mCurrentPhotoPath);
//		Bitmap srcBmp = ElunVal.decodeSampledBitmapFromFile(mCurrentPhotoPath, 800, 800);
		if(bitmap_desdeSD==null)
			return;
		//rotar
//		Matrix matrix = new Matrix();
//		matrix.postRotate(90);

//		Bitmap bitmap_rotated = Bitmap.createBitmap(bitmap_desdeSD, 0, 0,
//				bitmap_desdeSD.getWidth(), bitmap_desdeSD.getHeight(), matrix,
//				false);
		//escalar

//		int nh = (int) (bitmap_rotated.getHeight() * (100.0 / bitmap_rotated
//				.getWidth()));
//		Bitmap bitmap_scaled = Bitmap.createScaledBitmap(bitmap_rotated, 100, nh,
//				true);

		 File file = new File(mCurrentPhotoPath);
		 Bitmap srcBmp1 = ElunVal.decodeSampledBitmapFromFile(file.getAbsolutePath(), 800, 800);

        try {
            ExifInterface exif = new ExifInterface(file.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
            Log.d("ORIENTATION", "" + orientation);
            Matrix matrix = new Matrix();
            switch (orientation) {
                case 6:
                    matrix.postRotate(90);
                    srcBmp1 = Bitmap.createBitmap(srcBmp1, 0, 0, srcBmp1.getWidth(), srcBmp1.getHeight(), matrix, true);
                    break;
                case 3:
                    matrix.postRotate(180);
                    srcBmp1 = Bitmap.createBitmap(srcBmp1, 0, 0, srcBmp1.getWidth(), srcBmp1.getHeight(), matrix, true);
                    break;
                case 8:
                    matrix.postRotate(270);
                    srcBmp1 = Bitmap.createBitmap(srcBmp1, 0, 0, srcBmp1.getWidth(), srcBmp1.getHeight(), matrix, true);
                    break;

                default:
                    break;
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }		 try {
             fOut = new FileOutputStream(file);
             srcBmp1.compress(Bitmap.CompressFormat.JPEG, 80, fOut);
         } catch (FileNotFoundException e1) {
             // TODO Auto-generated catch block
             e1.printStackTrace();
         } finally {
             try {
                 fOut.flush();
                 fOut.close();
             } catch (Throwable ignore) {
             }
         }

        Bitmap bitmap_rotated = srcBmp1;
		Log.i("bitmap_foto scaled","with = " + bitmap_rotated.getWidth() + " heigth = " + bitmap_rotated.getHeight());

		if (requestCode == 1) {//intent camara normal, para 3 contenedores de fotos
			Log.i("requestCode", "1");
			//thumbnail para los 3 imageview condiciones.
			if (resultCode == RESULT_OK) {
				//setPic();
				Log.i("onact result", "setpic"+ " " + mCurrentPhotoPath );
			}

			if(flag_pictures == 1){
				ImageView imageViewImagen1 = (ImageView) linear_contenedor_imagenes.findViewById(R.id.orden_trabajo_imageview_img1);
				//imageViewImagen1.setBackgroundResource(R.drawable.check_icon);
				imageViewImagen1.setImageBitmap(bitmap_rotated);
				imageViewImagen1.invalidate();
				imageViewImagen1.requestFocus();
				textViewHidden.setText(Integer.toString(2));

				String foto_base_64 = BitMapToString(bitmap_rotated).replace("\\n", "");
				//sp.edit().putString(NombreMaquina+"foto" + "1", foto_base_64).commit(); //así se guarda

				try {
					//buscar el id
					int indice = json_imagenes.length();

					//si es 0, no hay objetos en el json, la primera vez
					if(indice==0){
						JSONObject json_temp = new JSONObject();
						json_temp.put("id_maquina", id_maquina);
						json_temp.put("foto_1", foto_base_64);
						//json_imagenes.put(NombreMaquina + "img_" + "1_" + "base_64", foto_base_64);
						json_imagenes.put(json_temp);
						Log.i("Json_img put 1","id_maquina : "+ id_maquina + "/ foto_1");

					}else{

						Log.i("flag = 1", "numero elementos en el json = " + indice);
						for(int i=0;i<indice;i++){
							String id_temp=  json_imagenes.getJSONObject(i).getString("id_maquina");

							//si lo encuentra agrega la imagen en el objeto
							if (id_temp == id_maquina){

								Log.i("flag = 1", "encontro elemento con id_maquina");

								json_imagenes.getJSONObject(i).put("foto_1", foto_base_64);

								Log.i("Json_imagenes put","id_maquina : "+ id_maquina + "/ foto_1");
							}
						}

						JSONObject json_temp = new JSONObject();
						json_temp.put("id_maquina", id_maquina);
						json_temp.put("foto_1", foto_base_64);
						//json_imagenes.put(NombreMaquina + "img_" + "1_" + "base_64", foto_base_64);
						json_imagenes.put(json_temp);
						Log.i("Json_imagenes put","id_maquina : "+ id_maquina + "/ foto_1");


						//si no es primera vez, y no lo encuentra  hayque crearlo


						//							}else{
						//								JSONObject json_temp = new JSONObject();
						//								json_temp.put("id_maquina", id_maquina);
						//								json_temp.put("foto_1", foto_base_64);
						//								//json_imagenes.put(NombreMaquina + "img_" + "1_" + "base_64", foto_base_64);
						//								json_imagenes.put(json_temp);
						//								Log.i("Json_imagenes put","id_maquina : "+ id_maquina + "/ foto_1");
						//							}



					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}







			}else if(flag_pictures==2){
				ImageView imageViewImagen2 = (ImageView) linear_contenedor_imagenes.findViewById(R.id.orden_trabajo_imageview_img2);
				imageViewImagen2.setImageBitmap(bitmap_rotated);
				//imageViewImagen2.setBackgroundResource(R.drawable.check_icon);
				imageViewImagen2.invalidate();
				imageViewImagen2.requestFocus();
				textViewHidden.setText(Integer.toString(3));

				String foto_base_64 = BitMapToString(bitmap_rotated).replace("\\n", "");
				/*try {
					json_imagenes.put(NombreMaquina+"img_" + "2_"+"base_64", foto_base_64);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/

				try {
					//buscar el id
					int indice = json_imagenes.length();
					Log.i("flag = 2", "numero elementos en el json = " + indice);

					for(int i=0;i<indice;i++){
						String id_temp=  json_imagenes.getJSONObject(i).getString("id_maquina");
						//si lo encuentra agrega la imagen en el objeto
						if (id_temp == id_maquina){

							Log.i("flag = 2", "encontro elemento con id_maquina");

							json_imagenes.getJSONObject(i).put("foto_2", foto_base_64);

							Log.i("Json_imagenes put","id_maquina : "+ id_maquina + "/ foto_2");

						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else if(flag_pictures == 3){
				ImageView imageViewImagen3 = (ImageView) linear_contenedor_imagenes.findViewById(R.id.orden_trabajo_imageview_img3);
				imageViewImagen3.setImageBitmap(bitmap_rotated);
				//imageViewImagen3.setBackgroundResource(R.drawable.check_icon);
				imageViewImagen3.invalidate();
				imageViewImagen3.requestFocus();
				textViewHidden.setText(Integer.toString(1));

				String foto_base_64 = BitMapToString(bitmap_rotated).replace("\\n", "");

				try {
					//buscar el id
					int indice = json_imagenes.length();
					Log.i("flag = 3", "numero elementos en el json = " + indice);

					for(int i=0;i < indice;i++){
						String id_temp=  json_imagenes.getJSONObject(i).getString("id_maquina");

						//si lo encuentra agrega la imagen en el objeto
						if (id_temp == id_maquina){
							Log.i("flag = 3", "encontro elemento con id_maquina");
							json_imagenes.getJSONObject(i).put("foto_3", foto_base_64);
							Log.i("Json_imagenes put","id_maquina : "+ id_maquina + "/ foto_3");
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//				try {
				//					json_imagenes.put(NombreMaquina + "img_" + "3_"+"base_64", foto_base_64);
				//				} catch (JSONException e) {
				//					// TODO Auto-generated catch block
				//					e.printStackTrace();
				//				}
			}
		}


		//
		if (requestCode == 2) {//intent QR
			Log.i("requestCode", "2");
			if (resultCode == RESULT_OK) {

				if(bitmap_rotated== null){
					Log.i("bitmap_desdeSD_qr","NULLLL!!!!!!!!!!!!");
				}else {
					Log.i("bitmap_desdeSD_qr","OKKKKKK!!!!!!!!!!!!");
				}

				string_qr = decodeQR(bitmap_rotated);

				if (string_qr == "No se reconoce QR"){

					LinearLayout contenedorQR = (LinearLayout) v_temp.getParent().getParent();//linearlayout de la maquina

					if(contenedorQR== null){
						Log.i("contenedorQR","NULLLL!!!!!!!!!!!!");
					}else {
						Log.i("contenedorQR","OKKKKKK!!!!!!!!!!!!");
					}
					TextView textView_string_qr = (TextView) contenedorQR.findViewById(R.id.textView_string_qr);
					textView_string_qr.setText("Intente nuevamente");
					textView_string_qr.setTextColor(Color.rgb(255,0,0));

					ImageView imageViewQr = (ImageView) contenedorQR.findViewById(R.id.orden_trabajo_imageview_qr);

					if(imageViewQr== null){
						Log.i("imageViewQr","NULLLL!!!!!!!!!!!!");
					}else {
						Log.i("imageViewQr","OKKKKKK!!!!!!!!!!!!");
					}

					imageViewQr.setImageBitmap(bitmap_rotated);
					imageViewQr.requestFocus();
				}else{
					LinearLayout contenedorQR = (LinearLayout) v_temp.getParent().getParent();//linearlayout de la maquina

					TextView textView_string_qr = (TextView) contenedorQR.findViewById(R.id.textView_string_qr);
					textView_string_qr.setText(string_qr);
					textView_string_qr.setTextColor(Color.rgb(0,255,0));

					ImageView imageViewQr = (ImageView) contenedorQR.findViewById(R.id.orden_trabajo_imageview_qr);
					imageViewQr.setImageBitmap(bitmap_rotated);
					imageViewQr.requestFocus();

					String foto_base_64 = BitMapToString(bitmap_rotated).replace("\\n", "");

					try {
						JSONObject json_temp = new JSONObject();
						json_temp.put("id_maquina", id_maquina);
						json_temp.put("foto_qr", foto_base_64);
						json_temp.put("string_qr", string_qr);

						json_imagenes_qr.put(json_temp);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					imageViewQr.requestFocus();

				}
			}
		}

	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String imageFileName = null;
		imageFileName = "FOTO_" + System.currentTimeMillis() + "_";
		/*if(flag_pictures==1){
			imageFileName = "FOTO1_" + timeStamp + "_";
			flag_pictures++;
		}else if(flag_pictures==2){
			imageFileName = "FOTO2_" + timeStamp + "_";
			flag_pictures++;
		}else if(flag_pictures==3){
			imageFileName = "FOTO3_" + timeStamp + "_";
			flag_pictures = 1;
		}*/
		File storageDir = Environment.getExternalStoragePublicDirectory(AppParameters.applicationPath);

		Log.i("createImageFile", "imagen a guardar" + storageDir.toString()+ "/ "+ imageFileName );

		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
				);

		// Save a file: path for use with ACTION_VIEW intents

		//Depende de la version de android si va el "file:"  ................

		//mCurrentPhotoPath = "file:" + image.getAbsolutePath();
		mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}

	private void setPic() {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		/* Get the size of the ImageView */
		ImageView mImageView = (ImageView) findViewById(R.id.orden_trabajo_imageview_img1);
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;

		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		Log.i("get size img desde la sd", "ruta" + " "+ mCurrentPhotoPath + "  "+ "with"+"/"+photoW +"height"+"/"+photoH);

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		bitmap_scaled = bitmap;

		Log.i("final del escalado", "with"+" "+ bitmap_scaled.getWidth());
		/* Associate the Bitmap to the ImageView */

	}

	public String BitMapToString(Bitmap bitmap){
		ByteArrayOutputStream baos = new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 35, baos);
		byte [] b=baos.toByteArray();
		String temp=Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
	}
	//fin camara

	public void intentGaleria(View v){
		File root = new File(Environment.getExternalStorageDirectory().getPath()
				+ "/mnt/sdcard/data/com.diversey.servicio.logistica");
		Uri uri = Uri.fromFile(root);


		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivity(intent);  
	}

	public void terminarOt(){
		//Detalle
		Spinner spinTipoServicio = (Spinner) findViewById(R.id.orden_trabajo_tipo_servicio);
		Spinner spinServicio = (Spinner) findViewById(R.id.orden_trabajo_tipo_mantencion);
		
		EditText horometroEt = (EditText)findViewById(R.id.orden_trabajo_horometro);
		EditText horasFactEt = (EditText)findViewById(R.id.orden_trabajo_horas_facturar);

		tipo_mantencion = spinServicio.getSelectedItem().toString();
		tipo_servicio = spinTipoServicio.getSelectedItem().toString();

		horometro = horometroEt.getText().toString();

		if(horometro== ""){
			horometro= "0";
		}

		horasFact = horasFactEt.getText().toString();
		if(horasFact == ""){
			horasFact = "0";
		}

		//captura control tecnico
		EditText diagEditText = (EditText)findViewById(R.id.diagnostico_editText);
		diagnostico = diagEditText.getText().toString();

		EditText obsGeneralEditText = (EditText)findViewById(R.id.observacion_gral_editText);
		obsGeneral = obsGeneralEditText.getText().toString();

		//tipo de reparacion
		/*RadioGroup radiotipoFacturacion = (RadioGroup)findViewById(R.id.radioGroup_tipoFacturacion);
		int indexsag = radiotipoFacturacion.getCheckedRadioButtonId();
		RadioButton radioTipoFacturacionButton = (RadioButton) findViewById(indexsag);
		tipoFacturacion = radioTipoFacturacionButton.getText().toString();*/

		EditText horasHombreEditText = (EditText)findViewById(R.id.horas_hombre_editText_hh);
		EditText minutosHombreEditText = (EditText)findViewById(R.id.minutos_hombre_editText_mm);
		
	
		horasHombre= " ";
		minutosHombre= " ";
		
		if(!horasHombreEditText.getText().toString().equals("") && !minutosHombreEditText.getText().toString().equals("")){
			
			horasHombre = horasHombreEditText.getText().toString();
			minutosHombre = minutosHombreEditText.getText().toString();
			
			int transformar_horas_a_minutos = (Integer.parseInt(horasHombre)) * 60;
			minutosTrabajados = transformar_horas_a_minutos + (Integer.parseInt(minutosHombre));
			
			
			
			String hh = Integer.toString(minutosTrabajados);
			
			horas_hombre = hh;
			
			//int hhInt = Integer.parseInt(horasHombreEditText.getText().toString());
			
			Log.i("capturar HH", "hh : " + horasHombre + "otra var : " + hh + "horas_hombre  : " + horas_hombre);
		}
		
		

		EditText obsFinalesEditText = (EditText)findViewById(R.id.obs_finales_editText);
		obsFinales = obsFinalesEditText.getText().toString();

		//Capturar bitmap firma y transformarla a base_64 ********************
		pathFirma  = "firma.ot." + idOT + ".png";

		if(dragFirma!=null)
			dragFirma.saveCanvasToFile(AppParameters.cachePicturePath + pathFirma);
		Bitmap bitmap_firma = BitmapFactory.decodeFile(AppParameters.cachePicturePath + pathFirma);
		string_base64_firma = BitMapToString(bitmap_firma).replace("\\n", "");

		//********************

		//Captura de las maquinas, y por cada maquina sus respectivas piezas

		ViewGroup layoutContent = (ViewGroup) findViewById(R.id.orden_trabajo_contenedor_maquinas);//layout padre de los productos
		int childcount = layoutContent.getChildCount();
		Log.i("Numero de maquinas Ot",Integer.toString(childcount));

		for (int x = 1; x <= childcount; x++){//por cada maquina en el contenedor_maquinas
			JSONObject json_maquinaX = new JSONObject();
			JSONObject json_piezas = new JSONObject();
			LinearLayout maquinaX = (LinearLayout)(layoutContent.getChildAt(x-1));//se obtiene la maquina en la posicion

			TextView textViewID = (TextView)maquinaX.findViewById(R.id.id_maquina_hidden);
			String Id_maquina=textViewID.getText().toString();

			Log.i("Iteracion FOR maquina",Integer.toString(x));
			int childcountX = maquinaX.getChildCount();
			int id_maquinaX = maquinaX.getId();		
			//Log.i("Id producto desde getId(backend)",Integer.toString(id_maquinaX));

			LinearLayout contenedor_piezas = (LinearLayout) maquinaX.findViewById(R.id.orden_trabajo_piezas_dinamicas);
			int childcount_piezas = contenedor_piezas.getChildCount();


			//por cada pieza
			for (int y = 1; y <= childcount_piezas; y++){//por cada pieza en la maquina
				JSONObject json_piezaX = new JSONObject();
				Log.i("Iteraciones pieza", "pieza numero : " + y);

				LinearLayout piezaX= (LinearLayout)(contenedor_piezas.getChildAt(y-1));//se obtiene la pieza en la posicion;

				Spinner spinNombrePieza = (Spinner) piezaX.findViewById(R.id.orden_trabajo_spinner_nombre);
				TextView textviewCodigo = (TextView) piezaX.findViewById(R.id.textViewCodigo);
				TextView textviewCantidad = (TextView) piezaX.findViewById(R.id.editText_cantidad_pieza);
				TextView textviewIdPiezaHidden = (TextView) piezaX.findViewById(R.id.id_pieza_hidden);

				String id_pieza =textviewIdPiezaHidden.getText().toString();

				String cantidad_pieza = textviewCantidad.getText().toString();
				if (cantidad_pieza == ""){
					cantidad_pieza = "0";
				}

				try{
					//json_piezaX.put("nombre_pieza", nombre_pieza);
					//json_piezaX.put("codigo_pieza", codigo_pieza);
					json_piezaX.put("cantidad", cantidad_pieza);
					json_piezas.put(id_pieza, json_piezaX);//la pieza actual, al json_piezas de la maquina

					Log.i("pieza : " + y,json_piezaX.toString());

				}catch (JSONException e) {
					e.printStackTrace();
				}	
			}//termina iteracion de piezas de la maquinax

			//el json_piezas al json_maquina
			try{
				json_maquinaX.put("json_piezas", json_piezas);
				json_maquinaX.put("id_maquina", Id_maquina);
				//Log.i("pieza : " + y,json_piezaX.toString());
			}catch (JSONException e) {
				e.printStackTrace();
			}	
			//la maquinax al json de maquinas

			json_maquinas.put(json_maquinaX);

			/*try{
				json_maquinas.put( json_maquinaX);
				Log.i("maquina: " + x, json_maquinaX.toString());
			}catch (JSONException e) {
				e.printStackTrace();
			}	*/



		}

		//datos de la firma

		TextView textViewFirmaNombre = (TextView)findViewById(R.id.firma_nombre_editText);
		TextView textViewFirmaMail = (TextView)findViewById(R.id.firma_mail_editText);
		TextView textViewFirmaRut = (TextView)findViewById(R.id.firma_rut_editText);

		rut_receptor=textViewFirmaRut.getText().toString();
		nombre_receptor= textViewFirmaNombre.getText().toString();
		mail_receptor= textViewFirmaMail.getText().toString();


		Log.i("json_maquinas : ",json_maquinas.toString());

	}

	public void endOt(){
		terminarOt();

		List<UserRecord> users  = UserRecord.find(UserRecord.class, "idot = ?", idOT);

		for (int i =0; i< users.size(); i++){
			u = users.get(i);
			break;
		}

		sp.edit().putString("json_imagenes", json_imagenes.toString()).commit(); //así se guarda
		sp.edit().putString("json_imagenes_qr", json_imagenes_qr.toString()).commit(); //así se guarda

		u.json_imagenes = json_imagenes.toString();
		u.json_imagenes_qr = json_imagenes_qr.toString();



		try {

			JSONArray jsonimagenes = new JSONArray(json_imagenes.toString());
			JSONArray jsonimagenes_qr = new JSONArray(json_imagenes_qr.toString());

			datosOT.put("json_imagenes", jsonimagenes);
			datosOT.put("json_imagenes_qr", jsonimagenes_qr);


			//sobre la OT
			datosOT.put("orden_trabajo_id", idOT);

			datosOT.put("tipo_orden_id", "4");
			u.tipo_orden_id = "4";
			//datosOT.put("tipo_orden_id", "2");

			datosOT.put("diagnostico", diagnostico);
			u.diagnostico = diagnostico;

			datosOT.put("obsGeneral", obsGeneral);
			u.obs_general = obsGeneral;

			datosOT.put("comentarios", comentarioGeneral);
			u.comentarios = comentarioGeneral;

			datosOT.put("string_base64_firma", string_base64_firma);
			u.firma_mail = string_base64_firma;

			datosOT.put("latitud_empleado", latitudTecnico);
			u.latitud = latitudTecnico;

			datosOT.put("longitud_empleado", longitudTecnico);
			u.longitud = longitudTecnico;

			datosOT.put("empleado_id", idTecnico);

			datosOT.put("fecha_inicio_ejecucion", fecha_inicio_ejecucion);
			u.fecha_ejecucion = fecha_inicio_ejecucion;
			//detalle

			datosOT.put("tipo_mantencion", tipo_mantencion);
			u.tipo_mantencion = tipo_mantencion;

			datosOT.put("tipo_servicio", tipo_servicio);
			u.tipo_servicio = tipo_servicio;

			//Sobre las maquinas

			//Tipos de reparacion
			//			datosOT.put("tipoFacturacion", tipoFacturacion);
			datosOT.put("id_tecnico", diverseyIntent.getStringExtra("id_tecnico"));


			
			datosOT.put("horometro", horometro);

			datosOT.put("prueba", "prueba");

			datosOT.put("horas_facturadas", horasFact);

			datosOT.put("obsFinales", obsFinales);
			u.obs_finales = obsFinales;

			datosOT.put("json_maquinas", json_maquinas);
			u.json_maq = json_maquinas.toString();
			//datosOT.put("img_maquinas", new JSONArray().put(json_imagenes));
			
			Log.i("horas_hombre ", " :" + horas_hombre);
			datosOT.put("horas_hombre", horas_hombre );
			u.horas_hombre = horas_hombre;
			//datosOt.put("minutos", minutos_hombre);
			
			
			
			//firma
			datosOT.put("rut_receptor", rut_receptor);
			u.firma_rut = rut_receptor;

			datosOT.put("nombre_receptor", nombre_receptor);
			u.firma_nombre = nombre_receptor;

			datosOT.put("email_receptor", mail_receptor);
			u.firma_mail = mail_receptor;

		} catch (JSONException e) {
			e.printStackTrace();
		}


		Log.i("FIN OT", "Json = " + datosOT.toString());



		//
		Intent it = new Intent();
		Bundle bl = new Bundle();

		bl.putString("json-datos", datosOT.toString());
		//it.putExtras(bl);
		
		
		if(uploadDataThr(datosOT)){
			Log.i("Data: ", "BUNDLE CONSTRUIDO !!! OK !!!");
			//bl.putString("json-datos", datosOT.toString());
			it.putExtras(bl);
			Log.i("Data: ", "INTENT PUTEXTRAS CONSTRUIDO !!! OK !!!");
			setResult(Main.CASO_HIGIENE, it);
			Log.i("Data: ", "setResult CONSTRUIDO !!! OK !!!");
			finishActivity(Main.CASO_HIGIENE);
			Log.i("Data: ", "finishActivity !!! OK !!!");
			progDailogGenera.dismiss();
			progDailogGenera = null;
			//AvisoExito("�xito", "Los datos se ingresaron \ncorrectamente");
			onBackPressedEndot("Éxito", "La OT fue enviada exitosamente.", true);
		
			Log.i("Data: ", "onBackPressed !!! OK !!!");
			Log.i("Base de datos", "true");
			u.save();
			Log.d("SugarServidor", u.toString());
		}else{
			progDailogGenera.dismiss();
			progDailogGenera = null;
			u.status = "true";
			u.allJsonOT = datosOT.toString();
			u.save();
			Log.d("SugarLocal", u.toString());
			onBackPressedEndot("Sin conexión a internet", "Se ha almacendado localmente la OT, para su posterior sincronización con el servidor.", true);
			Log.i("Base de datos", "false");
		}
	}
	
	private void PreguntaAccion(String title, String mensaje) {

		  new AlertDialog.Builder(otDiversey.this)
		     .setTitle(title.toString())
		     .setMessage(mensaje.toString())
		     .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		         public void onClick(DialogInterface dialog, int which) {
		          dialog.dismiss();

		          progDailogGenera = new ProgressDialog(otDiversey.this);
		          progDailogGenera.setTitle("Generando OT");
		          progDailogGenera.setMessage("por favor, espera...");
		          progDailogGenera.show();

		          Handler espera = new Handler();
		          espera.postDelayed(new Runnable() {

					public void run() {
						 RealizarAccion();

					}
				}, 1500);


		         }
		      })
		     .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {

		   public void onClick(DialogInterface dialog, int which) {
		    // TODO Auto-generated method stub
		    dialog.dismiss();
		    //GenerarReporte = false;
		   }
		  })
		     .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();

	}

	protected void RealizarAccion() {


	endOt();
	}

@SuppressWarnings("deprecation")
	public void AvisoError(String title, String Mensaje) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setTitle("Aviso...");
		alertDialog.setMessage("No tiene conexión a Internet");
		alertDialog.setButton("Aceptar", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {

		}
		});
		alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
		alertDialog.show();
	}
	
	public synchronized boolean uploadDataThr(final JSONObject ot) {

			/*String string_base64_bitmap = sp.getString("string_base64_bitmap", "null"); //así se asigna

			try {
				ot.put("string_base64_bitmap", string_base64_bitmap);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				nombres_imagenes = ot.getJSONArray("nombres_imagenes");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int numero_imgs = nombres_imagenes.length();

			for (int i =0; i<numero_imgs;i++){
				try {
					String nombre_img = nombres_imagenes.getString(i);
					String string_base64_bitmap = sp.getString(nombre_img, "null"); //así se asigna
					ot.put(nombre_img, string_base64_bitmap);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			*/

			try {
				JSONArray json_imagenes = new JSONArray(sp.getString("json_imagenes", "null"));
				JSONArray json_imagenes_qr = new JSONArray(sp.getString("json_imagenes_qr", "null"));

				ot.put("json_imagenes", json_imagenes);
				ot.put("json_imagenes_qr", json_imagenes_qr);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


				if(AppParameters.postData(ot, "actualizar")){
				Log.i("actualizando", "Enviar a utilizar");
					//otPendientes--;
				listo = true;
				return true;
			}

	return false;
	//otPendientes++;

}

	public void onBackPressedEndot(String title, String mensaje, final boolean insertado){

	  new AlertDialog.Builder(otDiversey.this)
	     .setTitle(title.toString())
	     .setMessage(mensaje.toString())
	     .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int which) {
	        	 if(insertado){
			          dialog.dismiss();
					  finish();
	        	 }else{
	        		 dialog.dismiss();
	        	 }
	         }
	      })

	     .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	 }

	@Override
	public void onBackPressed(){

		Toast.makeText(getApplicationContext(), " Press Back again to Exit ", Toast.LENGTH_SHORT).show();

		//sp.edit().putString("screen_state", "WORK_OT");

		sp.getString("screen_state","");

		Log.i("onBackPressed","STATE : " + sp.getString("screen_state", ""));
		//Volver a vista preot
		if (sp.getString("screen_state","").equalsIgnoreCase("WORK_OT")) {

			sp.edit().putString("screen_state", "WORK_OT");

			//SimpleDateFormat s = new SimpleDateFormat("ddMMyyyyhhmmss");
			//fecha_inicio_ejecucion = s.format(new Date());

			LinearLayout controlTecnico = (LinearLayout)findViewById(R.id.orden_trabajo_control_tecnico);
			controlTecnico.setVisibility(View.GONE);

			LinearLayout tipoReparacion = (LinearLayout)findViewById(R.id.orden_trabajo_tipo_reparacion);
			tipoReparacion.setVisibility(View.GONE);

			LinearLayout firmaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_firma_layout);
			firmaLayout.setVisibility(View.GONE);

			LinearLayout layoutMaquinas = (LinearLayout)findViewById(R.id.orden_trabajo_maquinas);
			layoutMaquinas.setVisibility(View.GONE);

			LinearLayout layoutDetalle = (LinearLayout)findViewById(R.id.orden_trabajo_detalle);
			layoutDetalle.setVisibility(View.GONE);

			RelativeLayout mapaLayout = (RelativeLayout)findViewById(R.id.orden_trabajo_mapa_layout);
			mapaLayout.setVisibility(View.VISIBLE);

			LinearLayout fallaLayout = (LinearLayout)findViewById(R.id.orden_trabajo_falla_layout);
			fallaLayout.setVisibility(View.VISIBLE);

			LinearLayout detalleLayout = (LinearLayout)findViewById(R.id.orden_trabajo_detalle);
			detalleLayout.setVisibility(View.VISIBLE);

			LinearLayout comentLayout = (LinearLayout)findViewById(R.id.orden_trabajo_comentarios);
			comentLayout.setVisibility(View.VISIBLE);

			sp.edit().putString("screen_state", "PRE_OT").commit();

		}else if(sp.getString("screen_state","").equalsIgnoreCase("PRE_OT")){

			super.onBackPressed();
		}

	}

	public void MensajeCodigoBlanco(){

	  new AlertDialog.Builder(otDiversey.this)
	     .setTitle("Alerta")
	     .setMessage("No se seleccinó el código")
	     .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
	         public void onClick(DialogInterface dialog, int which) {
	        	 	 dialog.dismiss();

	         }
	      })

	     .setIcon(android.R.drawable.ic_dialog_alert)
	     .show();
	 }

	private class TakeQR extends AsyncTask <String, String, Boolean> {

		private ProgressDialog progDailog;
		private Context ctx;
		private ArrayList<UserRecord> users;
		private Bitmap bitmapQR;

		public TakeQR(Context c, Bitmap bitmap){
			ctx = c;
			bitmapQR = bitmap;

		}

		protected void onPreExecute() {


		}

		protected Boolean doInBackground(String... urls) {


			if(bitmapQR== null){
				Log.i("bitmapQR","NULLLL!!!!!!!!!!!!");
			}else {
				Log.i("bitmapQR","OKKKKKK!!!!!!!!!!!!");
			}

			string_qr = decodeQR(bitmapQR);

			if (string_qr == "No se reconoce QR"){

				LinearLayout contenedorQR = (LinearLayout) v_temp.getParent().getParent();//linearlayout de la maquina

				if(contenedorQR== null){
					Log.i("contenedorQR","NULLLL!!!!!!!!!!!!");
				}else {
					Log.i("contenedorQR","OKKKKKK!!!!!!!!!!!!");
				}
				TextView textView_string_qr = (TextView) contenedorQR.findViewById(R.id.textView_string_qr);
				textView_string_qr.setText("Intente nuevamente");

				ImageView imageViewQr = (ImageView) contenedorQR.findViewById(R.id.orden_trabajo_imageview_qr);

				if(imageViewQr== null){
					Log.i("imageViewQr","NULLLL!!!!!!!!!!!!");
				}else {
					Log.i("imageViewQr","OKKKKKK!!!!!!!!!!!!");
				}

				imageViewQr.setImageBitmap(bitmapQR);
				imageViewQr.requestFocus();
			}else{
				LinearLayout contenedorQR = (LinearLayout) v_temp.getParent().getParent();//linearlayout de la maquina

				TextView textView_string_qr = (TextView) contenedorQR.findViewById(R.id.textView_string_qr);
				textView_string_qr.setText(string_qr);

				ImageView imageViewQr = (ImageView) contenedorQR.findViewById(R.id.orden_trabajo_imageview_qr);
				imageViewQr.setImageBitmap(bitmapQR);
				imageViewQr.requestFocus();

			}




			return true;
		}

		protected void onPostExecute(Boolean result) {


		}
	}


	}