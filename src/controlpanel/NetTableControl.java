package controlpanel;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class NetTableControl {
	private NetworkTableInstance nti;
	private NetworkTable autoControlTable;
	
	public NetTableControl() {
		// Instantiate Network Tables
		nti = NetworkTableInstance.getDefault();
		
		// Create/find the table "hashboard"
		autoControlTable = nti.getTable("hashboard");
		
		// From documentation: recommended if running on DS computer; this gets the robot IP from the DS
	    nti.startDSClient();  
		nti.startClientTeam(3663);
		
		// Gets/creates the tag "autoChoice" and puts the default value -1 in
		autoControlTable.getEntry("autoChoice").getDouble(-1);
	}
	
	//Takes an int in and sends it to the autoChoice tag
	//The robot will read that value in at autoInit() and perform the appropriate CommandGroup
	public void sendAutoChoice(int choice) {
		autoControlTable.getEntry("autoChoice").setDouble(choice);
	}
	
	//Returns true if the DS is connected to the Field? Robot? Actually not sure
	public boolean isConnected() {
		return nti.isConnected();
	}
	
	//Returns the value from the Network Table. Returns -1 if it's not found
	public int getAutoChoice() {
		return (int) autoControlTable.getEntry("autoChoice").getDouble(-1);
	}
}
