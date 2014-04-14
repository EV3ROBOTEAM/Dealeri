package dealer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.robotics.subsumption.Behavior;

//Holdem s��nn�t:
/*
 * 1. Kaikille 2 korttia.
 * 2. Odotetaan ett� panostettu.
 * 3. Poltetaan  kortti.
 * 4. Jaetaan 3 korttia p�yd�lle.
 * 5. Odotetaan ett� panostettu.
 * 6. Poltetaan  kortti.
 * 7. Jaetaan 1 kortti. p�yd�lle.
 * 8. Odotetaan ett� panostettu.
 * 9. Poltetaan kortti.
 * 10.Jaetaan 1 kortti p�yd�lle.
 */
public class TexasHoldem implements Behavior {
	// moottorit
	private EV3LargeRegulatedMotor jakaja;
	private EV3MediumRegulatedMotor heittaja;
	
	// konstruktori	
	public TexasHoldem(EV3MediumRegulatedMotor h, EV3LargeRegulatedMotor j) {
		heittaja = h;
		jakaja = j;
	}

	@Override
	public boolean takeControl() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void action() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void suppress() {
		// TODO Auto-generated method stub
		
	}
	
	public void jaaKortti(int maara) {
		for (int i = 0; i < maara; i++) {
			heittaja.backward();
			jakaja.rotate(-300);
			heittaja.stop();
			jakaja.rotate(200);
		}
	}
}
