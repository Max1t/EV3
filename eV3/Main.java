package eV3;


import lejos.hardware.lcd.LCD;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.utility.Delay;

public class Main {

	public static void main(String[] args) {
		EV3IRSensor irSensor = new EV3IRSensor(SensorPort.S1);
		KaukoOhjain KO = new KaukoOhjain(irSensor);
		
		LCD.drawString("123 test", 4, 4);
		Delay.msDelay(3000);
		KO.start();

	}

}
