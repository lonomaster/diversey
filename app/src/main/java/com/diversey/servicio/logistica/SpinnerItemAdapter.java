package com.diversey.servicio.logistica;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpinnerItemAdapter extends ArrayAdapter<String> {

	Context context;
	ArrayList<String> list;
	private int defaultPosition;
	public Typeface BebasNeueBook;

	public int getDefaultPosition() {
		return defaultPosition;
	}

	public SpinnerItemAdapter(Context context, ArrayList<String> objects) {
		super(context, 0, objects);
		BebasNeueBook = Typeface.createFromAsset(context.getAssets(),
				"BebasNeueBook.ttf");
		this.context = context;
		list = objects;
	}

	public void setDefaultPostion(int position) {
		this.defaultPosition = position;
	}

	@Override
	public View getDropDownView(int position, View convertView,
								ViewGroup parent) {
		return getCustom(position, convertView, parent);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return getCustom(position, convertView, parent);
	}

	public View getCustom(int position, View convertView, ViewGroup parent) {

		View row = LayoutInflater.from(context).inflate(
				R.layout.item_spinner, parent, false);
		TextView label = (TextView) row.findViewById(R.id.spinner_text);
		label.setTypeface(BebasNeueBook);
		label.setText(list.get(position));

		return row;
	}

}