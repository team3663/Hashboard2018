package controlpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

/**
 * README ************************************************
 * *******************************************************
 * There will be 3 rows: position (0), primary (1), and secondary (2)
 * Each row will have a set of MyButtons
 * Each MyButton in each row will be numbered 0 to n
 * There can only be one button selected in each row
 * If a button is pushed, and there is a button selected for every row, send the selection to the networkTable
 * The number that will be sent will be (position * 100 + primary * 10 + secondary)
 * For instance: If you have position[1], primary[2], and secondary[5] selected, it will send the number 125 to the networkTable
 * I realize that this will require changes in the robot code. I couldn't think of another way to do it.
 */
@SuppressWarnings({ "serial", "unused" })
public class Window extends JFrame {
	private boolean isActive;	//If we're connected to the robot
	
	private MyButton center;
	private MyButton right;
	private MyButton left;
	private MyButton[] robotPos;
	
	private MyButton sc;
	private MyButton sw;
	private MyButton nothing;
	private MyButton driveForward;
	private MyButton twoCubeSc;
	private MyButton[] primaryTarget;
	
	private MyButton secondarySc;
	private MyButton secondarySw;
	private MyButton secondaryNothing;
	private MyButton secondaryDriveForward;
	private MyButton[] secondaryTarget;
	
	private Color[] colors;
	
	/*TODO: change the buttons
	private MyButton nothing;
	//private MyButton center;
	private MyButton leftSwitchOnly;
	private MyButton rightSwitchOnly;
	private MyButton leftScalePriority;
	private MyButton rightScalePriority;
	private MyButton leftSwitchPriority;
	private MyButton rightSwitchPriority;
	private MyButton driveForward;
	private MyButton leftTwoCubeScale;
	private MyButton leftTwoCubeSwitch;
	private MyButton extra11;
	private MyButton rightTwoCubeSwitch;
	private MyButton rightTwoCubeScale;
	private MyButton extra14;
	private MyButton leftEitherScale;
	private MyButton rightEitherScale;
	
	//TODO: Make this 3 button arrays
	// position[], primary[], secondary[]
	private MyButton[] buttons;*/

	private NetTableControl ntc;
	private JPanel positionPanel;
	private JPanel position;
	private JPanel primaryPanel;
	private JPanel primary;
	private JPanel secondaryPanel;
	private JPanel secondary;
	private JPanel messagePanel;
	private JLabel message;
	private int currentPosition;
	private int currentPrimary;
	private int currentSecondary;
	private int queuedPosition;
	private int queuedPrimary;
	private int queuedSecondary;
	final int connectingDelay;	//Attempt to connect to robot every half second
	final int pingDelay;		//Check if we're still connected every 1 second
	private static Color LIGHT_RED;
	private static Color LIGHT_GREEN;
	
