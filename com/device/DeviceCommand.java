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
	public static final String CommandAccept = "ACCEPT_COMMAND";
	private static final int WaitCommandTimeout = 2000;
	public void SendCommand(String command, String ip, int port, int sourceport){
        try{
	    do{
            DatagramSocket datagramSocket = new DatagramSocket(sourceport);
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
            localip = socket.getLocalSocketAddress().toString();            
            datagramPacket = new DatagramPacket(buf, buf.length);
            socket.receive(datagramPacket);
	    socket.close();
        }catch(Exception e){
            e.printStackTrace();
        } 
            System.out.println("recvdata:"+ new String(buf,0,datagramPacket.getLength()));                                         
            System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort()); 
	    ReturnCommand(CommandAccept, datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort(), port);
	    char[] command=new char[64];
            List<String> params = new ArrayList<String>();
	    int paramnum = AnalysisCommand(new String(buf,0,datagramPacket.getLength()), command, params);
            return new CommandParams(command, paramnum, params, datagramPacket.getAddress().getHostAddress(),
			             datagramPacket.getPort(), datagramPacket.getAddress().getAddress()/*localip*/, port);
	}
	private int AnalysisCommand(String s, char[] command, List<String>params){
		StringTokenizer st = new StringTokenizer(s);
		String tmpcommand = st.nextToken();
		System.arraycopy(tmpcommand.toCharArray(), 0, command ,0 , tmpcommand.length());
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
            System.out.println("recvdata:"+ new String(buf,0,datagramPacket.getLength()));
            System.out.println("ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort());
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
			command = new String(icommand);
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
