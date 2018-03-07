package eV3;

import lejos.hardware.lcd.LCD;
import lejos.hardware.sensor.EV3IRSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
/**
 * 
 * @author Teo
 *
 */
public class BeaconF extends Thread {

	private RegulatedMotor rightmotor;
	private RegulatedMotor leftmotor;
	private RegulatedMotor liftmotor;
	private EV3IRSensor beaconIR;
	boolean jatkaB = true;
	boolean etsi = false;
	private LedValo valo;
	// Main constructor, requires 2 large motors, 1 medium motor, 1 IR Sensor
	public BeaconF(RegulatedMotor mB, RegulatedMotor mC, RegulatedMotor mA, EV3IRSensor s) {
		this.rightmotor = mB;
		this.leftmotor = mC;
		this.liftmotor = mA;
		this.beaconIR = s;
		this.rightmotor.synchronizeWith(new RegulatedMotor[] { this.leftmotor });
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
		SensorMode seek = beaconIR.getSeekMode(); // Sets the IR sensor to seekmode
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
					rightmotor.forward();
					leftmotor.forward();
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
	private void lowerS() {
		liftmotor.rotate(-100);
		Delay.msDelay(3000);

	}
	// Stops all motors
	private void stopMo() {
		rightmotor.startSynchronization();
		rightmotor.stop(true);
		leftmotor.stop(true);
		rightmotor.endSynchronization();
	}
	//
	// All methods below control the motors of the robot
	//
	private void defaultSpeed() {
		rightmotor.setSpeed(900);
		leftmotor.setSpeed(900);
	}

	private void turnSpeed() {
		rightmotor.setSpeed(850);
		leftmotor.setSpeed(800);
	}

	@SuppressWarnings("unused")
	private void backwards() {
		LCD.drawString("peruutus2", 6, 6);
		rightmotor.startSynchronization();
		rightmotor.forward();
		leftmotor.forward();
		rightmotor.endSynchronization();
	}

	@SuppressWarnings("unused")
	private void forwards() {

		rightmotor.startSynchronization();
		rightmotor.backward();
		leftmotor.backward();
		rightmotor.endSynchronization();
	}

	private void turnRight() {
		rightmotor.startSynchronization();
		rightmotor.backward();
		leftmotor.stop();
		rightmotor.endSynchronization();
	}

	private void turnLeft() {
		rightmotor.startSynchronization();
		leftmotor.backward();
		rightmotor.stop();
		rightmotor.endSynchronization();

	}
}
