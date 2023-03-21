package com.application.schedule;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.application.schedule.controller.ScheduleController;


@SpringBootApplication
public class ScheduleApplication  {
	
	
	
	public static void main(String[] args)  {
		//SpringApplication.run(ScheduleApplication.class, args);
		SpringApplication app = new SpringApplication(ScheduleApplication.class);
		app.setBanner(null);
		
		System.out.println();
		System.out.println("----------------------------------------------------");
		System.out.println("       START PROGRAM SFTP MOTOR POLICY ");
		System.out.println("----------------------------------------------------");
		
		try {
			ScheduleController.setSchedule();
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
