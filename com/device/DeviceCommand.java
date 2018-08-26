package com.device;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.ArrayList;

public class DeviceCommand{
	//public List<String> mCommandParams;
	//public String mCommand
    private static final String TAG = "[DeviceCommand] ";
	public static final String CommandAccept = "ACCEPT_COMMAND";
	private static final int WaitCommandTimeout = 2000;
    private int RecvCommandPort;
    private int SendCommandPort;
    //=====================new interface===========================//
    public int mPort;
    DatagramSocket mSocket;
    public DeviceCommand(int port){
        try{
            DatagramSocket mSocket = new DatagramSocket(port);
            System.out.println(TAG+"localport:"+mSocket.getLocalPort());
            mPort = datagramSocket.getLocalPort(); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void SendNoReply(String command, String ip, int port){
        try{
            String data = command;
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            mSocket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void Send(String command, String ip, int port){
        try{
	    do{
            String data = command;
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            mSocket.send(packet);
	    }while(!WaitCommandReturn());
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public CommandParams RecvCommand(){
        DatagramPacket datagramPacket=null;
        byte[] buf = new byte[1024];
        try{
            datagramPacket = new DatagramPacket(buf, buf.length);
            mSocket.setSoTimeout(0);
            mSocket.receive(datagramPacket);
        }catch(Exception e){
            e.printStackTrace();
        } 
            System.out.println(TAG+"recvdata:"+ new String(buf,0,datagramPacket.getLength()));                                         
            System.out.println(TAG+"ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort()); 
	    //ReturnCommand(CommandAccept, datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort());
	    char[] command=new char[64];
            List<String> params = new ArrayList<String>();
	    int paramnum = AnalysisCommand(new String(buf,0,datagramPacket.getLength()), command, params);
        return new CommandParams(command, paramnum, params, datagramPacket.getAddress().getHostAddress(),
			             datagramPacket.getPort(), mSocket.getLocalAddress().toString(), port);
    }
    private boolean WaitCommandReturn(){
        byte[] buf = new byte[1024];
        DatagramPacket datagramPacket=null;
        try{
            datagramPacket = new DatagramPacket(buf, buf.length);
            mSocket.setSoTimeout(WaitCommandTimeout); 
            mSocket.receive(datagramPacket);
	    }catch(SocketTimeoutException e){
		    return false;
	    }catch(Exception e){
            e.printStackTrace();
        }finally{
	        //socket.close();
	    }
        System.out.println(TAG+"recvdata:"+ new String(buf,0,datagramPacket.getLength()));
        System.out.println(TAG+"ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
        char[] command=new char[64];
        List<String> params = new ArrayList<String>();
	    int paramnum = AnalysisCommand(new String(buf,0,datagramPacket.getLength()), command, params);
        if(new String(command).equals(CommandAccept)){
		    return true;
	    }else{
		    return false;
	    }
    }
    private void ReturnCommand(String result, String ip, int port){
        try{
            String data = result;
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            mSocket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
	}
    public void release(){
        mSocket.close();
    }
    //=================================================//
    public void SendCommandNoReply(String command, String ip, int port, int sourceport){
        try{
            DatagramSocket datagramSocket = new DatagramSocket(sourceport);
            if(SendCommandPort==0){
                System.out.println(TAG+"localport:"+datagramSocket.getLocalPort());
                SendCommandPort = datagramSocket.getLocalPort();
            }
            if(sourceport==0){
                sourceport = datagramSocket.getLocalPort();
            }
            String data = command;
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            datagramSocket.send(packet);
            datagramSocket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
	public void SendCommand(String command, String ip, int port, int sourceport){
        try{
	    do{
            DatagramSocket datagramSocket = new DatagramSocket(sourceport);
            if(SendCommandPort==0){
                System.out.println(TAG+"localport:"+datagramSocket.getLocalPort());
                SendCommandPort = datagramSocket.getLocalPort();
            }
            if(sourceport==0){
                sourceport = datagramSocket.getLocalPort();
            }
            String data = command;
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            datagramSocket.send(packet);
            datagramSocket.close();
	    }while(!WaitCommandReturn(sourceport));
        }catch(Exception e){
            e.printStackTrace();
        }
	}
    public void SendCommand(String command, String ip, int port){
        try{
            int localport;
            do{
                DatagramSocket datagramSocket = new DatagramSocket();
                String data = command;
                DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
                datagramSocket.send(packet);
                localport = datagramSocket.getLocalPort();
                datagramSocket.close();
            }while(!WaitCommandReturn(localport));
            
        }catch(Exception e){
            e.printStackTrace();
        }  
	}
	public CommandParams RecvCommand(int port){
        DatagramPacket datagramPacket=null;
        byte[] buf = new byte[1024];
	    String localip=null;
        try{
            DatagramSocket  socket = new DatagramSocket(port);
            if(RecvCommandPort == 0){
                System.out.println(TAG+"localport:"+socket.getLocalPort());
                RecvCommandPort = socket.getLocalPort();
            }
            if(port == 0){
                port = socket.getLocalPort();
            }
            datagramPacket = new DatagramPacket(buf, buf.length);
            socket.receive(datagramPacket);
            localip = socket.getLocalAddress().toString();
	        socket.close();
        }catch(Exception e){
            e.printStackTrace();
        } 
            System.out.println(TAG+"recvdata:"+ new String(buf,0,datagramPacket.getLength()));                                         
            System.out.println(TAG+"ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort()); 
	    ReturnCommand(CommandAccept, datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort(), port);
	    char[] command=new char[64];
            List<String> params = new ArrayList<String>();
	    int paramnum = AnalysisCommand(new String(buf,0,datagramPacket.getLength()), command, params);
        return new CommandParams(command, paramnum, params, datagramPacket.getAddress().getHostAddress(),
			             datagramPacket.getPort(), localip, port);
	}
    public int getRecvCommandPort(){
        return RecvCommandPort;
    }
    public int getSendCommandPort(){
        return SendCommandPort;
    }
	private int AnalysisCommand(String s, char[] command, List<String>params){
		StringTokenizer st = new StringTokenizer(s);
		String tmpcommand = st.nextToken();
		System.arraycopy(tmpcommand.toCharArray(), 0, command ,0 , tmpcommand.length());
        //System.out.println("tmpcommand_char:"+new String(command).trim()+" end");
		//System.out.println("tmpcommand_char:"+tmpcomm.toCharArray()+" command:"+command);
		int paramsnum = 0;
		while(st.hasMoreElements()){
			paramsnum++;
			params.add(st.nextToken());
		}
		return paramsnum;	
	}
	private void ReturnCommand(String result, String ip, int port, int sourceport){
        try{
            DatagramSocket datagramSocket = new DatagramSocket(sourceport);
            String data = result;
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            datagramSocket.send(packet);
	    datagramSocket.close();
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	private boolean WaitCommandReturn(int port){
        byte[] buf = new byte[1024];
        DatagramPacket datagramPacket=null;
        DatagramSocket socket=null;
        try{
	        socket = new DatagramSocket(port); 
	    }catch(Exception e){                                                                                                                      
            e.printStackTrace();
        }
        try{
            //socket = new DatagramSocket(port);
            datagramPacket = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(WaitCommandTimeout); 
            socket.receive(datagramPacket);
	    }catch(SocketTimeoutException e){
		    return false;
	    }catch(Exception e){
            e.printStackTrace();
        }finally{
	        socket.close();
	    }
        System.out.println(TAG+"recvdata:"+ new String(buf,0,datagramPacket.getLength()));
        System.out.println(TAG+"ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
        if(new String(buf,0,datagramPacket.getLength()).equals(CommandAccept)){
		    return true;
	    }else{
		    return false;
	    }
	}

	class CommandParams{
		public List<String> params;
		public int paramsnum;
		public String command;
		public String sourceip;
		public int sourceport;
		public String destip;
		public int destport;
        CommandParams(char[] icommand, int num, List<String> paramlist, String sip,                                     
                      int sport, String lip, int lport){
			command = new String(icommand).trim();
			paramsnum = num;
			params = paramlist;
			sourceip = sip;
			sourceport = sport;
			destip = lip;
			destport = lport;
		}
		/*public String toString(){
			return new String()
		}*/
	}
}
