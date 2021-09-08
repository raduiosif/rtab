package encoder;

import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

import org.stringtemplate.v4.*;

public abstract class WsEncoder {
	
	public WsEncoder(recursive.System system, String goalname, String importfilename, int flags) {
		String filename = "../../src/encoder/" + target() + "-vc.stg";
		m_stg = new STGroupFile(filename);
		m_system = system;
		m_automaton = new RuleArgumentAutomaton(system);
		m_goalname = goalname;
		m_importfilename = importfilename;
		m_flags = flags;
	}	
	
	public void run() {
		String filename = m_system.name() + ".mona";
		m_system.information(target() + " encoding...");
		String text = generate();
		// java.lang.System.out.println(text);
	    try {
	        FileWriter writer = new FileWriter(filename);
	        writer.write(text);
	        writer.close();
	    } catch (IOException e) {
	    	// TODO: not yet implemented
	    }
		m_system.information("done.");
	}
	
	public abstract String target(); // ws2s, ws1s-tweak, ws1s

	public abstract String generate();

	public String readFile(String filename) {
		StringBuilder sb = new StringBuilder();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();		
			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
		    }
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	protected STGroup m_stg;
	
	protected recursive.System m_system;
	protected RuleArgumentAutomaton m_automaton;

	protected String m_goalname;
	protected String m_importfilename;
	protected int m_flags;

	public static final int TRAP_INVARIANT = 1; // trap invariants
	public static final int ONE_INVARIANT = 2; // one invariants
	
}
