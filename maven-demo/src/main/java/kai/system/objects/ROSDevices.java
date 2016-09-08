package kai.system.objects;

public class ROSDevices {
	// Harccoded devices IDs
	public static int DOOR_ID = 1;
	public static int WINDOWS_ID = 1;
	public static int COFEE_ID = 1;
	public static int LIGHT_ID = 1;
	
	public static boolean ON = true;
	public static boolean OFF = false;
	
	private int numDevices = 4;
	private int[] devices = new int[numDevices];
	
	public ROSDevices() {}
	
	public void turnOnDevice(int devId) {
		if(this.isDeviceValid(devId)) {
			devices[devId] = 1;
		}
	}
	
	public void turnOFFDevice(int devId) {
		if(this.isDeviceValid(devId)) {
			devices[devId] = 0;
		}
	}
	
	public void toggleDevice(int devId) {
		if(this.isDeviceValid(devId)) {
			if(devices[devId] == 0) {
				devices[devId] = 1;
			} else {
				devices[devId] = 0;
			}
		}
	}
	
	private boolean isDeviceValid(int id) {
		if(id < 0 || id >= numDevices) {
			return false;
		}
		return true;
	}
}
