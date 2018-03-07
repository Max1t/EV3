package eV3;

import lejos.hardware.Button;

public class LedValo {
	//Robot LED light controls
	
	public LedValo() {
		
	}//Clear LED
	public void noLED() {
		Button.LEDPattern(0);
	}
	//Red LED
	public void setRed() {
		Button.LEDPattern(2);
	}
	//Green LED
	public void setGreen() {
		Button.LEDPattern(1);
	}
	//Orange LED
	public void setOrange() {
		Button.LEDPattern(3);
	}
	//Blinking Green LED
	public void setBlinkGreen() {
		Button.LEDPattern(4);
	}
	//Blinking Orange LED
	public void setBlinkOrange() {
		Button.LEDPattern(6);
	}
	//Blinking Red LED
	public void setBlinkRed() {
		Button.LEDPattern(5);
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
