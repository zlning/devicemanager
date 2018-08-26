package com.device;
import java.util.Scanner;
import java.io.IOException;
import java.util.Map;

public class DeviceServerClient extends Thread{
    private static final String TAG = "[DeviceServerClient] ";
    private DeviceCommand mDeviceCommand;
    
    private int ClientPort;
    private String ClientIp;
    private String ServertIp;
	private int ServerPort;
    private int DeviceServerManagerDemoPort=9095;
    private String DeviceServerManagerIp="192.168.1.201";
    
    private static final String THISISSERVER_COMMAND = "ThisIsServer";
    private static final String WHEREIS_COMMAND = "AskDeviceServerAddress";
    private static final String SENDNEWADDRESS_COMMAND = "SendNewAddress";
    private static final String CREATE_COMMAND = "CreateNewDeviceServer";
    private static final String THISISCLIENT_COMMAND = "ThisIsClient";
    
    public DeviceServerClient(){
        mDeviceCommand = new DeviceCommand();
        CreateNewDeviceServer();
        ClientPort = mDeviceCommand.getSendCommandPort();
    }
    public static void main(String[] args){
        DeviceServerClient iDeviceServerClient = new DeviceServerClient();
        iDeviceServerClient.start();
        iDeviceServerClient.AskDeviceServerAddress();
        while(true){
            //if(ServertIp && )
            try{
                Thread.sleep(20*1000);
            }catch(Exception e){
                e.printStackTrace();
            }
            iDeviceServerClient.sendHeartBeat();
        }   
    }
    private void CreateNewDeviceServer(){
        mDeviceCommand.SendCommand(CREATE_COMMAND+" "+getHostName()+" "+getHostSN(),DeviceServerManagerIp,DeviceServerManagerDemoPort,ClientPort);
    }
    private void AskDeviceServerAddress(){
        mDeviceCommand.SendCommand(WHEREIS_COMMAND+" "+getHostName()+" "+getHostSN(),DeviceServerManagerIp,DeviceServerManagerDemoPort,ClientPort);
    }
    private void sendClientAddress(){
        mDeviceCommand.SendCommand(THISISCLIENT_COMMAND,ServertIp,ServerPort,ClientPort);
    }
    public void sendHeartBeat(){
        mDeviceCommand.SendCommandNoReply(THISISCLIENT_COMMAND,ServertIp,ServerPort,ClientPort);
    }
    private void setServerAddress(String Ip, int Port){
        ServerPort = Port;
        ServertIp = Ip;
        sendClientAddress();
    }
    private String getHostSN(){
        String serial=null;
        try {  
        //long start = System.currentTimeMillis();  
            Process process = Runtime.getRuntime().exec(  
            new String[] { "wmic", "cpu", "get", "ProcessorId" });  
            process.getOutputStream().close();  
            Scanner sc = new Scanner(process.getInputStream());  
            String property = sc.next();  
            serial = sc.next();  
            System.out.println(property + ": " + serial); 
        //System.out.println("time:" + (System.currentTimeMillis() - start));  
        } catch (IOException e) {   
            e.printStackTrace();  
        }finally{
            return serial;
        }
    }
    private String getHostName(){
        Map<String,String> map = System.getenv();  
        return map.get("USERNAME");
    }
    private void ExeCommand(DeviceCommand.CommandParams s){
        System.out.println(TAG+"command:"+s.command+" paramsnum:"+s.paramsnum);
		if(s.command.equals(THISISSERVER_COMMAND) && s.paramsnum == 1){
            setServerAddress(s.sourceip,Integer.parseInt(s.params.get(0)));
		}else{                                                                                                                            
            System.out.println(TAG+"DeviceServerManager demo Do Not Supported Commond");                                                  
        } 
	}
    @Override
    public void run(){
        while(true){
			ExeCommand(mDeviceCommand.RecvCommand(ClientPort));
        }
	}
}
