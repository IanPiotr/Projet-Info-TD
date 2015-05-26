 import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;

import java.io.File;

import javax.imageio.ImageIO;


public abstract class Tour extends Element {
	
	public int prix;
	protected int puissance; //nombre de dégâts infligés à un ennemi par tir
	protected Arc2D.Double portee;
	protected int coeffVit; //coefficient Vitesse: variable pour le ralentissement des ennemis: coeffVit vitesse standard = 2; coeffVitvitesse ralentie = 1
	public Tour next;
	protected int focus; //nombre d'ennemis que la tour peut "focus" pour un tir
	public Ennemis[] cibles;
	protected int rate;	//cadence de tir
	public int aTire;	//durée d'affichage du tir (en tours de boucles)
	
	public Tour(String nomImage, int px, int py, int cout, int force, int range, int c, int nbFocus){
		super();
		
		try {
			image= ImageIO.read(new File(nomImage));
        } catch(Exception err){
			System.out.println(nomImage+" introuvable !");            
            System.exit(0);    
        }
        posx = px;
        posy = py;

		prix = cout;
		puissance = force;
		coeffVit = c;
		focus = nbFocus;
		cibles = new Ennemis[focus];
		aTire = 0;
		
		next = null;
		
		largeur = image.getWidth(null);
		hauteur = image.getHeight(null);
		
		cadre = new Rectangle(posx, posy, largeur, hauteur);
		portee = new Arc2D.Double(posx - range/2 + largeur/2, posy - range/2 + hauteur/2, range, range, 0, 360, Arc2D.OPEN);
	}
	
	/*REDEFINITION DE LA METHODE COLLISION DE ELEMENT
	 * Pour avoir une meilleur hitbox entre la portee de la tour
	 * et les ennemis
	 */
	public boolean collision(Element elem){
		return this.portee.intersects(elem.cadre);
	}
	//*//
	
	public void tir(long cadence, ListeEnnemis le){
		int nbFocus = 0;
		aTire = 7;
		Ennemis curE = le.root;
		while(curE != null){	//on parcourt la liste d'ennemis
			if(collision(curE) && cadence%rate == 0){ //si l'ennemi est à portée de tir de la tour, et que la tour est prête à tirer
				if(nbFocus<focus){ //on vérifie que la tour n'a pas dépassé son nombre max de "focus"
					curE.setVie(puissance); //on enlève des points de vie à l'ennemi
					cibles[nbFocus] = curE;
					nbFocus++; 
				}
			}	
			if(curE != null){
				curE = curE.next;
			}
		}
	}
}

