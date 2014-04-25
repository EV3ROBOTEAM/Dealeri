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
	
	static EV3TouchSensor nappi;
	static HiTechnicCompass cs;
	static RMIRegulatedMotor rotatoija;
	static RMIRegulatedMotor jakaja;
	static RMIRegulatedMotor heittaja;
	
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

		
		RemoteEV3 yhteys = null;

		// REKTYHTEYS
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {}
		try {
			BrickInfo[] bricks = BrickFinder.discover();
			for (BrickInfo info : bricks) {
				System.out.println("Ev3 found on Bluetooth ip: "
						+ info.getIPAddress());
			}

			yhteys = new RemoteEV3(bricks[0].getIPAddress());

		} catch (Exception e) {
			lopeta();
			e.printStackTrace();
		}

		yhteys.setDefault();
		
		
		rotatoija = yhteys.createRegulatedMotor("A", 'L');
		jakaja = yhteys.createRegulatedMotor("B", 'L');
		heittaja = yhteys.createRegulatedMotor("C", 'M');


		boolean returnWhenInactive = true;

		// sensorien konstruktorit ja sampleprovierit
		nappi = new EV3TouchSensor(SensorPort.S1);
		cs = new HiTechnicCompass(SensorPort.S3);

		// moottorien nopeudet
		rotatoija.setSpeed(100);
		jakaja.setSpeed(720);
		heittaja.setSpeed(720);


		Arbitrator arby = null;
		int peli = 0;
		DiileriView view = new DiileriView();
		while (!view.getvoiAloittaa()) {
			peli = view.getValinta();
			System.out.println("loops");
		}
		
		Kalibrointi kalib = new Kalibrointi(rotatoija, nappi, cs);
		kalib.ezkalib();

		// haetaan ja tallennetaan sijainnit ja pelaajanro2
		float[] pelaajaSijainnit = kalib.sijainnit();

		// behaviorien konstruktorit
		Behavior pelaajat = new SeuraavaPelaaja(pelaajaSijainnit, rotatoija, cs, kalib);
		Behavior pokeri = new Pokeri(heittaja, jakaja, kalib);
		Behavior valinta = new PelaajanValinta(nappi, kalib);
		Behavior holdem = new TexasHoldem(heittaja, jakaja, nappi);
		Behavior kaikki = new JaetaanKaikki(heittaja, jakaja, kalib);
		boolean end;
		do {

			switch (peli) {
			case 1:
				Behavior[] poker = { pelaajat, pokeri, valinta };

				arby = new Arbitrator(poker, end = true);
				break;

			case 2:
				Behavior[] jaaKaikki = { pelaajat, kaikki };

				arby = new Arbitrator(jaaKaikki, returnWhenInactive);
				break;

			case 3:
				Behavior[] texasholdem = { pelaajat, holdem };

				arby = new Arbitrator(texasholdem);
				break;

			case 4:
				lopeta();
				System.exit(0);
				break;

			}

			System.out.println("Arby.start");
			// arbitraattori ja käynnistys

			arby.start();
			
			

			System.out.println("main lopussa");

			//kalib.nollaaMuuttujat();
			
			yhteys = null;
			view.setStoppable();

		} while (!view.getStoppable());
		System.out.println("Main - stoppable = true ja nyt close ja rekt");

		lopeta();
		System.exit(0);
		// nayton valinta hommasta return int joka menee jako- ja
		// pelaajaluokkien konstruktoreihin

		/*
		 * pelin lopetus, haluatko pelata uudestaan? (k/e) valitse peli
		 */
	}
	
	public static void lopeta() throws RemoteException {
		System.out.println("Lopetetaan moottorit ja sensorit");
		nappi.close();
		cs.close();
		rotatoija.close();
		heittaja.close();
		jakaja.close();
	}

}