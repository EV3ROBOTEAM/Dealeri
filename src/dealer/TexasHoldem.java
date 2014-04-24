package dealer;

import java.rmi.RemoteException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;
import lejos.robotics.SampleProvider;
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
	private RMIRegulatedMotor jakaja;
	private RMIRegulatedMotor heittaja;
	public static int pelaajamaara;
	int[] jaettu = new int[pelaajamaara];
	public static boolean alkukortitJaettu;
	public static boolean jaettuHoldem = false;

	int jakolkm = 1;
	int z = 0;
	int tarkastus = 0;
	SampleProvider painallus;
	private long click, click2, painalluksenPituus;
	float[] n�yte = new float[1];
	// supressed flagi
	private volatile boolean suppressed = false;
	// konstruktori	
	public TexasHoldem(RMIRegulatedMotor heittaja2, RMIRegulatedMotor jakaja2, EV3TouchSensor nappi) {
		heittaja = heittaja2;
		jakaja = jakaja2;
		this.painallus = nappi.getTouchMode();
	}

	@Override
	public boolean takeControl() {
		// jos alkukortit on jaettu, t�nne p��see ainoastaan 
		// pelaajanValinta-luokasta
		return SeuraavaPelaaja.kohdalla;
	}

	@Override
	public void action() {
		suppressed = false;
		SeuraavaPelaaja.kohdalla = false;
		System.out.println("action hem");
		
		if(!jaettuHoldem && !suppressed && jakolkm <= (2*pelaajamaara)){
			System.out.println("jaetaan yhdelle pelaajalle 1 kortti...");
			jaaKortti(1);

			jakolkm++;
			suppress();
		}
		
		jaettuHoldem = true;
		System.out.println("JAETTUHOLDEM ======= TRUE   BLOW IT OUT URE ASS!!!");
		System.out.println("Jaettu kaikille");
		System.out.println("Panostus");
		odotaPanostusta();
		//poltetaan
		
		System.out.println("POLTETAAN");
		jaaKortti(1);
		
		
		//jaetaan 3 p�yd�lle
		System.out.println("Jaetaan 3 p�yd�lle");
		jaaKortti(3);
		System.out.println("Panostus");
		odotaPanostusta();
		
		
		//poltetaan
		System.out.println("POLTETAAN");
		jaaKortti(1);

		//jako
		System.out.println("Jaetaan 1 p�yd�lle");
		jaaKortti(1);
		System.out.println("Panostus");
		odotaPanostusta();
		System.out.println("POLTETAAN");

		//poltetaan
		System.out.println("POLTETAAN");
		jaaKortti(1);
		//jako
		System.out.println("Jaetaan 1 p�yd�lle");
		jaaKortti(1);
		
		suppress();
		
	}

	@Override
	public void suppress() {
		System.out.println("holdem suppress");
		suppressed = true;
	}
	
	public void jaaKortti(int maara) {
		for (int i = 0; i < maara; i++) {
			try {
				heittaja.backward();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jakaja.rotate(-300);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				heittaja.stop(true);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				jakaja.rotate(200);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void tarkastus() {
		jaettu[z] += 1;
		// katsotaan ollaanko viimeisess� pelaajassa
		if (z+1 >= pelaajamaara) {
			z = 0;
			System.out.println("NOLLATTU");
		} else {
			// jos ei, siirryt��n seuraavaan pelaajaan
			z++;
		}
		
		// tarkastetaan onko kaikille jaettu 5 korttia
		for (int g = 0; g < jaettu.length; g++) {
			if (jaettu[g] == 2){
				tarkastus++;
			}
		}
				
		// jos on fl�g�t��n lis�kortit
		if (tarkastus == jaettu.length) {
			SeuraavaPelaaja.kohdalla = true;
			alkukortitJaettu = true;
		} else {
			tarkastus = 0;
		}
	}
		
	public void odotaPanostusta(){
		n�yte[0] = 0;
		while(true){
			while (n�yte[0] == 0) {
			painallus.fetchSample(n�yte, 0);
			}
			break;
		}
	}
}


