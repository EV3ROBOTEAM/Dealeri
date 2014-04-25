package dealer;

import java.rmi.RemoteException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;
import lejos.robotics.SampleProvider;

// ei ole Behavior, kalibroidaan vaan ja otetaan pelaajasijainnit talteen
public class Kalibrointi {
	// moottorit
	private RMIRegulatedMotor rotatoija;
	private RMIRegulatedMotor jakaja;
	private RMIRegulatedMotor heittaja;
	private MoottoriSaie saie;
	// sensorit
	private SampleProvider painallus;
	private SampleProvider kompassi;

	// palautettavat (pelaajasijainnit)
	private float[] pelaajaSijainnit;

	// sensorien muuttujia jne
	float[] kompassinArvo = new float[1];
	float[] napinArvo = new float[1];
	int pelaajanro = 0;

	// tarvittavat getterit kalibroinnin j‰lkeen
	public float[] sijainnit() {
		return pelaajaSijainnit;
	}
	
	public int getPelaajamaara() {
		return this.pelaajanro;
	}
	
	public void nollaaMuuttujat() {
		this.pelaajanro = 0;
		pelaajaSijainnit = new float[20];
		
	}
	
	// kalibroinnin konstruktori
	public Kalibrointi(RMIRegulatedMotor rotatoija2, EV3TouchSensor n, HiTechnicCompass c) {
		// moottorit
		rotatoija = rotatoija2;

		// sensorit
		painallus = n.getTouchMode();
		kompassi = c.getCompassMode();

		// pelaajien sijainnit ja muut s‰‰dˆt
		pelaajaSijainnit = new float[20];
		
		//s‰ie

	}

	public void ezkalib() {
		saie = new MoottoriSaie(this.rotatoija);

		System.out.println("ROTATETAAN");
		// otetaan ensimm‰isen pelaajan piste, jonka j‰lkeen pyˆrit‰‰n 420-astetta
		try {
			rotatoija.forward();
		} catch (RemoteException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		while (napinArvo[0] == 0) {
			painallus.fetchSample(napinArvo, 0);
			kompassi.fetchSample(kompassinArvo, 0);
		}
		pelaajaSijainnit[pelaajanro] = kompassinArvo[0];
		try {
			Thread.sleep(120);
		} catch (InterruptedException e) {}
		pelaajanro++;
		System.out.println("Pelaajanro = " + pelaajanro
				+ ", sijainti = " + pelaajaSijainnit[pelaajanro-1]);
		try {
			rotatoija.stop(true);
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		saie.start(); // k‰ynnistet‰‰n moottoris‰ie, jossa kierret‰‰n moottoria 420 astetta

		while (saie.isAlive()) {
			// haetaan kompassin arvoja, koska kompassista ei saa tarkkoja 
			// arvoja ellei sill‰ ole skannattu jo muutamaa
			painallus.fetchSample(napinArvo, 0);
			kompassi.fetchSample(kompassinArvo, 0);
			System.out.println(kompassinArvo[0]);
			
			// kun nappia painetaan, tallennetaan sijainti taulukkoon
			if (napinArvo[0] == 1.0) {
				//kompassi.fetchSample(kompassinArvo, 0);
				pelaajaSijainnit[pelaajanro] = kompassinArvo[0];
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {}
				pelaajanro++;
				System.out.println("Pelaajanro = " + pelaajanro
						+ ", sijainti = " + pelaajaSijainnit[pelaajanro-1]);
			}
		}
		// l‰hetet‰‰n pelaajien m‰‰r‰
		TexasHoldem.pelaajamaara = pelaajanro;
	}
}
