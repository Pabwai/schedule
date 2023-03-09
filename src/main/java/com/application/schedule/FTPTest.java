package com.application.schedule;

import java.io.File;
import java.util.Optional;
import org.apache.commons.net.ftp.FTPClient;

public class FTPTest {
	
	static String server = "10.35.0.152";
    static int port = 21;
    static String user = "ebaows_uat";
    static String pass = "34>zd/U!NtGSRn?6";
    
    static String fileSetting = "D:\\NTLTESTER\\sftp.json"; 
    static FTPClient ftpClient;

	public static void main(String[] args) {
		
	
		String name1 = "TSuccess.TXT"; 
		String name2 = "TMSTHYYYYMMDDM-PA-POLICY_Error.TXT"; 
		String name3 = "TMSTHYYYYMMDDM-PA-POLICY.TXT"; 
		String[] ans = name2.split("_");
		System.out.println(name3.lastIndexOf("Success"));
		System.out.println(name2.contains("Success"));
		System.out.println(ans[0]+ans.length);
//      Lists files and directories
		 
		
//            File file = new File("D:\\workspec\\data\\");  
//            File[] files = file.listFiles();  
//            for (File fileItem:files){  
//                System.out.println(fileItem.getAbsolutePath()); 
//                try {
//	                Optional<String> typ = getExtensionByStringHandling(fileItem.getAbsolutePath());
//	                System.out.println(typ.get());
//                }
//	                catch (Exception e) {
//	                   
//                }
//            } 
            
            
            // uses simpler methods
            //String[] files2 = ftpClient.listNames();
            //printNames(files2);
            
       
    }
	
	public static Optional<String> getExtensionByStringHandling(String filename) {
	    return Optional.ofNullable(filename)
	      .filter(f -> f.contains("."))
	      .map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}

}


