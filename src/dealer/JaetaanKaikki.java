package dealer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.robotics.subsumption.Behavior;

public class JaetaanKaikki implements Behavior{
	// moottorit
	private EV3LargeRegulatedMotor jakaja;
	private EV3MediumRegulatedMotor heittaja;
	public static int pelaajamaara;
	int[] jaettu = new int[pelaajamaara];
	int z = 0;
	int tarkastus = 0;
	// supressed flagi
	private volatile boolean suppressed = false;
		
	public JaetaanKaikki(EV3MediumRegulatedMotor h, EV3LargeRegulatedMotor j) {
		heittaja = h;
		jakaja = j;
	}

	@Override
	public boolean takeControl() {
		return SeuraavaPelaaja.kohdalla;
	}

	@Override
	public void action() {
		SeuraavaPelaaja.kohdalla = false;
		int korttimaara = (int) (52 / pelaajamaara);
		jaaKortti(korttimaara);
		// tarkastetaan kenelle seuraava kortti jaetaa ja ett‰ onko alkukortit jaettu jo
		tarkastus();

		suppress();
	}

	@Override
	public void suppress() {
		suppressed = true;		
	}

	public void jaaKortti(int maara) {
		for (int i = 0; i < maara; i++) {
			heittaja.backward();
			jakaja.rotate(-300);
			heittaja.stop();
			jakaja.rotate(200);
		}
	}
	public void tarkastus() {
		jaettu[z] += 1;
		// katsotaan ollaanko viimeisess‰ pelaajassa
		if (z+1 >= pelaajamaara) {
			z = 0;
			System.out.println("NOLLATTU");
		} else {
			// jos ei, siirryt‰‰n seuraavaan pelaajaan
			z++;
		}
		
		// tarkastetaan onko kaikki kortit jaettu
		for (int g = 0; g < jaettu.length; g++) {
			if (jaettu[g] == 1){
				tarkastus++;
			}
		}
				
		// jos on lopetetaan
		if (tarkastus == jaettu.length) {
			System.exit(0);
		}
	}
	
}
