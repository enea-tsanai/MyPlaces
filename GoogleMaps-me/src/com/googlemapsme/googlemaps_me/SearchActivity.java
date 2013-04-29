package com.googlemapsme.googlemaps_me;

import java.util.ArrayList;
import java.util.HashMap;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
 
public class SearchActivity extends Activity {
 
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Google Places
    GooglePlacesRequest googlePlaces;
 
    // Places List
    public static PlacesList nearPlaces;
    //public static PlacesList nearplaces;

    // Button
    Button btnShowOnMap;
 
    // Progress dialog
    ProgressDialog pDialog;
 
    // Places Listview
    ListView lv;
 
    // ListItems data
    ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
 
    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_NAME = "name"; // name of the place
    public static String KEY_VICINITY = "vicinity"; // Place area name
    public static String KEY_ICON = "icon";
    public static String KEY_PHOTO = "photo_reference";
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
		                
        btnShowOnMap = (Button) findViewById(R.id.btn_show_map);        
        lv = (ListView) findViewById(R.id.list);
        
				btnShowOnMap.setOnClickListener(new View.OnClickListener() {
					   
			           @Override
			           public void onClick(View arg0) {
			        	   Intent i = new Intent(getApplicationContext(),
			                       GoogleMaps_me.class);
			               i.putExtra("near_places", nearPlaces);
			               // staring activity
			               startActivity(i);
			           }
			       });
        /**
         * ListItem click event
         * On selecting a listitem SinglePlaceActivity is launched
         * */
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting values from selected ListItem
                String reference = ((TextView) view.findViewById(R.id.reference)).getText().toString();
                String icon = ((TextView) view.findViewById(R.id.icon)).getText().toString();
                String photo = ((TextView) view.findViewById(R.id.photo)).getText().toString();

                // Starting new intent
                Intent in = new Intent(getApplicationContext(), SinglePlaceActivity.class);
                // Sending place refrence id to single place activity
                // place refrence id used to get "Place full details"
                
                in.putExtra(KEY_REFERENCE,reference);
                in.putExtra(KEY_ICON,icon);
                in.putExtra(KEY_PHOTO,photo);
                //Toast.makeText(SearchActivity.this, "photo_ref" + photo , Toast.LENGTH_LONG).show(); //just for debuging
                startActivity(in);
            }
        });
           new LoadPlaces().execute();
    }
    
    /**
     * Background Async Task to Load Google places
     * */
   
    /**
     * Background Async Task to Load Google places
     * */
    class LoadPlaces extends AsyncTask<String, String, PlacesList >{
    	
    	@Override
         protected void onPreExecute() {
             super.onPreExecute();
             pDialog = new ProgressDialog(SearchActivity.this);
             pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(false);
             pDialog.show();
         }
    	
    	@Override
    	 protected PlacesList doInBackground(String... params) {
    		// TODO Auto-generated method stub
			 googlePlaces = new GooglePlacesRequest();
			 PlacesList pl = null;
			 String s = GoogleMaps_me.search;
			 s = s.replace(" ", "+");
			 try {
				 pl = googlePlaces.search(s); 
			 } catch (Exception e) {
				 // TODO Auto-generated catch block
				 e.printStackTrace();
			 }
			 return pl;
    	}
    	
    	  	@Override
    	 	protected void onPostExecute( PlacesList result) {
    		// dismiss the dialog after getting all products
    		pDialog.dismiss();
    		//nearPlaces=result;
    		
    		if (result != null) {
        		nearPlaces=result;
             }
            // Get json response status
    		final String status = nearPlaces.status;
    		
    		SearchActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    // Check for all possible status
                     if(status.equals("OK")){
                        // Successfully got places details
                        if (nearPlaces.results != null) {
                            // loop through each place
                            for (Place p : nearPlaces.results) {
                                HashMap<String, String> map = new HashMap<String, String>();
 
                                // Place reference won't display in listview - it will be hidden
                                // Place icon_reference won't display in listview - it will be hidden
                                // Place photo_reference won't display in listview - it will be hidden
                                // Place reference is used to get "place full details"
                                map.put(KEY_REFERENCE, p.reference);
                                map.put(KEY_NAME, p.name);
                                map.put(KEY_ICON, p.icon);
                                String photo_ref= "not_initialized_yet";
                                
                                if(p.photos!=null){
                                	photo_ref = p.photos[0].photo_reference;	
                                }
                                
                                map.put(KEY_PHOTO,photo_ref);
                               
                                //adding HashMap to ArrayList
                                placesListItems.add(map);
                            }
                            // list adapter
                            ListAdapter adapter = new SimpleAdapter(SearchActivity.this, placesListItems,
                                    R.layout.list_item,
                                    new String[] { KEY_REFERENCE, KEY_NAME, KEY_ICON, KEY_PHOTO}, new int[] {
                                            R.id.reference, R.id.name, R.id.icon, R.id.photo});
 
                            // Adding data into listview
                            lv.setAdapter(adapter);
                        }
                        
                     }
                     else if(status.equals("ZERO_RESULTS")){
                            // Zero results found
                            alert.showAlertDialog(SearchActivity.this, "Near Places",
                                    "Sorry no places found. Try to change the types of places",
                                    false);
                     }
                     else if(status.equals("UNKNOWN_ERROR"))
                     {
                            alert.showAlertDialog(SearchActivity.this, "Places Error",
                                    "Sorry unknown error occured.",
                                    false);
                     }
                     else if(status.equals("OVER_QUERY_LIMIT"))
                     {
                            alert.showAlertDialog(SearchActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                      }
                      else if(status.equals("REQUEST_DENIED"))
                      {
                            alert.showAlertDialog(SearchActivity.this, "Places Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                      }
                      else if(status.equals("INVALID_REQUEST"))
                      {
                            alert.showAlertDialog(SearchActivity.this, "Places Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                      }
                      else
                      {
                            alert.showAlertDialog(SearchActivity.this, "Places Error",
                                    "Sorry error occured.",
                                    false);
                      }
                }
            });
         
    	}
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
 
}