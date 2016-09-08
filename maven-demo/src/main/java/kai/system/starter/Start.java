package kai.system.starter;

import kai.system.clients.SocketConnection;
import kai.system.ros.RosConnection;

public class Start {
	
	// Class members definition
	RosConnection rosConn;
	SocketConnection webConn;
	boolean bridge;
	int[] devStatus = new int[4];
	
	// Default Constructor
	public Start() {
		rosConn = new RosConnection("localhost", 9090);
		webConn = new SocketConnection("https://sleepy-inlet-14613.herokuapp.com/");
		this.setBridge();
	}
	
	public RosConnection getROSConn() {
		return rosConn;
	}
	
	public SocketConnection getWebConn() {
		return webConn;
	}
	
	public boolean isBridgeSet() {
		return bridge;
	}
	
	public void setBridge() {
		bridge = rosConn.isConnected() && webConn.isConnected();
	}
	
	public void setDeviceStatus(int id) {
		if(devStatus[id] == 1) {
			devStatus[id] = 0;
		} else {
			devStatus[id] = 1;
		}
	}
	
	public int getDeviceLastStatus(int id) {
		return devStatus[id];
	}
	
	public void closingDevice(int id) {
		
		String cmdMsg1 = "";
		switch(id) {
		case 0:
			System.out.println("CLOSING DOOR");
			cmdMsg1 = "{\"DevId\":1,\"S\":\"OF\"}";
			rosConn.sendMsg(cmdMsg1);
			break;
		case 1:
			System.out.println("CLOSING LIGHT");
			break;
		case 2:
			System.out.println("CLOSING COFFEE");
			break;
		case 3:
			System.out.println("CLOSING WINDOW");
			break;
		}
		
		this.setDeviceStatus(id);
	}
	
	public void openingDevice(int id) {
		String cmdMsg1 = "";
		
		switch(id) {
		case 0:
			System.out.println("OPEN DOOR");
			cmdMsg1 = "{\"DevId\":1,\"S\":\"ON\"}";
			rosConn.sendMsg(cmdMsg1);
			break;
		case 1:
			System.out.println("OPEN LIGHT");
			break;
		case 2:
			System.out.println("OPEN COFFEE");
			break;
		case 3:
			System.out.println("OPEN WINDOW");
			break;
		}
		
		this.setDeviceStatus(id);
	}
	
	public void operateDevice(int id) {
		if(devStatus[id] == 1) {
			closingDevice(id);
		} else {
			openingDevice(id);
		}
	}
	
	public static void main(String args[]) {
		Start start = new Start();
		
		// Wait for bridge to be set
		while(!start.isBridgeSet()) {
			start.setBridge();
			System.out.println("Setting bridge between Web App and ROS");
		}
		
		// Start the program
		System.out.println("Bridge set successfully");
		
		// Create other ROS variables
		start.getROSConn().setCurrTopic("/Config_vals", "std_msgs/String");
		
		while(true) {
			int arr[] = start.getWebConn().getDeviceStatus();
			for(int i=0; i!=arr.length; ++i) {
				if(start.getDeviceLastStatus(i) != arr[i]) {
					start.operateDevice(i);
				} 
			}
		}
		
	}
	
}
