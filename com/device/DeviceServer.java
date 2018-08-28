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
    
    private DeviceServer mOtherDeviceServer;
    private String ServertIp;
    private int ServerPort;
    private static final String THISISCLIENT_COMMAND = "ThisIsClient";
    private static final String  HEARTBEAT_COMMAND = "HeartBeat";
    private static final String  GETADDRESS_COMMAND = "GetNewClientAddress";
    private static final String  SENDOTHERADDRESS_COMMAND = "SendOtherClientAddress";
    private static final String ANEWADDRESS_COMMAND = "NewClientAddress";
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
        mDeviceCommand = new DeviceCommand(0); 
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
            ExeCommand(mDeviceCommand.RecvCommand());
		}
	}
    public int getServerPort(){
        ServerPort = mDeviceCommand.mPort;
        return ServerPort;
    }
    private void ExeCommand(DeviceCommand.CommandParams s){
        if(s.command.equals(THISISCLIENT_COMMAND) && s.paramsnum == 0){
            setClientAdress(s.sourceip, s.sourceport);
        }else if(s.command.equals(HEARTBEAT_COMMAND) && s.paramsnum == 0){
            //ShowDevices();
            System.out.println(TAG+HEARTBEAT_COMMAND+"from"+s.sourceip+" "+s.sourceport);
        }else if(s.command.equals(SENDOTHERADDRESS_COMMAND) && s.paramsnum == 0){
            //need find the other
            if(mOtherDeviceServer!=null){
                mOtherDeviceServer.SendOtherClientAddress(s.sourceip, s.sourceport);
            }
        }
        else{                                                                                                                            
            System.out.println(TAG+"DeviceServerManager demo Do Not Supported Commond");                                                  
        } 
    }
//HoleNewClientAddress
    public void HoleNewClientAddress(int requestport, DeviceServer dest){
        mDeviceCommand.SendNoReply(new String(GETADDRESS_COMMAND+" "+requestport+" "+dest.ClientIp+" "+dest.ClientPort), ClientIp, ClientPort);
        mOtherDeviceServer = dest;
    }
    public void SendOtherClientAddress(String otherip, int otherport){
        mDeviceCommand.SendNoReply(new String(SENDOTHERADDRESS_COMMAND+" "+otherip+" "+otherport), ClientIp, ClientPort);
    }
}
