package dealer;


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
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;
import lejos.utility.TextMenu;

public class Paaohjelma {
	
	public static void main(String[] args) {
		// Valikon muuttujat jne.
		String[] pelit = { "Pokeri", "Paskahousu", "Swagger" };
		TextMenu menu = new TextMenu(pelit, 1, "Pelit:");
		GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
		final int SW = g.getWidth();
		final int SH = g.getHeight();
		final int DELAY = 1000;
		
		
		
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

		// Valitaan peli
		g.clear();
		g.refresh();
		g.setFont(Font.getLargeFont());

		g.drawString("Valitse", SW / 2, SH / 2, GraphicsLCD.BASELINE
				| GraphicsLCD.HCENTER);
		Button.waitForAnyPress(DELAY);
		g.clear();
		g.refresh();
		g.drawString("Peli", SW / 2, SH / 2, GraphicsLCD.BASELINE
				| GraphicsLCD.HCENTER);
		g.refresh();
		Button.waitForAnyPress(DELAY);
		g.clear();

		int peli = menu.select();
		/* TODO Switch-case mink‰ mukaan tehd‰‰n tietynlainen arbitraattori
		 * 
		 */

		
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
		Behavior kaikki = new JaetaanKaikki(heittaja, jakaja);
		Arbitrator arby = null;

		switch (peli)
		{
		case 0: 
			Behavior [] poker = {pelaajat, pokeri, valinta};
			
			arby = new Arbitrator(poker);
			break;
			
		case 1:
			Behavior [] jaaKaikki = {pelaajat, kaikki};
			
			arby = new Arbitrator(jaaKaikki);
			break;
			
		}
		// arbitraattori ja k‰ynnistys

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
