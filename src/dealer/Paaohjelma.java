package dealer;

import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import lejos.hardware.Brick;
import lejos.hardware.BrickFinder;
import lejos.hardware.BrickInfo;
import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;
import lejos.remote.ev3.RemoteEV3;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.TextMenu;

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
	static Kalibrointi kalib;
	static boolean loopz = false;

	public Paaohjelma(String host) throws RemoteException,
			MalformedURLException, NotBoundException {
		super(host);
		// TODO Auto-generated constructor stub
	}

	public String ip() throws RemoteException, MalformedURLException,
			NotBoundException {
		RemoteEV3 yhteys = new RemoteEV3("10.0.1.1");
		return yhteys.getName();
	}

	public static void main(String[] args) throws RemoteException,
			MalformedURLException, NotBoundException {

		// REKTYHTEYS
		/*for (int i = 0; i < 10; i++) {
			System.out.println("yhteyskok: "+i);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e1) {
			}
			try {
				BrickInfo[] bricks = BrickFinder.discover();
				for (BrickInfo info : bricks) {
					System.out.println("Ev3 found on Bluetooth ip: "
							+ info.getIPAddress());
				}

				yhteys = new RemoteEV3(bricks[0].getIPAddress());

			} catch (Exception e) {
				System.out.println("ythteyttä ei löydetty");
				lopeta();
				e.printStackTrace();
			}

		}*/
		yhteys = new RemoteEV3("10.0.1.1");
		
		System.out.println(yhteys.getName());
		
		yhteys.setDefault();
		// sensorien konstruktorit ja sampleprovierit
		nappi = new EV3TouchSensor(SensorPort.S1);
		cs = new HiTechnicCompass(SensorPort.S3);

		rotatoija = yhteys.createRegulatedMotor("A", 'L');
		jakaja = yhteys.createRegulatedMotor("B", 'L');
		heittaja = yhteys.createRegulatedMotor("C", 'M');

		boolean returnWhenInactive = true;



		// moottorien nopeudet
		rotatoija.setSpeed(100);
		jakaja.setSpeed(720);
		heittaja.setSpeed(720);

		int peli = 0;
		DiileriView view = new DiileriView();
		while (!view.getvoiAloittaa()) {
			peli = view.getValinta();
		}
		System.out.println("PELI VALITTU");
		view.setvoiAloittaa();

		kalib = new Kalibrointi(rotatoija, nappi, cs);
		view.KalibrointiValikko();
		kalib.ezkalib();

		// haetaan ja tallennetaan sijainnit ja pelaajanro2
		float[] pelaajaSijainnit = kalib.sijainnit();

		// behaviorien konstruktorit

		boolean end;
		boolean uusiPeli = false;
		
		do {

			// jos uusiPeli on valittu trueksi
			// aloitetaan uudestaan kalibroimalla
			if (uusiPeli) {
				System.out.println("uudessaPelissä");
				view.Paavalikko();
				while (!view.getvoiAloittaa()) {
					peli = view.getValinta();
				}
				if (peli != 4) {
					kalib.ezkalib();
				}
				view.setvoiAloittaa();
			}

			uusiPeli = false;
			// tehdään uudet behaviorit uusilla kalibroinnin arvoilla
			
			pelaajat = new SeuraavaPelaaja(pelaajaSijainnit, rotatoija, cs,
					kalib);
			pokeri = new Pokeri(heittaja, jakaja, kalib);
			valinta = new PelaajanValinta(nappi, kalib);
			holdem = new TexasHoldem(rotatoija, heittaja, jakaja, nappi, kalib);
			kaikki = new JaetaanKaikki(heittaja, jakaja, kalib);
			
			System.out.println(peli);
			
			switch (peli) {
			case 1:
				view.PokeriValikko();
				Behavior[] poker = { pelaajat, pokeri, valinta };
				System.out.println("Poker");

				arby = new Arbitrator(poker, end = true);
				break;

			case 2:
				view.JaaKaikkiValikko();
				Behavior[] jaaKaikki = { pelaajat, kaikki };
				System.out.println("Jaa Kaikki");

				arby = new Arbitrator(jaaKaikki, returnWhenInactive);
				break;

			case 3:
				view.HoldemValikko();
				Behavior[] texasholdem = { pelaajat, holdem };
				System.out.println("HoldEm");

				
				arby = new Arbitrator(texasholdem, end = true);
				break;

			case 4:
				view.DemoValikko();
				// il,mari
				break;

			}

			// arbitraattorin käynnistys
			System.out.println("Arby.start");
			arby.start();
			System.out.println("Arby loppu!");
			// TODO
			// Kysytäänkö pelataanko uusi peli
			// jos pelataan, muutetaan uusiPeli trueksi
			view.VoiPainaa();

			while (!loopz) {
				loopz = view.getUudestaan();
				if (loopz) {
					System.out.println("iffissä: ");
					uusiPeli = true;
					break;
				}
			}
			loopz = false;

			System.out.println("main lopussa " + uusiPeli);
			// Nollataan muuttujat
			nollaaMuuttujat();
			view.NollaaElementit();

			// yhteys = null;
			view.setStoppable();

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