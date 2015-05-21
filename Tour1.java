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

public class Tour1 extends Tour{

	private static final String nomImage = "Tour1.png";
	public static final int PRIX = 200;

	public Tour1(Rectangle fenetre, int x, int y){
		super(nomImage, x, y, PRIX, 5, 200, 0, 1); //tour standard, Focus 1 ennemi
		rate = 4;
	}

}
