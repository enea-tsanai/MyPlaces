package com.googlemapsme.googlemaps_me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Directions extends Activity { 
	EditText Dest;
	EditText Src;
	TextView Destview;
	TextView Srcview;
    Button Submit;
    Button Gps;
    Button Car;
    Button Walk;
    Button Cycle;
    Button Transit;
    
    public static int clicked=0;
    public static PlacesList nearplaces;
    double latitude;
    double longitude;
    String strd;
    String strs;
    String dest;
    String mode;
    String ok="ok";
    TextView txt1; 
    public static String unit;
    
	 @Override
	 public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directions);
		Submit=(Button)findViewById(R.id.Submitbutton);
		Gps=(Button)findViewById(R.id.GPSbutton);
		Dest=(EditText)findViewById(R.id.Desttext);
		Src=(EditText)findViewById(R.id.Srctext);
		Destview = (TextView)findViewById(R.id.DestView);
		Srcview = (TextView)findViewById(R.id.Srcview);
		// Find the ListView resource.     
		Car = (Button) findViewById(R.id.car); 
		Cycle = (Button) findViewById(R.id.cycle); 
		Walk = (Button) findViewById(R.id.walk); 
		Transit = (Button) findViewById(R.id.transit); 
		
			       
        for (Place place : SearchActivity.nearPlaces.results) {
 		   if ( SinglePlaceActivity.place_name.toString().equals(place.name.toString()) ){
 		      
 			  //Toast.makeText(getApplicationContext(),"same name= " + place.name, Toast.LENGTH_LONG).show(); //just for debugging 
 			  InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
 			  imm.hideSoftInputFromWindow(Dest.getWindowToken(), 0);   
 			  Dest.setText(place.formatted_address);	
 	    }
 	    }
		
		Gps.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
       
                 // Perform action on click
            	 InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
  				 imm.hideSoftInputFromWindow(Dest.getWindowToken(), 0);   
  				 Src.setText((GoogleMaps_me.my_latitude)+","+(GoogleMaps_me.my_longitude));
             }
         });
	
		
		Submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            	strd = Dest.getText().toString();
            	strs = Src.getText().toString();
            	dest=Dest.getText().toString();
            	dest=dest.replace(" ", "+");
            	//Toast.makeText(getApplicationContext(),"Strd= " + strd+"\n Mode="+mode+"\nStrs="+strs, Toast.LENGTH_LONG).show();
                if(Dest.getText().toString().trim().length()== 0 || Src.getText().toString().trim().length()== 0){
                	Toast.makeText(Directions.this, "Source or Destination field missing!You need to fill all the fields", Toast.LENGTH_SHORT).show();
                } else {
                	SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
    				unit=SP.getString("listPref2", "metric");
    				GoogleMaps_me.language = SP.getString("listPref", "en");
                	SearchSrv srv = new SearchSrv();
          		    setProgressBarIndeterminateVisibility(true);
          			srv.execute();         		                 
                }
            }
        });
		
		
		
		Car.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"car is selected", Toast.LENGTH_LONG).show();
            mode = "driving";	
            }		
		});
		
		Cycle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"cycle is selected", Toast.LENGTH_LONG).show();
            mode = "bicycling";	
            }		
		});
		
		Walk.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"walking is selected", Toast.LENGTH_LONG).show();
            mode = "walking";	
            }		
		});
	
		Transit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            Toast.makeText(getApplicationContext(),"transit is selected", Toast.LENGTH_LONG).show();
            mode = "transit";	
            }		
		});
		
		
	 }
	 
	 private class SearchSrv extends AsyncTask<Void, Void, String>{
	    	@Override
	    	protected String doInBackground(Void... params) {
	    		// TODO Auto-generated method stub
				try {		 
	    			new DirectionsRequest().performSearch(strs,dest,mode);
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
				return ok;
	    	}
	    	
	    	@Override
	    	protected void onPostExecute(String ok) {
	    		// TODO Auto-generated method stub
           	    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
 				imm.hideSoftInputFromWindow(Dest.getWindowToken(), 0);   
	    		setProgressBarIndeterminateVisibility(false);
				Intent i = new Intent(getApplicationContext(), GoogleMaps_me.class);
				i.putExtra("near_places", SearchActivity.nearPlaces);
				i.putExtra("Result", ok );
				startActivity(i);
	    	}
	  }
}
