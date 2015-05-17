import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;

import java.io.File;

import javax.imageio.ImageIO;

public class Barriere extends Element{
	
	private static final String nomImage = "Barriere.png";
	public static final int PRIX = 200;
	//protected Barriere next;

	public Barriere(int px, int py, Case[][] tabCases, Chemin chemin){
		super();
		
		try {
			image= ImageIO.read(new File(nomImage));
        } catch(Exception err){
			System.out.println(nomImage+" introuvable !");            
            System.exit(0);    
        }
        /*px += 25;
        py += 12;
        
        int x = (int)((px + hauteur)/Case.LCASE);;
        int y = (int)((py + hauteur)/Case.LCASE);
        posx = x*Case.LCASE + 5;
        posy = y*Case.LCASE + 3;*/
        posx = px;
        posy = py;
        int j = (int)((posy + hauteur)/Case.LCASE);
		int i = (int)(posx/Case.LCASE);
		for(int c = i ; c <= i+1 ; c++){
			tabCases[c][j].setChemin(false);
			tabCases[c][j].setBarriere(true);
		}

		chemin.initBordures(tabCases);	
        		
		next = null;
		
		largeur = image.getWidth(null);
		hauteur = image.getHeight(null);
		
		cadre = new Rectangle(posx, posy, largeur, hauteur);
		
	}
	
}
