package dealer;


import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.HiTechnicCompass;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

public class Paaohjelma {
	public static void main(String[] args) {
		
		// moottorien konstruktorit
		EV3LargeRegulatedMotor rotatoija = new EV3LargeRegulatedMotor(MotorPort.A);
		EV3LargeRegulatedMotor jakaja = new EV3LargeRegulatedMotor(MotorPort.B);
		EV3MediumRegulatedMotor heittaja = new EV3MediumRegulatedMotor(MotorPort.C);

		// sensorien konstruktorit ja sampleprovierit
		EV3TouchSensor nappi = new EV3TouchSensor(SensorPort.S1);
		HiTechnicCompass cs = new HiTechnicCompass(SensorPort.S3);
		

		// moottorien nopeudet
		rotatoija.setSpeed(120);
		jakaja.setSpeed(720);
		heittaja.setSpeed(720);

		// pelaajien sijainnit ja kalibrointi, konstruktoriin moottor ja sensorit
		Kalibrointi kalib = new Kalibrointi(rotatoija, nappi, cs);
		kalib.ezkalib();
		
		// haetaan ja tallennetaan sijainnit ja pelaajanro2
		float[] pelaajaSijainnit = kalib.sijainnit();
		
		// behaviorien konstruktorit
		Behavior pelaajat = new SeuraavaPelaaja(pelaajaSijainnit, rotatoija, cs);
		Behavior pokeri = new Pokeri(heittaja, jakaja);
		Behavior valinta = new PelaajanValinta(nappi);
		Behavior holdem = new TexasHoldem(heittaja, jakaja);

		
		// arbitraattori ja k‰ynnistys
		Behavior [] kaytos = {pelaajat, pokeri, valinta};
		
		Arbitrator arby = new Arbitrator(kaytos);
		arby.start();

		// nayton valinta hommasta return int joka menee jako- ja
		// pelaajaluokkien konstruktoreihin
		
		/*
		 * miten pelaajat ja jakaminen riippuvat pelist‰? pidet‰‰n korteista
		 * lukua, jos liikaa pelaajia annetaan virhe ett‰ kortteja ei tarpeeksi.
		 * 
		 * pelin lopetus, haluatko pelata uudestaan? (k/e)
		 * valitse peli
		 */

	}
}
