package kai.system.clients;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class SocketConnection {
	// Members
	JSONObject rcvMsg;
	
	// Constants
	public final static String DOOR_ID = "#door";
	public final static String LIGHT_ID = "#light";
	public final static String COFFEE_ID = "#coffee";
	public final static String WINDOWS_ID = "#wind";

	// Members class
	private String host = "http://localhost:5000";
	private String link2 = "https://sleepy-inlet-14613.herokuapp.com/";
	private Socket socket;
	private boolean connected = false;

	// DEVICES
	private boolean door = false;
	private boolean coffee = false;
	private boolean windows = false;
	private boolean light = false;

	public SocketConnection(String host) {
		if(host == null) {
			// Throw error
			connected = false;
		} else {
			this.connect();
		}
	}
	
	public void connect() {
		try {
			socket = IO.socket(link2);
			this.sendMsg("connected_user", "Java Application");
			
			// Retrieving data from socket
			socket.on("connection_confirmation", new Emitter.Listener() {
				public void call(Object... args) {
					connected = (Boolean) args[0];
				}
			}).on("device status", new Emitter.Listener() {

				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					String id = "";
					boolean status = false;
					try {
						id = obj.getString("id");
						status = obj.getBoolean("status");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (id.equalsIgnoreCase(DOOR_ID)) {
						door = status;
					} else if (id.equalsIgnoreCase(COFFEE_ID)) {
						coffee = status;
					} else if (id.equalsIgnoreCase(WINDOWS_ID)) {
						windows = status;
					} else if (id.equalsIgnoreCase(LIGHT_ID)) {
						light = status;
					}
				}
			});
			socket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.out.println("SOCKET DID NOT GET CREATED");
		}
	}
	
	// CREATE, SEND, & RECEIVE MESSAGE
	public JSONObject createMsg(String id, String name, boolean status) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("id", id);
			msg.put("name", name);
			msg.put("status", status);
		} catch (JSONException e) {
			System.out.println("Some error happened while creating the message");
			e.printStackTrace();
		}
		return msg;
	}
	
	public boolean sendMsg(String msgType, String msgData) {
		if(socket == null) {
			return false;
		}
		socket.emit(msgType, msgData);
		return true;
	}
	
	public void sendMsg(String event, String id, String name, boolean status) {
		JSONObject msg = createMsg(id, name, status);
		socket.emit(event, msg);
	}
	
	public boolean receiveMessage() {
		return true;
	}

	// DEVICES STATUS
	public int[] getDeviceStatus() {
		int[] res = new int[4];
		if(door) {
			res[0] = 1;
		}
		
		if(light) {
			res[1] = 1;
		}
		
		if(coffee) {
			res[2] = 1;
		}
		
		if(windows) {
			res[3] = 1;
		}
		return res;
	}
	
	public static void main1(String[] args) {
		SocketConnection start = new SocketConnection("");
		Scanner in = new Scanner(System.in);
		int menuChoice;
		JSONObject msg;

		// Loop until user select to exit application
//		BorderFactory.createBevelBorder(3);
		do {
			start.printDevicesStatus();
			mainMenu();
			menuChoice = in.nextInt();
			in.nextLine();

			switch (menuChoice) {
			case 0:
				System.out.println("Exiting application");
				break;

			case 1:
				start.initConnection();
				while (!start.isConnected()) {
					System.out.println("Waiting for connection to be established");
				}
				System.out.println("Connection established successfully");
				break;

			case 2:
				if (!start.isConnected()) {
					System.out.println("Error! No connection established yet. Connect first");
					break;
				}
				if(start.isDoor()) {
					System.out.println("Closing Door");
				} else {
					System.out.println("Opening Door");
				}
				start.sendMsg("device status", DOOR_ID, "Door locked from Java", !start.isDoor());
				break;

			case 3:
				if (!start.isConnected()) {
					System.out.println("Error! No connection established yet. Connect first");
					break;
				}
				if(start.isLight()) {
					System.out.println("Switching Lights OFF");
				} else {
					System.out.println("Switching Lights ON");
				}
				start.sendMsg("device status", LIGHT_ID, "Light ON from Java", !start.isLight());
				break;

			case 4:
				if (!start.isConnected()) {
					System.out.println("Error! No connection established yet. Connect first");
					break;
				}
				if(start.isCoffee()) {
					System.out.println("Turning OFF Coffee Machine");
				} else {
					System.out.println("Turning ON Coffee Machine");
				}
				start.sendMsg("device status", COFFEE_ID, "Coffee Machine ON from Java", !start.isCoffee());
				break;

			case 5:
				if (!start.isConnected()) {
					System.out.println("Error! No connection established yet. Connect first");
					break;
				}
				if(start.isWindows()) {
					System.out.println("Closing Windows");
				} else {
					System.out.println("Opening Windows");
				}
				start.sendMsg("device status", WINDOWS_ID, "Windows closed from Java", !start.isWindows());
				break;

			case 6:
				if (!start.isConnected()) {
					System.out.println("Error! No connection established yet. Connect first");
					break;
				}
				start.printDevicesStatus();
				break;
				
			default:
				System.out.println("Wrong Menu choice");
			}
		} while (menuChoice != 0);
		in.close();
	}

	

	public void printDevicesStatus() {

		// Retrieve status from socket ONLY if the socket is connected
		// successfully
		System.out.println("--------------------------------------------------");
		System.out.println("Door Locked\t\t" + door);
		System.out.println("Lights ON\t\t" + light);
		System.out.println("Coffee Machine\t\t" + coffee);
		System.out.println("Windows\t\t\t" + windows);
		System.out.println("--------------------------------------------------\n");
	}

	public boolean isConnected() {
		return connected;
	}

	
	
	public void initConnection() {
		try {
			socket = IO.socket(link2);
			socket.emit("connected_user", "Java Application");
			// Retrieving data from socket
			socket.on("connection_confirmation", new Emitter.Listener() {
				public void call(Object... args) {
					connected = (Boolean) args[0];
				}
			}).on("device status", new Emitter.Listener() {

				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					String id = "";
					boolean status = false;
					try {
						id = obj.getString("id");
						status = obj.getBoolean("status");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (id.equalsIgnoreCase(DOOR_ID)) {
						door = status;
					} else if (id.equalsIgnoreCase(COFFEE_ID)) {
						coffee = status;
					} else if (id.equalsIgnoreCase(WINDOWS_ID)) {
						windows = status;
					} else if (id.equalsIgnoreCase(LIGHT_ID)) {
						light = status;
					}
				}
			});
			socket.connect();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.out.println("SOCKET DID NOT GET CREATED");
		}
	}

	public static void mainMenu() {

		System.out.println("**************************************************");
		System.out.println("*                  MAIN MENU                     *");
		System.out.println("**************************************************");
		System.out.println("1. Connect to web app");
		System.out.println("2. Close Door");
		System.out.println("3. Turn light OFF");
		System.out.println("4. Turn coffee machine OFF");
		System.out.println("5. Close front window");
		System.out.println("6. Print devices' status");
		System.out.println("0. Exit\n");

		System.out.println("Type and input choice");
	}

	
	public boolean isDoor() {
		return door;
	}

	public boolean isCoffee() {
		return coffee;
	}

	public boolean isWindows() {
		return windows;
	}

	public boolean isLight() {
		return light;
	}

}
