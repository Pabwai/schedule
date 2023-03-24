package com.application.schedule.controller;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;

import com.application.schedule.service.PullFTP;
import com.application.schedule.service.LoadFTP;

@Controller
public class ScheduleController {

   static String setSchedule; 
	
	public static void setSchedule(String seting) throws JSONException, IOException {	
		
		setSchedule = seting;
		
		
		
		JSONObject setTime = parseJSONFile(seting);
		
		if(setTime.has("getHour") &&
				setTime.has("getMinut") &&
				setTime.has("putHour") &&
				setTime.has("putMinut")) {
			
			int getHour = setTime.getInt("getHour");
			int getMinut = setTime.getInt("getMinut");
			int putHour = setTime.getInt("putHour");
			int putMinut = setTime.getInt("putMinut");

	        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	        scheduler.scheduleAtFixedRate(new Task1(), initialDelay(getHour,getMinut),  TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);	        
	        scheduler.scheduleAtFixedRate(new Task2(), initialDelay(putHour,putMinut),  TimeUnit.DAYS.toSeconds(1), TimeUnit.SECONDS);
	        //scheduler.scheduleAtFixedRate(new ScheduleController(), initialDelay2, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
			
		}

	}
	
	
	private static class Task1 implements Runnable {
	    @Override
	    public void run() {
	      
	      	System.out.println();
			System.out.println("Download File: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date()));
			try {
				
				LoadFTP loadftp  = new LoadFTP();
				loadftp.ftp(setSchedule);
				
				Thread.sleep(9000);
				
				JSONObject JSONObject = parseJSONFile(setSchedule);
				if(JSONObject.has("batch")) {
					String batch =  JSONObject.getString("batch");
					Runtime.getRuntime().exec("cmd /c start "+batch);
				}
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      
	      
	      
	    }
	  }
	  
	  private static class Task2 implements Runnable {
		
	    @Override
	    public void run() {
	    	System.out.println("Upload File: "+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date()));
	    	
	    	PullFTP upLoad = new PullFTP();
	    	try {
				upLoad.ftp(setSchedule);
			} catch (JSONException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }	
	  }
	
	
	  private static long initialDelay(int hour, int minut) {
		  
		  ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Bangkok"));
		  ZonedDateTime nextRun = now.withHour(hour).withMinute(minut).withSecond(0);
		  if(now.compareTo(nextRun) > 0)
		      nextRun = nextRun.plusDays(1);

		  Duration duration = Duration.between(now, nextRun);
		  long initialDelay = duration.getSeconds();
		  
		return initialDelay;

	  }
	
	

	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }

}
