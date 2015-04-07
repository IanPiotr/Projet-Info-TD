import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.RectangularShape.*;
import java.awt.geom.*;
import java.awt.geom.Arc2D.Double;
import java.awt.geom.Arc2D.*;

import java.io.File;

import javax.imageio.ImageIO;

import javax.swing.event.EventListenerList;

public abstract class Ennemis extends Element{

	protected int vie;
	protected int vitesse;
	public double dposx;
	public double dposy;
	protected Arc2D.Double cercle;
	protected Rectangle limEcran;
	private final EventListenerList listeners = new EventListenerList();
	public int IDEnnemi;
	public Ennemis next;
	
	public Ennemis(String nomImage, int px, int py, int hp, int vit){
		super();
		
		next = null;
		
		try {
			image= ImageIO.read(new File(nomImage));
        } catch(Exception err){
			System.out.println(nomImage+" introuvable !");            
            System.exit(0);    
        }
        
        posx = px;
        posy = py;
		dposx = 1;
		dposy = 1;
		vitesse = vit;
		
		vie = hp;
		
		largeur = image.getWidth(null);
		hauteur = image.getHeight(null);
		
		cadre = new Rectangle(posx, posy, largeur, hauteur);
		cercle = new Arc2D.Double(cadre, 0, 360, Arc2D.OPEN);

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
	
	/* METHODES D'ECOUTE DE LA MORT D'UN ENNEMI
	 * Pour utiliser un ecouteur mortListener de mort d'un ennemi
	 */
	 
	public void addMortListener(mortListener ml){
		listeners.add(mortListener.class, ml);
	}

	public void removeMortListener(mortListener ml){
		listeners.remove(mortListener.class, ml);
	}

	public mortListener[] getMortListeners() {
		return listeners.getListeners(mortListener.class);
	}
	
	protected void fireEnnemiMort(Ennemis ennemi){
		mortEvent event = null;
		for(mortListener ml : getMortListeners()){
			if(event == null){
				event = new mortEvent(ennemi);
				ml.ennemiMort(event);
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
			if(posx < limEcran.x){
				posx = limEcran.x;
				dposx = -dposx;
			} else if(posx + largeur > limEcran.x + limEcran.width){
				setPosx(limEcran.x + limEcran.width-largeur);
				dposx = -dposx;
			}
		}
		if(vert){
			setPosy(posy + (int)(vitesse*dposy));
			if(posy < limEcran.y){
				posy = limEcran.y;
				dposy = -dposy;
			} else if(posy + hauteur > limEcran.y + limEcran.height){
				setPosy(limEcran.y + limEcran.height-hauteur);
				dposy = -dposy;
			}
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
	 */
	public boolean collision(Element elem){
		return this.cercle.intersects(elem.cadre);
	}
	//*//
	
	public String toString(){
		return "Je suis le sbire numero " + IDEnnemi;
	}


}
