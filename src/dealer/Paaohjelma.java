package dealer;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Paaohjelma extends RemoteEV3 {

	static Arbitrator arby;
	static RemoteEV3 yhteys;
	static EV3TouchSensor nappi;
	static HiTechnicCompass cs;
	static RMIRegulatedMotor rotatoija;
	static RMIRegulatedMotor jakaja;
	static RMIRegulatedMotor heittaja;
	static Behavior pelaajat;
	static Behavior pokeri;
	static Behavior valinta;
	static Behavior holdem;
	static Behavior kaikki;
	static Behavior demo;
	static Kalibrointi kalib;
	static boolean loopz = false;

	public Paaohjelma(String host) throws RemoteException,
			MalformedURLException, NotBoundException {
		super(host);
	}

	public String ip() throws RemoteException, MalformedURLException,
			NotBoundException {
		RemoteEV3 yhteys = new RemoteEV3("10.0.1.1");
		return yhteys.getName();
	}

	public static void main(String[] args) throws RemoteException,
			MalformedURLException, NotBoundException {
		
		// Muuttujat
		boolean alussa = true;
		boolean returnWhenInactive = true;
		int peli = 0;
		boolean uusiPeli = false;
		float[] pelaajaSijainnit = null;
		
		// käyttöliittymä
		DiileriView view = new DiileriView();
		
		// yhteys
		yhteys = new RemoteEV3("10.0.1.1");		
		yhteys.setDefault();
		
		// sensorien konstruktorit ja sampleprovierit
		nappi = new EV3TouchSensor(SensorPort.S1);
		cs = new HiTechnicCompass(SensorPort.S3);
		
		// Moottorit ja nopeudet
		rotatoija = yhteys.createRegulatedMotor("A", 'L');
		jakaja = yhteys.createRegulatedMotor("B", 'L');
		heittaja = yhteys.createRegulatedMotor("C", 'M');
		rotatoija.setSpeed(100);
		jakaja.setSpeed(720);
		heittaja.setSpeed(720);
		view.Paavalikko();

		// Valitaan peli
		while (!view.getvoiAloittaa()) {
			peli = view.getValinta();
		}
		System.out.println("PELI VALITTU");
		view.setvoiAloittaa();

		// Pelaajien kalibrointi
		kalib = new Kalibrointi(rotatoija, nappi, cs, view);

		if (peli != 4) {
			view.KalibrointiValikko();
			kalib.ezkalib();
			// haetaan ja tallennetaan sijainnit
			pelaajaSijainnit = kalib.sijainnit();
		}


		

		do {

			// jos uusiPeli on valittu trueksi
			// valitaan uusi peli
			if (uusiPeli) {
				view.Paavalikko();
				while (!view.getvoiAloittaa()) {
					peli = view.getValinta();
				}
				if (peli != 4) {
					// kalibroidaan jos tarpeelista
					view.KalibrointiValikko();
					kalib.ezkalib();
					pelaajaSijainnit = kalib.sijainnit();
					
				}
				view.setvoiAloittaa();
			}
			
			uusiPeli = false;
			
			// behaviorien konstruktorit
			pelaajat = new SeuraavaPelaaja(pelaajaSijainnit, rotatoija, cs,
					kalib, alussa);
			pokeri = new Pokeri(heittaja, jakaja, kalib);
			valinta = new PelaajanValinta(nappi, kalib);
			holdem = new TexasHoldem(rotatoija, heittaja, jakaja, nappi, kalib);
			kaikki = new JaetaanKaikki(heittaja, jakaja, kalib);
			demo = new DiileriDemo(rotatoija, heittaja, jakaja, nappi, true);
			
			// Luodaan valitun pelin arbitrator
			switch (peli) {
			case 1:
				view.PokeriValikko();
				Behavior[] poker = { pelaajat, pokeri, valinta };
				
				System.out.println("Poker");
				
				arby = new Arbitrator(poker, returnWhenInactive);
				break;

			case 2:
				view.JaaKaikkiValikko();
				Behavior[] jaaKaikki = { pelaajat, kaikki };
				rotatoija.setSpeed(120);

				System.out.println("Jaa Kaikki");

				arby = new Arbitrator(jaaKaikki, returnWhenInactive);
				break;

			case 3:
				view.HoldemValikko();
				Behavior[] texasholdem = { pelaajat, holdem };
				System.out.println("HoldEm");

				
				arby = new Arbitrator(texasholdem, returnWhenInactive);
				break;

			case 4:
				view.DemoValikko();
				Behavior[] diileridemo = { demo };
				System.out.println("Diileridemo");
				
				arby = new Arbitrator(diileridemo, returnWhenInactive);
				break;

			}

			// arbitraattorin käynnistys ja nappien sulkeminen
			System.out.println("Arby.start");
			view.EiVoiPainaa();
			arby.start();
			System.out.println("Arby loppu!");
			view.VoiPainaa();
			view.setUudestaan();
			// Pelataanko uudestaan
			while (!loopz) {
				loopz = view.getUudestaan();
				if (loopz) {
					System.out.println("iffissä: ");
					uusiPeli = true;
					break;
				}
			}
			loopz = false;
			view.setUudestaan();
			
			System.out.println("main lopussa " + uusiPeli);
			// Nollataan muuttujat
			nollaaMuuttujat();
			rotatoija.setSpeed(100);

		} while (uusiPeli);
		System.out.println("Main - stoppable = true ja nyt close ja rekt");
		lopeta();
	}

	public static void lopeta() throws RemoteException {
		System.out.println("Lopetetaan moottorit ja sensorit");
		yhteys = null;
		nappi.close();
		cs.close();
		rotatoija.close();
		heittaja.close();
		jakaja.close();
		System.exit(0);
	}

	public static void nollaaMuuttujat() {
		arby = null;
		pelaajat = null;
		pokeri = null;
		valinta = null;
		holdem = null;
		kaikki = null;
		kalib.nollaaMuuttujat();
	}

}