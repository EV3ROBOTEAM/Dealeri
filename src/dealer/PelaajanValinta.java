package dealer;

import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;
import lejos.robotics.subsumption.Behavior;

public class PelaajanValinta implements Behavior {

	private volatile boolean suppressed = false;
	public static int pelaajamaara;
	SampleProvider painallus;
	private long click, click2, painalluksenPituus;
	float[] n�yte = new float[1];
	private int korttienLkm, vuoro = 0;
	public PelaajanValinta(EV3TouchSensor nappi) {
		this.painallus = nappi.getTouchMode();
	}

	@Override
	public boolean takeControl() {
		// jos alkukortit on jaettu, voidaan siirty� t�nne
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
		
		System.out.println("PELAAJAN VALINNASSA");
		while (true) {
			while (n�yte[0] == 0) {
				painallus.fetchSample(n�yte, 0);
			}
			//painallus alkaa t�st�
			click = System.currentTimeMillis();
			while (n�yte[0] == 1) {
				painallus.fetchSample(n�yte, 0);
			}
			click2 = System.currentTimeMillis();
			painalluksenPituus = click2 - click;
			//System.out.println("painalluksenPituus: " + painalluksenPituus);

			try {
				Thread.sleep(50);
			} catch (Exception e) {}
			
			
			// jos painalluksen pituus on alle 250, se lasketaan kortiksi
			// jos yli, passataan
			if (painalluksenPituus < 250) {
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
		if (vuoro == pelaajamaara) {
			System.out.println("Kortit jaettu");
			SeuraavaPelaaja.kohdalla = false;
			Pokeri.alkukortitJaettu = false;
			SeuraavaPelaaja.peliss� = false;
		}
		suppress();
	}

	@Override
	public void suppress() {
		suppressed = true;
	}
}
