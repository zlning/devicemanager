package com.device;
import java.util.ArrayList;
import java.util.List;
public class TestCase{
    public static void main(String[] args){
	//=========test DeviceCommand=================
        DeviceCommand command = new DeviceCommand();
        //command.SendCommand("showdevice namea nameb","10.180.89.205",9092,9099);
        //command.SendCommand("showdevice namea nameb","10.180.89.205",9092);
        DeviceCommand.CommandParams s = command.RecvCommand(9092);
	//char[] comm=new char[32];
	//List<String> params = new ArrayList<String>();
	//int paramnum = command.AnalysisCommand(s,comm,params);
	//System.out.println(comm);
	System.out.println("command:"+s.command+" paramnum:"+s.paramsnum+" parama1:"+s.params.get(0));
	System.out.println("sourceip:"+s.sourceip+" sourceport:"+s.sourceport);
	System.out.println("destip:"+s.destip+" destport:"+s.destport);
        //System.out.println("paramnum:"+paramnum+" parama1:"+params.get(0));
	//===============================
	
    }
}
