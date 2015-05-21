import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class Joueur {
	
	public String name;
	public int argent;
	protected int vie;
	JPanel cadreJoueur;


	public Joueur(String nomJoueur){
		
		argent = 5000; // à déterminer
		vie = 500; // à déterminer
		name = nomJoueur; 
		
		cadreJoueur = new JPanel();
        cadreJoueur.setLayout(new GridLayout(10,1));
        cadreJoueur.setBackground(Color.red);
        //JLabel monLabel = new JLabel(name);
        JButton monJButton = new JButton(name);
        //cadreJoueur.add("North", monLabel);
        cadreJoueur.add(monJButton);
        
	}
	
	public void setVie(int degats){
		vie -= degats;
	}
	
	public int getVie(){
		return vie;
	}
	
	
}

