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
		autoControlTable.getEntry("autoChoice").setDouble(-1);
	}
	
	//Takes an int in and sends it to the autoChoice tag
	//The robot will read that value in at autoInit() and perform the appropriate CommandGroup
	public void sendAutoChoice(int choice) {
		autoControlTable.getEntry("autoChoice").setDouble(choice);
	}
	
	public boolean isConnected() {
		return nti.isConnected();
	}
}
