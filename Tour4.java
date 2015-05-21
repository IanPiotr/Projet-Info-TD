import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

import java.io.File;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;

import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;

import javax.swing.*;

public class Tour4 extends Tour{

	private static final String nomImage = "Tour4.png";
	public static final int PRIX = 200;

	public Tour4(Rectangle fenetre, int x, int y){
		super(nomImage, x, y, PRIX, 50, 300, 0, 1);

	}
	
	public void tir(long cadence, ListeEnnemis le){
		int nbFocus = 0;
		Ennemis curE = le.root;
			while(curE != null){
				if(collision(curE) && cadence%50 == 0){
					if(nbFocus<focus){
						curE.setVie(puissance);
						nbFocus++;
					}
				}	
				if(curE != null){
					curE = curE.next;
				}
		}
	}

}
