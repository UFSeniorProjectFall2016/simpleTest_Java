package kai.system.starter;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import java.net.URISyntaxException;
import java.util.Scanner;

import org.json.JSONException;
import org.json.JSONObject;

public class Start {
	// Constants
	public final static String DOOR_ID = "#door";
	public final static String LIGHT_ID = "#light";
	public final static String COFFEE_ID = "#coffee";
	public final static String WINDOWS_ID = "#wind";

	// Members class
	private String link = "http://localhost:5000";
	private String link2 = "https://sleepy-inlet-14613.herokuapp.com/";
	private Socket socket;
	private boolean connected = false;

	// DEVICES
	private boolean door = false;
	private boolean coffee = false;
	private boolean windows = false;
	private boolean light = false;

	public static void main1(String[] args) {
		Start start = new Start();
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
				while (!start.isSocketConnected()) {
					System.out.println("Waiting for connection to be established");
				}
				System.out.println("Connection established successfully");
				break;

			case 2:
				if (!start.isSocketConnected()) {
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
				if (!start.isSocketConnected()) {
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
				if (!start.isSocketConnected()) {
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
				if (!start.isSocketConnected()) {
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
				if (!start.isSocketConnected()) {
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

	public boolean isSocketConnected() {
		return connected;
	}

	public void sendMsg(String event, String id, String name, boolean status) {
		JSONObject msg = createMsg(id, name, status);
		socket.emit(event, msg);
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
