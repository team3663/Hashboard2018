package controlpanel;

import javax.swing.JButton;

@SuppressWarnings("serial")
public class MyButton extends JButton {
	public int index;
	public String displayName;
	public MyButton() {
		super();
	}
	public MyButton(String name) {
		super(name);
		displayName = name;
	}
}
