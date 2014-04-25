package dealer;

import java.rmi.RemoteException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;
import lejos.robotics.subsumption.Behavior;

public class JaetaanKaikki implements Behavior{
	// moottorit
	private RMIRegulatedMotor jakaja;
	private RMIRegulatedMotor heittaja;
	private int pelaajamaara;
	// 
	private int z = 0;
	private int tarkastus = 0;
	// supressed flagi
	private volatile boolean suppressed = false;
	
	//nollaaja muuttujille
	public void nollaaMuuttujat () {
		this.z = 0;
		this.tarkastus = 0;
	}

	public JaetaanKaikki(RMIRegulatedMotor heittaja2, RMIRegulatedMotor jakaja2, Kalibrointi kalib) {
		heittaja = heittaja2;
		jakaja = jakaja2;
		pelaajamaara = kalib.getPelaajamaara();
	}

	@Override
	public boolean takeControl() {
		return SeuraavaPelaaja.kohdalla;
	}

	@Override
	public void action() {
		SeuraavaPelaaja.kohdalla = false;
		int korttimaara = (int) (52 / pelaajamaara);
		jaaKortti(2);
		// tarkastetaan kenelle seuraava kortti jaetaa ja ett‰ onko alkukortit jaettu jo
		tarkastus();

		suppress();
	}

	@Override
	public void suppress() {
		suppressed = true;		
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
				jakaja.rotate(120);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void tarkastus() {
		// katsotaan ollaanko viimeisess‰ pelaajassa
		if (z+1 >= pelaajamaara) {
			SeuraavaPelaaja.peliss‰ = false;
			z = 0;
		} else {
			// jos ei, siirryt‰‰n seuraavaan pelaajaan
			z++;
		}

	}

}