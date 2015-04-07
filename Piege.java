import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;


public abstract class Piege extends Element{
	
	protected int prix;

	public Piege(String nomImage, int cout){
		super();
		
		try {
			image= ImageIO.read(new File(nomImage));
        } catch(Exception err){
			System.out.println(nomImage+" introuvable !");            
            System.exit(0);    
        }
        
        prix = cout;
        
        largeur = image.getWidth(null);
		hauteur = image.getHeight(null);
		
		cadre = new Rectangle(posx, posy, largeur, hauteur);
	}

}
