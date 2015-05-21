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

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

		/* BOGS CONNUS
			 * Probleme de timer lorsqu'on lance un niveau avant la vraie fin du precedent
			 * Barrieres peuvent obstruer la totalite du chemin
			 * Pas de remise a jour des graphismes avant l'apparition des pop-ups (peut-etre pas plus mal...)
		*/

public class Fenetre extends JFrame{

	//private static final long serialVersionUID = 43L; 
	private Rectangle ecran;
    private BufferedImage monBuf;
    private Image background;
    private Timer timer;
    private Timer spawn;
    private ListeEnnemis listeEnnemis;
    private ListeTour listeTour;
    private ListeElement listeBarrieres;
    protected Menu menuTest;
    protected Joueur bizuth;
    private Case[][] tabCases;
    private Chemin chemin;
    private Niveau level;
    private int niveau;
    private int score;
    private int nbSpawn;
    private long cadence;
    private boolean enCours;
    private boolean niveauReady;
    //private boolean pauseDemandee;

	public Fenetre() {
        /* INIT FRAME */
        super("Tower Defense les ptits loups");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1203,770);
        setResizable(true);
        ecran = new Rectangle(getInsets().left, getInsets().top, getSize().width-getInsets().right-300, getSize().height-getInsets().bottom-10);
        
        /* INIT BUFFER */
        Dimension dim = getSize();
        monBuf = new BufferedImage(dim.width,dim.height,BufferedImage.TYPE_INT_RGB);
		
		/* INIT JOUEUR */
        bizuth = new Joueur("Bizuth1");
        score = 0;
        
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
        spawn = new Timer(600, new letsSpawn());
        
        /* INIT NIVEAU */
        niveau = 0;
        nbSpawn = -1;
		level = new Niveau(niveau, 13*Case.LCASE, 19*Case.LCASE);
		enCours = false;
		niveauReady = true;
				
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
			System.out.println("background charge"); 
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
		menuTest.paint(gb);
		gb.drawImage(background, 0, 0, null);
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
        g.drawImage(monBuf,8,31,null);
        
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
	
	private void boucle() {
		cadence++;
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
		 * Essayons de faire perdre de la vie aux ennemis ou de les ralentir
		 * lorsqu'ils passent a proximite d'une tour
		 * C'est a dire lorsqu'ils sont dans le cercle portee des tours
		 * Gestion du focus par un simple break et le placement en tete de liste du sbire le plus vieux !
		 */
		Tour curT = listeTour.root;
		//Mise a zero de la notation du ralentissement et du benissement
		cur = listeEnnemis.root;
		while(cur != null){
			cur.setRalenti(false);
			cur.setBeni(false);
			cur = cur.next;
		}
		//Tir
		while(curT != null){
			curT.tir(cadence, listeEnnemis);
			curT = curT.next;
		}
				
		/* FIN DE L'ESSAI */
		repaint();
	}
	
