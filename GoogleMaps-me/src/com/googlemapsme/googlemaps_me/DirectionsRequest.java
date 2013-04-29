package com.googlemapsme.googlemaps_me;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;

public class DirectionsRequest {
     
	// Create our transport.
	private static final HttpTransport transport = new ApacheHttpTransport();
	
	private static final String LOG_KEY = "GooglePlacesDirections";
	private static final String DIRECTIONS_SEARCH_URL =  "https://maps.googleapis.com/maps/api/directions/json?";
	
	public String result;
	
	// Get the Place current location
	double latitude = GoogleMaps_me.my_latitude;
	double longitude = GoogleMaps_me.my_longitude;
	
	public static JSONObject jObject;
	public static JSONArray array;
	public static JSONObject routes;
	public static JSONArray legs;
	public static JSONObject steps;
	public static JSONObject steps1;
	public static JSONArray legs1;
	static String encodedString;
	static JSONObject overviewPolylines;
	static List<GeoPoint> pointToDraw; 
	
	public void performSearch(String origin,String dest,String mode) throws Exception {
		try {
			Log.v(LOG_KEY, "Start Search");
			GenericUrl reqUrl = new GenericUrl(DIRECTIONS_SEARCH_URL);
			reqUrl.put("origin", origin);
			reqUrl.put("destination", dest);
			reqUrl.put("sensor", "false");
			reqUrl.put("mode", mode);
			reqUrl.put("location", latitude + "," + longitude);
			reqUrl.put("language",GoogleMaps_me.language);
			reqUrl.put("units",Directions.unit);
			Log.v(LOG_KEY, "url= " + reqUrl);
			HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
			HttpRequest request = httpRequestFactory.buildGetRequest(reqUrl);

			    result=request.execute().parseAsString();
				Log.v(LOG_KEY, result);	
				jObject = new JSONObject(result);
				array = jObject.getJSONArray("routes");
				routes = array.getJSONObject(0);
			    legs = routes.getJSONArray("legs");
				steps = legs.getJSONObject(0);
				legs1 = steps.getJSONArray("steps");
			    overviewPolylines = routes.getJSONObject("overview_polyline");
				encodedString = overviewPolylines.getString("points");
				pointToDraw = decodePoly(encodedString);
				
		} catch (HttpResponseException e) {
			Log.e(LOG_KEY, "error= "+e);
			throw e;
		}
		
		catch (IOException e) {
			// TODO: handle exception
			throw e;
		}
	}

	public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {
			   
		  return transport.createRequestFactory(new HttpRequestInitializer() {
		   @SuppressWarnings("deprecation")
		public void initialize(HttpRequest request) {
		    GoogleHeaders headers = new GoogleHeaders();
		    headers.setApplicationName("GooglemapsApp");
		    request.setHeaders(headers);
		    @SuppressWarnings("deprecation")
			JsonHttpParser parser = new JsonHttpParser(new JacksonFactory()) ;
		    request.addParser(parser);
		   }
		});
	}
	
	private List<GeoPoint> decodePoly(String encoded) {

	    List<GeoPoint> poly = new ArrayList<GeoPoint>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int b, shift = 0, result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            b = encoded.charAt(index++) - 63;
	            result |= (b & 0x1f) << shift;
	            shift += 5;
	        } while (b >= 0x20);
	        int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
	        lng += dlng;

	        GeoPoint p = new GeoPoint((int) (((double) lat / 1E5) * 1E6), (int) (((double) lng / 1E5) * 1E6));
	        poly.add(p);
	    }

	    return poly;
	}
}