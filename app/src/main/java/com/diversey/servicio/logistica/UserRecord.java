package com.diversey.servicio.logistica;

import com.orm.SugarRecord;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserRecord extends SugarRecord<UserRecord>
{

public UserRecord() {
		super();
		}

public String idot;
	public String rut;
	public String nombre;
	public String razon_social;
	public String direccion;
	public String logo;
	public String tipoordenid;
	
	public String tipo_mantencion;
	public String tipo_servicio;
	public String horas_hombre;
	public String diagnostico;
	public String obs_finales;
	public String obs_general;
	public String firma_nombre;
	public String firma_rut;
	public String firma_mail;

	
	
	
	public String fecha_creacion;
	public String fecha_ejecucion;
	public String latitud;
	public String longitud;
	public String estado;
	public String nombre_contacto;
	public String correo_contacto;
	public String descripcion;

	public String modelo_maquina;
	public String nro_serie;
	public String cantidad;
	public String codigo;
	public String comentarios;
	public String json_maq;
	public String json_maq_all;
	public String partes_usadas;
	public String json_imagenes;
	public String json_imagenes_qr;
	public String status; // status pendiente
	public String allJsonOT;
	
	public UserRecord(String nombre, String direccion) {
		this("0", "0", nombre, "0", direccion, "", "","","","","", "","", "", "","", "","","","","","","","","","","","","","","","");
	}
	
	
	public UserRecord(String id, String rut, String nombre, String razon_social, String direccion, String logo, String tipo_orden_id, String fecha_creacion, String fecha_ejecucion, String latitud, String longitud, String estado, String nombre_contacto, String correo_contacto, String descripcion, String modelo_maquina, String nro_serie, String cantidad, String codigo, String comentarios, String maquinas,String maquinas_all, String obs_finales, String horas_hombre, String obs_general,String diagnostico,String tipo_mantencion,String tipo_servicio,String firma_nombre,String firma_rut,String firma_mail,String partes_usadas){
		this.idot=id;
		this.rut=rut;
		this.nombre=nombre;
		this.razon_social=razon_social;
		this.direccion=direccion;
		
		this.horas_hombre=horas_hombre;
		this.obs_finales=obs_finales;
		
		this.diagnostico=diagnostico;
		this.firma_mail=firma_mail;
		this.firma_nombre=firma_nombre;
		this.firma_rut=firma_rut;
		this.tipo_mantencion=tipo_mantencion;
		this.tipo_servicio=tipo_servicio;
		
		this.logo=logo;
		this.obs_general=obs_general;
		if(logo.length()<4)
			this.logo="no_img.png"; //default image.
		
		this.tipoordenid=tipo_orden_id;
		this.fecha_creacion=fecha_creacion;
		this.fecha_ejecucion=fecha_ejecucion;
		this.latitud=latitud;
		if(latitud.length()==0)
			this.latitud="0"; //default pos
		this.longitud=longitud;
		if(longitud.length()==0)
			this.longitud="0"; //default pos.
		this.estado=estado;
		this.nombre_contacto=nombre_contacto;
		this.correo_contacto=correo_contacto;
		this.descripcion = descripcion;
		this.modelo_maquina = modelo_maquina;
		this.nro_serie = nro_serie;
		this.cantidad = cantidad;
		this.codigo = codigo;
		this.comentarios = comentarios;
		this.json_maq = maquinas;
		this.json_maq_all = maquinas_all;
		this.partes_usadas = partes_usadas;
		this.status = "false";
		this.allJsonOT = "";
	}
	
	public UserRecord(String fromJSONString){
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(fromJSONString);
			JSONObject jsonObject = jsonArray.getJSONObject(0);

			this.idot = jsonObject.getString("id");
			this.rut = jsonObject.getString("rut");
			this.nombre = jsonObject.getString("nombre");
			this.razon_social=jsonObject.getString("razon_social");
			this.direccion=jsonObject.getString("direccion");
			this.logo=jsonObject.getString("logo");
			this.tipoordenid=jsonObject.getString("tipo_orden_id");
			this.fecha_creacion=jsonObject.getString("fecha_creacion");
			this.fecha_ejecucion=jsonObject.getString("fecha_ejecucion");
			this.latitud=jsonObject.getString("latitud");
			this.longitud=jsonObject.getString("longitud");
			this.estado=jsonObject.getString("estado");
			this.nombre_contacto=jsonObject.getString("nombre_contacto");
			this.correo_contacto=jsonObject.getString("correo_contacto");
			this.descripcion=jsonObject.getString("descripcion");
			this.modelo_maquina=jsonObject.getString("modelo_maquina");
			this.nro_serie=jsonObject.getString("nro_serie");
			this.cantidad=jsonObject.getString("cantidad");
			this.codigo=jsonObject.getString("codigo");
			this.comentarios=jsonObject.getString("comentarios");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	@Override
	public String toString() {
		return " [{\"id\":\"" + id + "\", \"rut\":\"" + rut
				+ "\", \"nombre\":\"" + nombre + "\", \"razon_social\":\""
				+ razon_social + "\", \"direccion\":\"" + direccion
				+ "\", \"logo\":\"" + logo
				+ "\", \"tipo_orden_id\":\"" + tipoordenid
				+ "\", \"fecha_creacion\":\"" + fecha_creacion
				+ "\", \"fecha_ejecucion\":\"" + fecha_ejecucion
				+ "\", \"latitud\":\"" + latitud + "\", \"longitud\":\""
				+ longitud + "\", \"estado\":\"" + estado
				+ "\", \"nombre_contacto\":\"" + nombre_contacto
				+ "\", \"correo_contacto\":\"" + correo_contacto + "\"}]";
	}


}



