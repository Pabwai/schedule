package com.application.schedule.controller;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;

import com.application.schedule.service.PullFileFTP;
import com.application.schedule.service.SFTP;

@Component
public class ScheduleController {
	
	static int hour;
	static int minut;

	
	public static void setSchedule() {	
		
		
		Long delayTime;
	    
	    final Long initialDelay = LocalDateTime.now().until(LocalDate.now().atTime(hour, minut), ChronoUnit.MINUTES);
	    final Long initialDelay2 = LocalDateTime.now().until(LocalDate.now().atTime(hour, minut), ChronoUnit.MINUTES);
	    //final Long initialDelay2 = LocalDateTime.now().until(LocalDate.now().plusDays(1).atTime(0, 24), ChronoUnit.MINUTES);

        if (initialDelay > TimeUnit.DAYS.toMinutes(1)) {
            delayTime = LocalDateTime.now().until(LocalDate.now().atTime(hour, minut), ChronoUnit.MINUTES);
        } else {
            delayTime = initialDelay;
        }
        
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new SFTP(), delayTime, TimeUnit.SECONDS.toSeconds(1), TimeUnit.MINUTES);
        scheduler.scheduleAtFixedRate(new PullFileFTP(), initialDelay2, TimeUnit.SECONDS.toSeconds(1), TimeUnit.MINUTES);
        //scheduler.scheduleAtFixedRate(new ScheduleController(), initialDelay2, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
        
	}

}
