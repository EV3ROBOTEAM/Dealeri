package dealer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.robotics.subsumption.Behavior;

public class Pokeri implements Behavior{
	// moottorit
	private EV3LargeRegulatedMotor jakaja;
	private EV3MediumRegulatedMotor heittaja;
	public static int pelaajamaara;
	int[] jaettu = new int[pelaajamaara];
	public static boolean alkukortitJaettu;
	public static boolean jaa;
	public static int korttimaara = 1;
	int z = 0;
	int tarkastus = 0;
	// supressed flagi
	private volatile boolean suppressed = false;
		
	public Pokeri(EV3MediumRegulatedMotor h, EV3LargeRegulatedMotor j) {
		heittaja = h;
		jakaja = j;
	}

	@Override
	public boolean takeControl() {
		// jos alkukortit on jaettu, t‰nne p‰‰see ainoastaan 
		// pelaajanValinta-luokasta
		if (alkukortitJaettu) {
			
			return jaa;
		} else {
			return SeuraavaPelaaja.kohdalla;
		}
	}

	@Override
	public void action() {
		SeuraavaPelaaja.kohdalla = false;
		jaa = false;
		jaaKortti(korttimaara);
		// tarkastetaan, kenelle seuraava kortti jaetaa ja ett‰ onko alkukortit jaettu jo
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
		korttimaara = 1;
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
		
		// tarkastetaan onko kaikille jaettu 5 korttia
		for (int g = 0; g < jaettu.length; g++) {
			if (jaettu[g] == 2){
				tarkastus++;
			}
		}
				
		// jos on fl‰g‰t‰‰n lis‰kortit
		if (tarkastus == jaettu.length) {
			SeuraavaPelaaja.kohdalla = true;
			alkukortitJaettu = true;
		} else {
			tarkastus = 0;
		}
	}
	
}
