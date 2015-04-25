public abstract class GeneAleat{
	
	/*coefficients utilisés par la methode aleat2. En les modifiant, on peut modifier l'allure du chemin abtenu.*/
	public static final double ATTENUATION_LAT = 0.95;
	public static final double ATTENUATION_BAS = 0.8;
	public static final double DEBUT_CH_DIR = 0.9;
	
	/*POUR MULTIPLIER DEUX MATRICES*/
	public static double[][] produitMatrice( double[][] m1, double[][] m2){
		double res[][] = new double[m1.length][m2[0].length];
		double tmp = 0;
		//System.out.println(res.length + " , " +res[0].length);
		for(int i=0; i<res.length;i++){
			for(int j=0;j<res[0].length; j++){
				tmp = 0;
				for(int k = 0; k<m1[0].length; k++){
					tmp += m1[i][k]*m2[k][j];
				}
				res[i][j] = tmp;
			}
		}
		return res;
	}
	
	/*POUR GENERER LE CHEMIN PREDEFINI EN ATTENDANT QUE LA GENERATION ALEATOIRE SOIT AU POINT*/
	public static void predefini(Case[][] tabCases){
		
		for(int i=15; i<19; i++){
			for(int j=0; j<10; j++){
				tabCases[i][j].setChemin(true);
			}
		}
        for(int i=18; i>9; i--){
			for(int j=10; j<14; j++){
				tabCases[i][j].setChemin(true);
			}
		}
		for(int i=13; i>9; i--){
			for(int j=14; j<18; j++){
				tabCases[i][j].setChemin(true);
			}
		}
		for(int i=10; i<25; i++){
			for(int j=18; j<22; j++){
				tabCases[i][j].setChemin(true);
			}
		}
		for(int i=21; i<25; i++){
			for(int j=21; j<25; j++){
				tabCases[i][j].setChemin(true);
			}
		}
	}
		
		/*PREMIERE METHODE DE GENERATION ALEATOIRE*/
		public static void aleat1(Case[][] tabCases){
			
			/* GENERATION TRAJET*/
			int l = tabCases.length;
			int h = tabCases[0].length;
			int xdebut = 10;
			int x = xdebut;
			int y = 0;
			String dir = "bas";
			double rand;
			double[][] matProba = {	{0.7, 0.3, 0  },	//chaque a(i,j) represente la probabilité de passer de la direction i a la direction j (voir tp matlab).
									{0.2, 0.6, 0.2 },	//donc la somme des coefs d'une ligne doit etre égale a 1.
									{0  , 0.3, 0.7 }};	//De plus, la probabilite de passer de droite a gauche ou l'inverse est nulle
			
			double[][] dirProba = {	{0, 1, 0}};
			
			while(y<h-1){
				for (int i = 0;i<dirProba.length;i++){
					for(int j = 0; j<dirProba[0].length;j++){
						System.out.print("  " + dirProba[i][j] + "  ");
					}
					System.out.println();
				}
				
				
				rand = Math.random();
				if(rand<dirProba[0][0] && x>1){
					dir = "gauche";
					dirProba[0][0] = 1;
					dirProba[0][1] = 0;
					dirProba[0][2] = 0;
				}else if(rand<dirProba[0][0]+dirProba[0][1]){
					dir = "bas";
					dirProba[0][0] = 0;
					dirProba[0][1] = 1;
					dirProba[0][2] = 0;
				}else if(x<l-4){
					dir = "droite";
					dirProba[0][0] = 0;
					dirProba[0][1] = 0;
					dirProba[0][2] = 1;
				}
				int larg = (int)(Math.random()*2)+2;
				switch (dir){
					case "gauche":		//vers la gauche
						x--;
						for(int i = 0; i<larg; i++){
							tabCases[x][y-i].setChemin(true);
						}
						break;
						
					case "bas":		//vers le bas
						y++;
						for(int i = 0; i<larg; i++){
							tabCases[x+i][y].setChemin(true);
						}
						break;
						
					case "droite":		//vers la droite
						x++;
						for(int i = 0; i<larg; i++){
							tabCases[x][y-i].setChemin(true);
						}
						break;
						
				}
				//System.out.println(" x = " + x + " et y = " + y);
				System.out.println(dir);
				tabCases[x][y].setChemin(true);
				dirProba = produitMatrice(dirProba, matProba);
			}
		}
		
		/*DEUXIEME METHODE DE GENERATION ALEATOIRE (PLUS ADAPTEE QUE LA PREMIERE)*/
		public static void aleat2(Case[][] tabCases){
			
			/* GENERATION TRAJET*/
			int l = tabCases.length;
			int h = tabCases[0].length;
			int xdebut = 10;
			int x = xdebut;
			int y = 0;
			double tmp;
			double rand;
			int dir = 1;			// 0=droite, 1=bas, 2=gauche
			int dirprec = 1;
			double[] dirProba = {0,1,0};
			
			while(y<h-1){
				
				System.out.println(dir);
				System.out.print((double)Math.round(dirProba[0]*100)/100 + "  " + (double)Math.round(dirProba[1]*100)/100 + "  " + (double)Math.round(dirProba[2]*100)/100);
				System.out.println();
				
				rand = Math.random();
				if(rand<dirProba[0] && x>1){	//gauche
					if(dir ==0){				//si le chemin est deja en train d'aller à gauche, la probabilité de continuer à aller à gauche diminue petit a petit. De même s'il descend ou va vers la droite
						dirProba[0] = dirProba[0]*ATTENUATION_LAT;
						dirProba[1] = 1-dirProba[0];
						dirProba[2] = 0;
					}else{						//sinon, elle est fixée arbitrairement
						dir = 0;
						dirProba[0] = DEBUT_CH_DIR;
						dirProba[1] = 1-DEBUT_CH_DIR;
						dirProba[2] = 0;
					}
				}else if(rand<dirProba[0]+dirProba[1]){	//bas
					if(dir ==1){
						dirProba[1] = dirProba[1]*ATTENUATION_BAS;
						dirProba[0] = (1-dirProba[1])/2;
						dirProba[2] = (1-dirProba[1])/2;
					}else{
						dir = 1;
						dirProba[1] = DEBUT_CH_DIR;
						dirProba[0] = (1-DEBUT_CH_DIR)/2;
						dirProba[2] = (1-DEBUT_CH_DIR)/2;
					}	
				}else if(x<l-4){	//droite
					if(dir ==2){
						dirProba[2] = dirProba[1]*ATTENUATION_LAT;
						dirProba[0] = 0;
						dirProba[1] = 1-dirProba[1];
					}else{
						dir = 2;
						dirProba[1] = DEBUT_CH_DIR;
						dirProba[0] = 0;
						dirProba[2] = 1-DEBUT_CH_DIR;
					}			
				}
				
				int larg =3; /*(int)(Math.random()*2)+2;*/
				switch (dir){
					case 0:		//vers la gauche
						x--;
						for(int i = 0; i<larg; i++){
							if(y+i<h-1){
								tabCases[x][y+i].setChemin(true);
							}
						}
						break;
					case 1:		//vers le bas
						y++;
						for(int i = 0; i<larg; i++){
							tabCases[x+i][y].setChemin(true);
						}
						break;
					case 2:		//vers la droite
						x++;
						for(int i = 0; i<larg; i++){
							if(y+i<h-1){
								tabCases[x][y+i].setChemin(true);
							}
						}
						break;
				}
				tabCases[x][y].setChemin(true);
			}
		}
		
	}