	private void ecrireScore(int score){
		//Variables lecture fichier scores
		FileReader lecteur = null;
		BufferedReader tamponLecture = null;
		//Variables ecriture fichier scores
		FileWriter ecriveur = null;
		BufferedWriter tamponEcriture = null;
		//Potentielle String a ecrire dans le fichier des scores
		String scoreString = bizuth.name + " " + score;
		
		try {
			lecteur = new FileReader("scores.txt");
			tamponLecture = new BufferedReader(lecteur);
			String ligne;
			int exScore = 0;
			while(true) {
				ligne = tamponLecture.readLine();
				if(ligne == null) break;
				for(int i = 0 ; i < ligne.length() ; i++){
					if(ligne.charAt(i) == ' '){
						exScore = Integer.parseInt(ligne.substring(i+1, ligne.length()));
						break;
					}
				}
				if(exScore > 0) break;
			}
			System.out.println("exScore " + exScore);
			if(score > exScore) {
				ecriveur = new FileWriter("scores.txt");
				tamponEcriture = new BufferedWriter(ecriveur);
				tamponEcriture.write(scoreString);
				System.out.println("Nouveau Score " + score);
			} else System.out.println("Imbattu");
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			//On doit tout fermer
			try {
				//pour s'assurer que tous les octets ont etes envoyes au fichier texte
				if(ecriveur != null) {
					tamponEcriture.flush(); 
					tamponEcriture.close();
					ecriveur.close();
				}
				tamponLecture.close();
				lecteur.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
			
	}
	
	public void recommencer(){
		/* INIT JOUEUR */
        bizuth = new Joueur("Bizuth1");
        score = 0;
        
        /* INIT ENNEMIS */
        listeEnnemis = new ListeEnnemis();
		
		/* INIT TOURS */
        listeTour = new ListeTour();
        
        /* INIT BARRIERES */
        listeBarrieres = new ListeElement();
        
		
        /* INIT TIMERS */
        timer.stop();
        spawn.stop();
        
        /* INIT MENU */
        menuTest.argent.setText("Argent : " + bizuth.argent + "$");
        menuTest.vie.setText("PV : " + bizuth.vie);
        
        /* INIT NIVEAU */
        niveau = 0;
        nbSpawn = -1;
		level = new Niveau(niveau, 15*Case.LCASE, 19*Case.LCASE);
		enCours = false;
		niveauReady = true;
		
		repaint();
	}
	
	private class EcouteurClicSouris implements MouseListener {
		//BUTTON1 = clic gauche
		//BUTTON2 = clic molette
		//BUTTON3 = clic droit
		
		public void mouseClicked(MouseEvent e){
			//Prise du bouton sur lequel on a appuye
			int boutonSouris = e.getButton();
			//Initialisation booleen verification de pose pour ne pas cancel si on place au mauvais endroit
			boolean pose = true;
			//Prise des indices de la case cliquee
			int i = (int)((e.getX()-8)/Case.LCASE);;	//-8 pour contrer le decalage du buffer
			int j = (int)((e.getY()-31)/Case.LCASE);	//-31 pour contrer le decalage du buffer
			//Redefinition de la position associee
			int x = i*Case.LCASE;
			int y = j*Case.LCASE;
			//
			String str = "Tour";

			//Si on a cliquee sur un case vide
			if(!tabCases[i][j].isOccupe()){
				if(boutonSouris == e.BUTTON1){
					//Pour ne pas placer une tour hors de la zone de jeu :
					if(e.getX() < ecran.width - 12 && e.getY() < ecran.height - 25)					
					switch(menuTest.getVariable()){
						//NB : 	les objets sont places au centre des cases
						case 1:
							if(!tabCases[i][j].isChemin()){
								listeTour.insertTete(new Tour1(ecran, x+1, y-7));	//+1 -7 pour replacer l'image bien au centre
								bizuth.argent -= Tour1.PRIX;
							} else {
								pose = false;
							}
							break;
						
						case 2:
							if(!tabCases[i][j].isChemin()){
								listeTour.insertTete(new Tour2(ecran, x+1, y-2));
								bizuth.argent -= Tour2.PRIX;
								break;
							} else {
								pose = false;
							}
						
						case 3:
							if(!tabCases[i][j].isChemin()){
								listeTour.insertTete(new Tour3(ecran, x, y));
								bizuth.argent -= Tour3.PRIX;
								break;
							} else {
								pose = false;
							}
						
						case 4:
							if(!tabCases[i][j].isChemin()){
								listeTour.insertTete(new Tour4(ecran, x, y-2));
								bizuth.argent -= Tour4.PRIX;
								break;
							} else {
								pose = false;
							}
						
						case 5:
							if(!tabCases[i][j+1].isChemin() || !tabCases[i][j-1].isChemin() || !tabCases[i+1][j+1].isChemin() || !tabCases[i+1][j-1].isChemin() || (tabCases[i-1][j].isChemin() && tabCases[i+2][j].isChemin())){
								pose = false;
							} else {
								listeBarrieres.insertTete(new Barriere(x+5, y+3, tabCases));
								bizuth.argent -= Barriere.PRIX;
								menuTest.argent.setText("Argent : " + bizuth.argent + "$");
								str = "Barriere";
							}
							break;
							
						case 6:
							if(tabCases[i][j].isChemin()){
								listeTour.insertTete(new Piege1(x, y));
								bizuth.argent -= Piege1.PRIX;
								break;
							} else {
								pose = false;
							}
							
						case 7:
							if(tabCases[i][j].isChemin()){
								listeTour.insertTete(new Piege2(x, y));
								bizuth.argent -= Piege2.PRIX;
								break;
							} else {
								pose = false;
							}
								
						case 8:
							if(tabCases[i][j].isChemin()){
								listeTour.insertTete(new Piege3(x, y));
								bizuth.argent -= Piege3.PRIX;
								break;
							} else {
								pose = false;
							}
					}
					
					if(menuTest.getVariable() == 6 || menuTest.getVariable() == 7 || menuTest.getVariable() == 8){
						str = "Piege";
					}
				
					//Si on a bel et bien pose quelque chose
					if(pose){
						menuTest.setVariable(0);
						menuTest.argent.setText("Argent : " + bizuth.argent + "$");
						tabCases[i][j].setOccupe(true);
						menuTest.getInfoJeu().setText(str + " en position !");
					} else {
						menuTest.getInfoJeu().setText("Impossible de placer ca ici !");
					}
					repaint();

				} else if(boutonSouris == e.BUTTON3){
					menuTest.setVariable(0);
				}
			
			} else {
				menuTest.getInfoJeu().setText("Impossible de placer ca ici !");
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
				listeEnnemis.insertFantome(sbireFantome); //De cette maniere, les tours tirent en priorite sur les fantomes les plus vieux
			}
			score += level.matriceC[4][e.getEnnemi().IDEnnemi];
			int recompense;
			if(e.getEnnemi().isBeni()){
				recompense = 2*e.getEnnemi().getRecompense();
			} else {
				recompense = e.getEnnemi().getRecompense();
			}
			bizuth.argent += recompense;
			menuTest.argent.setText("Argent : " + bizuth.argent + "$");
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			finDuNiveau();
		}
		
		public void ennemiVictorieux(EnnemiEvent e){
			System.out.print("Un ennemi est victorieux ! ");
			System.out.println(e.getEnnemi());
			bizuth.setVie(1);
			if(bizuth.vie <= 0) {
				ecrireScore(score);
				spawn.stop();
				timer.stop();
				menuTest.infoJeu.setText("Fin du jeu, tu as perdu au niveau " + niveau + " !");
				System.out.println("infoJeu devrait afficher : 'Fin du jeu, tu as perdu au niveau " + niveau + " !'");
				enCours = false;
				menuTest.MajVersStart();
				niveauReady = true;
				JOptionPane finJeu = new JOptionPane();
				String[] fin = {"Recommencer", "Quitter"};
				int rang = finJeu.showOptionDialog(null, "Fin du jeu, tu as perdu au niveau " + niveau + " ! \nDommage, tu essaieras de faire mieux la prochaine fois !", "GAME OVER !", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, fin, fin[1]);
				if(rang == 1){
					System.out.println("Tu as choisi de quitter");
					System.exit(0);
				}
				if(rang == 0){
					System.out.println("Tu as choisi de recommencer");
					recommencer();
				}
			}
			menuTest.vie.setText("PV : " + bizuth.vie);
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			finDuNiveau();
		}
		
		public void finDuNiveau(){
			if(listeEnnemis.root == null && nbSpawn == -1){
				menuTest.infoJeu.setText("Fin du niveau " + niveau + " !");
				System.out.println("infoJeu devrait afficher : 'Fin du niveau " + niveau + " !'");
				enCours = false;
				niveauReady = true;
				menuTest.MajVersStart();
				spawn.stop();
				if(niveau == 2){
					JOptionPane level3 = new JOptionPane();
					level3.showMessageDialog(null, "Felicitations, tu as servecu aux deux premieres vagues !",
											"Niveau 3", JOptionPane.INFORMATION_MESSAGE);
					ImageIcon icone = new ImageIcon(Ennemis.nomImage2);
					level3.showMessageDialog(null, "L'ennemis n'est pas content, il va maintenant commencer a envoyer des Sbires Verts !" +
											"\n" + "Attention, ils sont plus resistants que ceux que tu as vu jusqu'a present...",
											"Niveau 3", JOptionPane.INFORMATION_MESSAGE, icone);
				} else if(niveau == 6){
					ImageIcon icone = new ImageIcon(Ennemis.nomImage3);
					JOptionPane level7 = new JOptionPane();
					level7.showMessageDialog(null, "L'ennemi va maintenant pouvoir envoyer des Sbires Rouges," +
											"\n" + "ses plus fideles combatants !",
											"Niveau 7", JOptionPane.INFORMATION_MESSAGE, icone);
					icone = new ImageIcon(SbireFantome.nomFantome);
					level7.showMessageDialog(null, "On raconte que certains d'entres eux sont encore plus terribles dans la mort," +
											"\n" + "et ne seraient ainsi plus soumis aux memes lois que les vivants...",
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
		gb.setColor(Color.white);
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
