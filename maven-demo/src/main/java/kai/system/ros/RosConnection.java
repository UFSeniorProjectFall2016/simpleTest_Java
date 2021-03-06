package kai.system.ros;

import java.util.Timer;
import java.util.TimerTask;

import javax.json.Json;
import javax.json.JsonObject;

import edu.wpi.rail.jrosbridge.JRosbridge;
import edu.wpi.rail.jrosbridge.Ros;
import edu.wpi.rail.jrosbridge.Service;
import edu.wpi.rail.jrosbridge.Topic;
import edu.wpi.rail.jrosbridge.callback.TopicCallback;
import edu.wpi.rail.jrosbridge.messages.Message;
import edu.wpi.rail.jrosbridge.services.ServiceRequest;
import edu.wpi.rail.jrosbridge.services.ServiceResponse;

public class RosConnection {
	
	// Member class variables
	private String host = "localhost";
	private int port = 9090;
	private Ros ros;
	private String msgString;
	private Message lstRecMsg;
	private Message currMsg;
	private Topic currTopic;
	private boolean connected = false;
	
	public static Ros con = new Ros("localhost", 9090);
	public static Topic echo = new Topic(con, "/Config_vals", "std_msgs/String");
	public static Topic echoBack = new Topic(con, "/test", "std_msgs/String");
	
	public static String cmdMsg1 = "{\"DevId\":1, \"S\":\"ON\"}";
//	public static String cmdMsg = "{\"S\": 123}";
	public static String cmdMsg = Json.createObjectBuilder().add("data", cmdMsg1).build().toString();
	public static Message msg = new Message(cmdMsg);
	public static String publishId = "publish:" + echo.getName() + ":" + con.nextId();
	public static JsonObject call = Json.createObjectBuilder().add(JRosbridge.FIELD_OP, JRosbridge.OP_CODE_PUBLISH).add(JRosbridge.FIELD_ID, publishId).add(JRosbridge.FIELD_TOPIC, echo.getName()).add("data", msg.toJsonObject()).build();
	
	public static void main3(String args[]) {
		Timer timer = new Timer();
		boolean connected = false;
		connected = con.connect();	
		System.out.println("Connected: " + connected);
		
		RosConnection rosConn = new RosConnection("localhost", 9090);
		if(rosConn.isConnected()) {
			timer.scheduleAtFixedRate(new TimerTask() {
				public void run() {
					echo.publish(msg);
					
					// Listen for changes
					echoBack.subscribe(new TopicCallback() {
						public void handleMessage(Message message) {
							// TODO Auto-generated method stub
							System.out.println("Message from ORS: " + message.toString());
						}
					});
				}
			}, 100, 100);
		}
		
//		
//		if(connected) {
//			// Sending message to ROS
//			timer.scheduleAtFixedRate(new TimerTask() {
//				public void run() {
//					echo.publish(msg);
//					
//					// Listen for changes
//					echoBack.subscribe(new TopicCallback() {
//						public void handleMessage(Message message) {
//							// TODO Auto-generated method stub
//							System.out.println("Message from ORS: " + message.toString());
//						}
//					});
//				}
//			}, 100, 100);
//		}
	}
	
	public RosConnection() {
		this.ros = new Ros(host, port);
		this.connect();
	}
	
	public RosConnection(Ros ros) {
		this.host = ros.getHostname();
		this.port = ros.getPort();
		this.ros = ros;
		this.connect();
	}
	
	public RosConnection(String host, int port) {
		this.host = host;
		this.port = port;
		this.ros = new Ros(host, port);
		this.connect();
	}
	
	public void connect() {
		connected = ros.connect();
	}
	
	public boolean isConnected() {
		return connected;
	}
	
	public boolean disconnect() {
		return ros.disconnect();
	}
	
	// CREATE, SET, & GET TOPIC
	public Topic createTopic(String topicName, String msgType) {
		return new Topic(this.ros, topicName, msgType); 
	}
	
	public void setCurrTopic(String topicName, String msgType) {
		currTopic = this.createTopic(topicName, msgType);
	}
	
	public void setCurrTopic(Topic topic) {
		currTopic = new Topic(this.ros, topic.getName(), topic.getType());
	}
	
	public Topic getCurrTopic() {
		return this.currTopic;
	}
	
	// CREATE, SET, & GET MESSAGE
	public Message createMessage(String msg) {
		JsonObject msgJSON = Json.createObjectBuilder().add("data", msg).build();
		return new Message(msgJSON);
	}
	
	public boolean sendMsg(Message msg) {
		// Message cannot be sent if topic fails
		if(currTopic == null) { return false; }
		
//		System.out.println("Publishing to ROS");
		currTopic.publish(msg);
		return true;
	}
	
	public boolean sendMsg(String msg) {
		// Message cannot be sent if topic fails
		if(msg.isEmpty()) { return false; }
		return this.sendMsg(this.createMessage(msg));
	}
	
	public boolean receiveMsg(Topic topic) {
		lstRecMsg = null;
		topic.subscribe(new TopicCallback() {
			public void handleMessage(Message message) {
				lstRecMsg = message.clone();
	        }
		});
		
		if(lstRecMsg == null) { 
//			System.out.println("null");
			return false; 
		}
		System.out.println("Message Received: " + lstRecMsg.toString());
//		String[] tmp = lstRecMsg.toString().split("+[\",]");
//		System.out.println("Size: " + tmp.length);
		return true;
	}
	
	public boolean getMsg() {
		lstRecMsg = null;
		this.currTopic.subscribe(new TopicCallback() {
			public void handleMessage(Message message) {
				lstRecMsg = message.clone();
	        }
		});
		
		if(lstRecMsg == null) { 
			return false; 
		}
		System.out.println("Message Received: " + lstRecMsg.toString());
		return true;
	}
	
	public void printRecMsg() {
		if(lstRecMsg != null) {
			System.out.println("Message Received: " + msgString);
		}
	}
	
	public static void main4(String[] args) throws InterruptedException {
	    Ros ros = new Ros("localhost");
	    ros.connect();

	    Topic echo = new Topic(ros, "/echo", "std_msgs/String");
	    Message toSend = new Message("{\"data\": \"hello, world!\"}");
	    echo.publish(toSend);

	    Topic echoBack = new Topic(ros, "/echo_back", "std_msgs/String");
	    echoBack.subscribe(new TopicCallback() {
	        public void handleMessage(Message message) {
	            System.out.println("From ROS: " + message.toString());
	        }
	    });

//	    Service addTwoInts = new Service(ros, "/add_two_ints", "rospy_tutorials/AddTwoInts");
//
//	    ServiceRequest request = new ServiceRequest("{\"a\": 10, \"b\": 20}");
//	    ServiceResponse response = addTwoInts.callServiceAndWait(request);
//	    System.out.println(response.toString());

	    ros.disconnect();
	}
	
	
}
