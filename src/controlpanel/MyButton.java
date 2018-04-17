package controlpanel;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class MyButton extends JButton {
	public int index;
	public String displayName;
	public int state;
	/*
	 * 0 Ready to be pressed
	 * 1 Pressed
	 * 2 Cannot be pressed
	 * 3 Disconnected
	 * 4 Queued
	 */
	public MyButton() {
		super();
	}
	public MyButton(String name) {
		super(name);
		displayName = name;
		state = 3;
	}
}
