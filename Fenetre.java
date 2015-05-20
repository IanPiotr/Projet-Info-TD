import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

		/* BOGS CONNUS
			 * Probleme de timer lorsqu'on lance un niveau avant la vraie fin du precedent
			 * Barrieres peuvent obstruer la totalite du chemin
			 * Pas de remise a jour des graphismes avant l'apparition des pop-ups (peut-etre pas plus mal...)
		*/

public class Fenetre extends JFrame{

	//private static final long serialVersionUID = 43L; 
	private Rectangle ecran;
    private BufferedImage monBuf;
    private Timer timer;
    private Timer spawn;
    private ListeEnnemis listeEnnemis;
    private ListeTour listeTour;
    private ListeElement listeBarrieres;
    protected Menu menuTest;
    protected Joueur bizuth;
    private Case[][] tabCases;
    private Niveau level;
    private int niveau;
    private int nbSpawn;
    private boolean enCours;
    private boolean niveauReady;
    private Chemin chemin;
    private Image background;
    //private boolean pauseDemandee;

	public Fenetre() {
        /* INIT FRAME */
        super("Tower Defense les ptits loups");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1203,770);
        setResizable(true);
        ecran = new Rectangle(getInsets().left, getInsets().top+20, getSize().width-getInsets().right-300, getSize().height-getInsets().bottom-10);
        
        /* INIT BUFFER */
        Dimension dim = getSize();
        monBuf = new BufferedImage(dim.width,dim.height,BufferedImage.TYPE_INT_RGB);
		
		/* INIT JOUEUR */
        bizuth = new Joueur("Bizuth1");
        
        /* INIT ENNEMIS */
        listeEnnemis = new ListeEnnemis();
		
		/* INIT TOURS */
        listeTour = new ListeTour();
        
        /* INIT BARRIERES */
        listeBarrieres = new ListeElement();
        
        /* INIT MENU */
        menuTest = new Menu(bizuth);
		getContentPane().add(menuTest);
		menuTest.getStart().addActionListener(new EcouteurBoutonStart());
		
        /* INIT TIMERS */
        timer = new Timer(15, new letsDance());
        timer.start();
        spawn = new Timer(600, new letsSpawn());
        //spawn.setInitialDelay(600);
        
        /* INIT NIVEAU */
        niveau = 0;
        nbSpawn = -1;
		level = new Niveau(niveau, 15*Case.LCASE, 19*Case.LCASE);
		enCours = false;
		niveauReady = true;
		//pauseDemandee = false;
		
        /* INIT QUADRILAGE DE LA CARTE
         * Uniquement sur la zone jouable
         * Chemin predefini pour le moment
         */
        tabCases = new Case[(getWidth()-300)/Case.LCASE][getHeight()/Case.LCASE];
        for(int i=0; i<tabCases.length; i++){
			for(int j=0; j<tabCases[0].length;j++){
				tabCases[i][j]= new Case(i*Case.LCASE, j*Case.LCASE);
			}
		}
		chemin = new Chemin(tabCases);
		
		try {
			background = ImageIO.read(new File("Background.jpg"));
        } catch(Exception err){
			System.out.println("background non trouvé"); 
        }

        this.addMouseListener(new EcouteurClicSouris());
        
