package com.device;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class DeviceServerManager{
    private static DeviceConsole mDConsole;
    private DeviceServerManagerDemo ServerDemo;
    private int DeviceServerManagerDemoPort=9095;

    private List<DeviceServer> DeviceServerList;	
    private DeviceCommand mDeviceCommand;
    
    private static final String TAG = "[DeviceServerManager] ";
    private static final String CREATE_COMMAND = "CreateNewDeviceServer";//CREATE_COMMAND name id
    private static final String SHOW_COMMAND = "ShowDevices";//SHOW_COMMAND 
    private static final String THISISSERVER_COMMAND = "ThisIsServer";//
    private static final String WHEREIS_COMMAND = "AskDeviceServerAddress";//WHEREIS_COMMAND name id
    private static final String SENDNEWADDRESS_COMMAND = "SendNewAddress";
    public static void main(String[] args){
		//DC = new DeviceConsole();
        DeviceServerManager iDeviceServerManager= new DeviceServerManager();
	    iDeviceServerManager.mDConsole.start();
	    iDeviceServerManager.ServerDemo.start();
	    while(true){
                synchronized(iDeviceServerManager.DeviceServerList) {
		    Iterator<DeviceServer> it = iDeviceServerManager.DeviceServerList.iterator();
                    while (it.hasNext()) {
                    DeviceServer next = it.next();
			if(next.getState() == Thread.State.NEW){
			    System.out.println(TAG+"next start");
			    next.start();
			}
                    }
		}
		try{
	            //Thread.sleep(1000);
		}catch(Exception e){
		    e.printStackTrace();
		}
	    }
	}
	public DeviceServerManager(){
		DeviceServerList = Collections.synchronizedList(new ArrayList<DeviceServer>());
		mDConsole = new DeviceConsole();
		mDeviceCommand = new DeviceCommand(DeviceServerManagerDemoPort);
                ServerDemo = new DeviceServerManagerDemo();
		//DeviceServerManagerDemoSocket = new DatagramSocket(DeviceServerManagerDemoPort);
	}
	public void ShowDevices(){
		Iterator<DeviceServer> it = DeviceServerList.iterator();	
		while (it.hasNext()) {
			System.out.println(TAG+it.next());
		}
	}
    public void StartHoleClientNat(int requestport, String sourceId, String destId){
        //GetNewClientAddress(requestport, String destip, int destport);
        Iterator<DeviceServer> it = DeviceServerList.iterator();
        while (it.hasNext()) {
			DeviceServer source = it.next();
            if(source.ClientId.equals(sourceId)){
				Iterator<DeviceServer> it1 = DeviceServerList.iterator(); 
                DeviceServer dest = it1.next();
				while (it1.hasNext()) {
                    if(dest.ClientId.equals(destId)){
					    source.HoleNewClientAddress(requestport, dest);
						break;
					}
				}
				break;
			}
        }
    }
    private void AddNewDevice(String name, String Id, String Ip, int Port){
        synchronized(DeviceServerList) {
	Iterator<DeviceServer> it = DeviceServerList.iterator();                                                                          
        while (it.hasNext()) {                                                                                                            
            DeviceServer next = it.next();
			if(next.ClientId.equals(Id)){
                System.out.println(TAG+"Id:"+Id+"has exist");
				return;
			}
		}
	}
        DeviceServerList.add(new DeviceServer(name, Id));
    }
    private void AskDeviceServerAddress(String name, String Id, String sourceip, int sourceport){
        DeviceServer next=null;
        synchronized(DeviceServerList) {
	Iterator<DeviceServer> it = DeviceServerList.iterator();        
        while (it.hasNext()) {                                                                                                            
            next = it.next();
			if(next.ClientId.equals(Id)){
                break;
            }
		}
        if(!it.hasNext() && !next.ClientId.equals(Id) && next != null){
            System.out.println(TAG+"Id:"+Id+"is not exist");
			return;
        }
        }
        System.out.println(TAG+new String(THISISSERVER_COMMAND+" "+next.getServerPort()));
        mDeviceCommand.SendNoReply(new String(THISISSERVER_COMMAND+" "+next.getServerPort()), sourceip, sourceport);
    }
    /*private void SendOtherClientAddress(String destId, String ip, int port){
        Iterator<DeviceServer> it = DeviceServerList.iterator();
        DeviceServer next=null;        
        while (it.hasNext()) {                                                                                                            
            next = it.next();
			if(next.ClientId.equals(destId)){
                break;
            }
		}
        if(!it.hasNext() && !next.ClientId.equals(destId) && next != null){
            System.out.println(TAG+"Id:"+destId+"is not exist");
			return;
        }
        next.SendOtherClientAddress(ip, port);
    }*/
	private void ExeCommand(DeviceCommand.CommandParams s){
        System.out.println(TAG+"command:"+s.command+" paramsnum:"+s.paramsnum);
		if(s.command.equals(CREATE_COMMAND) && s.paramsnum == 2){
			AddNewDevice(s.params.get(0), s.params.get(1), s.sourceip, s.sourceport);
		}else if(s.command.equals(SHOW_COMMAND) && s.paramsnum == 0){
            ShowDevices();
        }else if(s.command.equals(WHEREIS_COMMAND) && s.paramsnum == 2){
            AskDeviceServerAddress(s.params.get(0), s.params.get(1), s.sourceip, s.sourceport);
        }else if(s.command.equals(SENDNEWADDRESS_COMMAND) && s.paramsnum == 3){
            //SendOtherClientAddress(s.params.get(0), s.params.get(1), Integer.parseInt(s.params.get(2)));
        }else{                                                                                                                            
            System.out.println(TAG+"DeviceServerManager demo Do Not Supported Commond");                                                  
        } 
	}
	private class DeviceServerManagerDemo extends Thread{
		@Override
        	public void run(){
             while(true){
			    ExeCommand(mDeviceCommand.RecvCommand());
            }
		}
	}	
}
