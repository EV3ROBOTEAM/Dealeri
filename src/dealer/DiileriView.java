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
    //napit
    private BufferedImage pokerikuva, holdemkuva, jaakaikkikuva, demokuva, takaisinkuva, uudelleenkuva;
    private BufferedImage pokeripainettu, holdempainettu, jaakaikkipainettu, demopainettu, uudelleenpainettu;
    private JButton pokerinappi, holdemnappi, jaakaikkinappi, demonappi, takaisin, uudelleen;
    //tekstit
    private BufferedImage valpel, aloita, lopeta, sjainnit;
    private BufferedImage aloitapainettu, lopetapainettu;
    private BufferedImage pokerititle, jaakaikkititle, holdemtitle, demotitle, kalibrointitle;
    private JButton valpelnappi, aloitanappi, lopetanappi, sjainnitnappi;
    private JButton poktitle, jktitle, holdtitle, dmtitle, kalibtitle;
    //panelit
    private JLabel background;
    private JLabel otsikkopaneeli;
    private JLabel pelipaneeli;
    private JLabel kaynnistyspaneeli;
    //��net
    private AudioFormat audioFormat;
    private AudioInputStream audioInputStream;
    private SourceDataLine sourceDataLine;
    //liput & muut
    private boolean started = false;
    private volatile boolean voiAloittaa = false;
    private boolean stoppable = false;
    private boolean uudestaanjee = false;
    //mihin menuun menn��n
    private int valinta;

    public DiileriView() {
    
        setTitle("Dealer");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        //setDefaultCloseOperation(EXIT_ON_CLOSE);
        
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
        setLayout(new GridLayout()); //GridLayout(int rows, int cols)

        //napit, tekstit ja kuvat
        teeNapit();
        teeTekstit();

        //Kuuntelijat
        Kuuntelut();

        //P��valikko
        Paavalikko();

        //voiko n�kym�n kokoa muuttaa
        this.setResizable(false);
    }

    public void Paavalikko() {
        //ylin paneeli
        otsikkopaneeli = new JLabel();
        otsikkopaneeli.setLayout(new FlowLayout());
        otsikkopaneeli.add(valpelnappi);

        //keskimm�inen paneeli
        pelipaneeli = new JLabel();
        pelipaneeli.setLayout(new FlowLayout());
        pelipaneeli.add(pokerinappi);
        pelipaneeli.add(jaakaikkinappi);
        pelipaneeli.add(holdemnappi);
        pelipaneeli.add(demonappi);

        //alin paneeli
        kaynnistyspaneeli = new JLabel();
        kaynnistyspaneeli.setLayout(new FlowLayout());
        kaynnistyspaneeli.add(aloitanappi);
        kaynnistyspaneeli.add(lopetanappi);

        //taustakuva
        background = new JLabel(new ImageIcon("taustakuva.jpg"));
        add(background);
        background.setLayout(new GridLayout(3, 0)); //GridLayout(int rows, int cols)
        background.add(otsikkopaneeli);
        background.add(pelipaneeli);
        background.add(kaynnistyspaneeli);
    }

    public void PokeriValikko() {
        remove(background);
        setSize(1199, 799);
        //pokeri title
        otsikkopaneeli = new JLabel();
        otsikkopaneeli.setLayout(new FlowLayout());
        otsikkopaneeli.add(poktitle);

        //keskimm�inen paneeli
        pelipaneeli = new JLabel();
        pelipaneeli.setLayout(new FlowLayout());
        pelipaneeli.add(uudelleen);

        //alimmainen paneeli
        kaynnistyspaneeli = new JLabel();
        kaynnistyspaneeli.setLayout(new GridLayout(0, 2));
        kaynnistyspaneeli.add(takaisin);
        kaynnistyspaneeli.add(lopetanappi);

        //taustakuva
        background = new JLabel(new ImageIcon("taustakuva.jpg"));
        add(background);
        background.setLayout(new GridLayout(3, 0)); //GridLayout(int rows, int cols)
        background.add(otsikkopaneeli);
        background.add(pelipaneeli);
        background.add(kaynnistyspaneeli);
        setSize(1200, 800);
    }

    public void JaaKaikkiValikko() {

        remove(background);
        setSize(1199, 799);
        //title
        otsikkopaneeli = new JLabel();
        otsikkopaneeli.setLayout(new FlowLayout());
        otsikkopaneeli.add(jktitle);

        //keskimm�inen paneeli
        pelipaneeli = new JLabel();
        pelipaneeli.setLayout(new FlowLayout());
        pelipaneeli.add(uudelleen);

        //alin paneeli
        kaynnistyspaneeli = new JLabel();
        kaynnistyspaneeli.setLayout(new GridLayout(0, 2));
        kaynnistyspaneeli.add(takaisin);
        kaynnistyspaneeli.add(lopetanappi);

        //taustakuva
        background = new JLabel(new ImageIcon("taustakuva.jpg"));
        add(background);

        background.setLayout(new GridLayout(3, 0)); //GridLayout(int rows, int cols)
        background.add(otsikkopaneeli);
        background.add(pelipaneeli);
        background.add(kaynnistyspaneeli);
        setSize(1200, 800);
    }

    public void HoldemValikko() {

        remove(background);
        setSize(1199, 799);
        //title
        otsikkopaneeli = new JLabel();
        otsikkopaneeli.setLayout(new FlowLayout());
        otsikkopaneeli.add(holdtitle);

        //keskimm�inen paneeli
        pelipaneeli = new JLabel();
        pelipaneeli.setLayout(new FlowLayout());
        pelipaneeli.add(uudelleen);

        //alin paneeeli
        kaynnistyspaneeli = new JLabel();
        kaynnistyspaneeli.setLayout(new GridLayout(0, 2));
        kaynnistyspaneeli.add(takaisin);
        kaynnistyspaneeli.add(lopetanappi);

        //taustakuva
        background = new JLabel(new ImageIcon("taustakuva.jpg"));
        add(background);

        background.setLayout(new GridLayout(3, 0)); //GridLayout(int rows, int cols)
        background.add(otsikkopaneeli);
        background.add(pelipaneeli);
        background.add(kaynnistyspaneeli);
        setSize(1200, 800);
    }

    public void DemoValikko() {

        remove(background);
        setSize(1199, 799);
        //title
        otsikkopaneeli = new JLabel();
        otsikkopaneeli.setLayout(new FlowLayout());
        otsikkopaneeli.add(dmtitle);

        //keskimm�inen paneeli
        pelipaneeli = new JLabel();
        pelipaneeli.setLayout(new FlowLayout());
        pelipaneeli.add(sjainnitnappi);

        //alin paneeli
        kaynnistyspaneeli = new JLabel();
        kaynnistyspaneeli.setLayout(new GridLayout(0, 2));
        kaynnistyspaneeli.add(takaisin);
        kaynnistyspaneeli.add(lopetanappi);

        //taustakuva
        background = new JLabel(new ImageIcon("taustakuva.jpg"));
        add(background);

        background.setLayout(new GridLayout(3, 0)); //GridLayout(int rows, int cols)
        background.add(otsikkopaneeli);
        background.add(pelipaneeli);
        background.add(kaynnistyspaneeli);
        setSize(1200, 800);
    }

    public void KalibrointiValikko() {
        System.out.println("Kalibmenu");
        remove(background);
        setSize(1199, 799);
        //pokeri title
        otsikkopaneeli = new JLabel();
        otsikkopaneeli.setLayout(new FlowLayout());

        //keskimm�inen paneeli
        pelipaneeli = new JLabel();
        pelipaneeli.setLayout(new FlowLayout());
        pelipaneeli.add(kalibtitle);

        //alimmainen paneeli
        kaynnistyspaneeli = new JLabel();
        kaynnistyspaneeli.setLayout(new GridLayout(0, 2));

        //taustakuva
        background = new JLabel(new ImageIcon("taustakuva.jpg"));
        add(background);
        background.setLayout(new GridLayout(3, 0)); //GridLayout(int rows, int cols)
        background.add(otsikkopaneeli);
        background.add(pelipaneeli);
        background.add(kaynnistyspaneeli);
        setSize(1200, 800);
    }

    public void Kuuntelut() {
        //BUTTON LISTENERIT, PIT�� EHK� VAIHTAA LUOKAKSI
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
                System.out.println("uudestaan");
                Aanieffekti(2);
                uudestaanjee = true;
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

    public int getValinta() {
        return valinta;
    }
    
    public boolean getUudestaan() {
    	return uudestaanjee;
    }

    public void NollaaElementit() {
        //Pit�� nollata n�kymien v�liss�
        remove(background);
        setSize(1199, 799);
    }

    public void IconiMuuttaja(int i) {
        started = true;
        valinta = i;
        if (i == 1) {
            //pokeri painettu
            pokerinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(pokeripainettu)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(jaakaikkikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            holdemnappi.setIcon((Icon) new ImageIcon(((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demokuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        }
        if (i == 2) {
            //jaakaikki painettu
            pokerinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(jaakaikkipainettu)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            holdemnappi.setIcon((Icon) new ImageIcon(((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demokuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        }
        if (i == 3) {
            //holdem painettu

            pokerinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(jaakaikkikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            holdemnappi.setIcon((Icon) new ImageIcon(((new ImageIcon(holdempainettu)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demokuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        }
        if (i == 4) {
            //demo painettu
            pokerinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            jaakaikkinappi.setIcon((Icon) new ImageIcon(((new ImageIcon(jaakaikkikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            holdemnappi.setIcon((Icon) new ImageIcon(((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
            demonappi.setIcon((Icon) new ImageIcon(((new ImageIcon(demopainettu)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        }
        aloitanappi.setIcon((Icon) new ImageIcon(((new ImageIcon(aloitapainettu)).getImage()).getScaledInstance(297, 85, java.awt.Image.SCALE_SMOOTH)));
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

            //teksit
            valpel = ImageIO.read(new File("valitsepeli_title.png"));
            aloita = ImageIO.read(new File("Aloita.png"));
            sjainnit = ImageIO.read(new File("sijainnit_title.png"));
            lopeta = ImageIO.read(new File("Lopeta_painettu.png"));

            //"painetut" tekstit
            aloitapainettu = ImageIO.read(new File("Aloita_painettu.png"));
            lopetapainettu = ImageIO.read(new File("Lopeta_painettu.png"));

            //titlet
            pokerititle = ImageIO.read(new File("Pokeri_title.png"));
            jaakaikkititle = ImageIO.read(new File("JaaKaikki_title.png"));
            holdemtitle = ImageIO.read(new File("TexasHoldEm_title.png"));
            demotitle = ImageIO.read(new File("DiileriDemo_title.png"));

            //kalibroi
            kalibrointitle = ImageIO.read(new File("Kalibroidaan_notice.png"));

        } catch (IOException e) {
            System.out.println("Kuvanlataus ei onnistunut.");
            System.out.println("Kato ett� polut kuviin on oikein, tai ett� kuvat on projektikansion juuressa.");
            System.exit(0);
        }
        //SIVUJEN TITLET JA SKAALAUS
        poktitle = new JButton(new ImageIcon(((new ImageIcon(pokerititle)).getImage()).getScaledInstance(326, 100, java.awt.Image.SCALE_SMOOTH)));
        poktitle.setBorderPainted(false);
        poktitle.setFocusPainted(false);
        poktitle.setContentAreaFilled(false);

        jktitle = new JButton(new ImageIcon(((new ImageIcon(jaakaikkititle)).getImage()).getScaledInstance(496, 100, java.awt.Image.SCALE_SMOOTH)));
        jktitle.setBorderPainted(false);
        jktitle.setFocusPainted(false);
        jktitle.setContentAreaFilled(false);

        holdtitle = new JButton(new ImageIcon(((new ImageIcon(holdemtitle)).getImage()).getScaledInstance(496, 100, java.awt.Image.SCALE_SMOOTH)));
        holdtitle.setBorderPainted(false);
        holdtitle.setFocusPainted(false);
        holdtitle.setContentAreaFilled(false);

        dmtitle = new JButton(new ImageIcon(((new ImageIcon(demotitle)).getImage()).getScaledInstance(496, 100, java.awt.Image.SCALE_SMOOTH)));
        dmtitle.setBorderPainted(false);
        dmtitle.setFocusPainted(false);
        dmtitle.setContentAreaFilled(false);

        kalibtitle = new JButton(new ImageIcon(((new ImageIcon(kalibrointitle)).getImage()).getScaledInstance(637, 85, java.awt.Image.SCALE_SMOOTH)));
        kalibtitle.setBorderPainted(false);
        kalibtitle.setFocusPainted(false);
        kalibtitle.setContentAreaFilled(false);

        //TEHD��N JA SKAALTAAN
        valpelnappi = new JButton(new ImageIcon(((new ImageIcon(valpel)).getImage()).getScaledInstance(539, 85, java.awt.Image.SCALE_SMOOTH)));
        valpelnappi.setBorderPainted(false);
        valpelnappi.setFocusPainted(false);
        valpelnappi.setContentAreaFilled(false);

        aloitanappi = new JButton(new ImageIcon(((new ImageIcon(aloita)).getImage()).getScaledInstance(297, 85, java.awt.Image.SCALE_SMOOTH)));
        aloitanappi.setBorderPainted(false);
        aloitanappi.setFocusPainted(false);
        aloitanappi.setContentAreaFilled(false);

        sjainnitnappi = new JButton(new ImageIcon(((new ImageIcon(sjainnit)).getImage()).getScaledInstance(411, 99, java.awt.Image.SCALE_SMOOTH)));
        sjainnitnappi.setBorderPainted(false);
        sjainnitnappi.setFocusPainted(false);
        sjainnitnappi.setContentAreaFilled(false);

        lopetanappi = new JButton(new ImageIcon(((new ImageIcon(lopeta)).getImage()).getScaledInstance(297, 85, java.awt.Image.SCALE_SMOOTH)));
        lopetanappi.setBorderPainted(false);
        lopetanappi.setFocusPainted(false);
        lopetanappi.setContentAreaFilled(false);

    }

    public void VoiPainaa() {
        uudelleen.setIcon((Icon) new ImageIcon(((new ImageIcon(uudelleenpainettu)).getImage()).getScaledInstance(270, 170, java.awt.Image.SCALE_SMOOTH)));
    }

    public void teeNapit() {
        try {
            //napit
            pokerikuva = ImageIO.read(new File("Pokeri.png"));
            jaakaikkikuva = ImageIO.read(new File("Jaakaikki.png"));
            holdemkuva = ImageIO.read(new File("TexasHoldEm.png"));
            demokuva = ImageIO.read(new File("Diileridemo.png"));
            uudelleenkuva = ImageIO.read(new File("PelaaUudelleen.png"));
            //napit painettu
            pokeripainettu = ImageIO.read(new File("Pokeri_painettu.png"));
            jaakaikkipainettu = ImageIO.read(new File("Jaakaikki_painettu.png"));
            holdempainettu = ImageIO.read(new File("TexasHoldEm_painettu.png"));
            demopainettu = ImageIO.read(new File("Diileridemo_painettu.png"));
            uudelleenpainettu = ImageIO.read(new File("PelaaUudelleen_pressed.png"));
            //takaisinp�in
            takaisinkuva = ImageIO.read(new File("PienempiKuin_painettu.png"));

        } catch (IOException e) {
            System.out.println("Kuvanlataus ei onnistunut.");
            System.out.println("Kato ett� polut kuviin on oikein.");
            System.exit(0);
        }

        //TEHD��N NAPIT JA SKAALATAAN NE
        pokerinappi = new JButton(new ImageIcon(((new ImageIcon(pokerikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        pokerinappi.setBorderPainted(false);
        pokerinappi.setFocusPainted(false);
        pokerinappi.setContentAreaFilled(false);

        holdemnappi = new JButton(new ImageIcon(((new ImageIcon(holdemkuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        holdemnappi.setBorderPainted(false);
        holdemnappi.setFocusPainted(false);
        holdemnappi.setContentAreaFilled(false);

        jaakaikkinappi = new JButton(new ImageIcon(((new ImageIcon(jaakaikkikuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        jaakaikkinappi.setBorderPainted(false);
        jaakaikkinappi.setFocusPainted(false);
        jaakaikkinappi.setContentAreaFilled(false);

        demonappi = new JButton(new ImageIcon(((new ImageIcon(demokuva)).getImage()).getScaledInstance(240, 160, java.awt.Image.SCALE_SMOOTH)));
        demonappi.setBorderPainted(false);
        demonappi.setFocusPainted(false);
        demonappi.setContentAreaFilled(false);

        uudelleen = new JButton(new ImageIcon(((new ImageIcon(uudelleenkuva)).getImage()).getScaledInstance(270, 170, java.awt.Image.SCALE_SMOOTH)));
        uudelleen.setBorderPainted(false);
        uudelleen.setFocusPainted(false);
        uudelleen.setContentAreaFilled(false);

        takaisin = new JButton(new ImageIcon(((new ImageIcon(takaisinkuva)).getImage()).getScaledInstance(120, 100, java.awt.Image.SCALE_SMOOTH)));
        takaisin.setBorderPainted(false);
        takaisin.setFocusPainted(false);
        takaisin.setContentAreaFilled(false);
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

            audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            audioFormat = audioInputStream.getFormat();

            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

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
                while ((cnt = audioInputStream.read(
                        tempBuffer, 0, tempBuffer.length)) != -1) {
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
