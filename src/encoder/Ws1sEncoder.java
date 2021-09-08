package encoder;

public class Ws1sEncoder extends WsEncoder {

	public Ws1sEncoder(recursive.System system, String goalname, String importfilename, int flags) {
		super(system, goalname, importfilename, flags);
	}	
	
	public String target() { return "ws1s"; }
	
	public String generate() { 
		// not yet implemented
		return null;
	}

}
