import java.awt.Rectangle;

public class Piege3 extends Tour{
	
	private static final String nomImage = "Piege3.png"; 
	public static final int PRIX = 800;
	public static final int RANGE = 200;
	
	public Piege3(int x, int y){
		super(nomImage, x, y, PRIX, 0, RANGE, 1, 10); //mare de goudron : pas de dégats infligés, mais ralentissement de tous les ennemis (10 arbitraire)
	}
	
	public void tir(long cadence, ListeEnnemis le){
		int nbFocus = 0;
		Ennemis curE = le.root;
		while(curE != null){
			if(collision(curE)){
				if(nbFocus<focus){
					//Ralentissons l'ennemi courant :
					curE.setVitesse(coeffVit);
					//Faisons savoir aux autres pieges ralentisseurs que nous le ralentissons :
					curE.setRalenti(true);
					nbFocus++;
				}
			//Si ni nous ni quelqu'un d'autre ne le ralentissons, il gagne le droit de reprendre sa vitesse normale :
			} else if(!curE.isRalenti() && !(curE instanceof SbireFantome)){
				curE.setVitesse(2);
			}
				
			if(curE != null){
				curE = curE.next;
			}
		}
	}

}
