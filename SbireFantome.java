import java.awt.Rectangle;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;

public class SbireFantome extends Ennemis{
	
	public static final String nomImage = "SbireFantome.png";
	public static final String nomImageReverse = "SbireFantomeReverse.png";

	public SbireFantome(Rectangle fenetre, int num, int x, int y){
		super(fenetre, num, x, y, 1000, nomImage);
		recompense = 100;
		setVitesse(1);
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
