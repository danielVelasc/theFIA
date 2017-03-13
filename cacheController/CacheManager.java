package cacheController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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
	private long WeatherTTL;
	
	
	public CacheManager() { configure(); }
	
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
	private void configure() {
		
		File configurationFile = new File("CacheConfiguration.txt");
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(configurationFile));
		} catch (FileNotFoundException e) {
			System.err.println("There was an error locating the cache configuration file. Program Terminated.");
			System.exit(1);
		}
		
		try {
			
			
			String line = reader.readLine();
			
			while(line != null) {
				
				if (line.length() == 0) {
					line = reader.readLine();
					continue;
				}
				
				String[] lineSplit = line.split(" - ");
				String type = lineSplit[0];
				
				switch(type) {
				
				case "ACE":
				case "Archive":
				case "Embed":
				case "Image":
				case "Location":
				case "Weather":
					processTTL(type, lineSplit[1]);
					break;
					
				default:
					break;
						
				}
				
			}
			

			
		} catch (IOException e) {
			System.err.println("There was an error reading in the cache configurations. Program Terminated.");
			System.exit(1);
		}
		
		try {
			reader.close();
		} catch (IOException e) {
			System.err.println("There was an error closing the reader for the configuration file. Program Terminated.");
			System.exit(1);
		}
		
	}
	
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
		case "Image":
			ImageTTL = timeInMillis;
			break;
		case "Location":
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
