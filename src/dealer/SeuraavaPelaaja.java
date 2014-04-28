package dealer;

import java.rmi.RemoteException;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;

public class SeuraavaPelaaja implements Behavior {
	// suppressed flagi
	private volatile boolean suppressed = false;
	// moottorit
	private static RMIRegulatedMotor rotatoija;
	private int pelaajamaara;
	public static boolean jaettu = false;

	
	// sensorit
	private SampleProvider kompassi;
	
	// sijainnit
	private float[] Sijainnit;
	private int pelaaja = 0;
	private float[] kompassinArvo = new float[1];
	static boolean kohdalla = false;
	static boolean peliss‰ = true;
	float alaraja;
	float ylaraja;
	float suunta;

	public SeuraavaPelaaja(float pelaajaSijainnit[], RMIRegulatedMotor rotatoija2, HiTechnicCompass c, Kalibrointi kalib, boolean alussa) {
		// moottori
		rotatoija = rotatoija2;
		// sijainnit kalibroinnista
		Sijainnit = pelaajaSijainnit;
		// kompassi
		kompassi = c.getCompassMode();
		// pelaajam‰‰r‰t
		pelaajamaara = kalib.getPelaajamaara();
		// aloitetaan uusi peli
		peliss‰ = alussa;
	}
	

	@Override
	public boolean takeControl() {
		return peliss‰;
	}

	@Override
	public void action() {
		
		alaraja = (Sijainnit[pelaaja] - 6);
		if (alaraja <= 0) 
			alaraja = 360 + alaraja;
		
		ylaraja = (Sijainnit[pelaaja] + 6);
		if (ylaraja > 360)
			ylaraja = ylaraja - 360;
		
		// katsotaan kumpaan suuntaan kannattaa l‰hte‰ liikkumaan
		kompassi.fetchSample(kompassinArvo, 0);
		suunta = kompassinArvo[0];

		if ((Sijainnit[pelaaja] - suunta + 360) % 360 < 180) {
			  try {
				rotatoija.forward();
			} catch (RemoteException e) {}
			  
		}	else	{
			  try {
				rotatoija.backward();
			} catch (RemoteException e) {}
		}
		
		System.out.println("ETSITƒƒN: " + Sijainnit[pelaaja] + "YLƒRAJA: " + ylaraja + ", ALARAJA: " +
		 alaraja);
		
		// etsit‰‰n kompassin arvoa alarajan ja yl‰rajan v‰list‰
		while (!(kompassinArvo[0] > alaraja && kompassinArvo[0] < ylaraja)) {
			kompassi.fetchSample(kompassinArvo, 0);
		}
		System.out.println("Lˆydettiin: "+ kompassinArvo[0]);

		kohdalla = true;
		
		try {
			rotatoija.stop(true);
			System.out.println("STOPATTU");
		} catch (RemoteException e) {}

		
		
		
		// jos ollaan k‰yty kaikki pelaajat l‰pi, aloitetaan uudestaan ensimm‰isest‰
		if (pelaaja+1 >= pelaajamaara) {
			pelaaja = 0;
			System.out.println("NOLLATTU");
			
		} else {
			// tai siirryt‰‰n seuraavaan pelaajaan
			System.out.println("pelaaja " + pelaaja);
			pelaaja++;
		}
		suppress();
	}

	@Override
	public void suppress() {
	//	System.out.println("spelaaja supprrress");
		suppressed = true;
	}

}
