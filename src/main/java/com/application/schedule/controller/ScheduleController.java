package com.application.schedule.controller;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;

import com.application.schedule.service.PullFileFTP;
import com.application.schedule.service.SFTP;

@Controller
public class ScheduleController {
	
	@Value("${value.seting}")
	static String setting;
	
	
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
			
			ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

	        // Schedule the first task to run every day at a fixed time (in this example, 9:00 AM)
	        scheduler.scheduleAtFixedRate(new SFTP(), getDelayUntilNextExecution(getHour, getMinut), 24, TimeUnit.HOURS);

	        // Schedule the second task to run every day at a fixed time (in this example, 2:00 PM)
	        scheduler.scheduleAtFixedRate(new PullFileFTP(), getDelayUntilNextExecution(putHour, putMinut), 24, TimeUnit.HOURS);
			
			
//			Long delayTime;
//			Long delayTime2;
//		    final Long initialDelay = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(getHour, getMinut), ChronoUnit.MINUTES);
//		    final Long initialDelay2 = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(putHour, putMinut), ChronoUnit.MINUTES);
//		    //final Long initialDelay2 = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(0, 24), ChronoUnit.MINUTES);
//
//	        if (initialDelay > TimeUnit.DAYS.toMinutes(1)) {
//	            delayTime = LocalDateTime.now().until(LocalDate.now().atTime(getHour, getMinut), ChronoUnit.MINUTES);
//	            delayTime2 = LocalDateTime.now().until(LocalDate.now().atTime(putHour, putMinut), ChronoUnit.MINUTES);
//	            
//	        } else {
//	            delayTime = initialDelay;
//	            delayTime2 = initialDelay2;
//	        }
//
//	        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
//	        scheduler.scheduleAtFixedRate(new SFTP(), delayTime,  TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
//	        scheduler.scheduleAtFixedRate(new PullFileFTP(), delayTime2, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
//	        //scheduler.scheduleAtFixedRate(new ScheduleController(), initialDelay2, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
			
		}
		
		
        
	}
	
	private static long getDelayUntilNextExecution(int hour, int minute) {
        long now = System.currentTimeMillis();
        long nextExecutionTime = getNextExecutionTime(hour, minute);

        if (nextExecutionTime <= now) {
            nextExecutionTime = getNextExecutionTime(hour, minute + 1);
        }

        return nextExecutionTime - now;
    }

    private static long getNextExecutionTime(int hour, int minute) {
        java.util.Calendar nextExecutionTime = java.util.Calendar.getInstance();
        nextExecutionTime.set(java.util.Calendar.HOUR_OF_DAY, hour);
        nextExecutionTime.set(java.util.Calendar.MINUTE, minute);
        nextExecutionTime.set(java.util.Calendar.SECOND, 0);
        nextExecutionTime.set(java.util.Calendar.MILLISECOND, 0);

        if (nextExecutionTime.getTimeInMillis() <= System.currentTimeMillis()) {
            nextExecutionTime.add(java.util.Calendar.DAY_OF_MONTH, 1);
        }

        return nextExecutionTime.getTimeInMillis();
    }
	
	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }

}
