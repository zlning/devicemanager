package com.device;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;

public class DeviceConsole extends Thread{
	private final static int DeviceSocketPort = 2223;
	private ServerSocket server = null;
    	private Socket ServerSock = null;
    	private String mcommond = null;
    	//private PrintWriter pw = null;
    	private BufferedReader br = null;
	private String param1 = null;
	private String param2 = null;
        private DeviceServerManager mDeviceServerManager;
	/*public DeviceConsole(){
        	
        }*/
        public void setDeviceServerManager(DeviceServerManager m){
                mDeviceServerManager=m;
        }
	@Override
	public void run(){
		try {
            		server = new ServerSocket(DeviceSocketPort);
        	} catch (IOException e) {
            		System.out.println(e);
        	}
        	try {
			while(true){
				System.out.println("===========wait command=========");
            			ServerSock = server.accept();
            			br = new BufferedReader(new InputStreamReader(ServerSock.getInputStream()));
            			while ((mcommond = br.readLine()) != null) {
					System.out.println("===========command is comming========="+mcommond);
                			//execommond(mcommond);
                                        if(mDeviceServerManager!=null){
                                            mDeviceServerManager.ExeCommand(mDeviceServerManager.mDeviceCommand.AnalysisCommand(mcommond));
                                        }
            			}
			}
        	} catch (Exception e) {
        	}        
	}  
        private void execommond(String s){
		StringTokenizer st = new StringTokenizer(s);
		String commond = st.nextToken();
		if(st.hasMoreElements() && commond.equals("showdevices")){
			System.out.println("===========show devices=========");
		}
                else if(st.hasMoreElements() && commond.equals("connect")){
			if(st.hasMoreElements()){
				param1=st.nextToken();
			}else{
				System.out.println("DeviceServerManager Do Not Supported Commond");
				return;
			}
			if(st.hasMoreElements()){
				param2=st.nextToken();
			}else{
				System.out.println("DeviceServerManager Do Not Supported Commond");
				return;
			}
			//if()
			System.out.println("connect"+" name1 "+param1+" name2 "+param2);
		}else{
			System.out.println("DeviceServerManager Do Not Supported Commond");
		}
			
	}
}