	private HashMap<Integer, Integer> outputs = new HashMap<>();
	private HashMap<Integer, Integer> reverse = new HashMap<>();

	
	public Window(String title, NetTableControl ntc) {
		//Initialize all the things
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		this.setSize(400, 661);
		this.ntc = ntc;
		isActive = false;
		currentPosition = -1;
		currentPrimary = -1;
		currentSecondary = -1;
		queuedPosition = -1;
		queuedPrimary = -1;
		queuedSecondary = -1;
		pingDelay = 1500;	
		connectingDelay = 500;
		LIGHT_RED = Color.getHSBColor(0.0F, 0.7F, 1.0F);
		LIGHT_GREEN = Color.getHSBColor(0.3F, 0.6F, 1.0F);
		
		outputs.put(000, 15);
		outputs.put(001, 4);
		outputs.put(002, 4);
		outputs.put(003, 4);
		outputs.put(010, 6);
		outputs.put(011, 2);
		outputs.put(012, 2);
		outputs.put(013, 2);
		outputs.put(022, 0);
		outputs.put(033, 8);
		outputs.put(111, 1);
		outputs.put(122, 0);
		outputs.put(200, 16);
		outputs.put(201, 5);
		outputs.put(202, 5);
		outputs.put(203, 5);
		outputs.put(210, 7);
		outputs.put(211, 3);
		outputs.put(212, 3);
		outputs.put(213, 3);
		outputs.put(222, 0);
		outputs.put(233, 8);

		/**
		 * Initialize the Buttons and dd all buttons to the button array
		 * THE ORDER MATTERS. It determines the value sent to the table
		 */
		/*TODO: Add the buttons to their appropriate button array (delete 82 to 100)
		buttons = new MyButton[] {
			nothing = createSimpleButton("Nothing"), 								//0
			center = createSimpleButton("Center"), 									//1
			leftSwitchOnly = createSimpleButton("L Switch Only"),					//2
			rightSwitchOnly = createSimpleButton("R Switch Only"), 					//3
			leftScalePriority = createSimpleButton("L Scale Priority"), 			//4
			rightScalePriority = createSimpleButton("R Scale Priority"),		 	//5
			leftSwitchPriority = createSimpleButton("L Switch Priority"), 			//6
			rightSwitchPriority = createSimpleButton("R Switch Priority"), 			//7
			driveForward = createSimpleButton("Drive Forward"), 					//8
			leftTwoCubeScale = createSimpleButton("L Two Cube Scale Priority"),		//9
			leftTwoCubeSwitch = createSimpleButton("L Two Cube Switch Priority"),	//10
			extra11 = createSimpleButton("Test"),									//11
			rightTwoCubeSwitch = createSimpleButton("R Two Cube Switch Priority"),	//12
			rightTwoCubeScale = createSimpleButton("R Two Cube Scale Priority"),	//13
			extra14 = createSimpleButton("Test"),									//14
			leftEitherScale = createSimpleButton("L Choose Scale"),					//15
			rightEitherScale = createSimpleButton("R Choose Scale")					//16
		};*/
		
		robotPos = new MyButton[] {
				left = createSimpleButton("Left"),                         //0
				center = createSimpleButton("Center"),                     //1
				right = createSimpleButton("Right")                        //2
		};
		
		primaryTarget = new MyButton[] {
				sc = createSimpleButton("Near Scale"),                     //0
				sw = createSimpleButton("Switch"),                         //1
				nothing = createSimpleButton("Nothing"),                   //2
				driveForward = createSimpleButton("Drive Forward"),        //3
				twoCubeSc = createSimpleButton("Two Cube Scale")           //4
		};
		
		secondaryTarget = new MyButton[] {
				secondarySc = createSimpleButton("Far Scale"),             //0
				secondarySw = createSimpleButton("Switch"),                //1
				secondaryNothing = createSimpleButton("Nothing"),          //2
				secondaryDriveForward = createSimpleButton("Drive Forward")//3
		};
		
		@SuppressWarnings("rawtypes")
		Set set = outputs.entrySet();
		@SuppressWarnings("rawtypes")
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			@SuppressWarnings("rawtypes")
			Map.Entry mentry = (Map.Entry)iterator.next();
			reverse.put((int) mentry.getValue(), (int) mentry.getKey());
		}
		
		/*0 Ready to be pressed	Color.WHITE
		 * 1 Pressed				Color.LIGHT_GREEN
		 * 2 Cannot be pressed		Color.GRAY
		 * 3 Disconnected			Color.LIGHT_RED
		 * 4 Queued					Color.ORANGE*/
		colors = new Color[] {
				Color.WHITE,
				LIGHT_GREEN,
				Color.gray,
				LIGHT_RED,
				Color.ORANGE
		};
		
		//When each button is pressed, the corresponding value is sent to the network table
		//TODO: Do this 3 times, once for each button array.
		//The parameter for sendInput() will be 0 for position, 1 for primary, and 2 for secondary
		for(int i = 0; i < robotPos.length; i++) {
			robotPos[i].addActionListener(sendInput(0));
			robotPos[i].index = i;
			robotPos[i].setText("<html><center>" + robotPos[i].getText() + "<br>" + "(" + i + ")");
		}
		
		
		for(int i = 0; i < primaryTarget.length; i++) {
			primaryTarget[i].addActionListener(sendInput(1));
			primaryTarget[i].index = i;
			primaryTarget[i].setText("<html><center>" + primaryTarget[i].getText() + "<br>" + "(" + i + ")");
		}
		
		for(int i = 0; i < secondaryTarget.length; i++) {
			secondaryTarget[i].addActionListener(sendInput(2));
			secondaryTarget[i].index = i;
			secondaryTarget[i].setText("<html><center>" + secondaryTarget[i].getText() + "<br>" + "(" + i + ")");
		}
		
