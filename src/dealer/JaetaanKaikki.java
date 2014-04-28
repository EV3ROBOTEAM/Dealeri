package dealer;

import java.rmi.RemoteException;

import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.robotics.subsumption.Behavior;

public class JaetaanKaikki implements Behavior{
	// moottorit
	private RMIRegulatedMotor jakaja;
	private RMIRegulatedMotor heittaja;
	private int pelaajamaara;
	// 
	private int korttimaara;
	private int[] jaettu;
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
		this.jaettu = new int[pelaajamaara];
	}

	@Override
	public boolean takeControl() {
		return SeuraavaPelaaja.kohdalla;
	}

	@Override
	public void action() {
		
		SeuraavaPelaaja.kohdalla = false;
		korttimaara = (int) (52 / pelaajamaara);
		jaaKortti(1);
		// tarkastetaan kenelle seuraava kortti jaetaa ja ett‰ onko kortit jaettu jo
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
		jaettu[z] += 1;
		// katsotaan ollaanko viimeisess‰ pelaajassa
		if (z+1 >= pelaajamaara) {
			//SeuraavaPelaaja.peliss‰ = false;
			z = 0;
		} else {
			// jos ei, siirryt‰‰n seuraavaan pelaajaan
			z++;
		}
		
		// tarkastetaan onko pelaajille jaettu kaikki kortit
		for (int g = 0; g < jaettu.length; g++) {
			if (jaettu[g] == korttimaara){
				tarkastus++;
			} 
		}
		
		if (tarkastus == jaettu.length) {
			SeuraavaPelaaja.peliss‰ = false;
			
		} else {
			tarkastus = 0;
		}
		
	}

}