/*  FiCode: Android app for IT Fiscal Code calculation, with the support for 
    the omocodia and the generation of abroad codes.
    Copyright (C) 2013  Giacomo Marciani <giacomo.marciani@gmail.com>.

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */


package com.marciani.ficode;


import java.util.List;

import com.marciani.ficode.database.AbroadCodes;
import com.marciani.ficode.database.AbroadCodesDataSource;
import com.marciani.ficode.database.FiCodeHelper;
import com.marciani.ficode.database.ItalyCodes;
import com.marciani.ficode.database.ItalyCodesDataSource;

import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.text.Editable;
import android.text.TextWatcher;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;


/**
 * This class displays the list of municipalities and nationalities.
 * 
 * @author Giacomo Marciani <giacomo.marciani@gmail.com>
 * 
 * @version 1.0 03.04.2013
 * 
 */

public class CityList extends ListActivity {		

	private ItalyCodesDataSource datasourceItalyCodes;
	private AbroadCodesDataSource datasourceAbroadCodes;
	private static final String selectionFilterItalyCodes = FiCodeHelper.ItalyCodesColumn.city + " like ";
	private static final String selectionFilterAbroadCodes = FiCodeHelper.AbroadCodesColumn.city + " like ";
	private static final String noFilter = null;
	
	EditText etCityBrowse;
	String city = "";
	String province = "";
	String nationalCode = "";	
	String userNationality = "";
	String filter = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city_list);
		setupActionBar();		
		
		Intent intFiCode = getIntent();
		city = intFiCode.getStringExtra(FiCode.Data.STR_CITY);
		userNationality = intFiCode.getStringExtra(FiCode.Data.STR_USER_NATIONALITY);
				
		etCityBrowse = (EditText) findViewById(R.id.etCityBrowse);	
		
		etCityBrowse.setText(city);	
		etCityBrowse.setSelection(etCityBrowse.getText().length());
		
		etCityBrowse.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {
				city = s.toString();
				
				if(userNationality.equals("Italy")) {
					filter = (city.length() == 0) ? noFilter : (selectionFilterItalyCodes + "'" + city + "%'");
					
					List<ItalyCodes> listItalyCode = datasourceItalyCodes.getItalyCodes(filter);
					ArrayAdapter<ItalyCodes> adapterItalyCode = new ArrayAdapter<ItalyCodes>(getApplicationContext(), android.R.layout.simple_list_item_1, listItalyCode);
					setListAdapter(adapterItalyCode);						
				} else {
					filter = (city.length() == 0) ? noFilter : (selectionFilterAbroadCodes + "'" + city + "%'");
					
					List<AbroadCodes> listAbroadCode = datasourceAbroadCodes.getAbroadCodes(filter);
					ArrayAdapter<AbroadCodes> adapterAbroadCode = new ArrayAdapter<AbroadCodes>(getApplicationContext(), android.R.layout.simple_list_item_1, listAbroadCode);
					setListAdapter(adapterAbroadCode);						
				}							
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub	
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub	
			}			
		});	
	}

	@Override
	public void onResume() {
		super.onResume();				
		
		if(userNationality.equals("Italy")) {
			datasourceItalyCodes = new ItalyCodesDataSource(this);
			datasourceItalyCodes.open();		
			
			List<ItalyCodes> listItalyCode = datasourceItalyCodes.getItalyCodes(selectionFilterItalyCodes + "'" + city + "%'");
			ArrayAdapter<ItalyCodes> adapterItalyCode = new ArrayAdapter<ItalyCodes>(this, android.R.layout.simple_list_item_1, listItalyCode);
			setListAdapter(adapterItalyCode);			
		} else {
			datasourceAbroadCodes = new AbroadCodesDataSource(this);
			datasourceAbroadCodes.open();		
			
			List<AbroadCodes> listAbroadCode = datasourceAbroadCodes.getAbroadCodes(selectionFilterAbroadCodes + "'" + city + "%'");
			ArrayAdapter<AbroadCodes> adapterAbroadCode = new ArrayAdapter<AbroadCodes>(this, android.R.layout.simple_list_item_1, listAbroadCode);
			setListAdapter(adapterAbroadCode);					
		}		
	}
	
	@Override
	public void onPause() {		
		super.onPause();
		
		if(userNationality.equals("Italy")) {
			datasourceItalyCodes.close();
		} else {
			datasourceAbroadCodes.close();
		}		
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);	
		
		Toast toastCity;
		
		if(userNationality.equals("Italy")) {
			ItalyCodes item = (ItalyCodes) l.getAdapter().getItem(position);
			
			city = item.getCity();
			province = item.getProvince();
			nationalCode = item.getNationalCode();
			
			etCityBrowse.setText(city);	
			etCityBrowse.setSelection(etCityBrowse.getText().length());
			
			toastCity = Toast.makeText(getApplicationContext(), city + " (" + province + ")", Toast.LENGTH_SHORT);
			toastCity.show();
		
		} else {
			AbroadCodes item = (AbroadCodes) l.getAdapter().getItem(position);
			
			city = item.getCity();
			nationalCode = item.getNationalCode();
			
			etCityBrowse.setText(city);	
			etCityBrowse.setSelection(etCityBrowse.getText().length());
			
			toastCity = Toast.makeText(getApplicationContext(), city, Toast.LENGTH_SHORT);
			toastCity.show();			
		}		
		
		Intent intFiCode = new Intent(getApplicationContext(), FiCode.class);
		intFiCode.putExtra(FiCode.Data.STR_CITY, city);
		intFiCode.putExtra(FiCode.Data.STR_NATIONAL_CODE, nationalCode);
		startActivity(intFiCode);
		
		finish();
	}
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setTitle(R.string.CityList);
		}
	}
}
