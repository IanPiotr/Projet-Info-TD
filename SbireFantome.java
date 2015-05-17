import java.awt.Rectangle;

public class SbireFantome extends Ennemis{
	
	public static final String nomFantome = "SbireFantome.png";

	public SbireFantome(Rectangle fenetre, int num, int x, int y){
		super(fenetre, 4, num, x, y, 1000);
		setVitesse(1);
	}

}
