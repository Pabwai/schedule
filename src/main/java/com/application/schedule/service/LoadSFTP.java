package com.application.schedule.service;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Component
public class LoadSFTP {
	
	private static Logger log = LogManager.getLogger("loggers");

	
	public void ftp(String setSchedules) throws JSchException, SftpException, JSONException, IOException {

		JSONObject data = parseJSONFile(setSchedules);

		String user 	= data.getString("ftpuser");
		String pass 	= data.getString("ftppass");
		int port 		= data.getInt("ftpport");
		String server 	= data.getString("ftpserver");
		
		Session session = setupJsch( user,  pass,  port, server);
		
		
		Channel channel = session.openChannel("sftp");
        channel.connect();
        ChannelSftp channelSftp = (ChannelSftp) channel;
		
		JSONArray files = (JSONArray)data.get("pathFileFTP");

		for (int i = 0; i < files.length(); i++) {
        	
        	JSONObject pathFile = new JSONObject();
        	pathFile = (JSONObject)files.get(i);
        	String remoteFile = pathFile.getString("remoteFile");
        	String localFile  = pathFile.getString("localFile");

            File theDir = new File(localFile+remoteFile);
    		if (!theDir.exists()){
    		    theDir.mkdirs();
    		}
    		
    		try {
    			
				listFilesRecursively(channelSftp,remoteFile,localFile+remoteFile);
    			//listFilesRecursively2(channelSftp,remoteFile);	
    			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

        }
		channelSftp.disconnect();
		channel.disconnect();
		session.disconnect();
	}
	
	
    
	private static Session setupJsch(String user, String pass, int port, String server) throws JSONException, IOException, JSchException {
		
		JSch jsch = new JSch();
		Session jschSession = jsch.getSession(user, server,port);
		jschSession.setPassword(pass);

		java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        jschSession.setConfig(config);
		jschSession.connect();
		return jschSession;
	}
	
	
	private static void listFilesRecursively2(ChannelSftp sftpChannel, String path) throws Exception {
        Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls(path);

        for (ChannelSftp.LsEntry entry : entries) {
            String entryName = entry.getFilename();

            if (".".equals(entryName) || "..".equals(entryName)) {
                continue;
            }

            String fullPath = path + "/" + entryName;
            if (entry.getAttrs().isDir()) {
                System.out.println("Directory: " + fullPath);
                //listFilesRecursively2(sftpChannel, fullPath);
            } else {
                System.out.println("File: " + fullPath);
            }
        }
    }
	
	private static void listFilesRecursively(ChannelSftp channelSftp, String pathRemote,String pathLocal) throws Exception {

     	Vector<ChannelSftp.LsEntry> list = channelSftp.ls(pathRemote);
		for(ChannelSftp.LsEntry entry:list) {
			String entryName = entry.getFilename();
			if (entry.getAttrs().isDir()) {
                continue;
            }
			String fullPath = pathRemote + "/" + entryName;
			
		    byte[] buffer = new byte[1024];
		    BufferedInputStream bis = new BufferedInputStream(channelSftp.get(fullPath));
		    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(pathLocal+"\\"+entryName));
		    int readCount;
		    while( (readCount = bis.read(buffer)) > 0) {
		    	outputStream.write(buffer, 0, readCount);
		    }
		    bis.close();
		    outputStream.close();
		    channelSftp.rm(fullPath);
		    System.out.println(entry.getFilename()+" downloaded success");
		}
		list.clear();
    }
	
	
	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }

}
