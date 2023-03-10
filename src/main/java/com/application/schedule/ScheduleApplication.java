package com.application.schedule;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import com.application.schedule.controller.ScheduleController;


@SpringBootApplication
@PropertySources({
    @PropertySource(value = "file:D:\\worksite\\application.properties",  ignoreResourceNotFound = true)
})
public class ScheduleApplication  {
	
	public static void main(String[] args) throws JSONException, IOException {
		SpringApplication.run(ScheduleApplication.class, args);
		System.out.println();
		System.out.println("----------------------------------------------------");
		System.out.println("       START PROGRAM SFTP MOTOR POLICY ");
		System.out.println("----------------------------------------------------");
		
		ScheduleController.setSchedule();
		
	}
	
}
