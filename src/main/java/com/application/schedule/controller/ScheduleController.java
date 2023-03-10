package com.application.schedule.controller;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.application.schedule.service.PullFileFTP;
import com.application.schedule.service.SFTP;

@Component
public class ScheduleController {
	
	
	static String setSchedule = "D:\\worksite\\schedule.json"; 
	
	public static void setSchedule() throws JSONException, IOException {	
		
		JSONObject setTime = parseJSONFile(setSchedule);
		
		if(setTime.has("getHour") &&
				setTime.has("getMinut") &&
				setTime.has("putHour") &&
				setTime.has("putMinut")) {
			
			int getHour = setTime.getInt("getHour");
			int getMinut = setTime.getInt("getMinut");
			int putHour = setTime.getInt("putHour");
			int putMinut = setTime.getInt("putMinut");
			
			Long delayTime;
			Long delayTime2;
		    final Long initialDelay = LocalDateTime.now().until(LocalDate.now().atTime(getHour, getMinut), ChronoUnit.MINUTES);
		    final Long initialDelay2 = LocalDateTime.now().until(LocalDate.now().atTime(putHour, putMinut), ChronoUnit.MINUTES);
		    //final Long initialDelay2 = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(0, 24), ChronoUnit.MINUTES);

	        if (initialDelay > TimeUnit.DAYS.toMinutes(1)) {
	            delayTime = LocalDateTime.now().until(LocalDate.now().atTime(getHour, getMinut), ChronoUnit.MINUTES);
	            
	        } else {
	            delayTime = initialDelay;
	        }
	        
	        if (initialDelay2 > TimeUnit.DAYS.toMinutes(1)) {
	            delayTime2 = LocalDateTime.now().until(LocalDate.now().atTime(putHour, putMinut), ChronoUnit.MINUTES);
	            
	        } else {
	            delayTime2 = initialDelay2;
	        }
	        
	        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	        scheduler.scheduleAtFixedRate(new SFTP(), delayTime, TimeUnit.SECONDS.toSeconds(1), TimeUnit.MINUTES);
	        scheduler.scheduleAtFixedRate(new PullFileFTP(), delayTime2, TimeUnit.MINUTES.toMinutes(1), TimeUnit.MINUTES);
	        //scheduler.scheduleAtFixedRate(new ScheduleController(), initialDelay2, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
			
		}
		
		
        
	}
	
	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }

}
