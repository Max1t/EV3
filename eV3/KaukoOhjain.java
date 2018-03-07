package eV3;

import lejos.hardware.sensor.EV3IRSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
/**
 * 
 * @author Max
 *
 */
public class KaukoOhjain extends Thread {

	private EV3IRSensor remoteIR;
	private EV3IRSensor beaconIR;
	private RegulatedMotor leftmotor;
	private RegulatedMotor rightMotor;
	private RegulatedMotor liftMotor;
	private BeaconF beacon;
	private LedValo valo;
	boolean jatka = true;	
	//Constructor uses 2 IR sensors, 2 Large motors, 1 medium motor. Other IR sensor is passed to BeaconF
	public KaukoOhjain(EV3IRSensor sensor2, EV3IRSensor sensor, RegulatedMotor b, RegulatedMotor c, RegulatedMotor a) {
		this.remoteIR = sensor;
		this.beaconIR = sensor2;
		this.leftmotor = b;
		this.rightMotor = c;
		this.liftMotor = a;
		beacon = new BeaconF(this.leftmotor, this.rightMotor, this.liftMotor, this.beaconIR);
		this.leftmotor.synchronizeWith(new RegulatedMotor[] { this.rightMotor });
		beacon.start();
		valo = new LedValo();
	}
	// Stops the thread
	public void setStop() {
		jatka = false;
	}

	// Run when [KaukoOhjain].start(); is called
	public void run() {
		Delay.msDelay(2000);
		liftMotor.setSpeed(100);
		leftmotor.setSpeed(900);
		rightMotor.setSpeed(900);
		//Boolean used to restrict lift movement currently unused
		@SuppressWarnings("unused")
		boolean up = false;
		while (jatka) { // Thread runs as long as jatka is true
			//Green light when ready
			valo.setGreen();
			int remoteCommand = remoteIR.getRemoteCommand(0);
			if (remoteCommand == 1) { // Button 1 sends robot forward in default speed
				do {
					valo.setOrange();
					defaultSpeed();
					forwards();
					remoteCommand = remoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 2) { // Button 2 sends robot backward in default speed
				do {
					valo.setRed();
					defaultSpeed();
					backwards();
					remoteCommand = remoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 3) { // Button 3 turn the robot left slowly
				do {
					valo.setOrange();
					slowSpeed();
					turnLeft();
					remoteCommand = remoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 4) { // Button 4 turns the robot right slowly
				do {
					valo.setOrange();
					slowSpeed();
					turnRight();
					remoteCommand = remoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 11) { // Button combination 3 and 4 ends beacon following
				beacon.setEtsi(false);
				remoteCommand = remoteIR.getRemoteCommand(0);
			}
			if (remoteCommand == 10) { // Button combination 1 and 2 starts beacon following
				beacon.setEtsi(true);
				remoteCommand = remoteIR.getRemoteCommand(0);
			}
			if (remoteCommand == 5) { // Button combination 1 and 3 lifts up the lift
				valo.setBlinkRed();
				liftMotor.rotate(100);
				up = true;
				remoteCommand = remoteIR.getRemoteCommand(0);
			}
			if (remoteCommand == 8) { // Button combination 2 and 4 sets the lift down
				valo.setBlinkRed();
				liftMotor.rotate(-100);
				up = false;
				remoteCommand = remoteIR.getRemoteCommand(0);
			}// Stop motors at the end of the thread
			leftmotor.startSynchronization();
			leftmotor.stop();
			rightMotor.stop();
			leftmotor.endSynchronization();

		}

	}
	// Sets large motors speed to default max speed
	public void defaultSpeed() {
		leftmotor.setSpeed(900);
		rightMotor.setSpeed(900);
	}
	// Halves the speed of the large motors
	public void slowSpeed() {
		leftmotor.setSpeed(400);
		rightMotor.setSpeed(400);
	}
	// Method to move robot backwards (Small quirk the motors are backwards on the robot hence the command to send the motors forward)
	public void backwards() {
		leftmotor.startSynchronization();
		leftmotor.forward();
		rightMotor.forward();
		leftmotor.endSynchronization();

	}
	// Method to move robot forwards (Same quirk
	public void forwards() {
		leftmotor.startSynchronization();
		leftmotor.backward();
		rightMotor.backward();
		leftmotor.endSynchronization();
	}
	// Method to turn the robot left
	public void turnLeft() {
		leftmotor.startSynchronization();
		leftmotor.backward();
		rightMotor.forward();
		leftmotor.endSynchronization();
	}
	// Method to turn the robot right
	public void turnRight() {
		leftmotor.startSynchronization();
		rightMotor.backward();
		leftmotor.forward();
		leftmotor.endSynchronization();
	}
}
