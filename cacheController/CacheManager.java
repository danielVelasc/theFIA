package cacheController;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
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
	
	public Response cacheGet(MultivaluedMap<String, String> parameterMap) throws UnirestException, JSONException {
		
		String type = parameterMap.get("type").get(0);
		String keyString = "type=" + type;
		int hashKey = createKey(parameterMap);
		
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
		
		if (Cache.getData().lifeMap.containsKey(hashKey)) {
			
			String typeCondition = type.toLowerCase();
			
			long lifeInMillis = Cache.getData().lifeMap.get(hashKey) - System.currentTimeMillis();
			
			switch(typeCondition) {
			
			case "ace":
				if (lifeInMillis < AceTTL || AceTTL == -1)
					useCache = true;
				break;
			case "archive":
				if (lifeInMillis < ArchiveTTL || ArchiveTTL == -1)
					useCache = true;
				break;
			case "embed":
				if (lifeInMillis < EmbedTTL || EmbedTTL == -1)
					useCache = true;
				break;
			case "images":
				if (lifeInMillis < ImageTTL || ImageTTL == -1)
					useCache = true;
				break;
			case "locations":
				if (lifeInMillis < LocationTTL || LocationTTL == -1)
					useCache = true;
				break;
			case "map":
				if (lifeInMillis < MapTTL || MapTTL == -1)
					useCache = true;
				break;
			case "weather":
				if (lifeInMillis < WeatherTTL|| WeatherTTL == -1)
					useCache = true;
				break;
			default:
				break;
					
			}
			
		}
		
		Response response = null;
		
		System.out.println("before deciding on cache in cacheManager");
		
		System.out.println("keyString: " + keyString);
		
		System.out.println("key: " + hashKey);
		
		if (useCache) {
			// call the cache here
			System.out.println("getting from cache");
			
			if (type.equals("map") || type.equals("images")) {
				byte[] entityBody = Cache.getData().imageCacheMap.get(hashKey);
				System.out.println(entityBody.length);
				System.out.println("returning the cache image response");
				
				InputStream stream = new ByteArrayInputStream(entityBody);
				
				return Response.status(200).entity(stream).type("image/jpg").build();
			}
				
			response = Cache.getData().cacheMap.get(hashKey);
			
		}
		else {
			// call the requestFacade
			// This is a placeholder. There should be a store of the item as well.
			
			byte[] imageStore = null;
			
			if((type.equals("images" ) && !parameterMap.containsKey("action")) || type.equals("embed" ) || type.equals("map"))
			{
				response = restFacade.composeImageRequest(parameterMap);
				imageStore = restFacade.getCacheImage(parameterMap);
				
				if (imageStore != null)
					cacheStore(hashKey, imageStore);
			}
			else {
				response = restFacade.composeAuroraRequest(parameterMap);
				cacheStore(hashKey, response);
			}
			
			System.out.println("Cache Store");
			
		}
		
		return response;
		
	}
	
	private void cacheStore(int hashKey, Response response) {
		
		// placeholder
		
		Cache.getData().cacheMap.put(hashKey, response);
		
		Cache.getData().lifeMap.put(hashKey, System.currentTimeMillis());
		
	}
	
	private void cacheStore(int hashKey, byte[] byteArray) {
	
		Cache.getData().imageCacheMap.put(hashKey, byteArray);
		
		Cache.getData().lifeMap.put(hashKey, System.currentTimeMillis());
		
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
	 * Map - TTL: DD:HH:MM:SS
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
//		String currentDir = System.getProperty("user.dir") + File.separator + 
//				System.getProperty("sun.java.command").substring(0, System.getProperty("sun.java.command").lastIndexOf(".")).replace(".", File.separator);
//		System.out.println(currentDir);
//		
//		File configurationFile = new File("cacheController/CacheConfiguration.txt");
//		BufferedReader reader = null;
//		
//		if (configurationFile.exists())
//			System.out.println("File Found");
//		else 
//			System.out.println("File Not Found");
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
//				case "Map":
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
//	
//	private void configure() {
//		
//		AceTTL = 100000;
//		ArchiveTTL = 100000;
//		EmbedTTL = 139139913;
//		ImageTTL = 80008;
//		LocationTTL = 13139931;
//		MapTTL = 2020020020;
//		WeatherTTL = 3333333;
//		
//	}
	
	
	/**
	 * a helper function for the configure method
	 * @param type the cache type for TTL to be processed
	 * @param str the string to be processed
	 */
//	private void processTTL(String type, String str) {
//		
//		
//		String[] strSplit = str.split(" ");
//		
//		long timeInMillis;
//		
//		if (strSplit[1] == "Indefinite Cache")
//			timeInMillis = -1;
//		else {
//			String[] splitTime = strSplit[1].split(":");
//			
//			int day = Integer.parseInt(splitTime[0]);
//			int hour = Integer.parseInt(splitTime[1]);
//			int minute = Integer.parseInt(splitTime[2]);
//			int second = Integer.parseInt(splitTime[3]);
//			
//			timeInMillis = (second * 1000);
//			timeInMillis += (minute * 60 * 1000);
//			timeInMillis += (hour * 3600 * 1000);
//			timeInMillis += (day * 24 * 3600* 1000);
//			
//		}
//		
//		
//		switch(type) {
//		
//		case "ACE":
//			AceTTL = timeInMillis;
//			break;
//		case "Archive":
//			ArchiveTTL = timeInMillis;
//			break;
//		case "Embed":
//			EmbedTTL = timeInMillis;
//			break;
//		case "Images":
//			ImageTTL = timeInMillis;
//			break;
//		case "Locations":
//			LocationTTL = timeInMillis;
//			break;
//		case "Map":
//			MapTTL = timeInMillis;
//			break;
//		case "Weather":
//			WeatherTTL = timeInMillis;
//			break;
//		default:
//			break;
//				
//		}
//		
//	}
//	
	private final String[] configureArguments = {"ace", "archive", "embed", "images", "locations", "map", "weather" };
	
	private boolean isValidArgument(String str) {
		
		for (int i = 0; i < configureArguments.length; i++ ) {
			
			if (configureArguments[i].compareToIgnoreCase(str) == 0)
				return true;
			
		}
		
		return false;

	}
	
	public void configure(MultivaluedMap<String, String> parameterMap) {
		
		boolean[] setDefault = new boolean[7];
		for(int i = 0; i < setDefault.length; i++)
			setDefault[i] = true;
		
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			if(isValidArgument(theKey)) {
				
				String switchArgument = theKey.toLowerCase();
				long timeInMillis = Long.parseLong(theValue);
				
				switch(switchArgument) {
				
				case "ace":
					AceTTL = timeInMillis;
					setDefault[0] = false;
					break;
				case "archive":
					ArchiveTTL = timeInMillis;
					setDefault[1] = false;
					break;
				case "embed":
					EmbedTTL = timeInMillis;
					setDefault[2] = false;
					break;
				case "images":
					ImageTTL = timeInMillis;
					setDefault[3] = false;
					break;
				case "locations":
					LocationTTL = timeInMillis;
					setDefault[4] = false;
					break;
				case "map":
					MapTTL = timeInMillis;
					setDefault[5] = false;
					break;
				case "weather":
					WeatherTTL = timeInMillis;
					setDefault[6] = false;
					break;
				default:
					break;
						
				}
			}
			
		}
		
		if (parameterMap.containsKey("default")) {
			
			long timeInMillis = Long.parseLong(parameterMap.get("default").get(0));
			
			for (int i = 0; i < setDefault.length; i++) {
				
				if (!setDefault[i])
					continue;
				
				switch(i) {
				
				case 0:
					AceTTL = timeInMillis;
					break;
				case 1:
					ArchiveTTL = timeInMillis;
					break;
				case 2:
					EmbedTTL = timeInMillis;
					break;
				case 3:
					ImageTTL = timeInMillis;
					break;
				case 4:
					LocationTTL = timeInMillis;
					break;
				case 5:
					MapTTL = timeInMillis;
					break;
				case 6:
					WeatherTTL = timeInMillis;
					break;
				default:
					break;
						
				}
			}
			
		}
		
	}
	
	private int createKey(MultivaluedMap<String, String> parameterMap) {
		
		int key = 0;
		
		Iterator<String> it = parameterMap.keySet().iterator();
		while(it.hasNext()){
			String theKey = (String)it.next();
			String theValue = parameterMap.get(theKey).get(0);
			
			key += theKey.hashCode();
			key += theValue.hashCode();

			
		}
		
		return key;
		
	}

}
