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

public class Fenetre extends JFrame{

	//Decalage buffer suivant l'OS
	// 8 , 31 sous Windows
	// 0 , 39 sous Linux
	private static final int DECX = 0;
	private static final int DECY = 39;

	
	private Rectangle ecran;		//Rectangle limite de l'ecran
    private BufferedImage monBuf;	//Buffer servant a dessiner
    private Image background;		//Image de fond
    private Timer timer;			//Timer de dessin et calcul
    private Timer spawn;			//Timer d'appartion des ennemis
    private long cadence;			//Pour reperer si une defense a fini son temps de recharge
    private ListeEnnemis listeEnnemis;	//Liste d'ennemis
    private ListeTour listeTour;		//Liste de tours et de pieges
    private ListeElement listeBarrieres;//Liste de barrieres
    protected Menu menuTest;		//Menu
    protected Joueur bizuth;		//Joueur
    private int score;				//Score actuel du joueur
    private Niveau level;		//Matrice representant le niveau en cours
    private int niveau;			//Numero du niveau en cours
    private int nbSpawn;		//Nombre d'ennemis apparu pour le niveau en cours
    private boolean enCours;	//Pour reperer si des ennemis sont generables
    private boolean niveauReady;//Pour reperer si le prochain niveau est apte a etre genere
    private Case[][] tabCases;		//Tableau de cases pavant la carte
    private Chemin chemin;			//Chemin ou se deplaceront les ennemis
    private boolean dessin;	//Pour savoir si on doit dessiner les portees de previsualisation
    private int x;			//Position en x du dessin de la portee de previsualisation
    private int y;			//Position en y du dessin de la portee de previsualisation
    private int portee;		//Rayon de la portee de previsualisation

	public Fenetre() {
        /* INIT FRAME */
        super("Le tower defense des petits loups");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1203,770);
        setResizable(true);
        ecran = new Rectangle(getInsets().left, getInsets().top, getSize().width-getInsets().right-300, getSize().height-getInsets().bottom-10);
        dessin = false;
        x = 0;
        y = 0;
        portee = 0;
        
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

		/* AJOUT DES ECOUTEURS */
        this.addMouseListener(new EcouteurClicSouris());
        this.addMouseMotionListener(new EcouteurMoveSouris());
        