		//Initialize Message Label. Font is large and centered
		message = new JLabel();
		message.setFont(new Font("Arial", Font.BOLD, 17));
		message.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Initialize JPanels
		position = new JPanel();
		position.setPreferredSize(new Dimension(80, 90));
		primary = new JPanel();
		primary.setPreferredSize(new Dimension(80, 90));
		secondary = new JPanel();
		secondary.setPreferredSize(new Dimension(80, 90));
		
		positionPanel = new JPanel();
		positionPanel.add(new JLabel("Robot Position"));
		primaryPanel = new JPanel();
		primaryPanel.add(new JLabel("Primary Action"));
		secondaryPanel = new JPanel();
		secondaryPanel.add(new JLabel("Secondary Action"));
		messagePanel = new JPanel(new GridLayout(1, 1));
		messagePanel.setBackground(Color.WHITE);
		messagePanel.setPreferredSize(new Dimension(800, 120));
				
		//Adding all the components to their respective panels
		//TODO: Make these for each loops that add the buttons from their respective button array
		for(MyButton mb : robotPos) {
			position.add(mb);
		}
		for(MyButton mb : primaryTarget) {
			primary.add(mb);
		}
		for(MyButton mb : secondaryTarget) {
			secondary.add(mb);
		}
		messagePanel.add(message);
				
		position.setLayout(new GridLayout(1, position.getComponentCount()));
		primary.setLayout(new GridLayout(1, primary.getComponentCount()));
		secondary.setLayout(new GridLayout(1, secondary.getComponentCount()));		
		
		//Adding all JPanels to the JFrame
		this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.add(positionPanel);
		this.add(position);
		this.add(primaryPanel);
		this.add(primary);
		this.add(secondaryPanel);
		this.add(secondary);
		this.add(messagePanel);
		this.setVisible(true);
		
