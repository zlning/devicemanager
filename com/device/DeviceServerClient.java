package com.device;
import java.util.Scanner;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.Iterator;

public class DeviceServerClient extends Thread{
    private static final String TAG = "[DeviceServerClient] ";
    private DeviceCommand mDeviceCommand;
    private DeviceCommand mHoleDeviceCommand;
    public String ClientName;
    public String ClientId;
    private int ClientPort;
    private String ClientIp;
    private String ServertIp;
    private int ServerPort;
    private int DeviceServerManagerDemoPort=9095;
    private String DeviceServerManagerIp="10.180.89.205";//"10.180.89.205";
    
    private static final String THISISSERVER_COMMAND = "ThisIsServer";
    private static final String WHEREIS_COMMAND = "AskDeviceServerAddress";
   // private static final String SENDNEWADDRESS_COMMAND = "SendNewAddress";
    private static final String CREATE_COMMAND = "CreateNewDeviceServer";
    private static final String THISISCLIENT_COMMAND = "ThisIsClient";
    private static final String GETADDRESS_COMMAND = "GetNewClientAddress";
    private static final String ANEWADDRESS_COMMAND = "NewClientAddress";   
    private static final String  SENDOTHERADDRESS_COMMAND = "SendOtherClientAddress";
    private static final String EXEC_COMMAND = "exec";
    private static final String RETURN_COMMAND = "ReturnResult";
    
    public DeviceServerClient(String name){
        ClientName = name;
        mDeviceCommand = new DeviceCommand(0);
        CreateNewDeviceServer();
        ClientPort = mDeviceCommand.mPort;
    }
    public static void main(String[] args){
        DeviceServerClient iDeviceServerClient=null;
        if(args.length>=1){
            iDeviceServerClient = new DeviceServerClient(args[0]);
        }else{
            iDeviceServerClient = new DeviceServerClient(null);
        }
        iDeviceServerClient.start();
        while(iDeviceServerClient.ServertIp == null ||iDeviceServerClient.ServerPort==0){
            iDeviceServerClient.AskDeviceServerAddress();
        try{                                                                                                                                  
                Thread.sleep(1*1000);                                                                                                            
            }catch(Exception e){                                                                                                                  
                e.printStackTrace();                                                                                                              
            } 
        }
        iDeviceServerClient.mDeviceCommand.SendNoReply("ShowDevices",iDeviceServerClient.DeviceServerManagerIp,iDeviceServerClient.DeviceServerManagerDemoPort);
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
       // mDeviceCommand.SendNoReply(CREATE_COMMAND+" "+getHostName()+" "+getHostSN(),DeviceServerManagerIp,DeviceServerManagerDemoPort);
        mDeviceCommand.SendNoReply(CREATE_COMMAND+" "+getHostName()+" "+getHostSN(),DeviceServerManagerIp,DeviceServerManagerDemoPort); 
    }
    private void AskDeviceServerAddress(){
        mDeviceCommand.SendNoReply(WHEREIS_COMMAND+" "+getHostName()+" "+getHostSN(),DeviceServerManagerIp,DeviceServerManagerDemoPort);
    }
    private void sendClientAddress(){
        mDeviceCommand.SendNoReply(THISISCLIENT_COMMAND,ServertIp,ServerPort);
    }
    public void sendHeartBeat(){
        mDeviceCommand.SendNoReply(THISISCLIENT_COMMAND,ServertIp,ServerPort);
    }
    private void setServerAddress(String Ip, int Port){
        ServerPort = Port;
        ServertIp = Ip;
        sendClientAddress();
    }
    private String getHostSN(){
        if(ClientId != null){
            return ClientId;
        }
        int length = 10;
        String str="zxcvbnmlkjhgfdsaqwertyuiopQWERTYUIOPASDFGHJKLZXCVBNM1234567890";
        Random random=new Random();  
        StringBuffer sb=new StringBuffer();
        for(int i=0; i<length; ++i){
          int number=random.nextInt(62);
          sb.append(str.charAt(number));
        }
        ClientId = sb.toString();
        return ClientId;
    }
    private String getHostName(){
        Map<String,String> map = System.getenv();  
        if(ClientName != null)
            return ClientName;
        else if(map.get("USERNAME")!=null)
            return map.get("USERNAME");
        else if(map.get("USER")!=null)
            return map.get("USER");
        else
            return "MachineNoName";//map.get("USERNAME");
    }
    private void GetNewClientAddress(int requestport, String destip, int destport){
         mHoleDeviceCommand = new DeviceCommand(requestport);
         mHoleDeviceCommand.SendNoReply(SENDOTHERADDRESS_COMMAND, ServertIp, ServerPort);
         mHoleDeviceCommand.SendNoReply(ANEWADDRESS_COMMAND, destip, destport);
    /*new Thread(new Runnable() {
  
    @Override
    public void run() {
        // TODO Auto-generated method stub
        ExeCommand(mHoleDeviceCommand.RecvCommand());
    }
    }).start();*/
    }
    private void ExecSendback(DeviceCommand.CommandParams s){
        String execommand=null;
        Iterator<String> it = s.params.iterator();
        if(it.hasNext()){
            execommand = it.next();
        }
        while(it.hasNext()){
            execommand = execommand+" "+it.next();
        }
        mDeviceCommand.SendNoReply(RETURN_COMMAND+" "+mDeviceCommand.PackageParams(mDeviceCommand.exec(execommand)),ServertIp,ServerPort);
    }
    private void ExeCommand(DeviceCommand.CommandParams s){
        System.out.println(TAG+"command:"+s.command+" paramsnum:"+s.paramsnum);
        if(s.command.equals(THISISSERVER_COMMAND) && s.paramsnum == 1){
            setServerAddress(s.sourceip,Integer.parseInt(s.params.get(0)));
        }else if(s.command.equals(GETADDRESS_COMMAND) && s.paramsnum == 3){
            GetNewClientAddress(Integer.parseInt(s.params.get(0)),s.params.get(1),Integer.parseInt(s.params.get(2)));
        }else if (s.command.equals(SENDOTHERADDRESS_COMMAND) && s.paramsnum == 2){
            mDeviceCommand.SendNoReply(ANEWADDRESS_COMMAND, s.params.get(0), Integer.parseInt(s.params.get(1)));
        }else if(s.command.equals(ANEWADDRESS_COMMAND)&& s.paramsnum ==0){
            System.out.println(TAG+"hole success!! command:"+ANEWADDRESS_COMMAND+" sourceip:"+s.sourceip+" sourceport:"+s.sourceport);
        }else if(s.command.equals(EXEC_COMMAND)){
            //mDeviceCommand.exec();
            //ExecSendback(s);
            new Thread(new Runnable() {
                 @Override
                 public void run() {
                      ExecSendback(s);
                 }
            }).start();
        }else{   
            System.out.println(TAG+"DeviceServerManager demo Do Not Supported Commond");                                                  
        }
    }
    @Override
    public void run(){
        while(true){
            ExeCommand(mDeviceCommand.RecvCommand());
        }
    }
}
