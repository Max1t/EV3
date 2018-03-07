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

	private RegulatedMotor Rightmotor;
	private RegulatedMotor Leftmotor;
	private RegulatedMotor Liftmotor;
	private EV3IRSensor s1;
	boolean jatkaB = true;
	boolean etsi = false;
	private LedValo valo;
	// Main constructor, requires 2 large motors, 1 medium motor, 1 IR Sensor
	public BeaconF(RegulatedMotor mB, RegulatedMotor mC, RegulatedMotor mA, EV3IRSensor s) {
		this.Rightmotor = mB;
		this.Leftmotor = mC;
		this.Liftmotor = mA;
		this.s1 = s;
		this.Rightmotor.synchronizeWith(new RegulatedMotor[] { this.Leftmotor });
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
					Rightmotor.forward();
					Leftmotor.forward();
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
		Liftmotor.rotate(-240);
		Delay.msDelay(3000);

	}
	// Stops all motors
	private void stopMo() {
		Rightmotor.startSynchronization();
		Rightmotor.stop(true);
		Leftmotor.stop(true);
		Rightmotor.endSynchronization();
	}
	//
	// All methods below control the motors of the robot
	//
	private void defaultSpeed() {
		Rightmotor.setSpeed(900);
		Leftmotor.setSpeed(900);
	}

	private void turnSpeed() {
		Rightmotor.setSpeed(850);
		Leftmotor.setSpeed(800);
	}

	@SuppressWarnings("unused")
	private void backwards() {
		LCD.drawString("peruutus2", 6, 6);
		Rightmotor.startSynchronization();
		Rightmotor.forward();
		Leftmotor.forward();
		Rightmotor.endSynchronization();
	}

	@SuppressWarnings("unused")
	private void forwards() {

		Rightmotor.startSynchronization();
		Rightmotor.backward();
		Leftmotor.backward();
		Rightmotor.endSynchronization();
	}

	private void turnRight() {
		Rightmotor.startSynchronization();
		Rightmotor.backward();
		Leftmotor.stop();
		Rightmotor.endSynchronization();
	}

	private void turnLeft() {
		Rightmotor.startSynchronization();
		Leftmotor.backward();
		Rightmotor.stop();
		Rightmotor.endSynchronization();

	}
}
