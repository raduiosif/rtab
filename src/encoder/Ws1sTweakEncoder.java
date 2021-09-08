package encoder;

// follows ws2s encoding for "linear" systems 
// rewrite root -> 0, ^ -> -1, .0 -> +1

public class Ws1sTweakEncoder extends Ws2sEncoder {

	public Ws1sTweakEncoder(recursive.System system, String goalname, String importfilename, int flags) {
		super(system, goalname, importfilename, flags);
	}	
	
    public String target() { return "ws1s-tweak"; }

}
