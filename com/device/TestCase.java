package com.device;

public class TestCase{

        public static void main(String[] args){
                DeviceCommand command = new DeviceCommand();
		command.SendCommand("showdevice namea nameb","127.0.0.1",9092,9093);
                //RecvCommand(9092);
        }
}
