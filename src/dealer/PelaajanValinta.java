package dealer;

import lejos.hardware.Sound;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;

public class PelaajanValinta implements Behavior {

	private volatile boolean suppressed = false;
	private int pelaajamaara;
	SampleProvider painallus;
	private long click, click2, painalluksenPituus;
	float[] näyte = new float[1];
	private int korttienLkm, vuoro;
	
	
	public PelaajanValinta(EV3TouchSensor nappi, Kalibrointi kalib) {
		this.painallus = nappi.getTouchMode();
		pelaajamaara = kalib.getPelaajamaara();
		vuoro = 0;
	}

	@Override
	public boolean takeControl() {
		// jos alkukortit on jaettu, voidaan siirtyä tänne
			if (Pokeri.alkukortitJaettu) {
				return SeuraavaPelaaja.kohdalla;
			} else {
				return false;
			}

	}

	@Override
	public void action() {
		SeuraavaPelaaja.kohdalla = false;
		korttienLkm = 0;
		
		if ((vuoro + 1) == pelaajamaara) {
			SeuraavaPelaaja.kohdalla = false;
			SeuraavaPelaaja.pelissä = false;
			Pokeri.viimeinenPelaaja = true;
		}
		
		System.out.println("PELAAJAN VALINNASSA");
		while (true) {
			while (näyte[0] == 0) {
				painallus.fetchSample(näyte, 0);
			}
			//painallus alkaa tästä
			click = System.currentTimeMillis();
			while (näyte[0] == 1) {
				painallus.fetchSample(näyte, 0);
			}
			click2 = System.currentTimeMillis();
			painalluksenPituus = click2 - click;

			try {
				Thread.sleep(50);
			} catch (Exception e) {}
			
			
			// jos painalluksen pituus on alle 250, se lasketaan kortiksi
			// jos yli, passataan
			if (painalluksenPituus < 250) {
				Sound.playTone(900, 50);
				korttienLkm++;
				Pokeri.jaa = true;
				Pokeri.korttimaara = korttienLkm;
				//System.out.println("KORTTIEN MAARA: " + Pokeri.korttimaara);
				if (Pokeri.korttimaara >= 4) {
					vuoro++;
					System.out.println("Vuoro jaettu: " + vuoro);
					break;
				}
			} else {
				vuoro++;
				System.out.println("Vuoro jaettu: " + vuoro);				
				break;
			}
		}
		Sound.playTone(200, 200);
		suppress();
		
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
}
