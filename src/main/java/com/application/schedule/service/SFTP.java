package com.application.schedule.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Component;


@Component
public class SFTP implements Runnable{
	
	private static Logger log = LogManager.getLogger("loggers");
	
	static String server = "10.35.0.152";
    static int port = 21;
    static String user = "ebaows_uat";
    static String pass = "34>zd/U!NtGSRn?6";
    
    static String setSchedule = "D:\\worksite\\schedule.json"; 
   
    
    static FTPClient ftpClient;
	
	public static  void ftp() throws JSONException, IOException {
	
		
		JSONObject JSONObject = parseJSONFile(setSchedule);
		
    	String fileSetting =  JSONObject.getString("setFTP");
		
		JSONObject data = parseJSONFile(fileSetting);
		
		user 	= data.getString("ftpuser");
		pass 	= data.getString("ftppass");
		port 	= data.getInt("ftpport");
		server 	= data.getString("ftpserver");
		
		JSONArray files = (JSONArray)data.get("pathFileFTP");

		 
        ftpClient = new FTPClient();
        try {

        	ftpClient.connect(server, port);
        	ftpClient.login(user, pass);
        	ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            

            // Lists files and directories
            for (int i = 0; i < files.length(); i++) {
            	
            	JSONObject pathFile = new JSONObject();
            	pathFile = (JSONObject)files.get(i);
            	String remoteFile = pathFile.getString("remoteFile");
            	String localFile  = pathFile.getString("localFile");
	            FTPFile[] files1 = ftpClient.listFiles(remoteFile);
	            File theDir = new File(localFile+remoteFile);
        		if (!theDir.exists()){
        		    theDir.mkdirs();
        		}
	            printFileDetails(files1,remoteFile,localFile);
            }
            
            
            // uses simpler methods
            //String[] files2 = ftpClient.listNames();
            //printNames(files2);
            
            ftpClient.logout();
            ftpClient.disconnect();
        } catch (IOException ex) {
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
	

	private static void printFileDetails(FTPFile[] files,String remotePath,String localPath) {
        ///DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (FTPFile file : files) {
            //String details = file.getName();
            if (file.isDirectory()) {
                //details = "[" + details + "]";
                continue;
            }
            //details += "\t\t" + file.getSize();
            //details += "\t\t" + dateFormater.format(file.getTimestamp().getTime());
            
            try {
//            	System.out.println(remotePath+"/"+file.getName()+"--------"+localPath + file.getName());
//            	OutputStream fileOut = new BufferedOutputStream(new FileOutputStream(localPath +remotePath+ file.getName()));
//            	ftpClient.retrieveFile(remotePath+"/"+file.getName(), fileOut);  //get file 
//				fileOut.close();
            	
            	OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(localPath+remotePath+'/'+ file.getName()));
                InputStream inputStream = ftpClient.retrieveFileStream(remotePath+"/"+file.getName());
                byte[] bytesArray = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = inputStream.read(bytesArray)) != -1) {
                    outputStream.write(bytesArray, 0, bytesRead);
                }
     
                boolean success = ftpClient.completePendingCommand();
                if (success) {
                    System.out.println(file.getName() +" downloaded success");
                }
                outputStream.close();
                inputStream.close();

	            
			} catch (FileNotFoundException ex) {
				log.debug("Error: " + ex.getMessage());
				ex.printStackTrace();
			} catch (IOException ex) {
				log.debug("Error: " + ex.getMessage());
				ex.printStackTrace();
			}
            
            //System.out.println(details);
        }
    }
	
//	private static void printNames(String files[]) {
//		if (files != null && files.length > 0) {
//            for (String aFile: files) {
//                System.out.println(aFile);
//            }
//        }
//	}
	
	
	public static JSONObject parseJSONFile(String filename) throws JSONException, IOException {
        String content = new String(Files.readAllBytes(Paths.get(filename)),StandardCharsets.UTF_8);
        return new JSONObject(content);
    }
	

	@Override
    public void run() {
		System.out.println("Download File"+new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date()));
		try {
			ftp();
		} catch (JSONException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println();
    }

}
