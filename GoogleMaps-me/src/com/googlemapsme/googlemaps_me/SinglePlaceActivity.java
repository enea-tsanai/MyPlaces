package com.googlemapsme.googlemaps_me;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
 
public class SinglePlaceActivity extends Activity {
 
    // Alert Dialog Manager
    AlertDialogManager alert = new AlertDialogManager();
 
    // Google Places
    GooglePlacesRequest googlePlaces;
 
    // Place Details
    PlaceDetails placeDetails;
 
    // Progress dialog
    ProgressDialog pDialog;
 
    // KEY Strings
    public static String KEY_REFERENCE = "reference"; // id of the place
    public static String KEY_PHOTO = "photo_reference";
    public static String KEY_ICON = "icon";
    public boolean icon_exists;
    public boolean icon_downloaded;
    public boolean photo_exists;
    public boolean photo_downloaded;
    public static String place_name;    
	private Bitmap bitmap = null;
    Button GetDirections;
    Button Picture;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_place);
 
        Intent i = getIntent();
 
        // Place referece id
        String reference = i.getStringExtra(KEY_REFERENCE);
        String photo_reference = i.getStringExtra(KEY_PHOTO);
        String icon = i.getStringExtra(KEY_ICON);
        
        new LoadSinglePlaceDetails().execute(reference);
        
        icon_exists = false;
        icon_downloaded = false;
        if(!icon.equals("not_initialized_yet")){ 
       	icon_exists = true;   
        downloadImage(icon);
        }
        
        photo_exists = false;
        photo_downloaded = false;
        if(!photo_reference.equals("not_initialized_yet")){
        photo_exists = true;	   
        downloadImage("https://maps.googleapis.com/maps/api/place/photo?maxwidth=2048&photoreference="+photo_reference+"&sensor=true&key=AIzaSyDG5s7wUC7M6ANf90cvb7zJzSBDs1QpjXM");
        }
        else Toast.makeText(SinglePlaceActivity.this, "no photo available", Toast.LENGTH_SHORT).show();
       
        GetDirections = (Button) findViewById(R.id.getdirections);        
        GetDirections.setOnClickListener(new View.OnClickListener() {
					   
			           @Override
			           public void onClick(View arg0) {			        	   
			        	  Intent directionsmenu = new Intent();                
			 		      directionsmenu.setClass(SinglePlaceActivity.this,Directions.class);
			 		      startActivity(directionsmenu);   
			           }
			           
        });
    }
    
    /**
     * Background Async Task to Load Google places
     * */
    class LoadSinglePlaceDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SinglePlaceActivity.this);
            pDialog.setMessage("Loading profile ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }
 
        /**
         * getting Profile JSON
         * */
        protected String doInBackground(String... args) {
            String reference = args[0];
 
            // creating Places class object
            googlePlaces = new GooglePlacesRequest();
 
            // Check if used is connected to Internet
            try {
                placeDetails = googlePlaces.getPlaceDetails(reference);
 
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    /**
                     * Updating parsed Places into LISTVIEW
                     * */
                    if(placeDetails != null){
                        String status = placeDetails.status;
 
                        // check place deatils status
                        // Check for all possible status
                        if(status.equals("OK")){
                            if (placeDetails.result != null) {
                                String name = placeDetails.result.name;
                                String address = placeDetails.result.formatted_address;
                                String phone = placeDetails.result.formatted_phone_number;
                                String latitude = Double.toString(placeDetails.result.geometry.location.lat);
                                String longitude = Double.toString(placeDetails.result.geometry.location.lng);
                                //String types[] = placeDetails.result.types;
                                place_name = name;
                                
                           
                                float rating = placeDetails.result.rating;
                                boolean open_now = placeDetails.result.open_now;
                                
                                Log.d("Place ", name + address + phone + rating + open_now + latitude + longitude);
 
                                // Displaying all the details in the view
                                // single_place.xml
                                TextView lbl_name = (TextView) findViewById(R.id.name);
                                TextView lbl_address = (TextView) findViewById(R.id.address);
                                TextView lbl_phone = (TextView) findViewById(R.id.phone);
                                TextView lbl_location = (TextView) findViewById(R.id.location);
                                TextView lbl_rating = (TextView) findViewById(R.id.rating);
                                //TextView lbl_types = (TextView) findViewById(R.id.types);
                                //TextView lbl_open_now = (TextView) findViewById(R.id.open_now);
                                
                                // Check for null data from google
                                // Sometimes place details might missing
                                name = name == null ? "Not present" : name; // if name is null display as "Not present"
                                address = address == null ? "Not present" : address;
                                phone = phone == null ? "Not present" : phone;
                                latitude = latitude == null ? "Not present" : latitude;
                                longitude = longitude == null ? "Not present" : longitude;
                                //rating = rating == null ? "Not present" : rating;
                                
                                lbl_name.setText(name);
                                lbl_address.setText(address);
                                lbl_phone.setText(Html.fromHtml("<b>Phone:</b> " + phone));
                                //lbl_types.setText(Html.fromHtml("<b>Type:</b> " + types));
                                lbl_rating.setText(Html.fromHtml("<b>Rating:</b> " + rating));
                                //lbl_open_now.setText(Html.fromHtml("<b>Open now:</b> " + open_now));
                                lbl_location.setText(Html.fromHtml("<b>Latitude:</b> " + latitude + ", <b>Longitude:</b> " + longitude));
                                
                            }
                        }
                        else if(status.equals("ZERO_RESULTS")){
                            alert.showAlertDialog(SinglePlaceActivity.this, "Near Places",
                                    "Sorry no place found.",
                                    false);
                        }
                        else if(status.equals("UNKNOWN_ERROR"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry unknown error occured.",
                                    false);
                        }
                        else if(status.equals("OVER_QUERY_LIMIT"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry query limit to google places is reached",
                                    false);
                        }
                        else if(status.equals("REQUEST_DENIED"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Request is denied",
                                    false);
                        }
                        else if(status.equals("INVALID_REQUEST"))
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured. Invalid Request",
                                    false);
                        }
                        else
                        {
                            alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                    "Sorry error occured.",
                                    false);
                        }
                    }else{
                        alert.showAlertDialog(SinglePlaceActivity.this, "Places Error",
                                "Sorry error occured.",
                                false);
                    }
 
                }
            });
 
        }
 
    }
    
    
    private void downloadImage(String urlStr) {
		//progressDialog = ProgressDialog.show(this, "", 
		//"Downloading Image from " + urlStr);
		final String url = urlStr;
		
		new Thread() {
			public void run() {
				InputStream in = null;
				Message msg = Message.obtain();
				msg.what = 1;
				try {
				    in = openHttpConnection(url);
				    bitmap = BitmapFactory.decodeStream(in);
				    Bundle b = new Bundle();
				    b.putParcelable("bitmap", bitmap);
				    msg.setData(b);
				    in.close();
				} catch (IOException e1) {
				    e1.printStackTrace();
				}
				messageHandler.sendMessage(msg);					
			}
 		}.start();
	}
	
	private InputStream openHttpConnection(String urlStr) {
		InputStream in = null;
		int resCode = -1;
		
		try {
			URL url = new URL(urlStr);
			URLConnection urlConn = url.openConnection();
			
			if (!(urlConn instanceof HttpURLConnection)) {
			
				throw new IOException ("URL is not an Http URL");
			}
			
			HttpURLConnection httpConn = (HttpURLConnection)urlConn;
			httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect(); 

            resCode = httpConn.getResponseCode();                 
            if (resCode == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();                                 
            }         
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return in;
	}
	
	private Handler messageHandler = new Handler() {
		
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 1:
				if((icon_exists==true)&&(photo_downloaded==false)){
					ImageView img = (ImageView) findViewById(R.id.imageview01);
					img.setImageBitmap((Bitmap)(msg.getData().getParcelable("bitmap")));
					icon_downloaded=true;
				}
				if((icon_downloaded==true)&&(photo_exists==true)){
					ImageView img1 = (ImageView) findViewById(R.id.imageview02);
					img1.setImageBitmap((Bitmap)(msg.getData().getParcelable("bitmap")));
					photo_downloaded=true;
				}
				break;
			case 2:
				TextView text = (TextView) findViewById(R.id.textview01);
				text.setText(Html.fromHtml(""));
				break;
			}
	//		progressDialog.dismiss();
		}
	};
 
}