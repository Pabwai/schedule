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

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component
public class PullFileFTP {
	
	private static Logger log = LogManager.getLogger("loggers");
	
    static FTPClient ftpClient;
    
    //static String setSchedule = "D:\\worksite\\schedule.json"; 

	
    public  void ftp(String setSchedule) throws JSONException, IOException {
    	
    	JSONObject data = parseJSONFile(setSchedule);
		
		String user 	= data.getString("ftpuser");
		String pass 	= data.getString("ftppass");
		int port 		= data.getInt("ftpport");
		String server 	= data.getString("ftpserver");
		
		JSONArray ifiles = (JSONArray)data.get("pathFileFTP");

		 
	   ftpClient = new FTPClient();
	   
        try {

        	ftpClient.connect(server, port);
        	ftpClient.login(user, pass);
        	ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            
            
            // Lists files and directories
            for (int i = 0; i < ifiles.length(); i++) {
            	
            	JSONObject pathFile = new JSONObject();
            	pathFile = (JSONObject)ifiles.get(i);
            	if(pathFile.has("localSucce")) {
            		
            		//localSucce
                	String localFile  = pathFile.getString("localSucce");
                	File file = new File(localFile);  
                	File[] files = file.listFiles();  
                	
                	for (File fileItem:files) {  
                          
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
    					
    					boolean variPath = checkDirectoryExists(remoteFile);
    					
    					
    					if(variPath) {
    						remoteFile = remoteFile+"/"+fileItem.getName();
    						InputStream inputStream = new FileInputStream(fileItem.getAbsolutePath());
    						OutputStream outputStream = ftpClient.storeFileStream(remoteFile);
    						byte[] bytesIn = new byte[4096];
    		                int bytesRead = -1;
    		                while ((bytesRead = inputStream.read(bytesIn)) != -1) {
    		                    outputStream.write(bytesIn, 0, bytesRead);
    		                }
    		                outputStream.close();
    		                inputStream.close();
    		                boolean success = ftpClient.completePendingCommand();
    		                
    		                if (success) {
                                System.out.println(fileItem.getName() + " uploaded success");
                            }else {
                            	System.out.println(fileItem.getName() + " uploaded false");
                            }
    		                Thread.sleep(1000);
    					}else {
    						 System.out.println(remoteFile + "  directory does exist");
    					}        
                        
                        
                       
                    }
            		
            	}else {
            		System.out.println("Can't find path uploaded");
            	}
            	
        
            }

            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException | InterruptedException ex) {
        	log.debug("Error: " + ex.getMessage());
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
            	log.debug("Error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
    
	
    public JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }
	
    public Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}
    
    static boolean checkDirectoryExists(String dirPath) throws IOException {
        ftpClient.changeWorkingDirectory(dirPath);
        int returnCode = ftpClient.getReplyCode();
        if (returnCode == 550) {
            return false;
        }
        return true;
    }
    
	
}
