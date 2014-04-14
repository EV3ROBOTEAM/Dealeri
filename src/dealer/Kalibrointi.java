package dealer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.robotics.SampleProvider;

// ei ole Behavior, kalibroidaan vaan ja otetaan pelaajasijainnit talteen
public class Kalibrointi {
	// moottorit
	private EV3LargeRegulatedMotor rotatoija;
	private EV3LargeRegulatedMotor jakaja;
	private EV3MediumRegulatedMotor heittaja;
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

	// kalibroinnin konstruktori
	public Kalibrointi(EV3LargeRegulatedMotor r, EV3TouchSensor n, HiTechnicCompass c) {
		// moottorit
		rotatoija = r;

		// sensorit
		painallus = n.getTouchMode();
		kompassi = c.getCompassMode();

		// pelaajien sijainnit ja muut s‰‰dˆt
		pelaajaSijainnit = new float[20];
		
		//s‰ie
		saie = new MoottoriSaie(r);

	}

	public void ezkalib() {
		System.out.println("ROTATETAAN");
		// otetaan ensimm‰isen pelaajan piste, jonka j‰lkeen pyˆrit‰‰n 420-astetta
		rotatoija.forward();
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
					Thread.sleep(120);
				} catch (InterruptedException e) {}
				pelaajanro++;
				System.out.println("Pelaajanro = " + pelaajanro
						+ ", sijainti = " + pelaajaSijainnit[pelaajanro-1]);
			}
		}
		// l‰hetet‰‰n pelaajien m‰‰r‰
		SeuraavaPelaaja.pelaajamaara = pelaajanro;
		Pokeri.pelaajamaara = pelaajanro;
	}
}
