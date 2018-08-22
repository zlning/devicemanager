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
	private DatagramSocket BeatSocket;
	public DeviceServer(String name){
		ClientName = name;
		BeatSocket = new DatagramSocket(9091);
	}
	@Override
        public void run(){
        	byte[] buf = new byte[1024];
		while(true){
			
        		DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length); // 1024
        		BeatSocket.receive(datagramPacket);
			System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
			ClientBeatIp = datagramPacket.getAddress().getHostAddress();
			ClientBeatPort = datagramPacket.getPort();
		}
		BeatSocket.close();
	}
	public void PostCommand(){

	} 
	public void GetClientIpPort(){
	
	}
}
