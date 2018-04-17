package controlpanel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class Window extends JFrame {
	private boolean isActive;	//If we're connected to the robot
	private MyButton nothing;
	private MyButton center;
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
	private MyButton[] buttons;
	private NetTableControl ntc;
	private JPanel leftPanel;
	private JPanel middlePanel;
	private JPanel rightPanel;
	private JPanel messagePanel;
	private JPanel topHalf;
	private JLabel message;
	private int currentAuto;
	private int queuedAuto;
	final int connectingDelay;	//Attempt to connect to robot every half second
	final int pingDelay;		//Check if we're still connected every 1 second
	private static Color LIGHT_RED;
	private static Color LIGHT_GREEN;
	
	public Window(String title, NetTableControl ntc) {
		//Initialize all the things
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		this.setSize(400, 661);
		this.ntc = ntc;
		isActive = false;
		currentAuto = -1;
		queuedAuto = -1;
		pingDelay = 1500;	
		connectingDelay = 500;
		LIGHT_RED = Color.getHSBColor(0.0F, 0.7F, 1.0F);
		LIGHT_GREEN = Color.getHSBColor(0.3F, 0.6F, 1.0F);

		/**
		 * Initialize the Buttons and dd all buttons to the button array
		 * THE ORDER MATTERS. It determines the value sent to the table
		 */
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
		};
		
		//When each button is pressed, the corresponding value is sent to the network table
		for(int i = 0; i < buttons.length; i++) {
			buttons[i].addActionListener(sendInput());
			buttons[i].index = i;
			buttons[i].setText("<html><center>" + buttons[i].getText() + "<br>" + "(" + i + ")");
		}
		
		//Initialize Message Label. Font is large and centered
		message = new JLabel();
		message.setFont(new Font("Arial", Font.BOLD, 17));
		message.setHorizontalAlignment(SwingConstants.CENTER);
		
		//Initialize JPanels
		leftPanel = new JPanel();
		middlePanel = new JPanel();
		rightPanel = new JPanel();
		messagePanel = new JPanel(new GridLayout(1, 1));
		topHalf = new JPanel();
		topHalf.setPreferredSize(new Dimension(1000, 1000));
		topHalf.setBackground(Color.BLACK);
		messagePanel.setBackground(Color.WHITE);
		messagePanel.setPreferredSize(new Dimension(800, 120));
				
		//Adding all the components to their respective panels
		leftPanel.add(leftEitherScale);
		leftPanel.add(leftTwoCubeScale);
		leftPanel.add(leftTwoCubeSwitch);
		leftPanel.add(leftScalePriority);
		leftPanel.add(leftSwitchPriority);
		leftPanel.add(leftSwitchOnly);
		middlePanel.add(extra14);
		middlePanel.add(extra11);
		middlePanel.add(nothing);
		middlePanel.add(driveForward);
		middlePanel.add(center);
		messagePanel.add(message);
		rightPanel.add(rightEitherScale);
		rightPanel.add(rightTwoCubeScale);
		rightPanel.add(rightTwoCubeSwitch);
		rightPanel.add(rightScalePriority);
		rightPanel.add(rightSwitchPriority);
		rightPanel.add(rightSwitchOnly);
		
		topHalf.add(leftPanel);
		topHalf.add(middlePanel);
		topHalf.add(rightPanel);
		
		leftPanel.setLayout(new GridLayout(leftPanel.getComponentCount(), 1));
		middlePanel.setLayout(new GridLayout(middlePanel.getComponentCount(), 1));
		rightPanel.setLayout(new GridLayout(rightPanel.getComponentCount(), 1));
		GridLayout topHalfLayout = new GridLayout(1, topHalf.getComponentCount());
		topHalfLayout.setHgap(4);
		topHalf.setLayout(topHalfLayout);
		
		
		//Adding all JPanels to the JFrame
		this.setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		this.add(topHalf);
		this.add(messagePanel);
		this.setVisible(true);
		
		//Start checking for connectivity
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
					//Once connected, ping connectivity at a slower rate to reduce netowrk load
					timer.setDelay(pingDelay);
					//If we have nothing queued
					if (queuedAuto == -1) {
						updateDisplay();
					}
					//Set the thing to the queued thing
					else {
						changeSelectedAuto(queuedAuto);
						queuedAuto = -1;
					}
				}
				//Just Disconnected. It queues your current selection 
				else if (isActive && !ntc.isConnected()) {
					isActive = ntc.isConnected();
					if (currentAuto != -1) {
						queuedAuto = currentAuto;
						currentAuto = -1;
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
					if (tableNum != -1 && (currentAuto == -1 || currentAuto != tableNum)) {
						currentAuto = tableNum;
					}
					updateDisplay();
				}
			}
		});
		timer.start();
	}
	
	//Action listener to send an number to the network table
	private ActionListener sendInput() {
		ActionListener temp = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MyButton temp = (MyButton) e.getSource();
				//If connected, change auto
				if (isActive) {
					//If we have another auto selected and it's not the button we pressed
					if (currentAuto != -1 && currentAuto != temp.index) {
						//Open a dialog to make sure they want to change
						Object[] options = {"Change to new auto", "Keep current auto"};
						int n = JOptionPane.showOptionDialog(null, "You have " + buttons[currentAuto].getText() + " selected\nChange to " + 
								temp.getText() + "?", "Attention!", 
								JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
						
						//If they don't press "Change to new auto", do nothing
						if (n != 0)
							return;
					}
					
					changeSelectedAuto(temp.index);
				}
				//Queue an instruction
				else {
					queuedAuto = temp.index;
					updateDisplay();
				}
			}
		};
		return temp;
	}
	
	//Takes in an auto number and updates the netowrk table and display
	private void changeSelectedAuto(int p) {
		//Update the network table
		ntc.sendAutoChoice(p);
		
		//Update our current pos
		currentAuto = p;
		
		//Set the color of the buttons
		updateDisplay();
	}
	
	//updates the displayed number to match the Network Table
	public void updateDisplay()	{		
		if(ntc.isConnected()) {
			if(currentAuto == -1) {
				setAllButtonColor(Color.YELLOW);
				setMessage("Set an Autonomous");
			}
			else {
				setAllButtonColor(Color.WHITE);
				buttons[currentAuto].setBackground(LIGHT_GREEN);
				setMessage("AUTO: " + buttons[currentAuto].displayName);
			}
		}
		else {
			setAllButtonColor(LIGHT_RED);
			setMessage("Connecting");
			if(queuedAuto != -1) {
				buttons[queuedAuto].setBackground(Color.ORANGE);
			}
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

	//Self explanatory
	private void setAllButtonColor(Color c) {
		for(MyButton mb : buttons) {
			mb.setBackground(c);
		}
	}

	//Sets the label text
	public void setMessage(String msg) {
		message.setText(msg);
		//fileLogger.writeLine(msg);
	}
	
	//Gets the label text
	public String getMessage() {
		return message.getText();
	}
	
	public static void main(String[] args) {
		new Window("Hashboard 2018", new NetTableControl());
	}
}
