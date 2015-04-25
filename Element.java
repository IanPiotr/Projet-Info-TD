import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;

import java.io.File;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;

public abstract class Element {

	protected Image image;
	protected int largeur;		//Attributs largeur / hauteur utiles si jamais on veut faire grossir un ennemi ou une tour
	protected int hauteur;		//Il faudra alors des getter / setter speciaux
	protected int posx;
	protected int posy;
	protected Rectangle cadre;
	public Element next;
	
	public Element(){
		
	}
	
	public int getPosx(){
		return posx;
	}
	
	public int getPosy(){
		return posy;
	}
	
	public void setPosx(int x){
		posx = x;
		cadre.setLocation(getPosx(), getPosy());
	}
	
	public void setPosy(int y){
		posy = y;
		cadre.setLocation(getPosx(), getPosy());
	}
	
	public void setPos(int x, int y){
		posx = x;
		posy = y;
		cadre.setLocation(getPosx(), getPosy());
	}
	
	public void draw(Graphics g){
		g.drawImage(image, posx, posy, null);
	}
	
	public boolean collision(Element elem){
		return this.cadre.intersects(elem.cadre);
	}
	
	/*public abstract void moveAleatoire();
	public abstract void moveBasique(boolean h, boolean v);
	public abstract void moveBrownien();*/
	
}
