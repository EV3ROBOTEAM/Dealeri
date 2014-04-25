package dealer;

import java.rmi.RemoteException;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.EV3MediumRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.remote.ev3.RMIRegulatedMotor;
import lejos.remote.ev3.RMIRemoteRegulatedMotor;
import lejos.robotics.subsumption.Behavior;

public class Pokeri implements Behavior{
	// moottorit
	private RMIRegulatedMotor jakaja;
	private RMIRegulatedMotor heittaja;
	private int pelaajamaara;
	private int i = 0, x;
	int[] jaettu;
	int[] lopetus;
	public static boolean alkukortitJaettu;
	public static boolean jaa;
	public static int korttimaara = 1;
	int z = 0;
	int tarkastus = 0;
	// supressed flagi
	private volatile boolean suppressed = false;
		
	public Pokeri(RMIRegulatedMotor heittaja2, RMIRegulatedMotor jakaja2, Kalibrointi kalib) {
		heittaja = heittaja2;
		jakaja = jakaja2;
		pelaajamaara = kalib.getPelaajamaara();
		jaettu = new int[pelaajamaara];
		lopetus = new int[pelaajamaara];
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
		
		/*if (alkukortitJaettu) {
			System.out.println("jaetaan pelaajalle: " + i);
			lopetus[i] += 1;
			for (int g = 0; g < lopetus.length; g++) {
				if (lopetus[g] == 0){
					x++;
				}
			}
			System.out.println(x + ", pelaajalle jaettu lis‰kortit");
			if (x == lopetus.length) {
				System.out.println("Kortit jaettu");
				SeuraavaPelaaja.kohdalla = false;
				alkukortitJaettu = false;
				SeuraavaPelaaja.peliss‰ = false;
			}
			x = 0;
			i++;
		}*/
		System.out.println(" PELAAJAMƒƒRƒ: " + pelaajamaara);
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
