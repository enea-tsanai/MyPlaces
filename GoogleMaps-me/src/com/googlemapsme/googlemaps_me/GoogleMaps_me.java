package com.googlemapsme.googlemaps_me;


import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;


public class GoogleMaps_me extends MapActivity 
{
	
/* Class vars */	
	
	 // flag for Internet connection status
    Boolean isInternetPresent = false;
 
    // Connection detector class
    ConnectionDetector cd;
 
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();

    // GPS Location
    GPSTracker gps;
 
    // Progress dialog
    ProgressDialog pDialog;

    public static double gps_latitude;
    public static double gps_longitude;
    
	Gson gson = new Gson();

	 // Nearest places
    PlacesList nearPlaces;
 
    // Map view
    MapView mapView;
 
    // Map overlay items
    List<Overlay> mapOverlays;
 
    AddItemizedOverlay itemizedOverlay;
    
    GeoPoint geoPoint;
    
    // Map controllers
    MapController mc;
 
    public static double latitude;
    public static double longitude;
    public static double my_latitude;
    public static double my_longitude;    
    public String directions_status;
    public static String language;
    public static String radius="50000"; //maximum default value in meters    
    public static String search;

    /* Directions vars */
 	String s;
 	String s1;
 	String s2;
 	/* Directions vars */
 	
    OverlayItem overlayitem;
    GeoPoint point;

