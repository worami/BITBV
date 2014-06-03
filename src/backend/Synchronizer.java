package backend;

public class Synchronizer { //synchroniseert de hele tijd op de achtergrond.
	private static Getter getter = new Getter();
	public static void main(String[] args) {
		while(true) {
			getter.synchronize();
		}
	}

}
