import java.awt.Rectangle;

public class Piege1 extends Tour{
	
	private static final String nomImage = "Piege1.png"; 
	public static final int PRIX = 200;
	public static final int RANGE = 50;
	
	public Piege1(int x, int y){
		super(nomImage, x, y, PRIX, 1, RANGE, 2, 10); //fosse de braises: brûle tous les ennemis dans la zone sans les ralentir
		rate = 1;
	}

}
