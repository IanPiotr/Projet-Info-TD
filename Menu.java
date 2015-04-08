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


public class Menu extends JLayeredPane {

	private JPanel MonSousMenuTour;
	private JPanel MonSousMenuJoueur;
	private JPanel Groupement;
	private JPanel MonSousMenuJeu;
	private JButton [] MesTours;
	private JButton [] MesPieges;
	private JButton start;
	protected JButton argent;
	private JButton vie;
	private JLabel infoJeu; // permet de donner des infos au joueur
	private static final ImageIcon iconTour1 = new ImageIcon("Tour1.png");
	private static final ImageIcon iconPiege1 = new ImageIcon("Piege1.png");
	private static final ImageIcon iconVie = new ImageIcon("coeur.png");
	private static final ImageIcon iconStart = new ImageIcon("start.png");
	private static final ImageIcon iconArgent = new ImageIcon("lingot.png");
	private static final ImageIcon iconUser = new ImageIcon("user.png");
	private int variable = 0; //permet de savoir quelle tour ou piège va etre pose (1 = tour1 ... 4 = piege1 ..)
	private Joueur bizuth;
	
	
	public Menu(Joueur gameur, Fenetre fenetreJeu){
		super();
		setPreferredSize(new Dimension(285, 740));
		setDoubleBuffered(true);
		/*setMinimumSize(new Dimension(285, 740));
		setMaximumSize(new Dimension(285, 740));*/
		
		MonSousMenuTour = new JPanel();
		MonSousMenuTour.setLayout(new GridLayout(4,2));
		MonSousMenuJoueur = new JPanel();
		MonSousMenuJoueur.setLayout(new GridLayout(3,1));
		MonSousMenuJeu = new JPanel();
		MonSousMenuJeu.setLayout(new GridLayout(2,1));
		MonSousMenuJeu.setBackground(Color.orange);
		
		bizuth = gameur;
		
		MesTours = new JButton[4];
		for(int i = 0; i< MesTours.length; i++){
			int a = i+1;
			MesTours[i] = new JButton("Tour " +a, iconTour1);
			MonSousMenuTour.add(MesTours[i]);
			MesTours[i].addActionListener(new BDefense());
		}
		
		MesPieges = new JButton[4];
		for(int i = 0; i< MesPieges.length; i++){
			int a = i+1;
			MesPieges[i] = new JButton("Piege " +a, iconPiege1);
			MonSousMenuTour.add(MesPieges[i]);
			MesPieges[i].addActionListener(new BDefense());
		}
		
		JButton nom = new JButton("Nom : " + gameur.name, iconUser);
		vie = new JButton(" PV : " + gameur.vie, iconVie);
		argent = new JButton("Argent : " + gameur.argent + "$", iconArgent);
		MonSousMenuJoueur.add(nom);
		MonSousMenuJoueur.add(vie);
		MonSousMenuJoueur.add(argent);
		
		JButton start = new JButton(" ", iconStart);
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
		
		this.add(Groupement, new Integer(200));
			
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
			} else if(nomButton.equals("Piege 1")){
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
	
	
	public static void main (String args[]) {
	Fenetre test = new Fenetre();
	}

}
