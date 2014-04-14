package valikko;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.Font;
import lejos.hardware.lcd.GraphicsLCD;
import lejos.hardware.lcd.LCD;
import lejos.remote.ev3.RemoteGraphicsLCD;
import lejos.utility.TextMenu;

public class BasicValikko {

	static GraphicsLCD g = LocalEV3.get().getGraphicsLCD();
	final static int SW = g.getWidth();
	final static int SH = g.getHeight();
	final static int DELAY = 1000;
	static String[] pelit = { "Pokeri", "Paskahousu", "Swagger" };
	static TextMenu menu = new TextMenu(pelit, 1, "Pelit:");

	public static void main(String[] args) {
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

		int valinta = menu.select();

		System.out.println(valinta);
		LCD.drawInt(valinta, SW / 2, SH / 2);

		Button.waitForAnyPress(DELAY);
		g.clear();
		g.refresh();
	}
}