        setVisible(true); // A mettre a la fin, sinon grosse erreur lors du premier dessin !
        
	}
	
	/* METHODE PAINT
	 * Redessine pour le moment TOUT a chaque tour de boucle dans un buff, puis le buff
	 * Trois methodes de debugages disponibles :
	 * showBordures(gb)				pour visualiser les bordures du chemin
	 * showContoursEnnemi(gb, bob)	pour visualiser les zones de contact entre ennemis
	 * showPorteeTour(gb, eiffel)	pour visualiser la portee des tours ; desormais plus classee en tant que debugage
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
		/* PEINTURE TOURS */
		Tour cur2 = listeTour.root;
		while(cur2 != null){
			if(dessin){
				 showPorteeTour(gb, cur2);
			}
			cur2.draw(gb);
			//affichage des tirs
			gb.setColor(Color.white);
			for(int i =0; i<cur2.cibles.length; i++){
				if(cur2.cibles[i] != null && cur2.cibles[i].vie >0 && cur2.aTire>0 && !(cur2 instanceof Piege1)){
					gb.drawLine(cur2.getPosx()+cur2.getLargeur()/2, cur2.getPosy()+cur2.getHauteur()/2, cur2.cibles[i].getPosx()+cur2.cibles[i].getLargeur()/2,cur2.cibles[i].getPosy()+cur2.cibles[i].getHauteur()/2);
					cur2.aTire--;
					cur2.cibles[i] = null;
				}
			}
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
			cur.draw(gb);
			gb.drawString(Integer.toString(cur.getVie()) , cur.getPosx() + 10, cur.getPosy());
			cur = cur.next;
		}
		/* PEINTURE PREVISUALISATION PLACEMENT */
		if(dessin){
			gb.setColor(Color.CYAN);
			gb.drawArc(x-portee/2, y-portee/2, portee, portee, 0, 360);
			gb.setColor(Color.RED);
			gb.drawLine(0,90 , 900,90);
			gb.drawLine(0,690 , 900,690);
		}
		/* DESSIN GLOBAL */
        g.drawImage(monBuf,DECX,DECY,null);
        
    }
    
    /* CALCULS DEPLACEMENTS ENNEMIS */
    public class letsDance implements ActionListener{
		public void actionPerformed(ActionEvent e){
			moteur();
		}
	}
	
	/* CALCULS DEROULEMENT NIVEAU */
	public class letsSpawn implements ActionListener{
		public void actionPerformed(ActionEvent e){
			//SI le niveau n'est pas en cours, on le commence
			if(niveauReady && listeEnnemis.root == null){
				niveau++;
				level = new Niveau(niveau, 14*Case.LCASE, 19*Case.LCASE);
				menuTest.infoJeu.setText("Le niveau " + niveau + " a commence !");
				menuTest.MajMenu(niveau);
				nbSpawn++;
				niveauReady = false;
			}
			//SI le niveau est en cours de generation, on genere l'ennemis suivant
			if(nbSpawn != -1){
				Ennemis sbire = level.genererEnnemi(nbSpawn, ecran);
				sbire.addEnnemiListener(new EcouteurEnnemi());
				listeEnnemis.insertQueue(sbire);
			}
			//SI tous les ennemis du niveau ont ete generes, on arrete le spawn
			if(nbSpawn == level.matriceC[0].length -1){
				spawn.stop();
				nbSpawn = -1;
			//SINON, on se prepare a generer l'ennemi suivant
			} else if(nbSpawn != -1){
				nbSpawn++;
			}
		}
	}
	
	private void moteur() {
		//Incrementation du temps
		cadence++;
		Ennemis cur = listeEnnemis.root;
		//Deplacement des ennemis au sein du chemin
		while(cur != null){
			cur.moveChemin(tabCases, chemin.bordureGauche, chemin.bordureDroite, chemin.bordureHaut, chemin.bordureBas);
			cur = cur.next;
		}
		
		/* GESTION BOUSCULADE
		 * Essayons de se faire rentrer dedans les differents ennemis
		 * La tete de liste (=listeEnnemis.root = racine) contient le plus vieil ennemi,
		 * c'est a dire celui qui a spawn il y a le plus de temps,
		 * et qui n'est ni mort ni victorieux
		 * La priorite lui est donnee :
		 * il doit TOUJOURS se debloquer. Les autres attendent que se soit fait
		 * Pas de problemes avec la multi attente ; phenome de queue-leu-leu
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
				//Exception pour les sbireFantomes
				while(prev.collision(cur) && !(cur instanceof SbireFantome) && !(prev instanceof SbireFantome)){
					cur.moveBasique(true, true);
					aquecollision = true;
				}
				//On remet l'ennemi dans le bon sens de mouvement, sauf s'il en a tape un autre (pour se faire pousser par un plus vieux)
				/*if(!aquecollision){
					cur.dposx = - cur.dposx;
				} else {	//On avance dans le meme sens que les vieux
					cur.setDposx(prev.dposx);
					cur.buteeBas = prev.buteeBas;
					cur.enBas = true;	//On suit le vieux, on lui fait confiance, on ne re-check pas le bas
				}*/
				cur.dposx = - cur.dposx; //ligne a enlever si on fait suivre les vieux
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
		//*//
		
		/* GESTION TIR
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
				
		//*//
		//On met a jour le dessin une fois que tous les calculs ont ete faits
		repaint();
	}
	
	/* FENETRE PERSONNALISEE
	 * Pour demander son nom au joueur
	 * lorsqu'il realise un nouveau record
	 */
	private class TonNom extends JDialog {
		private boolean sendData = false;	//Pour savoir quand on a clique sur OK et recuperer le donnees du champs de texte
		private JTextField nom;				//Champs de texte ou le joueur rentre son nom
		TonNom(JFrame parent, String title, boolean modal, String text){
			super(parent, title, modal);
			setResizable(false);
			setLocationRelativeTo(null);
			initComponent(text);
		}
		
		/* Initialisation de la fenetre */
		private void initComponent(String str){
			//Panel global
			JPanel globalPane = new JPanel();
			globalPane.setPreferredSize(new Dimension(450, 150));
			
			//Texte
			JLabel textLabel = new JLabel(str);
			globalPane.add(textLabel);
			
			//Champ de saisi
			nom = new JTextField();
			nom.setPreferredSize(new Dimension(400, 25));
			nom.setHorizontalAlignment(JTextField.CENTER);
			globalPane.add(nom);
			
			//Bouton OK
			JButton okBouton = new JButton("OK !");
			okBouton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a) {
					sendData = true;
					setVisible(false);	//Dialogue ferme lorsqu'on rend la fenetre invisible
				}      
			});
			globalPane.add(okBouton);
			
			this.getContentPane().add(globalPane);
			pack();
		}
		
		public String showTonNom(){
			//Reinitialisation du booleen d'autorisation d'envoie des donnees
			sendData = false;
			//On lance le dialogue en rendant la fenetre visible
			setVisible(true);
			/* LE DIALOGUE S'EFFECTUE ICI : sendData peut passer a vrai si on clique sur OK */
			//Si on a clique sur OK, on envoie, sinon on envoie une chaine par default
			return (sendData)? nom.getText() : "Annonyme";
		}
	}
	
	/* POUR ECRIRE LES NOUVEAUX RECORDS */
	private void ecrireScore(int score){
		//Variables lecture fichier scores
		FileReader lecteur = null;
		BufferedReader tamponLecture = null;
		//Variables ecriture fichier scores
		FileWriter ecriveur = null;
		BufferedWriter tamponEcriture = null;
		//Potentielle String a ecrire dans le fichier des scores
		String scoreString = bizuth.name + " " + score + "\n";
		//Tableau des scores
		String[] scores = new String[5];
		int rangScore = -1;	//Rang du nouveau record ; -1 si aucun nouveau record
		
		System.out.println("Score " + score);
		
		JOptionPane finJeu = new JOptionPane();
		String[] fin = {"Recommencer", "Quitter", "Voir les autres strateges"};
		String strFin = "<html><body><div align='center'>Fin du jeu, tu as perdu au niveau " + niveau + " !<br/>";
		int rang = 0;

		try {
			lecteur = new FileReader("scores.txt");
			tamponLecture = new BufferedReader(lecteur);
			String ligne;	//Ligne tampon pour l'eventuelle nouveau record
			int exScore = 0;	//Initialisation de l'ancien record
			for(int r = 0 ; r < scores.length ; r++){
				//Lecture de la ligne r et stockage dans un tableau
				scores[r] = tamponLecture.readLine() + "\n";
				System.out.print("scores[" + r + "] " + scores[r]);
				for(int i = 0 ; i < scores[r].length() ; i++){
					//SI on n'a pas encore identifier un record
					if(scores[r].charAt(i) == ' ' && rangScore == -1){
						exScore = Integer.parseInt(scores[r].substring(i+1, scores[r].length()-1));
						//Si le nouveau score est un record
						if(score > exScore){
							//Stockage du rang du record
							rangScore = r;
							System.out.println("Nouveau record de " + score + "!!" + rangScore);
						}
					}
				}
			}
			
			//SI il y a eu nouveau score
			if(rangScore != -1){
				strFin += "<br/>Mais tu as tout de meme fait mieux que certains autres strateges.<br/>Quel est ton nom ?</div></body></html>";
				TonNom nom = new TonNom(null, "GAME OVER !", true, strFin);
				String str = nom.showTonNom();
				//JOptionPane tonNom = new JOptionPane();
				//String str = tonNom.showInputDialog(null, strFin, "GAME OVER !", JOptionPane.QUESTION_MESSAGE);
				//Pour ne pas corrompre le fichier des scores
				str = str.replaceAll(" ", "");
				if(str.equals("")){
					str = "Annonyme";
				}
				bizuth.name = str;
				JOptionPane andNow = new JOptionPane();
				rang = finJeu.showOptionDialog(null, "D'autres campements peuvent avoir besoin de ton aide.\nQue veux-tu faire ?", "Et maintenant ?", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, fin, fin[1]);
				scoreString = bizuth.name + " " + score + "\n";
				//Tampon de la ligne ou sera ecrite le nouveau score
				ligne = scores[rangScore];
				//Ecriture du nouveau score dans le tableau
				scores[rangScore] = scoreString;
				//SI on n'a pas fait le dernier score
				//On decale les scores d'un rang = une ligne vers le bas
				if(rangScore < 5){
					//Variable de stockage de l'ex score
					String ligneTampon;
					for(int i = rangScore + 1 ; i < scores.length; i++){
						//Tampon de la ligne i
						ligneTampon = scores[i];
						//Reecriture de la ligne i avec l'ancien score du dessus
						scores[i] = ligne;
						//Remise en place du tampon pour le prochain tour de boucle
						ligne = ligneTampon;
					}
				}
				/*DEBUGAGE*/
				for(int r = 0 ; r < scores.length ; r++){
					System.out.print("scores["+r+"] " + scores[r]);
				}
				//*//
				//Reecriture du tableau des scores dans le fichier bdd
				ecriveur = new FileWriter("scores.txt");
				tamponEcriture = new BufferedWriter(ecriveur);
				for(int r = 0 ; r < scores.length ; r++){
					tamponEcriture.write(scores[r]);
				}
			}  else {
				strFin += "Dommage, tu essaieras de faire mieux la prochaine fois !";
				rang = finJeu.showOptionDialog(null, strFin, "GAME OVER !", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, fin, fin[1]);
			}
		
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
		
		//Suivant l'option qu'a choisie le joueur
		switch(rang){
			case 0 :
				System.out.println("Tu as choisi de recommencer");
				recommencer();
				break;
			case 1 :
				System.out.println("Tu as choisi de quitter");
				System.exit(0);
			case 2 :
				Menu.affichageScore();
				recommencer();
				break;
			default :
				System.exit(0);
		}
			
	}
	
	/* POUR RECOMMENCER LE JEU */
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
        
        /* INIT NIVEAU */
        niveau = 0;
        nbSpawn = -1;
		level = new Niveau(niveau, 15*Case.LCASE, 19*Case.LCASE);
		enCours = false;
		niveauReady = true;
        
        /* INIT MENU */
        menuTest.setJoueur(bizuth);
        menuTest.reInit();
        menuTest.argent.setText("Argent : " + bizuth.argent + "$");
        menuTest.vie.setText("PV : " + bizuth.vie);
        menuTest.niveau.setText("Niveau: " + niveau);
        dessin = false;
		
		/* INIT QUADRILAGE DE LA CARTE
         * Uniquement sur la zone jouable
         * Remise a zero des attributs booleens
         */
        for(int i=0; i<tabCases.length; i++){
			for(int j=0; j<tabCases[0].length;j++){
				tabCases[i][j].setChemin(false);
				tabCases[i][j].setBarriere(false);
				tabCases[i][j].setHybride(false);
				tabCases[i][j].setOccupe(false);
				tabCases[i][j].setBordure(false);
				tabCases[i][j].setImage("caseLambda.png");
			}
		}
		chemin = new Chemin(tabCases);
		
		repaint();
	}
	
	/* CLASSE INTERNE D'ECOUTE DES MOUVEMENTS SOURIS */
	private class EcouteurMoveSouris extends MouseMotionAdapter {
		
		public void mouseMoved(MouseEvent e){
			if(menuTest.getVariable() != 0 && niveauReady){
				dessin = true;
				int i = (int)((e.getX()-DECX)/Case.LCASE);;	//-8 pour contrer le decalage du buffer
				int j = (int)((e.getY()-DECY)/Case.LCASE);	//-31 pour contrer le decalage du buffer
				x = i*Case.LCASE + Case.LCASE/2;	//On visualise au centre de la case
				y = j*Case.LCASE + Case.LCASE/2;
				//Pour previsualiser les portees des tours
				switch(menuTest.getVariable()){
					case 1 :
						portee = Tour1.RANGE;
						break;
					case 2 :
						portee = Tour2.RANGE;
						break;
					case 3 :
						portee = Tour3.RANGE;
						break;
					case 4 :
						portee = Tour4.RANGE;
						break;
					case 5 :
						portee = 0;	//Et pas dessin = false pour pouvoir aussi previsualiser lors du placement d'une barriere
						break;
					case 6 :
						portee = Piege1.RANGE;
						break;
					case 7 :
						portee = Piege2.RANGE;
						break;
					case 8 :
						portee = Piege3.RANGE;
						break;
					default :
						dessin = false;
						break;
				}
				repaint();
			} else {
				dessin = false;
			}
		}
	}
	
	/* CLASSE INTERNE D'ECOUTE DES CLICS SOURIS */
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
			int i = (int)((e.getX()-DECX)/Case.LCASE);;	//-8 pour contrer le decalage du buffer
			int j = (int)((e.getY()-DECY)/Case.LCASE);	//-31 pour contrer le decalage du buffer
			//Redefinition de la position associee
			int x = i*Case.LCASE;
			int y = j*Case.LCASE;
			//
			String str = "Tour";
			//Si on a cliquee sur un case vide
			if(niveauReady){
				if(!tabCases[i][j].isOccupe()){
					if(boutonSouris == e.BUTTON1){
						//Pour ne pas placer une tour hors de la zone de jeu :
						if(i < tabCases.length && j < tabCases[0].length - 2 && j > 2){
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
							
							case 5:	//Barriere
									//A noter que les verifications se font dans un ordre precis limitant la multiplication des cas
								//SI on veut placer une barriere sur la gauche du chemin
								if(tabCases[i][j].isChemin() && !tabCases[i-1][j].isChemin() && tabCases[i+1][j].isChemin() && i < tabCases.length-2 && j > 2 && j< tabCases[0].length-2){
									//SI il y a la place pour que les ennemis passent en haut
									if(tabCases[i+2][j].isChemin() && tabCases[i+3][j].isChemin() && tabCases[i+2][j-1].isChemin() && tabCases[i+3][j-1].isChemin() && tabCases[i+2][j-2].isChemin() && tabCases[i+3][j-2].isChemin() || (!tabCases[i][j-1].isChemin() && !tabCases[i+1][j-1].isChemin() || (!tabCases[i][j-2].isChemin() && !tabCases[i+1][j-2].isChemin()))){
										//SI il y a la place pour que les ennemis passent en bas
										if(tabCases[i+2][j+1].isChemin() && tabCases[i+3][j+1].isChemin() && tabCases[i+2][j+2].isChemin() && tabCases[i+3][j+2].isChemin() || (!tabCases[i][j+1].isChemin() && !tabCases[i+1][j+1].isChemin() || (!tabCases[i][j+2].isChemin() && !tabCases[i+1][j+2].isChemin()))){
											listeBarrieres.insertTete(new Barriere(x+5, y+3, chemin, tabCases));
											bizuth.argent -= Barriere.PRIX;
											menuTest.argent.setText("Argent : " + bizuth.argent + "$");
											str = "Barriere";
										} else {
											pose = false;
										}
									} else {
										pose = false;
									}
								//SINON SI on veut placer une barriere sur la droite du chemin
								} else if(tabCases[i][j].isChemin() && !tabCases[i+1][j].isChemin() && tabCases[i-1][j].isChemin() && i > 2 && j > 2 && j< tabCases[0].length-2){
									//SI il y a la place pour que les ennemis passent en haut
									if(tabCases[i-2][j].isChemin() && tabCases[i-3][j].isChemin() && tabCases[i-2][j-1].isChemin() && tabCases[i-3][j-1].isChemin() && tabCases[i-2][j-2].isChemin() && tabCases[i-3][j-2].isChemin() || (!tabCases[i][j-1].isChemin() && !tabCases[i-1][j-1].isChemin() || (!tabCases[i][j-2].isChemin() && !tabCases[i-1][j-2].isChemin()))){
										//SI il y a la place pour que les ennemis passent en bas
										if(tabCases[i-2][j+1].isChemin() && tabCases[i-3][j+1].isChemin() && tabCases[i-2][j+2].isChemin() && tabCases[i-3][j+2].isChemin() || (!tabCases[i][j+1].isChemin() && !tabCases[i-1][j+1].isChemin() || (!tabCases[i][j+2].isChemin() && !tabCases[i-1][j+2].isChemin()))){
											listeBarrieres.insertTete(new Barriere(x-Case.LCASE+5, y+3, chemin, tabCases));
											bizuth.argent -= Barriere.PRIX;
											menuTest.argent.setText("Argent : " + bizuth.argent + "$");
											str = "Barriere";
										} else {
											pose = false;
										}
									} else {
										pose = false;
									}
								} else {
									pose = false;
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
						} else pose = false;
						
						if(menuTest.getVariable() == 6 || menuTest.getVariable() == 7 || menuTest.getVariable() == 8){
							str = "Piege";
						}
					
						//Si on a bel et bien pose quelque chose
						if(pose){
							dessin = false;	//Pour ne pas redessiner une derniere fois la portee de previsualisation
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
						dessin = false;
						repaint();
					}
					
				} else {
					menuTest.getInfoJeu().setText("Impossible de placer ca ici !");
				}
			} else {
				menuTest.getInfoJeu().setText("Impossible de placer ca maintenant !");
				if(boutonSouris == e.BUTTON3){
					menuTest.setVariable(0);
				}
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
	
	/* CLASSE INTERNE D'ECOUTE POUR LANCER LES NIVEAUX OU LES ARRETER*/
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
				enCours = false;
				menuTest.MajVersStart();
			}
		}
	}
	
	/* CLASSE INTERNE D'ECOUTE DESENNEMIS */
	private class EcouteurEnnemi implements EnnemiListener {
		
		public void ennemiMort(EnnemiEvent e){
			System.out.print("Un ennemi est mort !! ");
			System.out.println(e.getEnnemi());
			//Une chance sur 2 pour les ennemis Sbire3 de pouvoir devenir des fantomes au debut
			if(e.getEnnemi() instanceof Sbire3 && (int)(2*Math.random()) == 1){
				SbireFantome sbireFantome = new SbireFantome(ecran, e.getEnnemi().IDEnnemi, e.getEnnemi().posx, e.getEnnemi().posy);
				sbireFantome.addEnnemiListener(new EcouteurEnnemi());
				listeEnnemis.insertFantome(sbireFantome); //De cette maniere, les tours tirent en priorite sur les fantomes les plus vieux
			}
			//Incrementation du score du joueur
			score += level.matriceC[4][e.getEnnemi().IDEnnemi];
			//Definition de l'argent que va gagner le joueur
			int recompense;
			//SI l'ennemi a ete beni par un piege et qu'il n'est pas fantome, la recompense est doublee
			if(e.getEnnemi().isBeni() && !(e.getEnnemi() instanceof SbireFantome)){
				recompense = 2*e.getEnnemi().getRecompense();
			//SINON, elle reste la meme
			} else {
				recompense = e.getEnnemi().getRecompense();
			}
			//Incrementation argent du joueur
			bizuth.argent += recompense;
			//Actualisation menu
			menuTest.argent.setText("Argent : " + bizuth.argent + "$");
			//Suppression de l'ennemi mort de la liste et de son ecouteur
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			//Verification de la fin di niveau
			finDuNiveau();
		}
		
		public void ennemiVictorieux(EnnemiEvent e){
			System.out.print("Un ennemi est victorieux ! ");
			System.out.println(e.getEnnemi());
			//On diminue la vie de joueur de 1
			bizuth.setVie(1);
			//SI le joueur n'a plus de vie
			if(bizuth.vie <= 0) {
				//On regarde s'il a fait un meilleur score
				ecrireScore(score);
				//On arrete les timers
				spawn.stop();
				timer.stop();
				//Actualisation menu
				menuTest.infoJeu.setText("Fin du jeu, tu as perdu au niveau " + niveau + " !");
				menuTest.MajVersStart();
				//Reinitialisation variables gestion niveau et menu
				enCours = false;
				niveauReady = true;
			}
			//Quoi qu'il arrive, on reactualise le menu
			menuTest.vie.setText("PV : " + bizuth.vie);
			//On supprime l'ennemi victorieux de la liste et l'ecouteur qui lui est associe
			e.getEnnemi().removeEnnemiListener(this);
			listeEnnemis.suppr(e.getEnnemi());
			//Verification de la fin di niveau
			finDuNiveau();
		}
		
		public void finDuNiveau(){
			//SI le niveau est fini
			if(listeEnnemis.root == null && nbSpawn == -1){
				repaint();
				//Actualisation menu
				menuTest.infoJeu.setText("Fin du niveau " + niveau + " !");
				menuTest.updateBoutons(niveau+1);
				menuTest.MajVersStart();
				menuTest.niveau.setText("Niveau : "+(niveau+1));
				////Reinitialisation variables gestion niveau et menu
				enCours = false;
				niveauReady = true;
				//Arret du timer de spawn
				spawn.stop();
				//SI on est au niveau 2 on donne des infos au joueur
				if(niveau == 2){
					JOptionPane level3 = new JOptionPane();
					level3.showMessageDialog(null, "Felicitations, tu as servecu aux deux premieres vagues !",
											"Niveau 3", JOptionPane.INFORMATION_MESSAGE);
					ImageIcon icone = new ImageIcon(Sbire2.nomImage);
					level3.showMessageDialog(null, "L'ennemis n'est pas content, il va maintenant commencer a envoyer des Sbires Feroces !" +
											"\n" + "Attention, ils sont plus resistants que ceux que tu as vu jusqu'a present...",
											"Niveau 3", JOptionPane.INFORMATION_MESSAGE, icone);
				//SINON SI on est au niveau 6 on donne d'autres infos au joueur
				} else if(niveau == 6){
					ImageIcon icone = new ImageIcon(Sbire3.nomImage);
					JOptionPane level7 = new JOptionPane();
					level7.showMessageDialog(null, "L'ennemi va maintenant pouvoir envoyer des Sbires Guerriers," +
											"\n" + "ses plus fideles combatants !",
											"Niveau 7", JOptionPane.INFORMATION_MESSAGE, icone);
					icone = new ImageIcon(SbireFantome.nomImage);
					level7.showMessageDialog(null, "On raconte que certains d'entres eux sont encore plus terribles dans la mort," +
											"\n" + "et ne seraient ainsi plus soumis aux memes lois que les vivants...",
											"Niveau 7", JOptionPane.INFORMATION_MESSAGE, icone);
				}
				//On arrete de redessiner
				timer.stop();
				//Mais on reactualise l'image une fois quand meme
				repaint();
			}
		}
	}
	
	public static void main(String[] args) {

		Fenetre game = new Fenetre();

    }
	
	/* METHODES DE DEBUGAGE */
	
	/* Visualisation bordures */
	private void showBordures(Graphics gb){
		Case curc = chemin.bordureBas.root;
		while(curc != null){
			gb.setColor(Color.green);
			if(curc.hybride){
				gb.setColor(Color.blue);
			}
			curc.drawCase(gb);
			curc = curc.next;				
		}
		curc = chemin.bordureHaut.root;
		while(curc != null){
			gb.setColor(Color.pink);
			if(curc.hybride){
				gb.setColor(Color.blue);
			}
			curc.drawCase(gb);
			curc = curc.next;
		}
		curc = chemin.bordureGauche.root;
		while(curc != null){
			gb.setColor(Color.orange);
			if(curc.hybride){
				gb.setColor(Color.blue);
			}
			curc.drawCase(gb);
			curc = curc.next;
		}
		curc = chemin.bordureDroite.root;
		while(curc != null){
			gb.setColor(Color.red);
			if(curc.hybride){
				gb.setColor(Color.blue);
			}
			curc.drawCase(gb);
			curc = curc.next;
		}
	}
	
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
    
}
