import java.awt.Rectangle;

public class Piege1 extends Tour{
	
	private static final String nomImage = "Piege1.png";
	public static final int PRIX = 50;
	
	public Piege1(int x, int y){
		super(nomImage, x, y, PRIX, 2, 100);
	}

}
