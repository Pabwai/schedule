package com.application.schedule.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

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
public class PullSFTP {
	
	
	public void ftp(String setSchedules) throws JSchException, SftpException, JSONException, IOException, InterruptedException {

		JSONObject data = parseJSONFile(setSchedules);

		String user 	= data.getString("ftpuser");
		String pass 	= data.getString("ftppass");
		int port 		= data.getInt("ftpport");
		String server 	= data.getString("ftpserver");
		
		Session session = setupJsch( user,  pass,  port, server);
		
		Channel channel = session.openChannel("sftp");
        channel.connect();
        
		
		
		JSONArray files = (JSONArray)data.get("pathFileFTP");
		ChannelSftp channelSftp = (ChannelSftp) channel;
		for (int i = 0; i < files.length(); i++) {
        	
        	JSONObject pathFile = new JSONObject();
        	pathFile = (JSONObject)files.get(i);
        	
        	if(pathFile.has("localSucce")) {
        		//localSucce
            	String localFile  = pathFile.getString("localSucce");
            	File file = new File(localFile);  
            	File[] filesall = file.listFiles();  
            	
            	for (File fileItem:filesall) {  
            		
            		Optional<String> filetyp = getExtensionByStringHandling(fileItem.getAbsolutePath());

                    if(!filetyp.get().toUpperCase().equals("TXT")) continue;
                    
                    String[] sentences = fileItem.getName().split("\\_");
                    String remoteFile = "/"+pathFile.getString("remoteFile");
					if(sentences.length==1) {
						remoteFile = remoteFile+"/Achived";
					}else if(sentences.length==2) {
						
						if(sentences[1].toUpperCase().equals("SUCCESS.TXT") || 
						   sentences[1].toUpperCase().equals("TSUCCESS.TXT") ||
						   sentences[1].toUpperCase().equals("LSUCCESS.TXT")   ) {
							remoteFile = remoteFile+"/Achived";
							
						}else {
							remoteFile = remoteFile+"/Error";
						}
					}
					
					try {
						
						listFilesRecursively(channelSftp,remoteFile,fileItem);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

            	}
        	}
        	
    		

        }
		channelSftp.exit();
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
	
	
	private static void listFilesRecursively(ChannelSftp channelSftp, String remoteFile,File fileItem) throws Exception {

		remoteFile = remoteFile+"/"+fileItem.getName();
		InputStream inputStream = new FileInputStream(fileItem.getAbsolutePath());
		OutputStream outputStream = channelSftp.put(remoteFile);
		byte[] bytesIn = new byte[4096];
        int bytesRead = -1;
        
        while ((bytesRead = inputStream.read(bytesIn)) != -1) {
            outputStream.write(bytesIn, 0, bytesRead);
        }
        
        outputStream.close();
        inputStream.close();
        
        System.out.println(fileItem.getName() + " uploaded success");
        fileItem.delete();
        Thread.sleep(1000);
    }
	
	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }
	
	public static Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
    
   

}
