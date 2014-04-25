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
	private EV3TouchSensor nappi;
	static boolean demossa = true;
	private volatile boolean suppressed = false;
	SampleProvider painallus;
	float[] näyte = new float[1];

	public DiileriDemo(RMIRegulatedMotor rotatoija2,
			RMIRegulatedMotor heittaja2, RMIRegulatedMotor jakaja2,
			EV3TouchSensor nappi2) {
		rotatoija = rotatoija2;
		jakaja = jakaja2;
		heittaja = heittaja2;
		nappi = nappi2;
	}

	@Override
	public boolean takeControl() {
		return demossa;
	}

	@Override
	public void action() {
		System.out.println("DIILERIDEMOSSA");
		näyte[0] = 0;
		
			
		do{
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
		
		for (int i = 0; i <= 10; i++) {
			Sound.playTone((nopeus * 11), 50);
			System.out.println("kiihdytys");
			nopeus += 22;
			try {
				rotatoija.setSpeed(nopeus);
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			jaaKortti(1);
			Sound.playTone((nopeus * 7), 30);
			try {
				Thread.sleep(23);
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
		}while(nappiPainettu() == 0);
		Sound.buzz();


		demossa = false;
		suppress();
		

	}

	@Override
	public void suppress() {
		suppressed = true;

	}

	public float nappiPainettu() {
		while (true) {
			painallus.fetchSample(näyte, 0);
			return näyte[0];
		}

	}

	public void jaaKortti(int maara) {
		for (int i = 0; i < maara; i++) {
			try {
				heittaja.backward();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jakaja.rotate(-300);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				heittaja.stop(true);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jakaja.rotate(200);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
