public class ListeEnnemis{

	public Ennemis root;
	
	public ListeEnnemis(){
		root = null;
	}
	
	public void insertTete(Ennemis ennem){
		ennem.next = root;
        root = ennem;
    }
    
    public void supprTete(){
		root = root.next;
	}
	
	public void suppr(Ennemis ennemSuppr){
		Ennemis cur = root;
		Ennemis prev = null;
		
		while(cur != null){
			if(cur == ennemSuppr){
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
	
	public boolean find(Ennemis elem){
		boolean trouve = false;
		Ennemis cur = root;
		
		while(!trouve && cur!= null){
			if(cur == elem){
				trouve = true;
			}
			cur = cur.next;
		}
		return trouve;
	}

}
