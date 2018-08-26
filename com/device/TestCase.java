package com.device;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Map;
public class TestCase{
    private static int DeviceServerManagerDemoPort=9095;
    private static final String CREATE_COMMAND = "CreateNewDeviceServer";
    private static final String SHOW_COMMAND = "ShowDevices";
    private static final String WHEREIS_COMMAND = "AskDeviceServerAddress";
    private static final String THISISCLIENT_COMMAND = "ThisIsClient";
    private static final String SENDNEWADDRESS_COMMAND = "SendNewAddress";
    public static void main(String[] args){
	//=========test DeviceCommand=================
        //DeviceCommand command = new DeviceCommand();
        //command.SendCommand("showdevice namea nameb","10.180.89.205",9092,9099);
        //command.SendCommand("showdevice namea nameb","192.168.1.201",9092);
       //DeviceCommand.CommandParams s = command.RecvCommand(0);
	//char[] comm=new char[32];
	//List<String> params = new ArrayList<String>();
	//int paramnum = command.AnalysisCommand(s,comm,params);
	//System.out.println(comm);
	//System.out.println("command:"+s.command+" paramnum:"+s.paramsnum+" parama1:"+s.params.get(0));
	//System.out.println("sourceip:"+s.sourceip+" sourceport:"+s.sourceport);
	//System.out.println("destip:"+s.destip+" destport:"+s.destport);
        //System.out.println("paramnum:"+paramnum+" parama1:"+params.get(0));
	//================test get SN===============
	 /* try {  
        long start = System.currentTimeMillis();  
        Process process = Runtime.getRuntime().exec(  
        new String[] { "wmic", "cpu", "get", "ProcessorId" });  
        process.getOutputStream().close();  
        Scanner sc = new Scanner(process.getInputStream());  
        String property = sc.next();  
        String serial = sc.next();  
        System.out.println(property + ": " + serial);  
        System.out.println("time:" + (System.currentTimeMillis() - start));  
    } catch (IOException e) {   
        e.printStackTrace();  
    }*/
    //=====================================================
        //System.out.println("port:"+(new InetSocketAddress(0)).getPort());
        //char[] test = {'a','b','c','d'};
        //test[2]='r';
        //System.out.println("test:"+new String(test)+" end");
        //==========================/
        Map<String,String> map = System.getenv();  
        System.out.println(map.get("USERNAME"));
                System.out.println(map.get("COMPUTERNAME"));
        System.out.println(map.get("USERDOMAIN"));
    //=========================test DeviceSerManager=========================
    /*DeviceCommand command = new DeviceCommand();
    command.SendCommand(CREATE_COMMAND+" machineA LC2343410","192.168.1.201",DeviceServerManagerDemoPort,9099);
    command.SendCommand(WHEREIS_COMMAND+" machineA LC2343410","192.168.1.201",DeviceServerManagerDemoPort,9099);
    DeviceCommand.CommandParams s = command.RecvCommand(9099);
    System.out.println("sourceip:"+s.sourceip+" sourceport:"+Integer.parseInt(s.params.get(0)));
    command.SendCommand(THISISCLIENT_COMMAND,s.sourceip, Integer.parseInt(s.params.get(0)),9098);
    command.SendCommand(SHOW_COMMAND,"192.168.1.201",DeviceServerManagerDemoPort,9099);
    command.SendCommand(SENDNEWADDRESS_COMMAND,"192.168.1.201",DeviceServerManagerDemoPort,9099);*/
    }
}
