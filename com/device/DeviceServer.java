package com.device;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
public class DeviceServer extends Thread{
    private static final String TAG = "[DeviceServer] ";
	public String ClientName;
    public String ClientId;
	public String ClientIp;
	public int ClientPort;
    private DeviceCommand mDeviceCommand;
    
	private String ServertIp;
	private int ServerPort;
    private static final String THISISCLIENT_COMMAND = "ThisIsClient";
    private static final String  HEARTBEAT_COMMAND = "HeartBeat";
    private static final String  GETADDRESS_COMMAND = "GetNewClientAddress";
    private static final String  SENDOTHERADDRESS_COMMAND = "SendOtherClientAddress";
	private int SshPort = 22;
	
	public DeviceServer(String name, String id, String ip, int port){
		ClientName = name;
		ClientIp = ip;
		ClientPort = port;
        ClientId = id;
	}
    public DeviceServer(String name, String id){
		ClientName = name;
        ClientId = id;
        mDeviceCommand = new DeviceCommand(); 
	}
    public void setClientAdress(String ip, int port){
        ClientIp = ip;
		ClientPort = port;
    }
    public String toString(){
        return new String(ClientName+" "+ClientId+" "+ClientIp+" "+ClientPort+" "+ServerPort);
    }
	@Override
    public void run(){
        System.out.println(TAG+"SN:"+ClientId+" starting");
		while(true){
            ExeCommand(mDeviceCommand.RecvCommand(ServerPort));
		}
	}
    public int getServerPort(){
        ServerPort = mDeviceCommand.getRecvCommandPort();
        return ServerPort;
    }
    private void ExeCommand(DeviceCommand.CommandParams s){
		if(s.command.equals(THISISCLIENT_COMMAND) && s.paramsnum == 0){
			setClientAdress(s.sourceip, s.sourceport);
		}else if(s.command.equals(HEARTBEAT_COMMAND) && s.paramsnum == 0){
            //ShowDevices();
            System.out.println(TAG+HEARTBEAT_COMMAND+"from"+s.sourceip+" "+s.sourceport);
        }else{                                                                                                                            
            System.out.println(TAG+"DeviceServerManager demo Do Not Supported Commond");                                                  
        } 
	}
	public void GetNewClientAddress(String destip, int destport){
		//ListenReturnport
        mDeviceCommand.SendCommand(new String(GETADDRESS_COMMAND+" "+destip+" "+destport), ClientIp, ClientPort, ServerPort);
	}
    public void GetNewClientAddress(int requestport, String destip, int destport){
		//ListenReturnport
        if(destport == 0){
            mDeviceCommand.SendCommand(new String(GETADDRESS_COMMAND+" "+destip+" "+destport), ClientIp, ClientPort, ServerPort);
        }else{
            mDeviceCommand.SendCommand(new String(GETADDRESS_COMMAND+" "+requestport+" "+destip+" "+destport), ClientIp, ClientPort, ServerPort);
        }
	}
    public void SendOtherClientAddress(String otherip, int otherport){
		//ListenReturnport
        mDeviceCommand.SendCommand(new String(SENDOTHERADDRESS_COMMAND+" "+otherport+" "+otherport), ClientIp, ClientPort, ServerPort);
	}
}
