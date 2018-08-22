package com.device;

public class DeviceServerManager{
        private static DeviceConsole DC = null;
	private int DeviceServerManagerDemoPort=9095;
	private DatagramSocket DeviceServerManagerDemoSocket;
	private List<DeviceServer> DeviceServerList;	

	public static void main(String[] args){
		//DC = new DeviceConsole();
		DC.start();
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
		StringTokenizer st = new StringTokenizer(s);
                String command = st.nextToken();
		
		if(st.hasMoreElements()){
                	String param1=st.nextToken();
		if(st.hasMoreElements())
			String param2=st.nextToken();
		if(command.equals("creatnewdeviceserver")){
			AddNewDevice(command, param1, Integer.parseInt(param2));
		}else{
			System.out.println("DeviceServerManager demo Do Not Supported Commond");
		}
	}
	private class DeviceServerManagerDemo extends Thread{
		@Override
        	public void run(){
		        byte[] buf = new byte[1024];
                	while(true){
                        	DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
                        	BeatSocket.receive(datagramPacket);
                        	System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
                        	//ClientBeatIp = datagramPacket.getAddress().getHostAddress();
                        	//ClientBeatPort = datagramPacket.getPort();
				ExeCommand(new String(buf,0,datagramPacket.getLength())+" "+datagramPacket.getAddress().getHostAddress()
					   +" "+datagramPacket.getPort());
                
				DatagramSocket datagramSocket1 = new DatagramSocket(DeviceServerManagerDemoPort);
                		String data = "accept";
                		DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, datagramPacket.getAddress().getHostAddress()
									   ,datagramPacket.getPort());
                		datagramSocket1.send(packet);
                		datagramSocket1.close();
                	}
                	DeviceServerManagerDemoSocket.close(); 
		}
	}	
}
