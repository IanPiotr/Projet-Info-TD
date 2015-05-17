public class ListeCases{

	public Case root;
	
	public ListeCases(){
		root = null;
	}
	
	public void insertTete(Case c){
		c.next = root;
        root = c;
    }
    
    /*public void insertTete2(Case c){
		c.next2 = root;
        root = c;
    }*/
    
    public void supprTete(){
		root = root.next;
	}
	
	public void suppr(Case caseSuppr){
		Case cur = root;
		Case prev = null;
		
		while(cur != null){
			if(cur == caseSuppr){
				if(prev == null){
					root = cur.next;
				} else {
					prev.next = cur.next;
				}
			}
		prev = cur;
		cur = cur.next;
		}
	}
	
	public boolean find(Case c){
		boolean trouve = false;
		Case cur = root;
		
		while(!trouve && cur!= null){
			if(cur == c){
				trouve = true;
			}
			cur = cur.next;
		}
		return trouve;
	}

}
