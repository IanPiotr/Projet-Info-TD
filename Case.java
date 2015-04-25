import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.io.File;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

public class Case extends Rectangle{
	
	protected static final int LCASE = 30; //dimension case
	protected boolean occupe; 	//true si la case est occupée par un élément (tour ou piège), false si elle est libre
	protected boolean chemin; 	//true si c'est une case située dans le "chemin"
	protected boolean bordure;	//true si cette case est une bordure du chemin
	protected boolean hybride;
								//NB : False par defaut dans le premier constructeur
	public Case next;
	
	public Case(int posx, int posy){
		super(posx, posy, LCASE, LCASE);
		occupe = false;
		chemin = false;
		bordure = false;
		hybride = false;
		next = null;
	}
	
	public Case(int posx, int posy, boolean isCh, boolean isOccup){
		super(posx, posy, LCASE, LCASE);
		occupe = isCh;
		chemin = isOccup;
		bordure = false;
		hybride = false;
		next = null;
	}
		
	public void drawCase(Graphics g){
		g.fillRect(x, y, width, height);
	}
	
	public void setChemin(boolean ch){
		chemin = ch;
	}
	
	public boolean isChemin(){
		return chemin;
	}
	
	
}
