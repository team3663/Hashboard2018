package controlpanel;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class Window extends JFrame {
	private boolean isActive = false;
	
	private JButton nothing;
	private JButton center;
	private JButton leftSwitch;
	private JButton rightSwitch;
	private JButton leftScale;
	private JButton rightScale;
	private NetTableControl ntc;
	private JPanel top;
	private JPanel mid;
	private JPanel bot;
	private JLabel message;
	
	final int n = 0;	// nothing
	final int c = 1;	// center
	final int lw = 2;	// left switch
	final int rw = 3;	// right switch
	final int lc = 4;	// left scale
	final int rc = 5;	// right scale
	
	final int connectingDelay = 500;	//Attempt to connect to robot every half second
	final int pingDelay = 1000;			//Check if we're still connected every 1 second
	
	Color myRed = Color.getHSBColor(0.0F, 0.7F, 0.8F);
	Color myGreen = Color.getHSBColor(0.3F, 0.6F, 0.9F);
	
	//JFrame with 6 buttons to send the autoChoice to the NetworkTable
	public Window(String title, NetTableControl ntc) {
		//Initialize frame
		super(title);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLayout(new FlowLayout());
		this.setSize(370, 250);
		
		//Set the NetTableControl
		this.ntc = ntc;

		//Initialize the Buttons
		nothing = createSimpleButton("Nothing");
		center = createSimpleButton("Center");
		leftSwitch = createSimpleButton("Left: SWITCH");
		rightSwitch = createSimpleButton("Right: SWITCH");
		leftScale = createSimpleButton("Left: SCALE");
		rightScale = createSimpleButton("Right: SCALE");

		//Generic Action listener that changes the colors of a button when pressed and sets the
		//message text to the button's name
		ActionListener changeColorPress = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive) {
					setAllButtonColor(Color.WHITE);
					((JButton) e.getSource()).setBackground(myGreen);
					setMessage("AUTO is " + ((JButton) e.getSource()).getText());
				}
			}
		};
		
		//Adding that ^ generic action listener to all buttons
		nothing.addActionListener(changeColorPress);
		center.addActionListener(changeColorPress);
		leftSwitch.addActionListener(changeColorPress);
		rightSwitch.addActionListener(changeColorPress);
		leftScale.addActionListener(changeColorPress);
		rightScale.addActionListener(changeColorPress);
		
		//When each button is pressed, the corresponding value is sent to the network table
		nothing.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive)
					ntc.sendAutoChoice(n);
			}
		});
		center.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive)
					ntc.sendAutoChoice(c);
			}
		});
		leftSwitch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive)
					ntc.sendAutoChoice(lw);
			}
		});
		rightSwitch.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive)
					ntc.sendAutoChoice(rw);
			}
		});
		leftScale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive)
					ntc.sendAutoChoice(lc);
			}
		});
		rightScale.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(isActive)
					ntc.sendAutoChoice(rc);
			}
		});
		
		//Adding all the components to the frame
		top = new JPanel(new GridLayout(1, 3));
		mid = new JPanel(new GridLayout(1, 3));
		bot = new JPanel(new GridLayout(1, 1));
		message = new JLabel();
		message.setFont(new Font("Arial", Font.BOLD, 17));
		message.setHorizontalAlignment(SwingConstants.CENTER);
		bot.setBackground(Color.WHITE);
		
		setAllButtonColor(myRed);
		setMessage("Connecting");
		
		top.add(leftScale);
		top.add(nothing);
		top.add(rightScale);
		
		mid.add(leftSwitch);
		mid.add(center);
		mid.add(rightSwitch);
		
		bot.add(message);
		
		this.setLayout(new GridLayout(3, 1));
		this.add(top);
		this.add(mid);
		this.add(bot);

		this.setVisible(true);
		
		//Connect to the Network Table
		connect(connectingDelay);
	}
	
	//Sets the label text
	public void setMessage(String msg) {
		message.setText(msg);
	}
	
	//Gets the label text
	public String getMessage() {
		return message.getText();
	}
	
	//Checks if ntc has connected to the networkTable at rate "delay".
	//The buttons don't do anything until it has connected
	//After it is connected, it will check less frequently to ensure it's still connected
	public void connect(int delay) {
		final Timer timer = new Timer(delay, null);
		timer.setRepeats(true);
		timer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				if (ntc.isConnected() && !isActive) {
					isActive = ntc.isConnected();
					timer.setDelay(pingDelay);
					setAllButtonColor(Color.WHITE);
					setMessage("Select an autonomous");
				}
				else if (!ntc.isConnected() && isActive) {
					isActive = ntc.isConnected();
					setAllButtonColor(myRed);
					setMessage("Connecting");
				}
				else if (!isActive) {
					setMessage(getMessage() + ".");
				}
			}
		});
		timer.start();
	}
	
	//An easy way to create a simple-looking button
	private JButton createSimpleButton(String text) {
		  JButton button = new JButton(text);
		  button.setForeground(Color.BLACK);
		  button.setBackground(Color.WHITE);
		  LineBorder line = new LineBorder(Color.BLACK);
		  button.setBorder(line);
		  return button;
	}
	
	//Self explanatory
	private void setAllButtonColor(Color c) {
		nothing.setBackground(c);
		center.setBackground(c);
		leftSwitch.setBackground(c);
		rightSwitch.setBackground(c);
		leftScale.setBackground(c);
		rightScale.setBackground(c);
	}
	
	public static void main(String[] args) {
		new Window("Hashboard 2018", new NetTableControl());
	}
}
