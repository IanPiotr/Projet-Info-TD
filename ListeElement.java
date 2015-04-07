public class ListeElement{

	public Element root;
	
	public ListeElement(){
		root = null;
	}
	
	public void insertTete(Element elem){
		elem.next = root;
        root = elem;
    }
    
    public void supprTete(){
		root = root.next;
	}
	
	public void suppr(Element elemSuppr){
		Element cur = root;
		Element prev = null;
		
		while(cur != null){
			if(cur == elemSuppr){
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
	
	public boolean find(Element elem){
		boolean trouve = false;
		Element cur = root;
		
		while(!trouve && cur!= null){
			if(cur == elem){
				trouve = true;
			}
			cur = cur.next;
		}
		return trouve;
	}

}
