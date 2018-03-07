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

	private EV3IRSensor RemoteIR;
	private EV3IRSensor BeaconIR;
	private RegulatedMotor Leftmotor;
	private RegulatedMotor Rightmotor;
	private RegulatedMotor Liftmotor;
	private BeaconF beacon;
	private LedValo valo;
	boolean jatka = true;	
	//Constructor uses 2 IR sensors, 2 Large motors, 1 medium motor. Other IR sensor is passed to BeaconF
	public KaukoOhjain(EV3IRSensor sensor2, EV3IRSensor sensor, RegulatedMotor b, RegulatedMotor c, RegulatedMotor a) {
		this.RemoteIR = sensor;
		this.BeaconIR = sensor2;
		this.Leftmotor = b;
		this.Rightmotor = c;
		this.Liftmotor = a;
		beacon = new BeaconF(this.Leftmotor, this.Rightmotor, this.Liftmotor, this.BeaconIR);
		this.Leftmotor.synchronizeWith(new RegulatedMotor[] { this.Rightmotor });
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
		Liftmotor.setSpeed(100);
		Leftmotor.setSpeed(900);
		Rightmotor.setSpeed(900);
		//Boolean used to restrict lift movement currently unused
		@SuppressWarnings("unused")
		boolean up = false;
		while (jatka) { // Thread runs as long as jatka is true
			//Green light when ready
			valo.setGreen();
			int remoteCommand = RemoteIR.getRemoteCommand(0);
			if (remoteCommand == 1) { // Button 1 sends robot forward in default speed
				do {
					valo.setOrange();
					defaultSpeed();
					forwards();
					remoteCommand = RemoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 2) { // Button 2 sends robot backward in default speed
				do {
					valo.setRed();
					defaultSpeed();
					backwards();
					remoteCommand = RemoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 3) { // Button 3 turn the robot left slowly
				do {
					valo.setOrange();
					slowSpeed();
					turnLeft();
					remoteCommand = RemoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 4) { // Button 4 turns the robot right slowly
				do {
					valo.setOrange();
					slowSpeed();
					turnRight();
					remoteCommand = RemoteIR.getRemoteCommand(0);
				} while (remoteCommand != 0);
			}
			if (remoteCommand == 11) { // Button combination 3 and 4 ends beacon following
				beacon.setEtsi(false);
				remoteCommand = RemoteIR.getRemoteCommand(0);
			}
			if (remoteCommand == 10) { // Button combination 1 and 2 starts beacon following
				beacon.setEtsi(true);
				remoteCommand = RemoteIR.getRemoteCommand(0);
			}
			if (remoteCommand == 5) { // Button combination 1 and 3 lifts up the lift
				valo.setBlinkRed();
				Liftmotor.rotate(240);
				up = true;
				remoteCommand = RemoteIR.getRemoteCommand(0);
			}
			if (remoteCommand == 8) { // Button combination 2 and 4 sets the lift down
				valo.setBlinkRed();
				Liftmotor.rotate(-240);
				up = false;
				remoteCommand = RemoteIR.getRemoteCommand(0);
			}// Stop motors at the end of the thread
			Leftmotor.startSynchronization();
			Leftmotor.stop();
			Rightmotor.stop();
			Leftmotor.endSynchronization();

		}

	}
	// Sets large motors speed to default max speed
	public void defaultSpeed() {
		Leftmotor.setSpeed(900);
		Rightmotor.setSpeed(900);
	}
	// Halves the speed of the large motors
	public void slowSpeed() {
		Leftmotor.setSpeed(400);
		Rightmotor.setSpeed(400);
	}
	// Method to move robot backwards (Small quirk the motors are backwards on the robot hence the command to send the motors forward)
	public void backwards() {
		Leftmotor.startSynchronization();
		Leftmotor.forward();
		Rightmotor.forward();
		Leftmotor.endSynchronization();

	}
	// Method to move robot forwards (Same quirk
	public void forwards() {
		Leftmotor.startSynchronization();
		Leftmotor.backward();
		Rightmotor.backward();
		Leftmotor.endSynchronization();
	}
	// Method to turn the robot left
	public void turnLeft() {
		Leftmotor.startSynchronization();
		Leftmotor.backward();
		Rightmotor.forward();
		Leftmotor.endSynchronization();
	}
	// Method to turn the robot right
	public void turnRight() {
		Leftmotor.startSynchronization();
		Rightmotor.backward();
		Leftmotor.forward();
		Leftmotor.endSynchronization();
	}
}