    /*End of class VARS*/	
	
/** Called when the activity is first created. */
   @Override
   public void onCreate(Bundle savedInstanceState)
   {
      super.onCreate(savedInstanceState);
      
      
      // Check if Internet present      
      cd = new ConnectionDetector(getApplicationContext());  
      // isInternetPresent = false;
      isInternetPresent = cd.isConnectingToInternet();
      
      //Toast.makeText(GoogleMaps_me.this, "isInternetPresent:"+ isInternetPresent, Toast.LENGTH_SHORT).show();
  
      if (isInternetPresent!=true) {
          // Internet Connection is not present
          alert.showAlertDialog(GoogleMaps_me.this, "Internet Connection Error",
                  "Please connect to working Internet connection", false);
          // stop executing code by return
          return;
      }

      // creating GPS Class object
      gps = new GPSTracker(this);
    

      // check if GPS location can get
      if (gps.canGetLocation()) {
          // Sending user current geo location
      	gps_latitude = gps.getLatitude();
      	gps_longitude = gps.getLongitude();
      	nearPlaces = null;
        Log.d("Your Location", "latitude:" + gps.getLatitude() + ", longitude: " + gps.getLongitude());
      } else {
          // Can't get user's current location
          alert.showAlertDialog(GoogleMaps_me.this, "GPS Status",
                  "Couldn't get location information. Please enable GPS",
                  false);
          // stop executing code by return
          return;
      }
      
//============== start displaying ============== \\ 
      
      setContentView(R.layout.activity_googlemaps);
    
      // Getting intent data
      Intent i = getIntent();
      
      // Nearplaces list
      nearPlaces = (PlacesList) i.getSerializableExtra("near_places");
      directions_status = i.getStringExtra("Result");
      
      mapView = (MapView) findViewById(R.id.mapview);
      mapView.setBuiltInZoomControls(true);
      mapOverlays = mapView.getOverlays();
      mapView.postInvalidate();
      
      my_latitude = gps_latitude;
      my_longitude = gps_longitude;
    
      Log.d("MY Location", "latitude:" + gps.getLatitude()*1E6 + ", longitude: " + gps.getLongitude()*1E6);
     
      //Toast.makeText(GoogleMaps_me.this, "Lat:"+my_latitude+","+"Lon:"+my_longitude, Toast.LENGTH_SHORT).show();

      //my_latitude = 38.246371 ;
      //my_longitude = 21.738939;
      // Geopoint to place on map
      //
      geoPoint = new GeoPoint((int) (my_latitude*1E6), (int) (my_longitude*1E6));
      
    //geoPoint = new GeoPoint((int) (my_latitude), (int) (my_longitude));
      
      
      // Drawable marker icon
      Drawable drawable_user = this.getResources().getDrawable(R.drawable.marker_red);
      itemizedOverlay = new AddItemizedOverlay(drawable_user, this);

      // Map overlay item
      overlayitem = new OverlayItem(geoPoint, "Your Location","That is you!");
      itemizedOverlay.addOverlay(overlayitem);
      mapOverlays.add(itemizedOverlay);
      itemizedOverlay.populateNow();

      // Drawable marker icon
      Drawable drawable = this.getResources()
              .getDrawable(R.drawable.marker_blue);

      itemizedOverlay = new AddItemizedOverlay(drawable, this);

      mc = mapView.getController();
     // mc.setZoom(10);
      mc.animateTo(geoPoint);
     // mapView.getController().zoomToSpan((int) (my_latitude*1e6),(int) (my_longitude*1e6));
//      mc.setZoom(14);
      // These values are used to get map boundary area
      // The area where you can see all the markers on screen
      int minLat = Integer.MAX_VALUE;
      int minLong = Integer.MAX_VALUE;
      int maxLat = Integer.MIN_VALUE;
      int maxLong = Integer.MIN_VALUE;

      // check for null in case it is null
      if (nearPlaces != null) {
    	  if (nearPlaces.results != null) {
    
          // loop through all the places
          for (Place place : nearPlaces.results) {
              latitude = place.geometry.location.lat; // latitude
              longitude = place.geometry.location.lng; // longitude
              // Geopoint to place on map
              geoPoint = new GeoPoint((int) (latitude * 1E6),
                      (int) (longitude * 1E6));

             
              // Map overlay item
              overlayitem = new OverlayItem(geoPoint, place.name,
                      place.vicinity);

              itemizedOverlay.addOverlay(overlayitem);

              // calculating map boundary area
              minLat  = (int) Math.min( geoPoint.getLatitudeE6(), minLat );
              minLong = (int) Math.min( geoPoint.getLongitudeE6(), minLong);
              maxLat  = (int) Math.max( geoPoint.getLatitudeE6(), maxLat );
              maxLong = (int) Math.max( geoPoint.getLongitudeE6(), maxLong );
          }
          mapOverlays.add(itemizedOverlay);

          // showing all overlay items
          itemizedOverlay.populateNow();
      }
      }
      // Adjusting the zoom level so that you can see all the markers on map
      mapView.getController().zoomToSpan(Math.abs( minLat - maxLat ), Math.abs( minLong - maxLong ));

      if(directions_status != null){
    	  
    	  
    	  String dist,dur;
		  String travel;
		  mapOverlays = mapView.getOverlays(); 
          Drawable makerDefault = this.getResources().getDrawable(R.drawable.route_info); 
          MirItemizedOverlay itemizedOverlay2 = new MirItemizedOverlay(makerDefault,mapView);
          mapView.getOverlays().add(new RoutePathOverlay(DirectionsRequest.pointToDraw));
          for (int j = 0; j < DirectionsRequest.legs1.length(); j++) {
			  try {
				  DirectionsRequest.steps1 = DirectionsRequest.legs1.getJSONObject(j);
				  if (j == 0){
					   s=DirectionsRequest.steps1.getString("start_location");
					   JSONObject distance = DirectionsRequest.steps.getJSONObject("distance");
					   dist=distance.getString("text");
					   JSONObject duration = DirectionsRequest.steps.getJSONObject("duration");
					   dur=duration.getString("text");
				  }
				  else{
					   s=DirectionsRequest.steps1.getString("end_location");
					   JSONObject distance2 = DirectionsRequest.steps1.getJSONObject("distance");
					   dist=distance2.getString("text");
					   JSONObject duration2 = DirectionsRequest.steps1.getJSONObject("duration");
					   dur=duration2.getString("text");
				  }
				  s1=s.substring(s.indexOf(":") + 1); // cut off beginning
				  s=s1.substring(s1.indexOf(":") + 1, s1.indexOf("}")); // isolate longitude
				  s1=s1.substring(0, s1.indexOf(",")); // isolate latitude
				  s.replaceAll(" ", ""); // remove spaces
				  s1.replaceAll(" ", "");
				  s2 = DirectionsRequest.steps1.getString("html_instructions");
				  s2 = s2.replaceAll("<b>", "");
				  s2 = s2.replaceAll("</b>", "");
				  s2=s2.replaceAll("<div style=\"font-size:0.9em\"", "");
				  s2=s2.replaceAll("</div>", "");
				  travel=DirectionsRequest.steps1.getString("travel_mode");
				  /* Get lat and lng of each direction*/
				  latitude=Double.valueOf(s).doubleValue();
				  longitude=Double.valueOf(s1).doubleValue();
				  point = new GeoPoint((int) (latitude * 1E6),(int) (longitude * 1E6));
				  OverlayItem overlayItem = new OverlayItem(point,"Directionpoint info","Html Instructions: "+s2+"\nDistance: "+dist+"\nDuration: "+dur+"\nLatitude: "+latitude+"\nLongitude:"+ longitude+"\nTravel mode: "+travel);
	              itemizedOverlay2.addOverlayItem(overlayItem);
	              mapOverlays.add(itemizedOverlay2);
				  /* Get lat and lng of each direction*/
				  s2 = DirectionsRequest.steps1.getString("html_instructions");
				  
			  } catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
			  }
		 
		  } 
          mapView.getController().animateTo(point);
      }
      mapView.postInvalidate();	    
      
  }
        
   
 
   /* Create Options Menu */ 
   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
      MenuInflater inflater = getMenuInflater();
      inflater.inflate(R.menu.menu, menu);
   
      SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
      searchView.setOnKeyListener(new OnKeyListener() {

          @Override
          public boolean onKey(View v, int keyCode, KeyEvent event) {
              return true; // This code never fires
          }
      });
      searchView.setOnQueryTextListener(queryTextListener);
      return true;
      
    }
 
   final SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
	    @Override
	    public boolean onQueryTextChange(String newText) {
	        return true;
	    }

	    @Override
	    public boolean onQueryTextSubmit(String query) {
	        // Do something
	    	search = query ;
			Intent searchactivity = new Intent();                
			searchactivity.setClass(GoogleMaps_me.this,SearchActivity.class);
	    	startActivity(searchactivity);
	        return true;
	    }
	};

  /* Menu items */
   @Override
   public boolean onOptionsItemSelected(MenuItem item)
   {

     switch (item.getItemId())
     {     
         
     case R.id.clear_menu: 
    	 mapOverlays.clear(); 
         mapView.invalidate();  
                
         //Toast.makeText(GoogleMaps_me.this, "Lat:"+my_latitude+","+"Lon:"+my_longitude, Toast.LENGTH_SHORT).show();

         // geoPoint = new GeoPoint((int) (38.244261*1e6), (int) (21.735506*1e6));
         geoPoint = new GeoPoint( (int) (my_latitude*1e6), (int) (my_longitude*1e6) );
         Drawable drawable_user = this.getResources().getDrawable(R.drawable.marker_red);
         itemizedOverlay = new AddItemizedOverlay(drawable_user, this);
         overlayitem = new OverlayItem(geoPoint, "Your Location", "That is you!");
         itemizedOverlay.addOverlay(overlayitem);
         mapOverlays.add(itemizedOverlay);
         itemizedOverlay.populateNow();
         return true;
         
     case R.id.preferences:
    	 SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
		 language = SP.getString("listPref", "en");
		 radius = SP.getString("pref_text", "50000");
         Intent settings = new Intent(getBaseContext(),Settings.class);
         startActivity(settings);
         return true;

     default:
         return super.onOptionsItemSelected(item);
     }
  }    
 /* End of Items Menu */
 
  @Override
  protected boolean isRouteDisplayed() {
    // TODO Auto-generated method stub
    return false;
  }
 
  /* MirItemizedOverlay Class (creates the markers on map )*/
  private class MirItemizedOverlay extends ItemizedOverlay<OverlayItem> {
    
	private List<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Context c;
    public MirItemizedOverlay(Drawable defaultMarker,MapView mapView) {
    	super(boundCenter(defaultMarker));
		c = mapView.getContext();
      // TODO Auto-generated constructor stub
    }
    @Override
    protected OverlayItem createItem(int i) {
      return mOverlays.get(i);
    } 

    @Override
    public int size() {
      return mOverlays.size();
    }

    public void addOverlayItem(OverlayItem overlayItem) {
      mOverlays.add(overlayItem);
      populate();
    }
    
    @Override
    protected boolean onTap(int index) {
      OverlayItem item = mOverlays.get(index);
      AlertDialog.Builder dialog = new AlertDialog.Builder(this.c);
      dialog.setTitle(item.getTitle());
      dialog.setMessage(item.getSnippet());
      dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
         public void onClick(DialogInterface dialog, int which) {
         }
     });
      dialog.show();
      return true;
    }

    public void addOverlayItem(double lat, double lon, String title) {
      GeoPoint point = new GeoPoint((int) (lat * 1E6),(int) (lon * 1E6));
      OverlayItem overlayItem = new OverlayItem(point, title, null);
      addOverlayItem(overlayItem);
    }

       }
 /* End of MirItemizedOverlay Class (creates the markers on map )*/


}