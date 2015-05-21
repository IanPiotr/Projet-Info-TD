import java.awt.Rectangle;

public class Piege2 extends Tour{
	
	private static final String nomImage = "Piege2.png"; 
	public static final int PRIX = 100;
	
	public Piege2(int x, int y){
		super(nomImage, x, y, PRIX, 1, 50, 2, 10); //fosse de braises: brûle tous les ennemis dans la zone sans les ralentir
		
	}
	
	public void tir(long cadence, ListeEnnemis le){
		int nbFocus = 0;
		Ennemis curE = le.root;
			while(curE != null){
				if(collision(curE) && cadence%1 == 0){
					if(nbFocus<focus){
						curE.setVie(puissance);
						nbFocus++;
					}
				}	
				if(curE != null){
					curE = curE.next;
				}
		}
	}

}
