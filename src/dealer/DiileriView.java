package dealer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

class DiileriView extends JFrame {
	// napit
	private BufferedImage pokerikuva, holdemkuva, jaakaikkikuva, demokuva,
			takaisinkuva, uudelleenkuva;
	private BufferedImage pokeripainettu, holdempainettu, jaakaikkipainettu,
			demopainettu, uudelleenpainettu, uudelleenmusta;
	private JButton pokerinappi, holdemnappi, jaakaikkinappi, demonappi,
			takaisin, uudelleen;
	//numerot
	private  JButton numeroalusta;
	private  BufferedImage yksi;
	private  BufferedImage kaksi;
	private  BufferedImage kolme;
	private  BufferedImage nelja;
	private  BufferedImage viisi;
	private  BufferedImage kuusi;
	private  BufferedImage seits;
	private  BufferedImage kasi;
	private  BufferedImage ysi;
	private  BufferedImage nolla;
	
	// tekstit
	private BufferedImage valpel, aloita, lopeta, sjainnit;
	private BufferedImage aloitapainettu, lopetapainettu;
	private BufferedImage pokerititle, jaakaikkititle, holdemtitle, demotitle,
			kalibrointitle, yhdistystitle;
	private JButton valpelnappi, aloitanappi, lopetanappi, sjainnitnappi;
	private JButton poktitle, jktitle, holdtitle, dmtitle, kalibtitle,
			yhdtitle;
	// panelit
	private JLabel background;
	private JLabel otsikkopaneeli;
	private JLabel pelipaneeli;
	private JLabel kaynnistyspaneeli;
	// äänet
	private AudioFormat audioFormat;
	private AudioInputStream audioInputStream;
	private SourceDataLine sourceDataLine;
	// liput & muut
	private boolean started = false;
	private volatile boolean voiAloittaa = false;
	private boolean stoppable = false;
	private volatile boolean uudestaanjee = false;
	private volatile boolean retrynappi = false;
	private static int kasvata = 0;
	// mihin menuun mennään
	private int valinta;

