import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.*;

import java.io.File;

import javax.imageio.ImageIO;

import javax.swing.event.EventListenerList;

import java.awt.Color;


public abstract class Ennemis extends Element{

	public int IDEnnemi;
	protected int vie;
	protected int vitesse;
	public double dposx;
	public double dposy;
	protected Arc2D.Double cercle;
	protected Rectangle limEcran;
	private final EventListenerList listeners = new EventListenerList();
	public Ennemis next;
	protected boolean enBas;
	protected int buteeBas;
	protected int recompense;
	protected boolean estRalenti;
	private boolean beni;
	public String nomImage;
	public String nomImageReverse;
	
	public Ennemis(){
		super();
		next = null;
		enBas = false;
		dposx = 0;
		dposy = 1;
		vitesse = 2;
		estRalenti = false;
		beni = false;
	}

	public Ennemis(Rectangle fenetre, int num, int px, int py, int hp, String img){
		this();
		
        IDEnnemi = num;
        
        posx = px;
        posy = py;

		vie = hp;
		
		try {
			image = ImageIO.read(new File(img));
        } catch(Exception err){
			System.out.println(img+" introuvable !");            
            System.exit(0);    
        }
        largeur = image.getWidth(null);
		hauteur = image.getHeight(null);
		
		cadre = new Rectangle(posx, posy, largeur, hauteur);
		cercle = new Arc2D.Double(cadre, 0, 360, Arc2D.OPEN);
        
		limEcran = fenetre;	//meme pointeur, c'est ce qu'il me faut
		buteeBas = limEcran.height;

	}
	
	/* REDEFINITION SETTERS ELEMENT
	 * On met de plus a jour la position de l'ellipse
	 */
	 
	public void setPos(int x, int y){
		super.setPos(x, y);
		Arc2D.Double c = new Arc2D.Double(this.cadre, 0, 360, Arc2D.OPEN);
		this.cercle.setArc(c);
	}
	
	public void setPosx(int x){
		super.setPosx(x);
		Arc2D.Double c = new Arc2D.Double(this.cadre, 0, 360, Arc2D.OPEN);
		this.cercle.setArc(c);
	}
	
	public void setPosy(int y){
		super.setPosy(y);
		Arc2D.Double c = new Arc2D.Double(this.cadre, 0, 360, Arc2D.OPEN);
		this.cercle.setArc(c);
	}
	
	//*//
	
	/* NOUVEAUX SETTERS / GETTERS
	 * pour les nouveaux attributs propres a Ennemis
	 */
	
	public int getVitesse(){
		return vitesse;
	}
	
	public void setVitesse(int vit){
		vitesse = vit;
	}
	
	public abstract void setDposx(int dx);
	
	public void setImage(String img){
		try {
			image = ImageIO.read(new File(img));
        } catch(Exception err){
			System.out.println(img+" introuvable !");            
            System.exit(0);    
        }
	}
	
	public boolean isRalenti(){
		return estRalenti;
	}
	
	public void setRalenti(boolean ral){
		estRalenti = ral;
	}
	
	public boolean isBeni(){
		return beni;
	}
	
	public void setBeni(boolean b){
		beni = b;
	}
	
	public int getRecompense(){
		return recompense;
	}
	
	public void setRecompense(int gain){
		recompense = gain;
	}
	
	public int getVie(){
		return vie;
	}
	
	public void setVie(int degats){
		vie -= degats;
		if(vie <= 0){
			fireEnnemiMort(this);
		}
	}
	
	//*//
	
	/* METHODES D'ECOUTE D'UN ENNEMI
	 * Pour utiliser un ecouteur EnnemiListener d'un ennemi
	 */
	 
	public void addEnnemiListener(EnnemiListener el){
		listeners.add(EnnemiListener.class, el);
	}

	public void removeEnnemiListener(EnnemiListener el){
		listeners.remove(EnnemiListener.class, el);
	}

	public EnnemiListener[] getEnnemiListeners() {
		return listeners.getListeners(EnnemiListener.class);
	}
	
	protected void fireEnnemiMort(Ennemis ennemi){
		EnnemiEvent event = null;
		for(EnnemiListener el : getEnnemiListeners()){
			if(event == null){
				event = new EnnemiEvent(ennemi);
				el.ennemiMort(event);
			}
		}
	}
	
	protected void fireEnnemiVictorieux(Ennemis ennemi){
		EnnemiEvent event = null;
		for(EnnemiListener el : getEnnemiListeners()){
			if(event == null){
				event = new EnnemiEvent(ennemi);
				el.ennemiVictorieux(event);
			}
		}
	}
	