		//Start checking for connectivity
		setMessage("Connecting");
		updateDisplay();
		checkConnection(connectingDelay);
	}
	
	//Checks if ntc has connected to the networkTable at rate "delay".
	//The buttons don't do anything until it has connected
	//After it is connected, it will check less frequently to ensure it's still connected
	public void checkConnection(int delay) {
		Timer timer = new Timer(delay, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				//Just Connected
				if (!isActive && ntc.isConnected()) {
					isActive = ntc.isConnected();
					//Once connected, ping connectivity at a slower rate to reduce network load
					timer.setDelay(pingDelay);
					//If we have nothing queued
					if (queuedPosition == -1 && queuedPrimary == -1 && queuedSecondary == -1) {
						setAllButtonState(0);
					}
					//Set the thing to the queued thing
					else {
						currentPosition = queuedPosition;
						currentPrimary = queuedPrimary;
						currentSecondary = queuedSecondary;
						changeSelectedAuto();
						queuedPosition = -1;
						queuedPrimary = -1;
						queuedSecondary = -1;
						setAllButtonState(0);
						robotPos[currentPosition].state = 1;
						primaryTarget[currentPrimary].state = 1;
						secondaryTarget[currentSecondary].state = 1;
					}
					updateDisplay();
				}
				//Just Disconnected. It queues your current selection 
				else if (isActive && !ntc.isConnected()) {
					isActive = ntc.isConnected();
					if (currentPosition != -1 || currentPrimary != -1 || currentSecondary != -1) {
						queuedPosition = currentPosition;
						queuedPrimary = currentPrimary;
						queuedSecondary = currentSecondary;
						currentPosition = -1;
						currentPrimary = -1;
						currentSecondary = -1;
					}
					updateDisplay();
				}
				//While Disconnected (the ntc does all the work automatically)
				else if (!isActive) {
					setMessage(getMessage() + ".");
					if (getMessage().length() > 13)
						setMessage("Connecting");
				}
				//While active. It detects changes (via another Window maybe) and updates
				//the selected item accordingly
				
				else {
					int tableNum = ntc.getAutoChoice();
					if (tableNum != -1 && encode() != tableNum) {
						int compare = decode(tableNum);
						currentSecondary = compare % 10;
						compare /= 10;
						currentPrimary = compare % 10;
						compare /= 10;
						currentPosition = compare % 10; 
						updateDisplay();
					}
				}
			}
		});
		timer.start();
	}
	
	//Action listener to send an number to the network table
	//row is: 0 for position, 1 for primary, 2 for secondary
	//the row parameter is to differentiate between position, primary, and secondary
	private ActionListener sendInput(int row) {
		ActionListener temp = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyButton temp = (MyButton) e.getSource();
				//If connected, change auto
				if (isActive) {
					//TODO: Make it so it deselects the other buttons in its row/button array, then selects itself
					//Change all the states to match. This one is 1, pressed. All others are 0, ready to be pressed
					//TODO: Check if(something is selected in all 3 rows) then changeSelectedAuto(num);
					switch (row) {
						case 0:
							for(MyButton mb : robotPos) {
								mb.state = 0;
							}
							break;
						case 1:
							for(MyButton mb : primaryTarget) {
								mb.state = 0;
							}
							break;
						case 2:
							for(MyButton mb : secondaryTarget) {
								mb.state = 0;
							}
							break;
					}
					temp.state = 1;
					if (currentPosition != -1 && currentPrimary != -1 && currentSecondary != -1) {
						changeSelectedAuto();
					}
					updateDisplay();
				}
				//Queue an instruction
				else {
					//TODO: deselect all the other buttons, and queue this one
					//Change all states to match. This one is 4, queued. All others are 3, disconnected\
					switch (row) {
						case 0:
							for(MyButton mb : robotPos) {
								mb.state = 3;
							}
							break;
						case 1:
							for(MyButton mb : primaryTarget) {
								mb.state = 3;
							}
							break;
						case 2:
							for(MyButton mb : secondaryTarget) {
								mb.state = 3;
							}
							break;
					}
					temp.state = 4;
					updateDisplay();
				}
				updateDisplay();
			}
		};
		return temp;
	}
	
	private int encode() {
		return outputs.get(currentPosition * 100 + currentPrimary * 10 + currentSecondary);
	}
	
	private int decode(int num) {
		return reverse.get(num);
	}
	
	//Takes in an auto number and updates the netowrk table and display
	private void changeSelectedAuto() {
		//TODO: 
		//Update the network table
		
		/*ntc.sendAutoChoice(p);
		
		currentAuto = p;*/
		
		int p = encode();
		ntc.sendAutoChoice(p);
		setMessage(p + " - " + primaryTarget[currentPrimary].displayName + "then" + secondaryTarget[currentSecondary].displayName);
		
		//Set the color of the buttons
		updateDisplay();
	}
	
	//updates the displayed number to match the Network Table

	public void updateDisplay()	{		
		//TODO: Change this so that it iterates through every button and changes each
		//one's color to match its state
		/*
		 * 0 Ready to be pressed	Color.WHITE
		 * 1 Pressed				Color.LIGHT_GREEN
		 * 2 Cannot be pressed		Color.GRAY
		 * 3 Disconnected			Color.LIGHT_RED
		 * 4 Queued					Color.ORANGE
		 */
		
		for(MyButton mb : robotPos) {
			mb.setBackground(colors[mb.state]);
		}
		for(MyButton mb : primaryTarget) {
			mb.setBackground(colors[mb.state]);
		}
		for(MyButton mb : secondaryTarget) {
			mb.setBackground(colors[mb.state]);
		}
	}
	
	//An easy way to create a simple-looking button
	private MyButton createSimpleButton(String text) {
		MyButton button = new MyButton(text);
		button.setForeground(Color.BLACK);
		button.setBackground(Color.WHITE);
		button.setFont(new Font("Arial", Font.BOLD, 14));
		LineBorder line = new LineBorder(Color.BLACK);
		button.setBorder(line);
		return button;
	}

	//TODO: delete this. updateDisplay() should do all the work for us
	//Self explanatory
	private void setAllButtonState(int c) {
		for(MyButton mb : robotPos) {
			mb.state = c;
		}
		for(MyButton MB : primaryTarget) {
			MB.state = c;
		}
		for(MyButton mB : secondaryTarget) {
			mB.state = c;
		}
	}

	//Sets the label text
	public void setMessage(String msg) {
		message.setText(msg);
	}
	
	//Gets the label text
	public String getMessage() {
		return message.getText();
	}
	
	public static void main(String[] args) {
		new Window("Hashboard 2018", new NetTableControl());
	}
}
