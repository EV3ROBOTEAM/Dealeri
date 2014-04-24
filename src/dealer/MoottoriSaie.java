package dealer;

import java.rmi.RemoteException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;

public class MoottoriSaie extends Thread{
	private RMIRegulatedMotor rotatoija;
	
	public MoottoriSaie (RMIRegulatedMotor rotatoija2) {
		rotatoija = rotatoija2;
	}
	
	public void run() {
		try {
			rotatoija.rotate(600);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
