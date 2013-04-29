package com.googlemapsme.googlemaps_me;
import java.io.Serializable;

import com.google.api.client.util.Key;
 
/** Implement this class from "Serializable"
* So that you can pass this class Object to another using Intents
* Otherwise you can't pass to another actitivy
* */
public class Place implements Serializable {
 
    @Key
    public String id;
 
    @Key
    public String name;
 
    @Key
    public String reference;
 
    @Key
    public  Photos photos[];
 
    @Key
    public String icon;
 
    @Key
    public String vicinity;
 
    @Key
    public Geometry geometry;
 
    @Key
    public String formatted_address;
 
    @Key
    public String formatted_phone_number;

    @Key
    public String types[];
    
    @Key
    public float rating;
	
    @Key
	public boolean open_now;
	 
 
    @Override
    public String toString() {
        return name + " - " + id + " - " + reference;
    }
 
    public static class Geometry implements Serializable
    {
        @Key
        public Location location;
    }
 
    public static class Location implements Serializable
    {
        @Key
        public double lat;
 
        @Key
        public double lng;
    }
    
    
    public static class Photos implements Serializable
    {
        @Key
        public String photo_reference;
    }
 
}