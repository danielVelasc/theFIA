package cacheController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.json.JSONException;

import com.mashape.unirest.http.exceptions.UnirestException;

import cache.Cache;
import dataRetriever.RESTRequestFacade;

public class CacheManager {

	/**
	 * The time to live for the specific types of objects stored inside the cache.
	 * These are stored in milliseconds.
	 * If the information is supposed to be stored indefinitely, this will be set -1.
	 * 
	 */
	private long AceTTL;
	private long ArchiveTTL;
	private long EmbedTTL;
	private long ImageTTL;
	private long LocationTTL;
	private long MapTTL;
	private long WeatherTTL;
	
	private RESTRequestFacade restFacade = new RESTRequestFacade();
	
	public CacheManager() { 
		System.out.println("configuring...");
		configure();
		System.out.println("Configuration done");
		}
	
	public Response cacheGet(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		String type = parameterMap.get("type").get(0);
		String keyString = "type=" + type;
		
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			if(!theKey.equals("type"))
			{
				keyString = keyString + "&" +theKey+"=";
				keyString = keyString + theValue;
			}
			
		}
		
		boolean useCache = false;
		
		if (Cache.getData().lifeMap.containsKey(keyString)) {
			
			String typeCondition = type.toLowerCase();
			
			long lifeInMillis = Cache.getData().lifeMap.get(keyString) - System.currentTimeMillis();
			
			switch(typeCondition) {
			
			case "ace":
				if (lifeInMillis < AceTTL)
					useCache = true;
				break;
			case "archive":
				if (lifeInMillis < ArchiveTTL)
					useCache = true;
				break;
			case "embed":
				if (lifeInMillis < EmbedTTL)
					useCache = true;
				break;
			case "images":
				if (lifeInMillis < ImageTTL)
					useCache = true;
				break;
			case "locations":
				if (lifeInMillis < LocationTTL)
					useCache = true;
				break;
			case "map":
				if (lifeInMillis < MapTTL)
					useCache = true;
				break;
			case "weather":
				if (lifeInMillis < WeatherTTL)
					useCache = true;
				break;
			default:
				break;
					
			}
			
		}
		
		Response response = null;
		
		System.out.println("before deciding on cache in cacheManager");
		
		System.out.println("keyString: " + keyString);
		
		if (useCache) {
			// call the cache here
			System.out.println("getting from cache");
			response = Cache.getData().cacheMap.get(keyString);
			
		}
		else {
			// call the requestFacade
			// This is a placeholder. There should be a store of the item as well.
			
			if((type.equals("images" ) && !parameterMap.containsKey("action")) || type.equals("embed" ) || type.equals("map"))
			{
				response = restFacade.composeImageRequest(parameterMap);
			}
			else {
				response = restFacade.composeAuroraRequest(parameterMap);
			}
			
			System.out.println("Cache Store");
			
			cacheStore(keyString, response);
			
		}
		
		return response;
		
	}
	
	private void cacheStore(String keyString, Response response) {
		
		// placeholder
		
		Cache.getData().cacheMap.put(keyString, response);
		
		Cache.getData().lifeMap.put(keyString, System.currentTimeMillis());
		
	}
	
	/**
	 * Reads in the configuration settings from a text file.
	 * The file should be of the following format. 
	 * _____________________________________________________
	 * 
	 * 
	 * Cache Configurations - Version X.X
	 * Last Modified By - XXXXXXXXXXX
	 * 
	 * ACE - TTL: DD:HH:MM:SS
	 * Archive - TTL: DD:HH:MM:SS
	 * Embed - TTL: DD:HH:MM:SS
	 * Image - TTL: DD:HH:MM:SS
	 * Location - TTL: DD:HH:MM:SS
	 * Weather - TTL: DD:HH:MM:SS
	 * 
	 * _____________________________________________________
	 * 
	 * Note: DD:HH:MM:SS may be replaced by "Indefinite Cache" to 
	 * cache the items indefinitely.
	 * 
	 */
//	private void configure() {
//		
//		File configurationFile = new File("CacheConfiguration.txt");
//		BufferedReader reader = null;
//		
//		try {
//			reader = new BufferedReader(new FileReader(configurationFile));
//		} catch (FileNotFoundException e) {
//			System.err.println("There was an error locating the cache configuration file. Program Terminated.");
//			System.exit(1);
//		}
//		
//		try {
//			
//			
//			String line = reader.readLine();
//			
//			while(line != null) {
//				
//				if (line.length() == 0) {
//					line = reader.readLine();
//					continue;
//				}
//				
//				String[] lineSplit = line.split(" - ");
//				String type = lineSplit[0];
//				
//				switch(type) {
//				
//				case "ACE":
//				case "Archive":
//				case "Embed":
//				case "Images":
//				case "Locations":
//				case "Weather":
//					processTTL(type, lineSplit[1]);
//					break;
//					
//				default:
//					break;
//						
//				}
//				
//			}
//			
//
//			
//		} catch (IOException e) {
//			System.err.println("There was an error reading in the cache configurations. Program Terminated.");
//			System.exit(1);
//		}
//		
//		try {
//			reader.close();
//		} catch (IOException e) {
//			System.err.println("There was an error closing the reader for the configuration file. Program Terminated.");
//			System.exit(1);
//		}
//		
//	}
	
	private void configure() {
		
		AceTTL = 100000;
		ArchiveTTL = 100000;
		EmbedTTL = 139139913;
		ImageTTL = 80008;
		LocationTTL = 13139931;
		MapTTL = 2020020020;
		WeatherTTL = 3333333;
		
	}
	
	
	/**
	 * a helper function for the configure method
	 * @param type the cache type for TTL to be processed
	 * @param str the string to be processed
	 */
	private void processTTL(String type, String str) {
		
		
		String[] strSplit = str.split(" ");
		
		long timeInMillis;
		
		if (strSplit[1] == "Indefinite Cache")
			timeInMillis = -1;
		else {
			String[] splitTime = strSplit[1].split(":");
			
			int day = Integer.parseInt(splitTime[0]);
			int hour = Integer.parseInt(splitTime[1]);
			int minute = Integer.parseInt(splitTime[2]);
			int second = Integer.parseInt(splitTime[3]);
			
			timeInMillis = (second * 1000);
			timeInMillis += (minute * 60 * 1000);
			timeInMillis += (hour * 3600 * 1000);
			timeInMillis += (day * 24 * 3600* 1000);
			
		}
		
		
		switch(type) {
		
		case "ACE":
			AceTTL = timeInMillis;
			break;
		case "Archive":
			ArchiveTTL = timeInMillis;
			break;
		case "Embed":
			EmbedTTL = timeInMillis;
			break;
		case "Images":
			ImageTTL = timeInMillis;
			break;
		case "Locations":
			LocationTTL = timeInMillis;
			break;
		case "Weather":
			WeatherTTL = timeInMillis;
			break;
		default:
			break;
				
		}
		
	}

}
