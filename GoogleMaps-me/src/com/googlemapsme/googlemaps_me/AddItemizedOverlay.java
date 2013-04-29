package com.googlemapsme.googlemaps_me;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.googlemapsme.googlemaps_me.SinglePlaceActivity.LoadSinglePlaceDetails;
 
/**
 * Class used to place marker or any overlay items on Map
 * */
public class AddItemizedOverlay extends ItemizedOverlay<OverlayItem> {
 
	   public static int PLACE_INDEX;
	   public double my_lat;
	   public double my_lon;
	   
	   public static String KEY_REFERENCE = "reference";
	   public static String KEY_ICON = "icon";
	   public static String KEY_PHOTO = "photo_reference";
			   
       private ArrayList<OverlayItem> mapOverlays = new ArrayList<OverlayItem>();
 
       private Context context;
 
       public AddItemizedOverlay(Drawable defaultMarker) {
            super(boundCenterBottom(defaultMarker));
       }
 
       public AddItemizedOverlay(Drawable defaultMarker, Context context) {
            this(defaultMarker);
            this.context = context;
       }
 
       @Override
       public boolean onTouchEvent(MotionEvent event, MapView mapView)
       {   
 
           if (event.getAction() == 1) {
               GeoPoint geopoint = mapView.getProjection().fromPixels(
                   (int) event.getX(),
                   (int) event.getY());
               // latitude
               double lat = geopoint.getLatitudeE6() / 1E6;
               // longitude
               double lon = geopoint.getLongitudeE6() / 1E6;
               // display geopoint of pressed point on map
               // Toast.makeText(context, "Lat: " + lat + ", Lon: "+lon, Toast.LENGTH_SHORT).show(); 
           }
           return false;
       } 
 
       @Override
       protected OverlayItem createItem(int i) {
          return mapOverlays.get(i);
       }
 
       @Override
       public int size() {
          return mapOverlays.size();
       }
 
       @Override
       protected boolean onTap(int index) {
         OverlayItem item = mapOverlays.get(index);
         PLACE_INDEX = index;
         AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);
        
         if(item.getTitle() == "Your Location"){      
        	 dialog.setTitle(item.getTitle())
             .setMessage(item.getSnippet())
              	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
             AlertDialog alert = dialog.create();
             dialog.show();
         }
         else{
        	 dialog.setTitle(item.getTitle())
        	 .setMessage(item.getSnippet())
          	 .setPositiveButton("show details", new DialogInterface.OnClickListener() {
          		 public void onClick(DialogInterface dialog, int which) {
          			 Intent i = new Intent(context, SinglePlaceActivity.class);
          			 int place_clicked = 0;
          			 String details_ref = "not_initialized_yet";
          			 String icon_ref = "not_initialized_yet";
          			 String photo_ref = "not_initialized_yet"; 
          			 for (Place place : SearchActivity.nearPlaces.results) {
          				 if (place_clicked==AddItemizedOverlay.PLACE_INDEX){  
                 		 details_ref = place.reference;
                 		 icon_ref = place.icon;
                         if(place.photos!=null) photo_ref = place.photos[0].photo_reference;
                         //Toast.makeText(context, "photo : " + photo_ref , Toast.LENGTH_SHORT).show(); //just for debuging
          				 }
          				 place_clicked++;
          			 }
          			 i.putExtra( KEY_REFERENCE,details_ref);
          			 i.putExtra( KEY_PHOTO,photo_ref);
          			 i.putExtra( KEY_ICON,icon_ref);
          			 context.startActivity(i);             	
          		 }
          	 	})
          	 .setNegativeButton("back to map", new DialogInterface.OnClickListener() {
          		 public void onClick(DialogInterface dialog, int which) {
             }
          	 });
        	 AlertDialog alert = dialog.create();
        	 dialog.show();
         }
         return true;
       }
 
       public void addOverlay(OverlayItem overlay) {
          mapOverlays.add(overlay);
       }
 
       public void populateNow(){
           this.populate();
       }
 
    }
