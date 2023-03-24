package com.application.schedule;

import java.io.IOException;

import org.json.JSONException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.application.schedule.controller.ScheduleController;
import com.application.schedule.controller.ScheduleController2;
import com.application.schedule.service.LoadSFTP;
import com.application.schedule.service.PullSFTP;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;


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
			if(args[0].equals("FTP"))ScheduleController.setSchedule();
			else ScheduleController2.setSchedule();
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
