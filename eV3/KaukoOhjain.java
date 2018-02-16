package eV3;

	import lejos.hardware.Button;
	import lejos.hardware.lcd.LCD;
	import lejos.hardware.motor.EV3LargeRegulatedMotor;
	import lejos.hardware.motor.EV3MediumRegulatedMotor;
	import lejos.hardware.port.MotorPort;
	import lejos.hardware.sensor.EV3IRSensor;
	import lejos.robotics.RegulatedMotor;
	import lejos.utility.Delay;

	public class KaukoOhjain extends Thread {
		private EV3IRSensor infraredSensor;
		RegulatedMotor mB = new EV3LargeRegulatedMotor(MotorPort.B);
		RegulatedMotor mC = new EV3LargeRegulatedMotor(MotorPort.C);
		RegulatedMotor mA = new EV3MediumRegulatedMotor(MotorPort.A);

		public KaukoOhjain(EV3IRSensor sensor) {
			this.infraredSensor = sensor;
			mB.synchronizeWith(new RegulatedMotor[] { mC });
		}

		// this is run when the thread is started
		public void run() {
			LCD.clear();
			Delay.msDelay(2000);
			LCD.drawString("Running", 4, 4);
			mA.setSpeed(150);
			mB.setSpeed(900);
			mC.setSpeed(900);
			while (!Button.ESCAPE.isDown()) {
				int remoteCommand = infraredSensor.getRemoteCommand(0);
				if (remoteCommand == 1) {
					do {
						// Vanhaa koodia mB.startSynchronization();
						// taakse(mB);
						// taakse(mC);
						// mB.endSynchronization();
						
						backwards();
						remoteCommand = infraredSensor.getRemoteCommand(0);

					} while (remoteCommand != 0);
				}
				if (remoteCommand == 2) {
					do {
						// Vanhaa koodia mB.startSynchronization();
						// eteen(mB);
						// eteen(mC);
						// mB.endSynchronization();
						forwards();
						remoteCommand = infraredSensor.getRemoteCommand(0);

					} while (remoteCommand != 0);
				}
				if (remoteCommand == 9) {
					do {
						// Vanhaa koodiamB.startSynchronization();
						// taakse(mB);
						// eteen(mC);
						// mB.endSynchronization();
						
						turn();
						remoteCommand = infraredSensor.getRemoteCommand(0);
					} while (remoteCommand != 0);
				}
				if (remoteCommand == 3) {
					mA.rotate(160);

				}
				if (remoteCommand == 4) {
					mA.rotate(-150);

				}
				mB.startSynchronization();
				mB.stop();
				mC.stop();
				mB.endSynchronization();

			}
			System.exit(0);

		}

		public void forwards() {
			mB.startSynchronization();
			mB.forward();
			mC.forward();
			mB.endSynchronization();

		}

		public void backwards() {
			mB.startSynchronization();
			mB.backward();
			mC.backward();
			mB.endSynchronization();
		}

		public void turn() {
			mB.startSynchronization();
			mB.backward();
			mC.forward();
			mB.endSynchronization();
		}
	}
	/*
	 * public void eteen(RegulatedMotor m) { m.forward();
	 * 
	 * }
	 * 
	 * public void taakse(RegulatedMotor m) { m.backward();
	 * 
	 * } }
	 */

