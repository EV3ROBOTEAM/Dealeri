package dealer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class MoottoriSaie extends Thread{
	private EV3LargeRegulatedMotor rotatoija;
	
	public MoottoriSaie (EV3LargeRegulatedMotor r) {
		rotatoija = r;
	}
	
	public void run() {
		rotatoija.rotate(600);
	}

}