	//*//


	/* METHODE MOUVEMENTS BASIQUES ENNEMIS
	 * Permet le deplacement dans toutes les directions,
	 * suivant les parametres predefinis des ennemis
	 * Rebonds sur les murs
	 * NB : on utilise les setteurs setPosx et setPosy
	 * 		bien que les variables soient accessibles (protected)
	 * 		car ceux-ci permettent en plus l'actualisation
	 * 		des rectangles et ellipses englobant l'image
	 */
	public void moveBasique(boolean horizon, boolean vert){
        if(horizon){
			setPosx(posx + (int)(vitesse*dposx));
		}
		if(vert){
			setPosy(posy + (int)(vitesse*dposy));
		}
	}
	//*//
	
	/* METHODE MOUVEMENTS ALEATOIRES ENNEMIS
	 * Mouvement aleatoire :
	 * 1 chance sur 500 de changer de direction selon x
	 * 1 chance sur 500 de changer de direction selon y
	 * 1 chance sur 500 de stopper le mouvement vertical
	 * 1 chance sur 500 de stopper le mouvement horizontal
	 * 2 chances sur 500 de retablir le mouvement vertical
	 * 2 chances sur 500 de retablir le mouvement horizontal
	 * Deplacements rectilignes
	 * Rebonds sur les murs
	 * NB : utilisation des stetteurs pour les meme raisons que precedemment
	 */
	public void moveAleatoire(){
		int alea = (int)(500*Math.random());
		
		switch(alea){
			case 0 :
				dposy = -dposy;
				break;
			case 1 :
				dposx = -dposx;
				break;
			case 2 :
				dposx = 0;
				break;
			case 3 :
				dposy = 0;
				break;
			case 4: case 5:
				dposx = -1;
				break;
			case 7: case 8:
				dposy = -1;
				break;
			case 6: case 9:
				dposx = 1;
				break;
			case 10: case 11:
				dposy = 1;
				break;
		}
		
		setPosx(posx + (int)(vitesse*dposx));
		setPosy(posy + (int)(dposy*vitesse));
		
		if(posx < limEcran.x){
			posx = limEcran.x;
			dposx = - dposx;
		} else if(posx + largeur > limEcran.x + limEcran.width){
			setPosx(limEcran.x + limEcran.width-largeur);
			dposx = - dposx;
		}
		if(posy < limEcran.y){
			posy = limEcran.y;
			dposy = - dposy;
		} else if(posy + hauteur > limEcran.y + limEcran.height){
			setPosy(limEcran.y + limEcran.height-hauteur);
			dposy = - dposy;
		}
	}
	//*//
	
	/* METHODE MOUVEMENTS ALEATOIRE ENTRE LES MURS
	 * Meme probabilites que la methode precedente
	 * Deplacement uniquement sur le chemin
	 * Rebonds sur les bords du chemin
	 * NB : utilisation setters pour les meme raisons que precedemment
	 */
	public void moveAleatoire2(ListeCases lg, ListeCases ld, ListeCases lh, ListeCases lb){
		int alea = (int)(500*Math.random());
			//Alea des deplacements
		switch(alea){
			case 0 :
				dposy = -dposy;
				break;
			case 1 :
				dposx = -dposx;
				break;
			case 2 :
				dposx = 0;
				break;
			case 3 :
				dposy = 0;
				break;
			case 4: case 5:
				dposx = -1;
				break;
			case 7: case 8:
				dposy = -1;
				break;
			case 6: case 9:
				dposx = 1;
				break;
			case 10: case 11:
				dposy = 1;
				break;
		}
			//Nouvelle potentielle position, aleatoire pris en compte
		setPosx(posx + (int)(vitesse*dposx));
		setPosy(posy + (int)(dposy*vitesse));
			//Verification colision sur les murs
		Case cur;
				//Mur de gauche
		cur = lg.root;
		while(cur != null){
			if(cur.intersects(cadre)){
				//Cas particulier de la case faisant un angle (ennemis deplace potentiellement deux fois sinon)
				if(cur.hybride){
					//Cas de la case bordure GAUCHE et BAS
					if(cadre.y + cadre.height <= cur.y + 4){	//Ce 4 représente la penetration possible du sbire dans la case bordure
						setPosy(cur.y - cadre.height);			//Fonction de la vitesse du sbire et de son angle d'approche => cas le plus emmerdant a prendre, la diago
						dposy = -dposy;							//Un bug constate avec 3
					} else {
						setPosx(cur.x + cur.width);
						dposx = -dposx;
					}
				} else {
					setPosx(cur.x + cur.width);
					dposx = -dposx;
				}
			}
			cur = cur.next;
		}
				//Mur de droite
		cur = ld.root;
		while(cur != null){
			if(cur.intersects(cadre)){
				//Cas particulier de la case faisant un angle (ennemis deplace potentiellement deux fois sinon)
				if(cur.hybride){
					//Cas de la case bordure DROITE et HAUT
					if(cadre.y >= cur.y + cur.height - 4){
						setPosy(cur.y + cur.height);
						dposy = -dposy;
					} else {
						setPosx(cur.x - cadre.width);
						dposx = -dposx;
					}
				} else {
					setPosx(cur.x - cadre.width);
					dposx = -dposx;
				}
			}
			cur = cur.next;
		}
				//Mur du haut
		cur = lh.root;
		while(cur != null){
			if(cur.intersects(cadre)){
				setPosy(cur.y + cur.height);
				dposy = -dposy;
			}
			cur = cur.next;
		}
				//Mur du bas
		cur = lb.root;
		while(cur != null){
			if(cur.intersects(cadre)){
				setPosy(cur.y - cadre.height);
				dposy = -dposy;
			}
			cur = cur.next;
		}
			//Si chemin au bord de l'ecran
		if(posx < limEcran.x){
			posx = limEcran.x;
			dposx = - dposx;
		} else if(posx + largeur > limEcran.x + limEcran.width){
			setPosx(limEcran.x + limEcran.width-largeur);
			dposx = - dposx;
		}
		if(posy < limEcran.y){
			posy = limEcran.y;
			dposy = - dposy;
		} else if(posy + hauteur > limEcran.y + limEcran.height){
			setPosy(limEcran.y + limEcran.height-hauteur);
			dposy = - dposy;
		}
	}
	//*//
	
