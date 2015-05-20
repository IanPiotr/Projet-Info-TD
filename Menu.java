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

public class Menu extends JLayeredPane {

	private JPanel MonSousMenuTour;
	private JPanel MonSousMenuJoueur;
	private JPanel Groupement;
	private JPanel MonSousMenuJeu;
	private JButton [] MesTours;
	private JButton [] MesPieges;
	private JButton start;
	private JButton niveau;
	protected JButton argent;
	protected JButton vie;
	protected JLabel infoJeu; // permet de donner des infos au joueur durant le jeu
	protected JOptionPane debutJeu;
	protected JOptionPane tuto;
	private static final ImageIcon iconTour1 = new ImageIcon("Tour1.png");
	private static final ImageIcon iconPiege1 = new ImageIcon("Piege1.png");
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
		
		
		String[] intro = {"Jouer", "Tutoriel"};
		debutJeu = new JOptionPane();
		int rang = debutJeu.showOptionDialog(null, "Salut toi, soit tu joues soit tu te casses !", "Tower Defense !", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, intro, intro[1]);
		if(rang == 1){
			System.out.println("Tu as choisi le tuto");
			tuto = new JOptionPane();
			tuto.showMessageDialog(null, "Le tower defense est un jeu très simple. L’objectif est d'empêcher des vagues successives d’ennemis de traverser le chemin.\n Tu perdras de la vie à chaque fois qu'un ennemi aura traversé complètement la chemin. Pour combattre les monstres, tu dois \n acheter différents pièges ou tours défensives avec l'argent que tu as gagné au cours de la partie en tuant des ennemis. Le jeu se \n présente sous forme de niveaux de plus en plus difficiles. A toi de faire tes preuves ! ", "Tutoriel", JOptionPane.INFORMATION_MESSAGE);
		}
		
		MesTours = new JButton[4];
		for(int i = 0; i< MesTours.length; i++){
			int a = i+1;
			MesTours[i] = new JButton("Tour " +a, iconTour1);
			MonSousMenuTour.add(MesTours[i]);
			MesTours[i].addActionListener(new BDefense());
		}
		
		MesPieges = new JButton[4];
		MesPieges[0] = new JButton("Barriere", iconBarriere);
			MonSousMenuTour.add(MesPieges[0]);
			MesPieges[0].addActionListener(new BDefense());
			MesPieges[0].setToolTipText("<html>Une barriere que les ennemis ne peuvent pas traverser vivants.<br/>" +
										"Ne peut etre placee que sur un bord droit ou gauche du chemin,<br/>" +
										"ou a la suite d'une autre barriere.<br/>" +
										"Ne peut pas obstruer totalement le chemin.</html>");
		for(int i = 1; i< MesPieges.length; i++){
			int a = i+1;
			MesPieges[i] = new JButton("Piege " +a, iconPiege1);
			MonSousMenuTour.add(MesPieges[i]);
			MesPieges[i].addActionListener(new BDefense());
		}
		
		niveau = new JButton("Niveau : " + 0, iconNiveau);
		vie = new JButton(" PV : " + gameur.vie, iconVie);
		argent = new JButton("Argent : " + gameur.argent + "$", iconArgent);
		MonSousMenuJoueur.add(niveau);
		MonSousMenuJoueur.add(vie);
		MonSousMenuJoueur.add(argent);
		
		start = new JButton(" ", iconStart);
		start.addActionListener(new BDefense());
		
		infoJeu = new JLabel("Bonjour à toi mon petit, prépares toi!");
		infoJeu.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
		infoJeu.setVerticalAlignment(javax.swing.SwingConstants.CENTER);
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
				if(bizuth.argent >= Tour1.PRIX){
					setVariable(2);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Tour 3")){
				if(bizuth.argent >= Tour1.PRIX){
					setVariable(3);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Tour 4")){
				if(bizuth.argent >= Tour1.PRIX){
					setVariable(4);
					infoJeu.setText("Choisis la position de ta tour !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Barriere")){
				if(bizuth.argent >= Piege1.PRIX){
					setVariable(5);
					infoJeu.setText("Choisis la position de ton piege !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Piege 2")){
				if(bizuth.argent >= Piege1.PRIX){
					setVariable(6);
					infoJeu.setText("Choisis la position de ton piege !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Piege 3")){
				if(bizuth.argent >= Piege1.PRIX){
					setVariable(7);
					infoJeu.setText("Choisis la position de ton piege !");
				} else {
					infoJeu.setText("Tu n'as pas assez d'argent !");
				}
			} else if(nomButton.equals("Piege 4")){
				if(bizuth.argent >= Piege1.PRIX){
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
	
}
