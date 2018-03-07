package eV3;

import lejos.hardware.Button;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
/**
 * 
 * @author Max
 *
 */
public class Main {
	// Main
	public static void main(String[] args) {
		// Creating sensor and motors.
		EV3IRSensor irSensor = new EV3IRSensor(SensorPort.S3);
		EV3IRSensor irSensor2 = new EV3IRSensor(SensorPort.S4);
		RegulatedMotor mB = new EV3LargeRegulatedMotor(MotorPort.B);
		RegulatedMotor mC = new EV3LargeRegulatedMotor(MotorPort.C);
		RegulatedMotor mA = new EV3MediumRegulatedMotor(MotorPort.A);
		LedValo valo = new LedValo();
		
		//Startup LED Sequence
		valo.setRed();
		Delay.msDelay(1000);
		valo.setBlinkOrange();
		Delay.msDelay(1000);
		valo.setGreen();
		
		//Create objects to be able to start and stop both threads
		KaukoOhjain KO = new KaukoOhjain(irSensor, irSensor2, mB, mC, mA);
		BeaconF KA =new BeaconF();
		
		Delay.msDelay(3000);
		KO.start();
		LCD.drawString("Escape to stop. ", 0, 4); // Press escape on the robot to stop all threads and end the program
		Button.waitForAnyPress();
		KO.setStop();
		KA.setStop();
		
	}

}
