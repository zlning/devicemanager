package com.device;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.util.List;

public class DeviceCommand{
	//public List<String> mCommandParams;
	//public String mCommand
	public static final String CommandAccept = "ACCEPT_COMMAND";
	private static final int WaitCommandTimeout = 2000;
	public void SendCommand(String command, String ip, int port, int sourceport){
		DatagramSocket datagramSocket = new DatagramSocket(sourceport);
                String data = command;
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
                do{
			datagramSocket.send(packet);
		}while(!WaitCommandReturn(sourceport));
                datagramSocket.close();
	}
	public String RecvCommand(int port){
		DatagramSocket  socket = new DatagramSocket(port);                                                                                
                byte[] buf = new byte[1024];                                                                                                      
                DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);                                                              
                socket.receive(datagramPacket);                                                                                                   
                System.out.println("recvdata:"+ new String(buf,0,datagramPacket.getLength()));                                         
                System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort()); 
		ReturnCommand(CommandAccept, datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort(), port);
		return new String(buf,0,datagramPacket.getLength());
	}
	public int AnalysisCommand(String s, String command, List<String>params){
		StringTokenizer st = new StringTokenizer(s);
		command = st.nextToken();
		int paramsnum = 0;
		while(st.hasMoreElements()){
			paramsnum++;
			params.add(st.nextToken());
		}
		return paramsnum;	
	}
	private void ReturnCommand(String result, String ip, int port, int sourceport){
                DatagramSocket datagramSocket = new DatagramSocket(sourceport);
                String data = result;
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
                datagramSocket.send(packet);
                datagramSocket.close();
	}
	private boolean WaitCommandReturn(int port){
        	DatagramSocket  socket = new DatagramSocket(port);
        	byte[] buf = new byte[1024];
        	DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);
		socket.setSoTimeout(WaitCommandTimeout); 
        	socket.receive(datagramPacket);
        	System.out.println("recvdata:"+ new String(buf,0,datagramPacket.getLength()));
        	System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
		if(new String(buf,0,datagramPacket.getLength()).equals(CommandAccept)){
			return true;
		}else{
			return false;
		}
	}
}
