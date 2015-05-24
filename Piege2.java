import java.awt.Rectangle;

public class Piege2 extends Tour{
	
	private static final String nomImage = "Piege2.png"; 
	public static final int PRIX = 500;
	public static final int RANGE = 200;
	
	public Piege2(int x, int y){
		super(nomImage, x, y, PRIX, 0, RANGE, 2, 10); //éventuellement presse à monnaie: pas de dégâts ni de ralentissement, mais tous les ennemis tués dans le champ d'action rapportent 2x plus
		
	}
	
	public void tir(long cadence, ListeEnnemis le){
		int nbFocus = 0;
		Ennemis curE = le.root;
			while(curE != null){
				if(collision(curE)){
					curE.setBeni(true);
				}
				if(curE != null){
					curE = curE.next;
				}
		}
	}

}
