package com.googlemapsme.googlemaps_me;

import java.io.IOException;

import org.apache.http.client.HttpResponseException;

import android.util.Log;
 
import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;
 
@SuppressWarnings("deprecation")
public class GooglePlacesRequest {
 
    /** Global instance of the HTTP transport. */
    //private static final HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    // Google API Key
    private static final String API_KEY = "AIzaSyDG5s7wUC7M6ANf90cvb7zJzSBDs1QpjXM";
    private static final String LOG_KEY = "GooglePlaces";
	
    // Google Places search url's
 
    // private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    // private static final String PLACES_PHOTO_URL = "https://maps.googleapis.com/maps/api/place/photo?parameters";
    
 
    /**
     * Searching places
     * @param latitude - latitude of place
     * @params longitude - longitude of place
     * @param radius - radius of searchable area
     * @param types - type of place to search
     * @return list of places
     * */
    

	// Create our transport.
    public PlacesList search(String searchtext)
            throws Exception {
 
        try {
 
        	HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_TEXT_SEARCH_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("query",searchtext);
            request.getUrl().put("sensor", "false");
            request.getUrl().put("location", GoogleMaps_me.my_latitude + "," + GoogleMaps_me.my_longitude);
            request.getUrl().put("radius", GoogleMaps_me.radius); // in meters   
            request.getUrl().put("language",GoogleMaps_me.language);
            // if(types != null) request.getUrl().put("types", types);
            // request.getUrl().put("sensor", "true");                      
            Log.d(LOG_KEY, request.execute().parseAsString());
            PlacesList list = request.execute().parseAs(PlacesList.class);
			Log.d(LOG_KEY, "STATUS = " + list.status);
			
            return list;
 
        } catch (HttpResponseException e) {
            Log.e("Error:", e.getMessage());
            return null;
        }
        catch (IOException e) {
			// TODO: handle exception
			throw e;
		}
 
    }
 
    /**
     * Searching single place full details
     * @param refrence - reference id of place
     *                 - which you will get in search api request
     * */
    public PlaceDetails getPlaceDetails(String reference) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("reference", reference);
            request.getUrl().put("sensor", "false"); 
            PlaceDetails place = request.execute().parseAs(PlaceDetails.class);
            return place;
 
        } catch (HttpResponseException e) {
            Log.e("Error in Perform Details", e.getMessage());
            throw e;
        }
    }
 /*
    public PlacePhotos getPlacephotos(String photoreference) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_PHOTO_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("photoreference", photoreference);
            request.getUrl().put("sensor", "false");
            request.getUrl().put("maxwidth",1500);
            
            PlacePhotos placephotos = request.execute().parseAs(PlacePhotos.class);
            return placephotos;
 
        } catch (HttpResponseException e) {
            Log.e("Error in Perform Details", e.getMessage());
            throw e;
        }
    }
 */
    /**
     * Creating http request Factory
     * */
    public static HttpRequestFactory createRequestFactory(
            final HttpTransport transport) {
        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                GoogleHeaders headers = new GoogleHeaders();
                headers.setApplicationName("AndroidHive-Places-Test");
                request.setHeaders(headers);
                JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
                request.addParser(parser);
            }
        });
    }
 
}