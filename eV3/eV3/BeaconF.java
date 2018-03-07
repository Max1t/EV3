package eV3;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;

public class BeaconF extends Thread {

	private RegulatedMotor motor1;
	private RegulatedMotor motor2;
	private RegulatedMotor motor3;
	private EV3IRSensor s1;
	boolean jatkaB = true;
	boolean etsi = false;
	private LedValo valo;
	// Main constructor, requires 2 large motors, 1 medium motor, 1 IR Sensor
	public BeaconF(RegulatedMotor mB, RegulatedMotor mC, RegulatedMotor mA, EV3IRSensor s) {
		this.motor1 = mB;
		this.motor2 = mC;
		this.motor3 = mA;
		this.s1 = s;
		this.motor1.synchronizeWith(new RegulatedMotor[] { this.motor2 });
		valo = new LedValo();
	}
	// Constructor without parameters for main class
	public BeaconF() {

	}
	// Stops the thread
	public void setStop() {
		jatkaB = false;
	}
	// Returns current status of jatkaB boolean
	public boolean getStart() {
		return jatkaB;
	}
	//Sets boolean etsi. True starts beacon following, false ends beacon following
	public void setEtsi(boolean etsi) {
		this.etsi = etsi;
	}
	// Run when [BeaconF].start() is called
	public void run() {
		SensorMode seek = s1.getSeekMode(); // Sets the IR sensor to seekmode
		float[] sample = new float[seek.sampleSize()];
		int x = 0;
		while (jatkaB) {
			if (etsi) {
				valo.setBlinkRed();
				seek.fetchSample(sample, 0);
				int direction = (int) sample[0];
				int distance = (int) sample[1];
				LCD.drawInt(direction, 2, 2);
				LCD.drawInt(distance, 3, 3);
				LCD.drawInt(x, 5, 5);
				LCD.clear();
				
				if (direction > 1 && distance > 200) {
					turnSpeed();
					turnLeft();
				}
				if (direction > 1) { // Turns the robot left when it senses the beacon on the left
					defaultSpeed();
					turnLeft();
				} else if (direction < -1) { // Turns the robot right when it senses the beacon on the right
					defaultSpeed();
					turnRight();
				} else if (distance < 60 && distance > 5) { // Lift lowering sequence when the robot reaches the beacon. Printing used for troubleshooting
					valo.setBlinkGreen();
					defaultSpeed();
					stopMo();
					lowerS();
					LCD.drawString("peruutus1", 1, 1);
					defaultSpeed();
					motor1.forward();
					motor2.forward();
					//backwards();
					Delay.msDelay(2000);
					LCD.drawString("peruutus3", 7, 7);
					stopMo();

					setEtsi(false);
				} else { 	//If no beacon is found the robot turns in place
					defaultSpeed();
					turnLeft();

				}
			}
		}
	}
	// Lowers the lift and delays the robot for 3 seconds
	public void lowerS() {
		motor3.rotate(-100);
		Delay.msDelay(3000);

	}
	// Stops all motors
	public void stopMo() {
		motor1.startSynchronization();
		motor1.stop(true);
		motor2.stop(true);
		motor1.endSynchronization();
	}
	//
	// All methods below control the motors of the robot
	//
	public void defaultSpeed() {
		motor1.setSpeed(900);
		motor2.setSpeed(900);
	}

	public void turnSpeed() {
		motor1.setSpeed(850);
		motor2.setSpeed(800);
	}

	public void backwards() {
		LCD.drawString("peruutus2", 6, 6);
		motor1.startSynchronization();
		motor1.forward();
		motor2.forward();
		motor1.endSynchronization();
	}

	public void forwards() {

		motor1.startSynchronization();
		motor1.backward();
		motor2.backward();
		motor1.endSynchronization();
	}

	public void turnRight() {
		motor1.startSynchronization();
		motor1.backward();
		motor2.stop();
		motor1.endSynchronization();
	}

	public void turnLeft() {
		motor1.startSynchronization();
		motor2.backward();
		motor1.stop();
		motor1.endSynchronization();

	}
}
