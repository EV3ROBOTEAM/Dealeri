package dealer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;

public class SeuraavaPelaaja implements Behavior {
	// suppressed flagi
	private volatile boolean suppressed = false;
	// moottorit
	private static EV3LargeRegulatedMotor rotatoija;
	public static int pelaajamaara;
	public static boolean jaettu = false;

	
	// sensorit
	private SampleProvider kompassi;
	
	// sijainnit
	private float[] Sijainnit;
	private int pelaaja = 0;
	private float[] kompassinArvo = new float[1];
	static boolean kohdalla = false;
	float alaraja;
	float ylaraja;
	float suunta;

	public SeuraavaPelaaja(float pelaajaSijainnit[], EV3LargeRegulatedMotor r, HiTechnicCompass c) {
		// moottori
		rotatoija = r;
		// sijainnit kalibroinnista
		Sijainnit = pelaajaSijainnit;
		// kompassi
		kompassi = c.getCompassMode();
	}
	
	public static void rotatoijaSpeed(int nopeus){
		rotatoija.setSpeed(nopeus);
	}

	@Override
	public boolean takeControl() {
		return true;
	}

	@Override
	public void action() {
		System.out.println("ETSITAAN SEURAAVA PELAAJA");
		
		alaraja = Sijainnit[pelaaja] - 7;
		if (alaraja <= 0) 
			alaraja = 1;
		
		ylaraja = Sijainnit[pelaaja] + 7;
		
		// katsotaan kumpaan suuntaan kannattaa l‰hte‰ liikkumaan
		kompassi.fetchSample(kompassinArvo, 0);
		suunta = kompassinArvo[0];

		if ((Sijainnit[pelaaja] - suunta + 360) % 360 < 180) {
			  rotatoija.forward();
		}	else	{
			  rotatoija.backward();
		}
		
		// etsit‰‰n kompassin arvoa alarajan ja yl‰rajan v‰list‰
		while (!(kompassinArvo[0] > alaraja && kompassinArvo[0] < ylaraja))
			kompassi.fetchSample(kompassinArvo, 0);
		
		kohdalla = true;

		rotatoija.stop();
		System.out.println("STOPATTU");
		
		// jos ollaan k‰yty kaikki pelaajat l‰pi, aloitetaan uudestaan ensimm‰isest‰
		if (pelaaja+1 >= pelaajamaara) {
			pelaaja = 0;
			System.out.println("NOLLATTU");
		} else {
			// tai siirryt‰‰n seuraavaan pelaajaan
			pelaaja++;
		}
		suppress();
	}

	@Override
	public void suppress() {
		suppressed = true;
	}

}
