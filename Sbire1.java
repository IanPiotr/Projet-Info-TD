import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

public class Sbire1 extends Ennemis{
	
	public static final String nomImage = "Sbire1.png";
	
	public Sbire1(Rectangle fenetre){
		super(fenetre, nomImage, fenetre.width/4, fenetre.height/4, 70, 3);
	}
	
	public Sbire1(Rectangle fenetre, int x, int y){
		super(fenetre, nomImage, x, y, 70, 2);	
	}
	
	public Sbire1(Rectangle fenetre, int x, int y, int num){
		super(fenetre, nomImage, x, y, 70, 2);	
		IDEnnemi = num;
	}

}
