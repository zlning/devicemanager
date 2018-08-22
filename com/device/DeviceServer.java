package com.device;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class DeviceServer extends Thread{
	private String ClientName;
	private String ClientIp;
	private int ClientPort;
	private String ClientBeatIp;
	private int ClientBeatPort;
	private int ListenBeatPort;
	private int ListenReturnPort = 9092;
	private int SshPort = 22;
	private DatagramSocket BeatSocket;
	
	public DeviceServer(String name, String ip, int port){
		ClientName = name;
		ClientBeatIp = ip;
		ClientBeatPort = port;
		BeatSocket = new DatagramSocket(Port);
	}
	@Override
        public void run(){
        	byte[] buf = new byte[1024];
		while(true){
			
        		DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
        		BeatSocket.receive(datagramPacket);
			System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
			//ClientBeatIp = datagramPacket.getAddress().getHostAddress();
			//ClientBeatPort = datagramPacket.getPort();
		}
		BeatSocket.close();
	}
	public void GetClientIpPort(){
		//ListenReturnport
		PostCommand("getclientipport", ListenReturnport, null);
		byte[] buf = new byte[1024];
		ReturnSocket = new DatagramSocket(ListenBeatPort);
		DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
                ReturnSocket.receive(datagramPacket);
                System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
                ClientIp = datagramPacket.getAddress().getHostAddress();
                ClientPort = datagramPacket.getPort();
		ReturnSocket.close();
	}
	public void Connect(DeviceServer dest){
		PostCommand("connect", dest.ClientIp, dest.ClientPort);
	}
	private void PostCommand(String s,String param1,int param2){
		if(s.equals("getclientipport")){
			DatagramSocket datagramSocket = new DatagramSocket();
        		String data = s+" "+param2;
        		DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length,InetAddress.getByName(ClientBeatIp),ClientBeatPort);
        		datagramSocket.send(packet);
        		datagramSocket.close();
			
		}else if(s.equals("connect")){
			DatagramSocket datagramSocket = new DatagramSocket();
                        String data = s+" "+param1+" "+param2;                                                                                               
                        DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length,InetAddress.getByName(ClientBeatIp),ClientBeatPort); 
                        datagramSocket.send(packet);
                        datagramSocket.close();
		}else{
			System.out.println("DeviceServerManager Post Do Not Supported Commond");
		}
	}
}
