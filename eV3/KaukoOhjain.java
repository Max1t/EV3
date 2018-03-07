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

	private EV3IRSensor infraredSensor;
	private EV3IRSensor infraredSensor2;
	private RegulatedMotor mB;
	private RegulatedMotor mC;
	private RegulatedMotor mA;
	private BeaconF beacon;
	private LedValo valo;

	boolean jatka = true;	
	//Constructor uses 2 IR sensors, 2 Large motors, 1 medium motor. Other IR sensor is passed to BeaconF
	public KaukoOhjain(EV3IRSensor sensor2, EV3IRSensor sensor, RegulatedMotor b, RegulatedMotor c, RegulatedMotor a) {
		this.infraredSensor = sensor;
		this.infraredSensor2 = sensor2;
		this.mB = b;
		this.mC = c;
		this.mA = a;
		beacon = new BeaconF(this.mB, this.mC, this.mA, this.infraredSensor2);
		this.mB.synchronizeWith(new RegulatedMotor[] { this.mC });
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
		mA.setSpeed(100);
		mB.setSpeed(900);
		mC.setSpeed(900);
		//Boolean used to restrict lift movement currently unused
		@SuppressWarnings("unused")
		boolean up = false;
		while (jatka) { // Thread runs as long as jatka is true
			//Green light when ready
			valo.setGreen();
			int remoteCommand = infraredSensor.getRemoteCommand(0);
			if (remoteCommand == 1) { // Button 1 sends robot forward in default speed
				do {
					valo.setOrange();
					defaultSpeed();
					forwards();
					remoteCommand = infraredSensor.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 2) { // Button 2 sends robot backward in default speed
				do {
					valo.setRed();
					defaultSpeed();
					backwards();
					remoteCommand = infraredSensor.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 3) { // Button 3 turn the robot left slowly
				do {
					valo.setOrange();
					slowSpeed();
					turnLeft();
					remoteCommand = infraredSensor.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 4) { // Button 4 turns the robot right slowly
				do {
					valo.setOrange();
					slowSpeed();
					turnRight();
					remoteCommand = infraredSensor.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 11) { // Button combination 3 and 4 ends beacon following
				beacon.setEtsi(false);
				remoteCommand = infraredSensor.getRemoteCommand(0);
			}
			if (remoteCommand == 10) { // Button combination 1 and 2 starts beacon following
				beacon.setEtsi(true);
				remoteCommand = infraredSensor.getRemoteCommand(0);
			}
			if (remoteCommand == 5) { // Button combination 1 and 3 lifts up the lift
				valo.setBlinkRed();
				mA.rotate(100);
				up = true;
				remoteCommand = infraredSensor.getRemoteCommand(0);
			}
			if (remoteCommand == 8) { // Button combination 2 and 4 sets the lift down
				valo.setBlinkRed();
				mA.rotate(-100);
				up = false;
				remoteCommand = infraredSensor.getRemoteCommand(0);
			}// Stop motors at the end of the thread
			mB.startSynchronization();
			mB.stop();
			mC.stop();
			mB.endSynchronization();

		}

	}
	// Sets large motors speed to default max speed
	public void defaultSpeed() {
		mB.setSpeed(900);
		mC.setSpeed(900);
	}
	// Halves the speed of the large motors
	public void slowSpeed() {
		mB.setSpeed(400);
		mC.setSpeed(400);
	}
	// Method to move robot backwards (Small quirk the motors are backwards on the robot hence the command to send the motors forward)
	public void backwards() {
		mB.startSynchronization();
		mB.forward();
		mC.forward();
		mB.endSynchronization();

	}
	// Method to move robot forwards (Same quirk
	public void forwards() {
		mB.startSynchronization();
		mB.backward();
		mC.backward();
		mB.endSynchronization();
	}
	// Method to turn the robot left
	public void turnLeft() {
		mB.startSynchronization();
		mB.backward();
		mC.forward();
		mB.endSynchronization();
	}
	// Method to turn the robot right
	public void turnRight() {
		mB.startSynchronization();
		mC.backward();
		mB.forward();
		mB.endSynchronization();
	}
}
