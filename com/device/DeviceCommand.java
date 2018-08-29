package com.device;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.StringTokenizer;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class DeviceCommand{
    //public List<String> mCommandParams;
    //public String mCommand
    private static final String TAG = "[DeviceCommand] ";
    public static final String CommandAccept = "ACCEPT_COMMAND";
    private static final int WaitCommandTimeout = 2000;
    private String mPath = Class.class.getClass().getResource("/").getPath()+"key";
    private int RecvCommandPort;
    private int SendCommandPort;
    //=====================new interface===========================//
    public int mPort;
    DatagramSocket mSocket;
    private String mADSkey;
    public DeviceCommand(int port){
        try{
            mSocket = new DatagramSocket(port);
            System.out.println(TAG+"localport:"+mSocket.getLocalPort());
            mPort = mSocket.getLocalPort();
            mADSkey = "c4BvSO6cs43fR0Rd";
            System.out.println(TAG+"mPath:"+mPath);
            mADSkey = AES.getFileMD5String(new File(mPath)).substring(3,19);
            System.out.println(TAG+"mADSkey:"+mADSkey);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void SendNoReply(String command, String ip, int port){
        System.out.println(TAG+"sendnoreply commad:"+command); 
	try{
            String data = AES.Encrypt(command,mADSkey);
            DatagramPacket packet = new DatagramPacket(data.getBytes(), data.getBytes().length, InetAddress.getByName(ip), port);
            mSocket.send(packet);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    @Deprecated
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
    public CommandParams AnalysisCommand(String s){
        
        char[] command=new char[64];
        List<String> params = new ArrayList<String>();
        int paramnum = AnalysisCommand(s, command, params);
        return new CommandParams(command, paramnum, params, null, 0, null, 0); 
    }
    public CommandParams RecvCommand(){
        DatagramPacket datagramPacket=null;
        String recdata=null;
        byte[] buf = new byte[1024];
        try{
            datagramPacket = new DatagramPacket(buf, buf.length);
            mSocket.setSoTimeout(0);
            mSocket.receive(datagramPacket);
            recdata = AES.Decrypt(new String(buf,0,datagramPacket.getLength()),mADSkey);
        }catch(Exception e){
            e.printStackTrace();
        } 
            System.out.println(TAG+"before decorder"+new String(buf,0,datagramPacket.getLength()));
            System.out.println(TAG+"recvdata:"+ recdata);                                         
            System.out.println(TAG+"ip:"+datagramPacket.getAddress().getHostAddress()+" port:"+datagramPacket.getPort()); 
	    // ReturnCommand(CommandAccept, datagramPacket.getAddress().getHostAddress(), datagramPacket.getPort());
	    char[] command=new char[64];
            List<String> params = new ArrayList<String>();
	    int paramnum = AnalysisCommand(recdata, command, params);
        return new CommandParams(command, paramnum, params, datagramPacket.getAddress().getHostAddress(),
			             datagramPacket.getPort(), mSocket.getLocalAddress().toString(), mPort);
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
    public String exec(String ss){
        //String s = null;
        // 创建命令集合,放入执行的命令
        List<String> commands = new ArrayList<String>();
        commands.add("/bin/sh");
        commands.add("-c");
        commands.add(ss);
        try{
        // 构建ProcessBuilder
        ProcessBuilder builder = new ProcessBuilder(commands);
        // 构建ProcessBuilder也可以不适用list,直接在括号里写命令,每个命令是一个String字符串
//        ProcessBuilder builder = new ProcessBuilder("/bin/sh", "-c", "ps -ef | grep chrome");
        Process start = builder.start();
        new Scanner(start.getInputStream());
        new Scanner(start.getErrorStream());
        byte[] bytes = new byte[0];
        bytes = new byte[start.getInputStream().available()];
        start.getInputStream().read(bytes);
        String str = new String(bytes);
        //String str = new String(ByteStreams.toByteArray(start.getInputStream()));
        byte[] bytes1 = new byte[0];
        bytes1 = new byte[start.getErrorStream().available()];
        start.getErrorStream().read(bytes1);
        String strerror = new String(bytes1);
        //String strerror = new String(ByteStreams.toByteArray(start.getErrorStream()));
        if(str.length()!=0){
            System.out.println("str:"+str);
            return str;
        }
        if(strerror.length()!=0){
            System.out.println("strerror:"+strerror);
            return strerror;
        } 
        /*if(start.getInputStream()){
             System.out.println("inputstream == null");
        }else if(start.getErrorStream() == null){
             System.out.println("errorstream == null");
        }*/
        
       /* System.out.println("命令执行结果为: \n");
        while (scanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }
        System.out.println("命令执行错误结果为: \n");
        while (errorScanner.hasNextLine()) {
            System.out.println(scanner.nextLine());
        }

        if (scanner != null) {
            scanner.close();
        }
        if (errorScanner != null) {
            errorScanner.close();
        }*/
        }catch (IOException e){
             e.printStackTrace();
             //return null;
        }finally{
             return null;
        }
        //System.exit(0);
    }
    public void release(){
        mSocket.close();
    }
    //=================================================//
    public DeviceCommand(){
	
    }
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
        System.out.println(TAG+"send1 commad:"+command);
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
	System.out.println(TAG+"send2 commad:"+command);
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
