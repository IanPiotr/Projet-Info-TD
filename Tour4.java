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
	public static final int PRIX = 1000;
	public static final int RANGE = 300;

	//Une tour Mortier plus performante sur les fantomes
	public Tour4(Rectangle fenetre, int x, int y){
		super(nomImage, x, y, PRIX, 40, RANGE, 0, 1);
		rate = 80;
	}
	
	public void tir(long cadence, ListeEnnemis le){
		int nbFocus = 0;
		aTire = 7;
		Ennemis curE = le.root;
		while(curE != null){
			if(collision(curE) && cadence%rate == 0){
				if(nbFocus<focus){
					if(curE instanceof SbireFantome){
						curE.setVie(2*puissance);
					} else {
						curE.setVie(puissance);
					}
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
