import java.awt.Rectangle;

public class Niveau{

	private int nbEnnemis;
	private int xMin;
	private int xMax;
	private static final int ENNEMIS_DE_BASE = 2;
	private static final int ENNEMIS_PAR_NIVEAU = 1;
	private static final int VIE_ENNEMIS_BASE = 70;
	private static final int VIE_ENNEMIS_PAR_NIVEAU = 10;

	/* MATRICE DU NIVEAU EN COURS
	 * Genere a partir du numero du niveau
	 * Contient toutes les donnees relatives aux ennemis qui vont apparaitre
	 * Une ligne contient les données d'un ennemis
	 * Autant de lignes que d'ennemis
	 * Colonne 0 : Type de l'Ennemi (1 = Sbire1, 2 = Sbire2)
	 * Colonne 1 : IDEnnemi
	 * Colonne 2 : position en x
	 * Colonne 3 : position en y
	 * Colonne 4 : vie
	 */
	protected int[][] matriceC;
	
	public Niveau(int level, int xSpawnMin, int xSpawnMax){
		nbEnnemis = ENNEMIS_DE_BASE + level*ENNEMIS_PAR_NIVEAU;
		genererMatrice(level, xSpawnMin, xSpawnMax);
	}
	
	private void genererMatrice(int level, int xmin, int xmax){
			
			//Generons la matrice suivante matriceC
			matriceC = new int[5][nbEnnemis];
			int ID = 0;
			for(int i = 0 ; i < matriceC[0].length ; i ++){
				matriceC[0][i] = genererTypeEnnemi(level);
				matriceC[1][i] = ID;
				ID++;
				matriceC[2][i] = genererPosx(xmax, xmin);
				matriceC[3][i] = -31;
				matriceC[4][i] = genererVie2(level, i);
			}
	}
	
	public Ennemis genererEnnemi(int numSpawn, Rectangle ecran){
		return new Ennemis(ecran, matriceC[0][numSpawn], matriceC[1][numSpawn], matriceC[2][numSpawn], matriceC[3][numSpawn], matriceC[4][numSpawn]);
	}
	
	/* METHODES DIVERGENTES DE GENERATION COEFFICIENTS MATRICE 
	 * Fonctions de references pour generer les colonnes de la matrice matriceC
	 * Toutes renvoient un entier
	 */
	private int genererVie1(int level){
		return VIE_ENNEMIS_BASE + level*VIE_ENNEMIS_PAR_NIVEAU;
	}
	
	private int genererPosx(int xmax, int xmin){
		return xmin + (int)((xmax - xmin)*Math.random());
	}
	//*//
	
	/* METHODES CONVERGENTES DE GENERATION COEFFICIENTS MATRICES
	 * VIE de 60 a 149, 150 a 249, (250 a 409)-> 10 a 159 pour les tests, ou pas d'ailleurs (concept interessant)
	 * Convergence pour le niveau 10 vers la vie maximale, au facteur aleatoire pres
	 */
	 private int genererTypeEnnemi(int level){
		//Pas d'ennemis d'upgrade 2 avant le niveau 3, car 2.8e^-0.2 = 2.29 > 2*x, x dans [0,1[
		//Niveau 3 : environ une chance sur trois pour avoir un ennemis d'upgrade 2, car 2.8e^-0.3 = 2.07 sur 2.999
		//Niveau 4 : environ une chance sur deux, car 2.8e^-0.4 = 1.89 sur 3.999
		//L'intervalle tant vers 0 par la suite, donc la proba d'avoir un sbire d'upgrade 1 aussi.
		if((level)*Math.random() < 2.8*Math.exp(-level*0.1)){
			return 1;
		//Pas d'ennemis d'upgrade 3 avant le niveau 7, car 20e^-1.2 = 6.02 > 6*x, x dans [0,1[
		//Niveau 7 : environ 2 chances sur 7 d'avoir un ennemis d'upgrade 3, car 20e^-1.4 = 4.93 sur 6.999
		//Niveau 8 : 1 chance sur 2 environ, car 20e^-1.7 = 4.93 sur 7.999
		//Puis de meme l'intervalle tend vers 0, donc la proba d'avoir un ennemis d'pgrade 2 aussi
		} else if((level)*Math.random() < 20*Math.exp(-level*0.2)){
			return 2;
		} else return 3;
	}
	
	private int genererVie2(int level, int numSpawn){
		int vie = 0;
		int probaMax = 0;
		int base = 0;
		switch(matriceC[0][numSpawn]){
			case 1 :
				vie = 60;
				probaMax = 40;
				base = 50;
				break;
			case 2 :
				vie = 150;
				probaMax = 70;
				base = 30;
				break;
			case 3 :
				vie = 10;
				probaMax = 80;
				base = 80;
				break;
		}
		return vie + (int)((base +probaMax*Math.random())*Math.exp(-10/(level+1)));
	}
	//*//
	
}
		
