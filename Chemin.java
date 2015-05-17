public class Chemin{
	
	/*coefficients utilisés par la methode aleat2. En les modifiant, on peut modifier l'allure du chemin obtenu.*/
	public final double ATTENUATION_LAT = 0.99;
	public final double ATTENUATION_BAS = 0.5;
	public final double DEBUT_CH_DIR = 0.8;
	
	protected ListeCases bordureDroite;
    protected ListeCases bordureGauche;
    protected ListeCases bordureHaut;
    protected ListeCases bordureBas;
    
    protected static String nomImDroite = "bordureDroite.png";
    protected static String nomImGauche= "bordureGauche.png";
    protected static String nomImHaut= "bordureHaut.png";
    protected static String nomImBas= "bordureBas.png";
    protected static String nomImHautDroite= "bordureDroiteHaut.png";
    protected static String nomImHautGauche= "bordureGaucheHaut.png";
    protected static String nomImBasDroite= "bordureDroiteBas.png";
    protected static String nomImBasGauche= "bordureGaucheBas.png";
    protected static String nomImBasGaucheCreux= "bordureGaucheBasCreux.png";
    protected static String nomImBasDroiteCreux= "bordureDroiteBasCreux.png";
    protected static String nomImHautGaucheCreux= "bordureGaucheHautCreux.png";
    protected static String nomImHautDroiteCreux= "bordureDroiteHautCreux.png";
    protected static String nomImBasHautDroite= "bordureDroiteHautBas.png";
    protected static String nomImBasHautGauche= "bordureGaucheHautBas.png";
    protected static String nomImBasCreuxHautDroite= "bordureBasCreuxDroiteHaut.png";
    protected static String nomImBasCreuxHautGauche= "bordureBasCreuxGaucheHaut.png";
    protected static String nomImHautCreuxBasGauche= "bordureHautCreuxGaucheBas.png";
    protected static String nomImHautCreuxBasDroite= "bordureHautCreuxDroiteBas.png";
    protected static String nomImCreuxDroiteHautBas=  "bordureCreuxDroiteHautBas.png";
    protected static String nomImCreuxGaucheHautBas=  "bordureCreuxGaucheHautBas.png";
    protected static String nomImHautBas= "bordureHautBas.png";

    public Chemin (Case[][] tabCases){
		aleat2(tabCases);
		initBordures(tabCases);
		majImaj(tabCases);
	}
	
	/* INIT BORDURES CHEMIN
		 * Une case ne doit pas appartenir a deux listes, sinon conflit (pointeurs)
		 * Donc insertion de clones et non de la case originel si elle est deja dans une autre liste
		 */
		public void initBordures(Case[][] tabCases){
			bordureBas = new ListeCases();
			bordureHaut = new ListeCases();
			bordureGauche = new ListeCases();
			bordureDroite = new ListeCases();
			//Reinitialisation hybride et bordure pour la mise a jour du chemin
			//(Sinon, toutes les bordures deviennent hybrides)
			for(int i=0; i<tabCases.length; i++){
				for(int j=0; j<tabCases[0].length;j++){
					tabCases[i][j].setHybride(false);
					tabCases[i][j].setBordure(false);
				}
			}
			for(int i=0; i<tabCases.length-1; i++){
				for(int j=0; j<tabCases[0].length-1; j++){
					if(!tabCases[i][j].chemin && tabCases[i+1][j].chemin){
						if(!tabCases[i][j].bordure){
							bordureGauche.insertTete(tabCases[i][j]);
							tabCases[i][j].bordure = true;
						} else {
							Case nc = (Case)tabCases[i][j].clone();
							tabCases[i][j].hybride = true;
							nc.hybride = true;
							bordureGauche.insertTete(nc);
						}
					} else if(tabCases[i][j].chemin && !tabCases[i+1][j].chemin){
						if(!tabCases[i+1][j].bordure){
							bordureDroite.insertTete(tabCases[i+1][j]);
							tabCases[i+1][j].bordure = true;
						} else {
							Case nc = (Case)tabCases[i+1][j].clone();
							tabCases[i+1][j].hybride = true;
							nc.hybride = true;
							bordureDroite.insertTete(nc);
						}
					}
					if(tabCases[i][j].chemin && !tabCases[i][j+1].chemin){
						if(!tabCases[i][j+1].bordure){
							bordureBas.insertTete(tabCases[i][j+1]);
							tabCases[i][j+1].bordure = true;
						} else {
							Case nc = (Case)tabCases[i][j+1].clone();
							tabCases[i][j+1].hybride = true;
							nc.hybride = true;
							bordureBas.insertTete(nc);
						}
					} else if(!tabCases[i][j].chemin && tabCases[i][j+1].chemin){
						if(!tabCases[i][j].bordure){
							bordureHaut.insertTete(tabCases[i][j]);
							tabCases[i][j].bordure = true;
						} else {
							Case nc = (Case)tabCases[i][j].clone();
							tabCases[i][j].hybride = true;
							nc.hybride = true;
							bordureHaut.insertTete(nc);
						}
					}
				}
			}
			//Cas particulier de la derniere ligne
			for(int i=0; i<tabCases.length-1; i++){
				if(!tabCases[i][tabCases[0].length -1].chemin && tabCases[i+1][tabCases[0].length -1].chemin){
					if(!tabCases[i][tabCases[0].length -1].bordure){
						bordureGauche.insertTete(tabCases[i][tabCases[0].length -1]);
						tabCases[i][tabCases[0].length -1].bordure = true;
					} else {
						Case nc = (Case)tabCases[i][tabCases[0].length -1].clone();
						nc.bordure = false;
						tabCases[i][tabCases[0].length -1].hybride = true;
						nc.hybride = true;
						bordureGauche.insertTete(nc);
					}
				} else if(tabCases[i][tabCases[0].length -1].chemin && !tabCases[i+1][tabCases[0].length -1].chemin){
					if(!tabCases[i+1][tabCases[0].length -1].bordure){
						bordureDroite.insertTete(tabCases[i+1][tabCases[0].length -1]);
						tabCases[i+1][tabCases[0].length -1].bordure = true;
					} else {
						Case nc = (Case)tabCases[i+1][tabCases[0].length -1].clone();
						nc.bordure = true;
						tabCases[i+1][tabCases[0].length -1].hybride = true;
						nc.hybride = true;
						bordureDroite.insertTete(nc);
					}
				}
			}
		}
	
	/*POUR ASSOCIER A CHAQUE CASE SON IMAGE*/
	public void majImaj(Case[][] tabCases){
		//cases du chemin
		for(int i=0; i<tabCases.length; i++){
			for(int j=0; j<tabCases[0].length;j++){
				if(tabCases[i][j].isChemin()){
					tabCases[i][j].setImage(null);
				}
			}
		}

		//cases de la bordure de gauche
		Case cur = bordureGauche.root;
		while(cur != null){
			if(cur.hybride == false){		//cases uniquement sur la bordure gauche
				cur.setImage(nomImGauche);
			}
			cur = cur.next;
		}
		
		//cases de la bordure droite
		cur = bordureDroite.root;
		while(cur != null){
			if(cur.hybride == false){		//cases uniquement sur la bordure droite
				cur.setImage(nomImDroite);
			}
			cur = cur.next;
		}
		
		//cases de la bordure haut
		cur = bordureHaut.root;
		while(cur != null){
			if(cur.hybride == false){		//cases uniquement en haut
				cur.setImage(nomImHaut);
			}
			cur = cur.next;
		}
		
		//cases de la bordure bas
		cur = bordureBas.root;
		while(cur != null){
			if(cur.hybride == false){		//cases uniquement en bas
				cur.setImage(nomImBas);
			}
			cur = cur.next;
		}
		
		/*Retour sur les cases hybrides*/
		int somme = 0;
		int indice = 0;
		for(int i=1; i<tabCases.length-1; i++){			//parcours du tableau
			for(int j=1; j<tabCases[0].length-1;j++){
				if(!tabCases[i][j].isChemin()){
					somme = 0;
					indice = 0;
					for(int l = j-1; l<=j+1;l++){	//parcours des 9 cases adjacentes à la case considérée
						for(int k =i-1; k<=i+1;k++){
							if(!tabCases[k][l].isChemin() && !(k==i && j==l)){
								somme = somme + (int)Math.pow(2.0,(double)indice);
							}
							if (!(k==i && j==l)){
								indice++;
							}
						}
					}
					//detection du cas et maj de la texture en conséquence
					if(somme==47 || somme==43 || somme==11 || somme==15){ //haut gauche
						tabCases[i][j].setImage(nomImHautGauche);
					}else if(somme==104 || somme==105 || somme==232 || somme==233){	//bas gauche
							tabCases[i][j].setImage(nomImBasGauche);
						}else if(somme==22 || somme==150 || somme==23 || somme==151){	//haut droite
							tabCases[i][j].setImage(nomImHautDroite);
						}else if(somme==208 || somme==240 || somme==212 || somme==244){	//bas droite
							tabCases[i][j].setImage(nomImBasDroite);
						}else if(somme==127){	//creux haut gauche
							tabCases[i][j].setImage(nomImHautGaucheCreux);
						}else if(somme==251){	//creux bas gauche
							tabCases[i][j].setImage(nomImBasGaucheCreux);
						}else if(somme==223){	//creux haut droite
							tabCases[i][j].setImage(nomImHautDroiteCreux);
						}else if(somme==254){	//creux bas droite
							tabCases[i][j].setImage(nomImBasDroiteCreux);
						}else if(somme==9 || somme==40 || somme==41 || somme==8 ){
							tabCases[i][j].setImage(nomImBasHautGauche);
						}else if(somme==16 || somme==20 || somme==148 || somme==144 ){
							tabCases[i][j].setImage(nomImBasHautDroite);
						}else if(somme==216 || somme==220){
							tabCases[i][j].setImage(nomImBasCreuxHautDroite);
						}else if(somme==120 || somme==121){
							tabCases[i][j].setImage(nomImBasCreuxHautGauche);
						}else if(somme==30 || somme==158){
							tabCases[i][j].setImage(nomImHautCreuxBasDroite);
						}else if(somme==27 || somme==59){
							tabCases[i][j].setImage(nomImHautCreuxBasGauche);
						}else if(somme==222){
							tabCases[i][j].setImage(nomImCreuxDroiteHautBas);
						}else if(somme==123){
							tabCases[i][j].setImage(nomImCreuxGaucheHautBas);
						}else if(somme==56 || somme==24 || somme==152 || somme==28 || somme==25 || somme==57 || somme==156){
							tabCases[i][j].setImage(nomImHautBas);
						}
					}
				}
			}
			
	}
		
		
	
	/*POUR GENERER LE CHEMIN PREDEFINI EN ATTENDANT QUE LA GENERATION ALEATOIRE SOIT AU POINT*/
	public void predefini(Case[][] tabCases){
		
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
		for(int i=21; i<24; i++){
			for(int j=21; j<24; j++){
				tabCases[i][j].setChemin(true);
			}
		}
	}
		
		/*METHODE DE GENERATION ALEATOIRE*/
		public void aleat2(Case[][] tabCases){
			
			/* GENERATION TRAJET*/
			int l = tabCases.length;
			int h = tabCases[0].length;
			int xdebut = 15;
			int x = xdebut;
			int y = 0;
			double tmp;
			double rand;
			int dir = 1;			// 0=droite, 1=bas, 2=gauche
			int dirprec = 1;
			double[] dirProba = {0,1,0};
			
			while(y<h-1){
				
				//System.out.println(dir);
				//System.out.print((double)Math.round(dirProba[0]*100)/100 + "  " + (double)Math.round(dirProba[1]*100)/100 + "  " + (double)Math.round(dirProba[2]*100)/100);
				//System.out.println();
				
				rand = Math.random();
				if(rand<dirProba[0] && x>2){	//gauche
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
				
				switch (dir){
					case 0:		//vers la gauche
						x--;
						break;
					case 1:		//vers le bas
						y++;
						break;
					case 2:		//vers la droite
						x++;
						break;
				}
				if(x+2<l && y+1<h){
					tabCases[x+1][y].setChemin(true);
					tabCases[x+1][y+1].setChemin(true);
					tabCases[x+2][y+1].setChemin(true);
				}
				tabCases[x][y].setChemin(true);
			}
		}
		
	}
