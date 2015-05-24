import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Color;
import javax.swing.*;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JLayeredPane;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Menu extends JLayeredPane {

	private JPanel MonSousMenuTour;
	private JPanel MonSousMenuJoueur;
	private JPanel Groupement;
	private JPanel MonSousMenuJeu;
	private JButton [] MesTours;
	private JButton [] MesPieges;
	private JButton start;
	protected JButton niveau;
	protected JButton argent;
	protected JButton vie;
	protected JLabel infoJeu; // permet de donner des infos au joueur durant le jeu
	protected JOptionPane debutJeu;
	private static final ImageIcon iconTour1 = new ImageIcon("Tour1.png");
	private static final ImageIcon iconTour2 = new ImageIcon("Tour2.png");
	private static final ImageIcon iconTour3 = new ImageIcon("Tour3.png");
	private static final ImageIcon iconTour4 = new ImageIcon("Tour4.png");
	private static final ImageIcon iconPiege1 = new ImageIcon("Piege1.png");
	private static final ImageIcon iconPiege2 = new ImageIcon("Piege2.png");
	private static final ImageIcon iconPiege3 = new ImageIcon("Piege3.png");
	private static final ImageIcon iconBarriere = new ImageIcon("Barriere.png");
	private static final ImageIcon iconVie = new ImageIcon("coeur.png");
	private static final ImageIcon iconStart = new ImageIcon("start.png");
	private static final ImageIcon iconPause = new ImageIcon("pause.png");
	private static final ImageIcon iconArgent = new ImageIcon("lingot.png");
	private static final ImageIcon iconNiveau = new ImageIcon("niveau.png");
	private int variable = 0; //permet de savoir quelle tour ou piège va etre pose (1 = tour1 ... 4 = piege1 ..)
	private Joueur bizuth;
	
	
	public Menu(Joueur gameur){
		super();
		ToolTipManager.sharedInstance().setDismissDelay(10000);
		setPreferredSize(new Dimension(285, 740));
		setDoubleBuffered(true);
		
		MonSousMenuTour = new JPanel();
		MonSousMenuTour.setLayout(new GridLayout(4,2));
		MonSousMenuJoueur = new JPanel();
		MonSousMenuJoueur.setLayout(new GridLayout(3,1));
		MonSousMenuJeu = new JPanel();
		MonSousMenuJeu.setLayout(new GridLayout(2,1));
		MonSousMenuJeu.setBackground(Color.orange);
		
		bizuth = gameur;
		
		/* Fenêtre de début du jeu */
		String[] intro = {"Oui !", "Non.", "Comment ?", "Voir les autres strateges"};
		debutJeu = new JOptionPane();
		int rang = debutJeu.showOptionDialog(null, new JLabel("<html><body><div align='center'>Bonjour a toi etranger !<br/>Des ordes d'ennemis deferlent sur nos campements,<br/>mais tout nos autres strateges sont sur d'autres fronts !<br/>Veux-tu nous aider a combattre ces monstres ?</div></body></html>", JLabel.CENTER), "Tower Defense !", JOptionPane.YES_NO_CANCEL_OPTION, -1, null, intro, intro[1]);
		switch(rang){
			case 0 :
				break;
			case 2 :
				System.out.println("Tu as choisi le tuto");
				Tuto tuto = new Tuto(null, "Tutoriel", true);
				break;
			case 3 :
				affichageScore();
				break;
			default :
				System.exit(0);
		}
		
		/* INIT des boutons des tours */
		
		MesTours = new JButton[4];			
			
		MesTours[0] = new JButton("Tour 1", iconTour1);
		MesTours[0].setToolTipText("<html>Cette tour a une grande portee et cause des degats importants a faible cadence.<br/>" +
										"Son prix est de 300$.</html>");
										
		MesTours[1] = new JButton("Tour 2", iconTour2);
		MesTours[1].setToolTipText("<html>Cette tour a une portee raisonnable et cause des degats moyens sur un seul ennemis.<br/>" +
										"Son prix est de 400$.</html>");
		MesTours[1].setEnabled(false);
			
		MesTours[2] = new JButton("Tour 3", iconTour3);
		MesTours[2].setToolTipText("<html>Cette tour a une portee raisonnable et cause des degats moyens sur tous les ennemis.<br/>" +
										"Son prix est de 600$.</html>");
		MesTours[2].setEnabled(false);
			
		MesTours[3] = new JButton("Tour 4", iconTour4);
		MesTours[3].setToolTipText("<html>Cette tour a une grande portee et cause des degats beaucoup plus importants aux fantomes a faible cadence.<br/>" +
										"Son prix est de 1000$.</html>");
		MesTours[3].setEnabled(false);
										
		for(int i=0; i<MesTours.length; i++){
			MonSousMenuTour.add(MesTours[i]);
			MesTours[i].addActionListener(new BDefense());
		}	
		
		
		/* INIT des boutons des pièges */
		
		MesPieges = new JButton[4];
		
		MesPieges[0] = new JButton("Barriere", iconBarriere);
		MesPieges[0].setToolTipText("<html>Une barriere que les ennemis ne peuvent pas traverser vivants.<br/>" +
										"Ne peut etre placee que sur un bord droit ou gauche du chemin,<br/>" +
										"ou a la suite d'une autre barriere.<br/>" +
										"Ne peut pas obstruer totalement le chemin.<br/>" +
										"Son prix est de 50$.</html>");
			
		MesPieges[1] = new JButton("Piege 1", iconPiege1);
		MesPieges[1].setToolTipText("<html>Ce piege a une faible portee et cause des degats moderes a tous les ennemis.<br/>" +
										"Son prix est de 200$.</html>");
			
		MesPieges[2] = new JButton("Piege 2", iconPiege2);
		MesPieges[2].setToolTipText("<html>Ce piege a une portee raisonnable.<br/>" +
										"et multiplie par 2 l'argent recupere sur les ennemis abattus.<br/>" +
										"Effets non cumulable.<br/>" +
										"Son prix est de 500$.</html>");
		MesPieges[2].setEnabled(false);
		
		MesPieges[3] = new JButton("Piege 3", iconPiege3);
		MesPieges[3].setToolTipText("<html>Ce piege a une portee raisonnable et ralenti les ennemis.<br/>" +
										"Effets non cumulable.<br/>" +
										"Son prix est de 800$.</html>");
		MesPieges[3].setEnabled(false);
			
										
		for(int i = 0; i< MesPieges.length; i++){
			MonSousMenuTour.add(MesPieges[i]);
			MesPieges[i].addActionListener(new BDefense());
		}
		
		/* INIT des boutons concernant les attributs joueurs et du jeu */
		
		niveau = new JButton("Niveau : " + 1, iconNiveau);
		vie = new JButton(" PV : " + gameur.vie, iconVie);
		argent = new JButton("Argent : " + gameur.argent + "$", iconArgent);
		
		
		start = new JButton(" ", iconStart);
		start.addActionListener(new BDefense());
		
		infoJeu = new JLabel("Prepare tes defenses !");
		infoJeu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		infoJeu.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
		
		/* Intégration des boutons dans le menu */
		
		MonSousMenuJoueur.add(niveau);
		MonSousMenuJoueur.add(vie);
		MonSousMenuJoueur.add(argent);
		MonSousMenuJeu.add(start);
		MonSousMenuJeu.add(infoJeu);
		
		Groupement = new JPanel();
		Groupement.setLayout(new GridLayout(3,1));
		Groupement.add(MonSousMenuTour);
		Groupement.add(MonSousMenuJoueur);
		Groupement.add(MonSousMenuJeu);
		Groupement.setBounds(900,0,300,730);
		
		this.add(Groupement, JLayeredPane.DEFAULT_LAYER);
			
	}
	
	public void setJoueur(Joueur j){
		bizuth = j;
	}
	
	public void setVariable(int v){
		variable = v;
		if(variable != 0 && variable != 9){
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public int getVariable(){
		return variable;
	}
	
	public JLabel getInfoJeu(){
		return infoJeu;
	}
	
	public JButton getStart(){
		return start;
	}
	
	public void MajMenu(int niv){
		infoJeu.setText("Le niveau " + niv + " a commence !");
		niveau.setText("Niveau : " + niv);
	}
	
	public void MajVersPause(){ 
		start.setIcon(iconPause);
	}
	
	public void MajVersStart(){
		start.setIcon(iconStart);
	}
	
	public void reInit(){
		for(int i = 1 ; i < 4 ; i++){
			MesPieges[i].setEnabled(false);
			MesTours[i].setEnabled(false);
		}
		MesPieges[1].setEnabled(true);
	}			
	
	public static void affichageScore(){
		//Initialisation de la String des scores
		String str = new String();
		//Variables lecture fichier scores
		FileReader lecteur = null;
		BufferedReader tamponLecture = null;
		//Tableau des scores
		String[] scores = new String[5];
		
		try {
			lecteur = new FileReader("scores.txt");
			tamponLecture = new BufferedReader(lecteur);
			for(int r = 0 ; r < scores.length ; r++){
				//Lecture de la ligne r et stockage dans la String des scores
				str += tamponLecture.readLine() + "\n";
			}
		} catch (IOException e){
			e.printStackTrace();
		} finally {
			//On doit tout fermer
			try {
				tamponLecture.close();
				lecteur.close();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		JOptionPane scorePane = new JOptionPane();
		scorePane.showMessageDialog(null, str, "Records des autres Strateges", -1, null);
	}
	
	public void updateBoutons(int level){
		switch(level){
			case 3 :
				MesTours[1].setEnabled(true);
				MesPieges[2].setEnabled(true);
				break;
			case 6 :
				MesTours[2].setEnabled(true);
				MesPieges[3].setEnabled(true);
				break;
			case 8 :
				MesTours[3].setEnabled(true);
				break;
				
		}
	}
	
	public class BDefense implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String nomButton = ((JButton)(e.getSource())).getText();
			//Utilisation d'un setter pour mettre a jour le curseur en meme temps que variable
			//Bugs sinon...
			if(nomButton.equals("Tour 1")){
				if(bizuth.argent >= Tour1.PRIX){
					setVariable(1);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Tour 2")){
				if(bizuth.argent >= Tour2.PRIX){
					setVariable(2);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Tour 3")){
				if(bizuth.argent >= Tour3.PRIX){
					setVariable(3);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Tour 4")){
				if(bizuth.argent >= Tour4.PRIX){
					setVariable(4);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Barriere")){
				if(bizuth.argent >= Barriere.PRIX){
					setVariable(5);
					infoJeu.setText("Choisis la position de ta Barriere !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Piege 1")){
				if(bizuth.argent >= Piege1.PRIX){
					setVariable(6);
					infoJeu.setText("Choisis la position de ton piege !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Piege 2")){
				if(bizuth.argent >= Piege2.PRIX){
					setVariable(7);
					infoJeu.setText("Choisis la position de ton piege !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Piege 3")){
				if(bizuth.argent >= Piege3.PRIX){
					setVariable(8);
					infoJeu.setText("Choisis la position de ton piege !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals(" ")){
				setVariable(9);
			} else {
				setVariable(0);
			}

		System.out.println(variable);
		}
	}
	
	private class Tuto extends JDialog{
		/* CONSTRUCTEUR */
		public Tuto(JFrame parent, String title, boolean modal){
			super(parent, title, modal);
			setResizable(false);
			setLocationRelativeTo(null);
			initComponent();
			setVisible(true);
			
		}
		
		/* Initialisation de la fenetre */
		private void initComponent(){
			
			//Panel global
			JPanel globalPane = new JPanel();
			globalPane.setPreferredSize(new Dimension(650, 375));

			//Les ennemis
			JPanel panEnnemis = new JPanel();
			panEnnemis.setPreferredSize(new Dimension(650, 100));
			JPanel panEnnemisExp = new JPanel();
			panEnnemisExp.setPreferredSize(new Dimension(550, 90));
			panEnnemisExp.setLayout(new BorderLayout());
			panEnnemisExp.setBorder(BorderFactory.createTitledBorder("Les Ennemis"));
			JLabel iconEnnemis = new JLabel(new ImageIcon("sbire1Reverse.png"));
			JLabel ennemisLabel = new JLabel("<html>Des vagues d'ennemis vont deferler du nord pour attaquer le campement !<br/>Le seul moyen de nous defendre est de placer des defenses sur le chemin qui y mene.<br/>Plus tu combattras de vagues, plus il seront puissants. Fais donc attention !</html>");
			panEnnemisExp.add(ennemisLabel, BorderLayout.WEST);
			panEnnemis.add(panEnnemisExp);
			panEnnemis.add(iconEnnemis);
			globalPane.add(panEnnemis);
			
			//Les tours et pieges
			JPanel panTour = new JPanel();
			panTour.setPreferredSize(new Dimension(650, 100));
			JPanel panTourExp = new JPanel();
			panTourExp.setPreferredSize(new Dimension(600, 90));
			panTourExp.setLayout(new BorderLayout());
			panTourExp.setBorder(BorderFactory.createTitledBorder("Les defenses"));
			JLabel iconTour = new JLabel(iconTour1);
			panTour.add(iconTour);
			JLabel tourLabel = new JLabel("<html>Une fois achetees, les tours et les pieges te permettront de tuer les ennemis pour gagner de l'or!<br/>Tu en debloqueras certaines apres un certain nombre de vagues, elles ont des effets differents.<br/>Pour plus d'informations, il te suffira de laisser ta souris sur l'icone de la defense que tu veux.</html>");
			panTourExp.add(tourLabel, BorderLayout.WEST);
			panTour.add(panTourExp);
			globalPane.add(panTour);
			
			//Les barrieres
			JPanel panBarriere = new JPanel();
			panBarriere.setPreferredSize(new Dimension(650, 100));
			JPanel panBarriereExp = new JPanel();
			panBarriereExp.setPreferredSize(new Dimension(550, 90));
			panBarriereExp.setLayout(new BorderLayout());
			panBarriereExp.setBorder(BorderFactory.createTitledBorder("Les Barrieres"));
			JLabel iconBarr = new JLabel(iconBarriere);
			JLabel barriereLabel = new JLabel("<html>Les barrieres sont des defenses particulieres qui bloqueront certaines parties du chemin.<br/>Tu ne pourras pas les placer n'importe ou : renseigne-toi en laissant ta souris sur l'icone<br/>de la barriere. Elles sont disponibles des le debut du jeu !</html>");
			panBarriereExp.add(barriereLabel, BorderLayout.WEST);
			panBarriere.add(panBarriereExp);
			panBarriere.add(iconBarr);
			globalPane.add(panBarriere);
			
			//Bouton OK
			JButton okBouton = new JButton("OK !");
			okBouton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent a) {
					setVisible(false);	//Dialogue ferme lorsqu'on rend la fenetre invisible
				}      
			});
			globalPane.add(okBouton);
			
			//Ajout du composant global a la fenetre de dialogue
			this.getContentPane().add(globalPane);
			pack();
		}
	}
	
}