	/* METHODE TRAVERSEMENT CHEMIN ENNEMIS
	 * Mouvements visant a faire traverser la carte aux ennemis,
	 * "le plus vite possible"
	 * (ceci sera toujours ameliorable)
	 * Le chemin doit toujours DESCENDRE et non remonter
	 * Les ennemis doivent donc toujours essayer de descendre (dposy = 1) sauf si rebond
	 * Pas de rebonds sur une case hybride, puisqu'on sait qu'on va pouvoir descendre sous peu
	 */
	public void moveChemin(Case[][] tabCases, ListeCases lg, ListeCases ld, ListeCases lh, ListeCases lb){
		 	//Nouvelle potentielle position
		setPos(posx + (int)(vitesse*dposx), posy + (int)(dposy*vitesse));
		
			//VERIFICATION COLLISIONS MURS
		Case cur;
				//Mur de GAUCHE
		cur = lg.root;
		while(cur != null){
			if(cur.intersects(cadre)  && ((!cur.isBarriere()) || (cur.isBarriere() && !(this instanceof SbireFantome)))){
			//Cas particulier de la case faisant un angle (ennemis deplace potentiellement deux fois sinon)
				//Cas de la case bordure GAUCHE et BAS, s'il a en fait tape en BAS
				if(cur.hybride && cadre.y + cadre.height <= cur.y + 4){ //Ce 4 (bug constate avec 3) représente la penetration possible du sbire dans la case bordure
					setPosy(cur.y - cadre.height);						//Fonction de la vitesse du sbire et de son angle d'approche => cas le plus emmerdant a prendre, la diago
					buteeBas = cur.y;
					if(!enBas){
						int j = (int)((posy + hauteur)/Case.LCASE);
						int imax = (int)(posx/Case.LCASE);
						setDposx(1);
						for(int i = 0 ; i <= imax ; i++){
							if(tabCases[i][j].isChemin()){
								setDposx(-1);
								break;
							}
						}
						enBas = true;
					}
				//Sinon cas general
				} else {
					setPosx(cur.x + cur.width);
					if(!cur.hybride){	//Pour eviter un rebond inutil lorsque case hybride g/h
						setDposx(1);
					}
				}
			}
			cur = cur.next;
		}
				//Mur de DROITE
				//Un bug certain tel quel si les ennemis tapent une hybride d/h en haut,
				//mais si le chemin ne fait que descendre ce cas ne se presentera pas,
				//puisque les ennemis ne peuvent que descendre
		cur = ld.root;
		while(cur != null){
			if(cur.intersects(cadre)  && ((!cur.isBarriere()) || (cur.isBarriere() && !(this instanceof SbireFantome)))){
			//Cas particulier de la case faisant un angle (ennemis deplace potentiellement deux fois sinon)
				//Cas de la case DROITE et BAS, s'il a en fait tape en BAS
				if(cur.hybride && cadre.y + cadre.height <= cur.y + 4){
					setPosy(cur.y - cadre.height);
					buteeBas = cur.y;
					if(!enBas){
						int j = (int)((posy + hauteur)/Case.LCASE);
						int imax = (int)(posx/Case.LCASE);
						setDposx(1);
						for(int i = 0 ; i <= imax ; i++){
							if(tabCases[i][j].isChemin()){
								setDposx(-1);
								break;
							}
						}
						enBas = true;
					}
				//Sinon cas normal
				} else {
					setPosx(cur.x - cadre.width);
					//Pour eviter un rebond inutil lorsque case hybride d/h
					if(!cur.hybride){
						setDposx(-1);
					}
				}
			}
			cur = cur.next;
		}
				//Mur du BAS
		cur = lb.root;
		while(cur != null){
			if(cur.intersects(cadre) && ((!cur.isBarriere()) || (cur.isBarriere() && !(this instanceof SbireFantome)))){
				buteeBas = cur.y;
				setPosy(cur.y - cadre.height);
				//Si on vient de taper en bas, on regarde de quel cote on doit partir
				if(!enBas){
					int j = (int)((posy + hauteur)/Case.LCASE);
					int imax = (int)(posx/Case.LCASE);
					setDposx(1);
					for(int i = 0 ; i <= imax ; i++){
						if(tabCases[i][j].isChemin()){
							setDposx(-1);
							break;
						}
					}
					enBas = true;
				}
			}
			cur = cur.next;
		}
				//Mur du HAUT
				//Ne devrait pas se presenter si on n'a qu'un chemin qui descend
				//Sauf si rebonds
		cur = lh.root;
		while(cur != null){
			if(cur.intersects(cadre)  && ((!cur.isBarriere()) || (cur.isBarriere() && !(this instanceof SbireFantome)))){
				setPosy(cur.y + cur.height);
				dposy = 1;
				System.out.println("YOLO");
			}
			cur = cur.next;
		}

			//Pour etre pret a rencontrer de nouveau une bordure de bas
		if(enBas && posy + hauteur > buteeBas){
			enBas = false;
		}
		
			//Si chemin au bord inferieur de l'ecran
		if(posy > limEcran.y + limEcran.height){
			fireEnnemiVictorieux(this);
		}
		
	}
	//*//
	
