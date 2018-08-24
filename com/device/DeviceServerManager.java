package com.device;

public class DeviceServerManager{
        private static DeviceConsole mDConsole = null;
	private int DeviceServerManagerDemoPort=9095;
	private DatagramSocket DeviceServerManagerDemoSocket;
	private List<DeviceServer> DeviceServerList;	
	private DeviceCommand mDeviceCommand;
	
	private static final String CREATE_COMMAND = "CreateNewDeviceServer";
	public static void main(String[] args){
		//DC = new DeviceConsole();
		mDConsole.start();
		DeviceServerManagerDemo.start();
		while(true){
			Iterator<DeviceServer> it = DeviceServerList.iterator();
                	while (it.hasNext()) {
                        	DeviceServer next = it.next();
				if(next.getState() == Thread.State.NEW){
					next.start();
				}
                	}
		}
	}
	public DeviceServerManager(){
		DeviceServerList = new ArrayList<DeviceServer>();
		DC = new DeviceConsole();
		mDeviceCommand = new DeviceCommand();
		DeviceServerManagerDemoSocket = new DatagramSocket(DeviceServerManagerDemoPort);
	}
	public void ShowDevices(){
		Iterator<DeviceServer> it = DeviceServerList.iterator();	
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}
	public void HoleNat(String sour, String dest, int holeport){
		Iterator<DeviceServer> it = DeviceServerList.iterator();
                while (it.hasNext()) {
			DeviceServer next = it.next();
                        if(next.ClientName.equals(sour)){
				Iterator<DeviceServer> it1 = DeviceServerList.iterator(); 
				while (it.hasNext()) {
                        		DeviceServer next1 = it.next();
                        		if(next1.ClientName.equals(dest)){
						next.Connect(next1, holeport);
						break;
					}
				}
				break;
			}
                }
	}
	public void AddNewDevice(String name, String Ip, int Port){
		Iterator<DeviceServer> it = DeviceServerList.iterator();                                                                          
                while (it.hasNext()) {                                                                                                            
                        DeviceServer next = it.next();
			if(next.ClientIp.equals(Ip)){
				return;
			}
		}
		DeviceServerList.add(new DeviceServer(name, Ip, Port));
	}
	private void ExeCommand(String s){
		char[] command=new char[32];
		List<String> params = new ArrayList<String>();
		int paramsnum = mDeviceCommand.AnalysisCommand(s, command, params);
		if((new String(command).equals(CREATE_COMMAND) && paramsnum == 2){
			AddNewDevice(params.get(0), params.get(1), Integer.parseInt(params.get(2)));
		}else{                                                                                                                            
                        System.out.println("DeviceServerManager demo Do Not Supported Commond");                                                  
                } 
	}
	private class DeviceServerManagerDemo extends Thread{
		@Override
        	public void run(){
                	while(true){
			    ExeCommand(mDeviceCommand.RecvCommand(DeviceServerManagerDemoPort));
                	}
		}
	}	
}
