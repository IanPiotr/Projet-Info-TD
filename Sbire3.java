import java.awt.Rectangle;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;

public class Sbire3 extends Ennemis{
	
	public static final String nomImage = "Sbire3.png";
	public static final String nomImageReverse = "Sbire3Reverse.png";
	public static final int VIE_MIN = 30;

	public Sbire3(Rectangle fenetre, int num, int x, int y, int hp){
		super(fenetre, num, x, y, hp, nomImage);
		recompense = 30;
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
