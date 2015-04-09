import java.util.EventListener;

public interface EnnemiListener extends EventListener{

	void ennemiMort(EnnemiEvent e);
	void ennemiVictorieux(EnnemiEvent e);
	
}
