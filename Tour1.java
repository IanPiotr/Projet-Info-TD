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
	public static final int PRIX = 300;
	public static final int RANGE = 250;

	public Tour1(Rectangle fenetre, int x, int y){
		super(nomImage, x, y, PRIX, 50, RANGE, 0, 10); //Tour fusil a pompe
		rate = 70;
	}

}