        setVisible(true); // A mettre a la fin, sinon grosse erreur lors du premier dessin !
        
	}
	
	/* METHODE PAINT
	 * Redessine pour le moment TOUT a chaque tour de boucle dans un buff, puis le buff
	 * Trois methodes de debugages disponibles :
	 * showBordures(gb)				pour visualiser les bordures du chemin
	 * showContoursEnnemi(gb, bob)	pour visualiser les zones de contact entre ennemis
	 * showPorteeTour(gb, eiffel)	pour visualiser la portee des tours
	 */
	public void paint(Graphics g) {
		Graphics gb = monBuf.getGraphics();
		gb.drawImage(background, 0, 0, null);
		menuTest.paint(gb);
		
		/* PEINTURE FOND */
		for(int i=0; i<tabCases.length; i++){
			for(int j=0; j<tabCases[0].length;j++){
				tabCases[i][j].drawCase(gb);
			}
		}
		//showBordures(gb);
		/* PEINTURE TOURS */
		Tour cur2 = listeTour.root;
		while(cur2 != null){
			showPorteeTour(gb, cur2);
			cur2.draw(gb);
			cur2 = cur2.next;
		}
		/* PEINTURE BARRIERES */
		Element cur3 = listeBarrieres.root;
		while(cur3 != null){
			cur3.draw(gb);
			cur3 = cur3.next;
		}
		/* PEINTURE ENNEMIS */
		gb.setColor(Color.white);
		Ennemis cur = listeEnnemis.root;
		while(cur != null){
			//showContoursEnnemi(gb, cur);
			cur.draw(gb);
			gb.drawString(Integer.toString(cur.getVie()) , cur.getPosx() + 10, cur.getPosy());
			cur = cur.next;
		}
		
		/* DESSIN GLOBAL */
        g.drawImage(monBuf,8,31,null); //8,31 sur windows
        
    }
    
    /* CALCULS DEPLACEMENTS ENNEMIS  */
    public class letsDance implements ActionListener{
		public void actionPerformed(ActionEvent e){
			boucle();
		}
	}
	
	/* CALCULS DEROULEMENT NIVEAU */
	public class letsSpawn implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//if(!pauseDemandee){
				if(niveauReady && listeEnnemis.root == null){
					niveau++;
					level = new Niveau(niveau, 14*Case.LCASE, 19*Case.LCASE);
					menuTest.infoJeu.setText("Le niveau " + niveau + " a commence !");
					menuTest.MajMenu(niveau);
					menuTest.MajVersPause();
					nbSpawn++;
					niveauReady = false;
				}
				if(nbSpawn != -1){
					Ennemis sbire = level.genererEnnemi(nbSpawn, ecran);
					sbire.addEnnemiListener(new EcouteurEnnemi());
					listeEnnemis.insertQueue(sbire);
				}
				if(nbSpawn == level.matriceC[0].length -1){
					spawn.stop();
					nbSpawn = -1;
				} else if(nbSpawn != -1) nbSpawn++;
			/*} else {
				pauseDemandee = false;
				spawn.stop();
			}*/
			
		}
	}
	
	public void boucle() {
		Ennemis cur = listeEnnemis.root;
		while(cur != null){
			cur.moveChemin(tabCases, chemin.bordureGauche, chemin.bordureDroite, chemin.bordureHaut, chemin.bordureBas);
			cur = cur.next;
		}
		
		/* GESTION BOUSCULADE - ESSAI CHEMIN
		 * Essayons de se faire rentrer dedans les differents ennemis
		 * La tete de liste (=listeEnnemis.root = racine) contient le plus vieil ennemi,
		 * c'est a dire celui qui a spawn il y a le plus de temps,
		 * et qui n'est ni mort ni victorieux
		 * La priorite lui est donnee :
		 * il doit TOUJOURS se debloquer. Les autres attendent que se soit fait
		 * Pas de problemes avec la multi attente ; phenome de queue-leu-leu
		 * NB : fonctionne desormais a priori correctement, pour un timer de spawn de 500ms
		 * 		De plus gros bugs pour un timer inferieur, mais d'une autre nature que les initiaux
		 */
		Ennemis prev = listeEnnemis.root;
		if(prev != null){
			cur = prev.next;
		}
		//Exception de l'ennemi le plus vieux faite par la nature de cette double boucle
		//En effet, si prev == listeEnnemis.root, c'est bien listeEnnemis.root.next qu'on deplace
		while(prev != null){
			while(cur != null){
				cur.dposx = - cur.dposx;
				cur.dposy = - cur.dposy;
				boolean aquecollision = false;
				//Si l'ennemi courant est coince par un ennemi plus vieux, on recule jusqu'a la FIN de l'intersection,
				//pour etre sur de ne jamais chevaucher un autre ennemi a la fin du deplacement
				//Exception pour les sbireFantomes d'upgrade = 4
				while(prev.collision(cur) && cur.upgrade != 4 && prev.upgrade != 4){
					cur.moveBasique(true, true);
					aquecollision = true;
				}
				//On remet l'ennemi dans le bon sens de mouvement, sauf s'il en a tape un autre (pour se faire pousser par un plus vieux)
				/*if(!aquecollision){
					cur.dposx = - cur.dposx;
				} else {	//On avance dans le meme sens que les vieux
					cur.dposx = prev.dposx;
					cur.buteeBas = prev.buteeBas;
					//cur.enBas = true;	//On suit le vieux, on lui fait confiance, on ne re-check pas le bas
				}*/
				cur.dposx = - cur.dposx;
				cur.dposy = -cur.dposy;
				if(cur != null){
					cur = cur.next;
				}
			}
			prev = prev.next;
			if(prev != null){
				cur = prev.next;
			}
		}
		/* FIN DE L'ESSAI */
		
		/* GESTION TIR - ESSAI AVEC FOCUS
		 * Essayons de faire perdre de la vie aux ennemis
		 * lorsqu'ils passent a proximite d'une tour
		 * C'est a dire lorsqu'ils sont dans le cercle portee des tours
		 * Gestion du focus par un simple break et le placement en tete de liste du sbire le plus vieux !
		 */
		Tour curT = listeTour.root;
		while(curT != null){
			Ennemis curE = listeEnnemis.root;
			while(curE != null){
				if(curT.collision(curE)){
					curE.setVie(curT.puissance);
					break;	//Permet le FOCUS d'un UNIQUE ennemi
							//Pour 2 ? Mettre un compteur et break a deux ! :)
							//On pourra differencier les cas suivant la nature des tours
				}
				if(curE != null){
					curE = curE.next;
				}
			}
			if(curT != null){
				curT = curT.next;
			}
		}
		
		//testCollisionBarrieres();
		
		/* FIN DE L'ESSAI */
		repaint();
	}
	
	private class EcouteurClicSouris implements MouseListener {
		//BUTTON1 = clic gauche
		//BUTTON2 = clic molette
		//BUTTON3 = clic droit
		
		public void mouseClicked(MouseEvent e){
			int boutonSouris = e.getButton();
			boolean pose = true;
			if(boutonSouris == e.BUTTON1){
				//Pour ne pas placer une tour hors de la zone de jeu :
				if(e.getX() < ecran.width - 12 && e.getY() < ecran.height - 25){
					
				switch(menuTest.getVariable()){
					//NB : 	les Tour1 sont placees par leur centre (d'ou le -12 -20, - facteur correctif 8,31 pour windows)
					//		de meme pour les Piege1
					case 1:
						listeTour.insertTete(new Tour1(ecran, e.getX()-20, e.getY()-51));
						listeTour.display();
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour1 en position !");
						break;
					
					case 2:
						listeTour.insertTete(new Tour1(ecran, e.getX(), e.getY()));
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour2 en position !");
						break;
					
					case 3:
						listeTour.insertTete(new Tour1(ecran, e.getX(), e.getY()));
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour3 en position !");
						break;
					
					case 4:
						listeTour.insertTete(new Tour1(ecran, e.getX(), e.getY()));
						bizuth.argent -= Tour1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Tour4 en position !");
						break;
					
					case 5:
						int i = (int)((e.getX()-8)/Case.LCASE);;
						int j = (int)((e.getY()-31)/Case.LCASE);
						int x = i*Case.LCASE + 5;
						int y = j*Case.LCASE + 3;
						j = (int)((y + 24)/Case.LCASE);
						i = (int)(x/Case.LCASE);
						if(!tabCases[i][j+1].isChemin() || !tabCases[i][j-1].isChemin() || !tabCases[i+1][j+1].isChemin() || !tabCases[i+1][j-1].isChemin() || (tabCases[i-1][j].isChemin() && tabCases[i+2][j].isChemin())){
							menuTest.getInfoJeu().setText("Impossible de placer une barriere ici !");
							pose = false;
						} else {
							listeBarrieres.insertTete(new Barriere(x, y, tabCases, chemin));
							bizuth.argent -= Barriere.PRIX;
							menuTest.argent.setText("Argent : " + bizuth.argent + "$");
							menuTest.getInfoJeu().setText("Barriere en position !");
						}
						break;
						
					case 6:
						listeTour.insertTete(new Piege1(e.getX(), e.getY()));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege2 en position !");
						break;
						
					case 7:
						listeTour.insertTete(new Piege1(e.getX(), e.getY()));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege3 en position !");
						break;
						
					case 8:
						listeTour.insertTete(new Piege1(e.getX(), e.getY()));
						bizuth.argent -= Piege1.PRIX;
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						menuTest.getInfoJeu().setText("Piege4 en position !");
						break;
						
				}
				
				}
				
			if(pose) menuTest.setVariable(0);
			repaint();

			} else if(boutonSouris == e.BUTTON3){
				menuTest.setVariable(0);
			}
		}
		
		public void mouseExited(MouseEvent e){
			
		}
		
		public void mouseEntered(MouseEvent e){

		}
		
		public void mouseReleased(MouseEvent e){
			
		}
		
		public void mousePressed(MouseEvent e){
			
		}
		
	}
	
	public class EcouteurBoutonStart implements ActionListener{
		public void actionPerformed(ActionEvent e){
			if(((JButton)(e.getSource())).getText().equals(" ") && !enCours){
				timer.start();
				spawn.start();
				enCours = true;
				menuTest.MajVersPause();
			} else if(((JButton)(e.getSource())).getText().equals(" ") && enCours){
				timer.stop();
				spawn.stop();
				//pauseDemandee = true;
				enCours = false;
				menuTest.MajVersStart();
			}
		}
	}
	
	private class EcouteurEnnemi implements EnnemiListener {
		
		public void ennemiMort(EnnemiEvent e){
			System.out.print("Un ennemi est mort !! ");
			System.out.println(e.getEnnemi());
			//Une chance sur 2 pour les ennemis d'upgrade 3 de pouvoir devenir des fantomes !
			if(e.getEnnemi().upgrade == 3 && (int)(2*Math.random()) == 1){
				SbireFantome sbireFantome = new SbireFantome(ecran, e.getEnnemi().IDEnnemi, e.getEnnemi().posx, e.getEnnemi().posy);
				sbireFantome.addEnnemiListener(new EcouteurEnnemi());

				listeEnnemis.insertQueue(sbireFantome); //De cette maniere, les tours tirent en priorite sur les fantomes les plus vieux	//Commentaire de Olivier : c'était marqué "listeEnnemis.insertFantome(sbireFantome)". J'ai supposé que c'était une erreur vu qu'il n'existe pas de méthode "inserfantome".

			}				
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			finDuNiveau();
		}
		
		public void ennemiVictorieux(EnnemiEvent e){
			System.out.print("Un ennemi est victorieux ! ");
			System.out.println(e.getEnnemi());
			bizuth.setVie(1);
			menuTest.vie.setText("PV : " + bizuth.vie);
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			finDuNiveau();
		}
		
		public void finDuNiveau(){
			if(listeEnnemis.root == null){
				menuTest.infoJeu.setText("Fin du niveau " + niveau + " !");
				System.out.println("infoJeu devrait afficher : 'Fin du niveau " + niveau + " !'");
				enCours = false;
				menuTest.MajVersStart();
				niveauReady = true;
				spawn.stop();
				if(niveau == 2){
					JOptionPane level3 = new JOptionPane();
					level3.showMessageDialog(null, "Felicitations, tu as survécu aux deux premières vagues !",
											"Niveau 3", JOptionPane.INFORMATION_MESSAGE);
					ImageIcon icone = new ImageIcon(Ennemis.nomImage2);
					level3.showMessageDialog(null, "L'ennemis n'est pas content, il va maintenant commencer a envoyer des Sbires Verts !" +
											"\n" + "Attention, ils sont plus resistants que ceux que tu as vu jusqu'à présent...",
											"Niveau 3", JOptionPane.INFORMATION_MESSAGE, icone);
				} else if(niveau == 6){
					ImageIcon icone = new ImageIcon(Ennemis.nomImage3);
					JOptionPane level7 = new JOptionPane();
					level7.showMessageDialog(null, "L'ennemi va maintenant pouvoir envoyer des Sbires Rouges," +
											"\n" + "ses plus fidèles combatants !",
											"Niveau 7", JOptionPane.INFORMATION_MESSAGE, icone);
					icone = new ImageIcon(SbireFantome.nomFantome);
					level7.showMessageDialog(null, "On raconte que certains d'entres eux sont encore plus terribles dans la mort," +
											"\n" + "et ne seraient ainsi plus soumis aux mêmes lois que les vivants...",
											"Niveau 7", JOptionPane.INFORMATION_MESSAGE, icone);
				}
				timer.stop();
				repaint();
			}
		}
	}
	
	public static void main(String[] args) {

		Fenetre game = new Fenetre();
		//game.menuTest.update(game.getGraphics());

    }
	
	/* METHODES DE DEBUGAGE */
	
	/* Visualisation Rectangle + Arc2D entourant les ennemis */
	private void showContoursEnnemi(Graphics gb, Ennemis bob){
		gb.setColor(Color.red);
		gb.fillRect(bob.getPosx(),bob.getPosy(),bob.cadre.width,bob.cadre.height);
        gb.setColor(Color.blue);
        gb.fillArc(bob.getPosx(), bob.getPosy(), bob.cadre.width, bob.cadre.height, 0, 360);
	}
	
	/* Visualisation Arc2D portee des tours */
	private void showPorteeTour(Graphics gb, Tour eiffel){
		gb.setColor(Color.red);
		gb.drawArc(eiffel.getPosx()-(int)eiffel.portee.getWidth()/2 + eiffel.cadre.width/2, eiffel.getPosy()-(int)eiffel.portee.getHeight()/2+ eiffel.cadre.height/2, (int)eiffel.portee.getWidth(), (int)eiffel.portee.getHeight(), 0, 360);
	}
    
	/* Test de la bonne collision sur une barriere, verification du bon placement des objets */
	private void testCollisionBarrieres(){
		Element curB = listeBarrieres.root;
		while(curB != null){
			Ennemis curE = listeEnnemis.root;
			while(curE != null){
				if(curB.collision(curE)){
					timer.stop();
				}
				if(curE != null){
					curE = curE.next;
				}
			}
			if(curB != null){
				curB = curB.next;
			}
		}
	}
}
