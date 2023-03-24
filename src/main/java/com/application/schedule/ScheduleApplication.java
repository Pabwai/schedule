package com.application.schedule;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.application.schedule.controller.ScheduleController;
import com.application.schedule.controller.ScheduleController2;


@SpringBootApplication
public class ScheduleApplication  {

	public static void main(String[] args){
		
		//SpringApplication.run(ScheduleApplication.class, args);
		
		SpringApplication app = new SpringApplication(ScheduleApplication.class);
		app.setBanner(null);
		System.out.println();
		System.out.println("----------------------------------------------------");
		System.out.println("          START PROGRAM "+args[0].toUpperCase()+" WORKSITE ");
		System.out.println("----------------------------------------------------");
		
		try {
			if(args[0].equals("FTP"))ScheduleController.setSchedule(args[1]);
			else ScheduleController2.setSchedule(args[1]);
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
