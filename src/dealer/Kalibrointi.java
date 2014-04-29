package dealer;

import java.rmi.RemoteException;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.robotics.SampleProvider;

// ei ole Behavior, kalibroidaan vaan ja otetaan pelaajasijainnit talteen
public class Kalibrointi {
	// moottorit
	private RMIRegulatedMotor rotatoija;
	private MoottoriSaie saie;
	// sensorit
	private SampleProvider painallus;
	private SampleProvider kompassi;
	private DiileriView view;

	// palautettavat (pelaajasijainnit)
	private float[] pelaajaSijainnit;

	// sensorien muuttujia jne
	float[] kompassinArvo = new float[1];
	float[] napinArvo = new float[1];
	int pelaajanro = 0;

	// tarvittavat getterit kalibroinnin jälkeen
	public float[] sijainnit() {
		return pelaajaSijainnit;
	}
	
	public int getPelaajamaara() {
		return this.pelaajanro;
	}
	
	public void nollaaMuuttujat() {
		this.pelaajanro = 0;
		for (int i = 0 ; i < pelaajaSijainnit.length ; i++) {
			pelaajaSijainnit[i] = 0;
		}
		this.pelaajaSijainnit = null;
		this.pelaajaSijainnit = new float[20];
		view.nollaaRetry();
		
	}
	
	// kalibroinnin konstruktori
	public Kalibrointi(RMIRegulatedMotor rotatoija2, EV3TouchSensor n, HiTechnicCompass c, DiileriView view) {
		//MODEL
		this.view = view;
		// moottorit
		rotatoija = rotatoija2;

		// sensorit
		painallus = n.getTouchMode();
	
		kompassi = c.getCompassMode();

		// pelaajien sijainnit ja muut säädöt
		pelaajaSijainnit = new float[20];
		kompassi.fetchSample(kompassinArvo, 0);
		//säie

	}

	public void ezkalib() {
		saie = new MoottoriSaie(this.rotatoija);
		view.KalibrointiValikko();

		System.out.println("ROTATETAAN");
		// otetaan ensimmäisen pelaajan piste, jonka jälkeen pyöritään 420-astetta
		try {
			rotatoija.forward();
		} catch (RemoteException e2) {}
		
		while (napinArvo[0] == 0) {
			kompassi.fetchSample(kompassinArvo, 0);
			painallus.fetchSample(napinArvo, 0);
		}
		
		while (napinArvo[0] == 1) {
			kompassi.fetchSample(kompassinArvo, 0);
			painallus.fetchSample(napinArvo, 0);
		}
		
		pelaajaSijainnit[pelaajanro] = kompassinArvo[0];
		Sound.beep();

		try {
			Thread.sleep(220);
		} catch (InterruptedException e) {}
		
		pelaajanro++;
		view.kasvataNumeroa();
		System.out.println("Pelaajanro = " + pelaajanro
				+ ", sijainti = " + pelaajaSijainnit[pelaajanro-1]);
		
		try {
			rotatoija.stop(true);
		} catch (RemoteException e1) {}
		
		saie.start(); // käynnistetään moottorisäie, jossa kierretään moottoria 420 astetta

		while (saie.isAlive()) {
			// haetaan kompassin arvoja, koska kompassista ei saa tarkkoja 
			// arvoja ellei sillä ole skannattu jo muutamaa
			
			painallus.fetchSample(napinArvo, 0);
			kompassi.fetchSample(kompassinArvo, 0);

			// kun nappia painetaan, tallennetaan sijainti taulukkoon
			if (napinArvo[0] == 1.0) {
				try {
					Thread.sleep(150);
				} catch (InterruptedException e) {}
				
				pelaajaSijainnit[pelaajanro] = kompassinArvo[0];
				Sound.beep();
				
				pelaajanro++;
				view.kasvataNumeroa();
				System.out.println("Pelaajanro = " + pelaajanro
						+ ", sijainti = " + pelaajaSijainnit[pelaajanro-1]);
			}
		}
	}
}
