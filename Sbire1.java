import java.awt.Rectangle;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;

public class Sbire1 extends Ennemis{
	
	public static final String nomImage = "Sbire1.png";
	public static final String nomImageReverse = "Sbire1Reverse.png";
	public static final int VIE_MIN = 60;

	public Sbire1(Rectangle fenetre, int num, int x, int y, int hp){
		super(fenetre, num, x, y, hp, nomImage);
		recompense = 25;
	}
	
	public void setDposx(int dx){
		dposx = dx;
		if(dposx >= 0){
			setImage(nomImage);
		} else {
			setImage(nomImageReverse);
		}
	}

}
