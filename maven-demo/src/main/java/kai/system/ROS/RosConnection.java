package kai.system.ROS;

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
	private int port = 9898;
	private Ros ros = new Ros(host, port);	
	private Message lstRecMsg;
	
	public RosConnection(String host, int port) {
		this.host = host;
		this.port = port;
		this.ros = new Ros(host, port);
	}
	
	public boolean connect() {
		return ros.connect();
	}
	
	public boolean disconnect() {
		return ros.disconnect();
	}
	
	public Topic createTopic(String topicName, String msgType) {
		Topic tp = new Topic(ros, topicName, msgType);
		return tp;
	}
	
	public Message createMessage(String msg) {
		return new Message(msg);
	}
	
	public void sendMsg(Topic topic, Message msg) {
		topic.publish(msg);
	}
	
	public Message receiveMsg(Topic topic) {
		topic.subscribe(new TopicCallback() {
			public void handleMessage(Message message) {
				lstRecMsg = message.clone();
	        }
		});
		return lstRecMsg;
	}
	
	public static void main1(String[] args) throws InterruptedException {
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

	    Service addTwoInts = new Service(ros, "/add_two_ints", "rospy_tutorials/AddTwoInts");

	    ServiceRequest request = new ServiceRequest("{\"a\": 10, \"b\": 20}");
	    ServiceResponse response = addTwoInts.callServiceAndWait(request);
	    System.out.println(response.toString());

	    ros.disconnect();
	}
	
	
}
