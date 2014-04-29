package dealer;

import java.rmi.RemoteException;

import lejos.hardware.Sound;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;
import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3TouchSensor;

public class DiileriDemo implements Behavior {
	private RMIRegulatedMotor rotatoija;
	private RMIRegulatedMotor jakaja;
	private RMIRegulatedMotor heittaja;
	static boolean demossa = true;
	private volatile boolean suppressed = false;
	SampleProvider painallus;
	float[] näyte = new float[1];

	public DiileriDemo(RMIRegulatedMotor rotatoija2,
			RMIRegulatedMotor heittaja2, RMIRegulatedMotor jakaja2,
			EV3TouchSensor nappi2, boolean peli) {
		rotatoija = rotatoija2;
		jakaja = jakaja2;
		heittaja = heittaja2;
		demossa = peli;
		painallus = nappi2.getTouchMode();
		näyte[0] = 0;
	}

	@Override
	public boolean takeControl() {
		return demossa;
	}

	@Override
	public void action() {
		System.out.println("DIILERIDEMOSSA");
		
		
		// VAIHTOEHTO DEMO
		try {
			rotatoija.setSpeed(250);
			rotatoija.forward();
			heittaja.backward();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		for (int i = 0; i <= 52; i++) {
			try {
				if (i == 52){
					jakaja.rotate(-300);
					break;
				}
				jakaja.rotate(-190-(i/2));
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			rotatoija.stop(true);
			heittaja.stop(true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*do{
		int nopeus = 20;
		try {
			rotatoija.setSpeed(nopeus);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			rotatoija.backward();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Sound.beepSequenceUp();
		
		for (int i = 0; i <= 24; i++) {
			Sound.playTone((nopeus * 11), 50);
			System.out.println("kiihdytys");
			nopeus += 10;
			try {
				rotatoija.setSpeed(nopeus);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			jaaKortti(1);
			Sound.playTone((nopeus * 7), 30);
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println("hidastus");
		for (int i = 0; i <= 100; i++) {
			Sound.playTone((nopeus * 11), 50);
			System.out.println("hidastus");
			nopeus -= 2;
			try {
				rotatoija.setSpeed(nopeus);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				Thread.sleep(4);
			} catch (InterruptedException e) {
			}
		}
			while (näyte[0] == 0) {
				painallus.fetchSample(näyte, 0);
			}
			break;
		} while(näyte[0] == 0);
		Sound.buzz();
		try {
			rotatoija.stop(true);
		} catch (RemoteException e) {}
		*/
		demossa = false;
		suppress();
		

	}

	@Override
	public void suppress() {
		suppressed = true;

	}

	public float nappiPainettu() {
		while (näyte[0] == 0) {
		}
		return näyte[0];
	}

	public void jaaKortti(int maara) {
		for (int i = 0; i < maara; i++) {
			try {
				heittaja.backward();
			} catch (RemoteException e) {}
			
			try {
				jakaja.rotate(-300);
			} catch (RemoteException e) {}
			
			try {
				heittaja.stop(true);
			} catch (RemoteException e) {}
			
			try {
				jakaja.rotate(300);
			} catch (RemoteException e) {}
		}
	}

}