	public DiileriView() {

		setTitle("Dealer");
		setSize(1200, 800);
		setLocationRelativeTo(null);
		// setDefaultCloseOperation(EXIT_ON_CLOSE);

		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent evt) {
				try {
					Paaohjelma.lopeta();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		setVisible(true);
		setLayout(new GridLayout()); // GridLayout(int rows, int cols)

		otsikkopaneeli = new JLabel();
		pelipaneeli = new JLabel();
		kaynnistyspaneeli = new JLabel();
		background = new JLabel(new ImageIcon("taustakuva.jpg"));

		// napit, tekstit ja kuvat
		teeNapit();
		teeTekstit();
		teeNumerot();

		// Kuuntelijat
		Kuuntelut();

		// Päävalikko
		LatausNaytto();

		// voiko näkymän kokoa muuttaa
		// this.setResizable(false);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void LatausNaytto() {
		System.out.println("Ladataan...");
		// remove(background);
		setSize(1199, 799);
		// pokeri title
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.setLayout(new FlowLayout());

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(yhdtitle);

		// alimmainen paneeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new GridLayout(0, 2));

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);
		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799);
		setSize(1200, 800);
	}

	public void kasvataNumeroa() {
		Aanieffekti(1);
		kasvata++;
		if (kasvata == 1) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(yksi))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 2) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(kaksi))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 3) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(kolme))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 4) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(nelja))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 5) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(viisi))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 6) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(kuusi))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 7) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(seits))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 8) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(kasi))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}
		if (kasvata == 9) {
			numeroalusta.setIcon((Icon) new ImageIcon(((new ImageIcon(ysi))
					.getImage()).getScaledInstance(200, 250,
							java.awt.Image.SCALE_SMOOTH)));
		}

	}

	public void Paavalikko() {
		NollaaElementit();
		// ylin paneeli
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.setLayout(new FlowLayout());
		otsikkopaneeli.add(valpelnappi);

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(pokerinappi);
		pelipaneeli.add(jaakaikkinappi);
		pelipaneeli.add(holdemnappi);
		pelipaneeli.add(demonappi);

		// alin paneeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new FlowLayout());
		kaynnistyspaneeli.add(aloitanappi);
		kaynnistyspaneeli.add(lopetanappi);

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);
		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void PokeriValikko() {
		remove(background);
		setSize(1199, 799);
		// pokeri title
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.setLayout(new FlowLayout());
		otsikkopaneeli.add(poktitle);

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(uudelleen);

		// alimmainen paneeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new FlowLayout());
		kaynnistyspaneeli.add(lopetanappi);

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);
		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void JaaKaikkiValikko() {

		remove(background);
		setSize(1199, 799);
		// title
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.setLayout(new FlowLayout());
		otsikkopaneeli.add(jktitle);

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(uudelleen);

		// alin paneeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new FlowLayout());
		kaynnistyspaneeli.add(lopetanappi);

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);

		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void HoldemValikko() {

		remove(background);
		setSize(1199, 799);
		// title
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.setLayout(new FlowLayout());
		otsikkopaneeli.add(holdtitle);

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(uudelleen);

		// alin paneeeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new FlowLayout());
		kaynnistyspaneeli.add(lopetanappi);

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);

		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void DemoValikko() {

		remove(background);
		setSize(1199, 799);
		// title
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.setLayout(new FlowLayout());
		otsikkopaneeli.add(dmtitle);

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(uudelleen);

		// alin paneeeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new FlowLayout());
		kaynnistyspaneeli.add(lopetanappi);

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);

		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void KalibrointiValikko() {
		System.out.println("Kalibmenu");
		remove(background);
		setSize(1199, 799);
		// pokeri title
		otsikkopaneeli = new JLabel();
		otsikkopaneeli.add(kalibtitle);
		otsikkopaneeli.setLayout(new FlowLayout());

		// keskimmäinen paneeli
		pelipaneeli = new JLabel();
		pelipaneeli.setLayout(new FlowLayout());
		pelipaneeli.add(numeroalusta);

		// alimmainen paneeli
		kaynnistyspaneeli = new JLabel();
		kaynnistyspaneeli.setLayout(new FlowLayout());

		// taustakuva
		background = new JLabel(new ImageIcon("taustakuva.jpg"));
		add(background);
		background.setLayout(new GridLayout(3, 0)); // GridLayout(int rows, int
													// cols)
		background.add(otsikkopaneeli);
		background.add(pelipaneeli);
		background.add(kaynnistyspaneeli);
		setSize(1199, 799); // varmistetaan että komponentit päivittyvät
		setSize(1200, 800);
	}

	public void Kuuntelut() {
		// BUTTON LISTENERIT, PITÄÄ EHKÄ VAIHTAA LUOKAKSI
		pokerinappi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("POKERI");
				IconiMuuttaja(1);
				Aanieffekti(1);
			}
		});
		jaakaikkinappi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("JAAKAIKKI");
				IconiMuuttaja(2);
				Aanieffekti(1);
			}
		});
		holdemnappi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("HOLDEM");
				IconiMuuttaja(3);
				Aanieffekti(1);
			}
		});
		demonappi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("DEMO");
				IconiMuuttaja(4);
				Aanieffekti(1);
			}
		});
		aloitanappi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (started) {
					System.out.println("ALOITA");
					Aanieffekti(2);
					// Valinta(valinta);
					voiAloittaa = true;
				}
			}
		});
		lopetanappi.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("QUIT");
				try {
					Paaohjelma.lopeta();
				} catch (RemoteException e1) {
				}

				System.exit(0);
				Aanieffekti(1);
				stoppable = true;
			}
		});
		takaisin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("back in time");
				Aanieffekti(2);
				NollaaElementit();
				Paavalikko();
			}
		});
		uudelleen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (retrynappi) {
					uudestaanjee = true;
					System.out.println("uudestaan");
					Aanieffekti(2);
				} else {
					Aanieffekti(4);
				}
			}
		});

	}

	public void Valinta(int i) {

		if (i == 1) {
			PokeriValikko();
		}
		if (i == 2) {
			JaaKaikkiValikko();
		}
		if (i == 3) {
			HoldemValikko();
		}
		if (i == 4) {
			DemoValikko();
		}
	}

	public void nollaaRetry() {
		this.kasvata = 0;
	}
	
	public int getValinta() {
		return valinta;
	}

	public boolean getUudestaan() {
		return uudestaanjee;
	}

	public void setUudestaan() {
		uudestaanjee = false;
	}

	public void NollaaElementit() {
		// Pitää nollata näkymien välissä
		remove(background);
		setSize(1199, 799);
	}

	public void IconiMuuttaja(int i) {
		started = true;
		valinta = i;
		if (i == 1) {
			// pokeri painettu
			pokerinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(
					pokeripainettu)).getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
			jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(
					jaakaikkikuva)).getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
			holdemnappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(
							240, 160, java.awt.Image.SCALE_SMOOTH)));
			demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demokuva))
					.getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
		}
		if (i == 2) {
			// jaakaikki painettu
			pokerinappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(
							240, 160, java.awt.Image.SCALE_SMOOTH)));
			jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(
					jaakaikkipainettu)).getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
			holdemnappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(
							240, 160, java.awt.Image.SCALE_SMOOTH)));
			demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demokuva))
					.getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
		}
		if (i == 3) {
			// holdem painettu

			pokerinappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(
							240, 160, java.awt.Image.SCALE_SMOOTH)));
			jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(
					jaakaikkikuva)).getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
			holdemnappi.setIcon((Icon) new ImageIcon(((new ImageIcon(
					holdempainettu)).getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
			demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demokuva))
					.getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
		}
		if (i == 4) {
			// demo painettu
			pokerinappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(
							240, 160, java.awt.Image.SCALE_SMOOTH)));
			jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(
					jaakaikkikuva)).getImage()).getScaledInstance(240, 160,
					java.awt.Image.SCALE_SMOOTH)));
			holdemnappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(
							240, 160, java.awt.Image.SCALE_SMOOTH)));
			demonappi.setIcon((Icon) new ImageIcon(
					((new ImageIcon(demopainettu)).getImage())
							.getScaledInstance(240, 160,
									java.awt.Image.SCALE_SMOOTH)));
		}
		aloitanappi.setIcon((Icon) new ImageIcon(
				((new ImageIcon(aloitapainettu)).getImage()).getScaledInstance(
						297, 85, java.awt.Image.SCALE_SMOOTH)));
	}

	public boolean getvoiAloittaa() {
		return voiAloittaa;
	}

	public void setvoiAloittaa() {
		this.voiAloittaa = false;
	}

	public boolean getStoppable() {
		return stoppable;
	}

	public void setStoppable() {
		stoppable = true;
	}

	public void teeTekstit() {
		try {

			// teksit
			valpel = ImageIO.read(new File("valitsepeli_title.png"));
			aloita = ImageIO.read(new File("Aloita.png"));
			sjainnit = ImageIO.read(new File("sijainnit_title.png"));
			lopeta = ImageIO.read(new File("Lopeta_painettu.png"));

			// "painetut" tekstit
			aloitapainettu = ImageIO.read(new File("Aloita_painettu.png"));
			lopetapainettu = ImageIO.read(new File("Lopeta_painettu.png"));

			// titlet
			pokerititle = ImageIO.read(new File("Pokeri_title.png"));
			jaakaikkititle = ImageIO.read(new File("JaaKaikki_title.png"));
			holdemtitle = ImageIO.read(new File("TexasHoldEm_title.png"));
			demotitle = ImageIO.read(new File("DiileriDemo_title.png"));
			yhdistystitle = ImageIO.read(new File("Yhdistetaan_notice.png"));

			// kalibroi
			kalibrointitle = ImageIO.read(new File("Kalibroidaan_notice.png"));

		} catch (IOException e) {
			System.out.println("Kuvanlataus ei onnistunut.");
			System.out
					.println("Kato että polut kuviin on oikein, tai että kuvat on projektikansion juuressa.");
			try {
				Paaohjelma.lopeta();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		// SIVUJEN TITLET JA SKAALAUS
		poktitle = new JButton(new ImageIcon(
				((new ImageIcon(pokerititle)).getImage()).getScaledInstance(
						326, 100, java.awt.Image.SCALE_SMOOTH)));
		poktitle.setBorderPainted(false);
		poktitle.setFocusPainted(false);
		poktitle.setContentAreaFilled(false);

		jktitle = new JButton(new ImageIcon(
				((new ImageIcon(jaakaikkititle)).getImage()).getScaledInstance(
						496, 100, java.awt.Image.SCALE_SMOOTH)));
		jktitle.setBorderPainted(false);
		jktitle.setFocusPainted(false);
		jktitle.setContentAreaFilled(false);

		holdtitle = new JButton(new ImageIcon(
				((new ImageIcon(holdemtitle)).getImage()).getScaledInstance(
						496, 100, java.awt.Image.SCALE_SMOOTH)));
		holdtitle.setBorderPainted(false);
		holdtitle.setFocusPainted(false);
		holdtitle.setContentAreaFilled(false);

		dmtitle = new JButton(new ImageIcon(
				((new ImageIcon(demotitle)).getImage()).getScaledInstance(496,
						100, java.awt.Image.SCALE_SMOOTH)));
		dmtitle.setBorderPainted(false);
		dmtitle.setFocusPainted(false);
		dmtitle.setContentAreaFilled(false);

		kalibtitle = new JButton(new ImageIcon(
				((new ImageIcon(kalibrointitle)).getImage()).getScaledInstance(
						637, 85, java.awt.Image.SCALE_SMOOTH)));
		kalibtitle.setBorderPainted(false);
		kalibtitle.setFocusPainted(false);
		kalibtitle.setContentAreaFilled(false);

		yhdtitle = new JButton(new ImageIcon(
				((new ImageIcon(yhdistystitle)).getImage()).getScaledInstance(
						637, 85, java.awt.Image.SCALE_SMOOTH)));
		yhdtitle.setBorderPainted(false);
		yhdtitle.setFocusPainted(false);
		yhdtitle.setContentAreaFilled(false);

		// TEHDÄÄN JA SKAALTAAN
		valpelnappi = new JButton(new ImageIcon(
				((new ImageIcon(valpel)).getImage()).getScaledInstance(539, 85,
						java.awt.Image.SCALE_SMOOTH)));
		valpelnappi.setBorderPainted(false);
		valpelnappi.setFocusPainted(false);
		valpelnappi.setContentAreaFilled(false);

		aloitanappi = new JButton(new ImageIcon(
				((new ImageIcon(aloita)).getImage()).getScaledInstance(297, 85,
						java.awt.Image.SCALE_SMOOTH)));
		aloitanappi.setBorderPainted(false);
		aloitanappi.setFocusPainted(false);
		aloitanappi.setContentAreaFilled(false);

		sjainnitnappi = new JButton(new ImageIcon(
				((new ImageIcon(sjainnit)).getImage()).getScaledInstance(411,
						99, java.awt.Image.SCALE_SMOOTH)));
		sjainnitnappi.setBorderPainted(false);
		sjainnitnappi.setFocusPainted(false);
		sjainnitnappi.setContentAreaFilled(false);

		lopetanappi = new JButton(new ImageIcon(
				((new ImageIcon(lopeta)).getImage()).getScaledInstance(297, 85,
						java.awt.Image.SCALE_SMOOTH)));
		lopetanappi.setBorderPainted(false);
		lopetanappi.setFocusPainted(false);
		lopetanappi.setContentAreaFilled(false);

	}

	public void VoiPainaa() {
		uudelleen.setIcon((Icon) new ImageIcon(((new ImageIcon(uudelleenkuva))
				.getImage()).getScaledInstance(270, 170,
				java.awt.Image.SCALE_SMOOTH)));
		Aanieffekti(3);
		retrynappi = true;
	}

	public void EiVoiPainaa() {
		uudelleen.setIcon((Icon) new ImageIcon(((new ImageIcon(uudelleenmusta))
				.getImage()).getScaledInstance(270, 170,
				java.awt.Image.SCALE_SMOOTH)));
	}

	public void teeNapit() {
		try {
			// napit
			pokerikuva = ImageIO.read(new File("Pokeri.png"));
			jaakaikkikuva = ImageIO.read(new File("Jaakaikki.png"));
			holdemkuva = ImageIO.read(new File("TexasHoldEm.png"));
			demokuva = ImageIO.read(new File("Diileridemo.png"));
			uudelleenkuva = ImageIO.read(new File("PelaaUudelleen.png"));
			uudelleenmusta = ImageIO.read(new File("PelaaUudelleen_musta.png"));
			// napit painettu
			pokeripainettu = ImageIO.read(new File("Pokeri_painettu.png"));
			jaakaikkipainettu = ImageIO
					.read(new File("Jaakaikki_painettu.png"));
			holdempainettu = ImageIO.read(new File("TexasHoldEm_painettu.png"));
			demopainettu = ImageIO.read(new File("Diileridemo_painettu.png"));
			uudelleenpainettu = ImageIO.read(new File(
					"PelaaUudelleen_pressed.png"));
			// takaisinpäin
			takaisinkuva = ImageIO.read(new File("PienempiKuin_painettu.png"));

		} catch (IOException e) {
			System.out.println("Kuvanlataus ei onnistunut.");
			System.out.println("Kato että polut kuviin on oikein.");
			try {
				Paaohjelma.lopeta();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}

		// TEHDÄÄN NAPIT JA SKAALATAAN NE
		pokerinappi = new JButton(new ImageIcon(
				((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(240,
						160, java.awt.Image.SCALE_SMOOTH)));
		pokerinappi.setBorderPainted(false);
		pokerinappi.setFocusPainted(false);
		pokerinappi.setContentAreaFilled(false);

		holdemnappi = new JButton(new ImageIcon(
				((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(240,
						160, java.awt.Image.SCALE_SMOOTH)));
		holdemnappi.setBorderPainted(false);
		holdemnappi.setFocusPainted(false);
		holdemnappi.setContentAreaFilled(false);

		jaakaikkinappi = new JButton(new ImageIcon(((new ImageIcon(
				jaakaikkikuva)).getImage()).getScaledInstance(240, 160,
				java.awt.Image.SCALE_SMOOTH)));
		jaakaikkinappi.setBorderPainted(false);
		jaakaikkinappi.setFocusPainted(false);
		jaakaikkinappi.setContentAreaFilled(false);

		demonappi = new JButton(new ImageIcon(
				((new ImageIcon(demokuva)).getImage()).getScaledInstance(240,
						160, java.awt.Image.SCALE_SMOOTH)));
		demonappi.setBorderPainted(false);
		demonappi.setFocusPainted(false);
		demonappi.setContentAreaFilled(false);

		uudelleen = new JButton(new ImageIcon(
				((new ImageIcon(uudelleenmusta)).getImage()).getScaledInstance(
						270, 170, java.awt.Image.SCALE_SMOOTH)));
		uudelleen.setBorderPainted(false);
		uudelleen.setFocusPainted(false);
		uudelleen.setContentAreaFilled(false);

		takaisin = new JButton(new ImageIcon(
				((new ImageIcon(takaisinkuva)).getImage()).getScaledInstance(
						120, 100, java.awt.Image.SCALE_SMOOTH)));
		takaisin.setBorderPainted(false);
		takaisin.setFocusPainted(false);
		takaisin.setContentAreaFilled(false);
	}

	public void teeNumerot() {
		try {
			// numerot
			yksi = ImageIO.read(new File("yksi.gif"));
			kaksi = ImageIO.read(new File("kaksi.gif"));
			kolme = ImageIO.read(new File("kolme.gif"));
			nelja = ImageIO.read(new File("nelja.gif"));
			viisi = ImageIO.read(new File("viiis.gif"));
			kuusi = ImageIO.read(new File("kuus.gif"));
			seits = ImageIO.read(new File("setis7.gif"));
			kasi = ImageIO.read(new File("kasi.gif"));
			ysi = ImageIO.read(new File("ysi.gif"));
			nolla = ImageIO.read(new File("nolla.gif"));
		} catch (IOException e) {
			System.out.println("Kuvanlataus ei onnistunut.");
			System.out.println("Kato että polut kuviin on oikein.");
			try {
				Paaohjelma.lopeta();
			} catch (RemoteException e1) {}
		}

		//teko ja skaalaus
		numeroalusta = new JButton(new ImageIcon(((new ImageIcon(nolla)).getImage()).getScaledInstance(200,250, java.awt.Image.SCALE_SMOOTH)));
		numeroalusta.setBorderPainted(false);
		numeroalusta.setFocusPainted(false);
		numeroalusta.setContentAreaFilled(false);

	}

	
	private void Aanieffekti(int i) {
		File soundFile = null;
		try {
			if (i == 1) {
				soundFile = new File("nappi.wav");
			}
			if (i == 2) {
				soundFile = new File("select.wav");
			}
			if (i == 3) {
				soundFile = new File("chime.wav");
			}
			if (i == 4) {
				soundFile = new File("locks.wav");
			}

			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			audioFormat = audioInputStream.getFormat();

			DataLine.Info dataLineInfo = new DataLine.Info(
					SourceDataLine.class, audioFormat);

			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);

			new MusaSaie().start();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}

	class MusaSaie extends Thread {

		byte tempBuffer[] = new byte[10000];

		public void run() {
			try {
				sourceDataLine.open(audioFormat);
				sourceDataLine.start();
				int cnt;
				while ((cnt = audioInputStream.read(tempBuffer, 0,
						tempBuffer.length)) != -1) {
					if (cnt > 0) {
						sourceDataLine.write(tempBuffer, 0, cnt);
					}
				}
				sourceDataLine.drain();
				sourceDataLine.close();
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(0);
			}
		}
	}
}