	/* METHODE MOUVEMENTS BROWNIEN ENNEMIS
	 * Mouvement aleatoire type brownien
	 * NB : utilisation des stetteurs pour les meme raisons que precedement
	 */
	public void moveBrownien(){
		int brownien = (int)(4*Math.random());
		switch(brownien){
			case 0 :
				setPosy(posy + (int)(dposy*vitesse));
				if(posy < limEcran.y){
					posy = limEcran.y;
				} else if(posy + hauteur > limEcran.y + limEcran.height){
					setPosy(limEcran.y + limEcran.height-hauteur);
				}
				break;
			case 1 :
				setPosy(posy - (int)(dposy*vitesse));
				if(posy < limEcran.y){
					posy = limEcran.y;
				} else if(posy + hauteur > limEcran.y + limEcran.height){
					setPosy(limEcran.y + limEcran.height-hauteur);
				}
				break;
			case 2 :
				setPosx(posx + (int)(dposx*vitesse));
				if(posx < limEcran.x){
					posx = limEcran.x;
				} else if(posx + largeur > limEcran.x + limEcran.width){
					setPosx(limEcran.x + limEcran.width-largeur);
				}
				break;
			case 3 :
				setPosx(posx - (int)(dposx*vitesse));
				if(posx < limEcran.x){
					posx = limEcran.x;
				} else if(posx + largeur > limEcran.x + limEcran.width){
					setPosx(limEcran.x + limEcran.width-largeur);
				}
				break;
		}
	}
	//*//
	
	/* REDEFINITION DE LA METHODE COLLISION DE ELEMENT
	 * Pour avoir une meilleur hitbox entre les ennemis
	 * On regarde l'intersection de l'ellispe et du rectangle
	 * Plutot que celle de deux rectangles
	 * (Pas de methode Arc2D.intersects(Arc2D) existante)
	 * Plus coherent EN THEORIE mais moins bel affichage en pratique
	 * Il faudrait faire l'intersection de deux cercles plutot
	 */
	/*public boolean collision(Element elem){
		return this.cercle.intersects(elem.cadre);
	}*/
	//*//
	
	public String toString(){
		return "Je suis le sbire numero " + IDEnnemi;
	}


}
