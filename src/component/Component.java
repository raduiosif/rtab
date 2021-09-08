package component;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.MatchResult;

import generic.Entity;

public class Component extends Entity {

	public Component(String name) { 
		super(name);
		m_ports = new ArrayList<Port>();
		m_states = new ArrayList<State>();
		m_transitions = new ArrayList<Transition>();
		autoload();
	}
	
	public String classname() { return "component"; }
	public List<Port> ports() { return m_ports; }
	public List<State> states() { return m_states; }
	public State initial() { return m_states.get(0); }
	public List<Transition> transitions() { return m_transitions; }
	
	public Port findPort(String name) { return find(m_ports, name); }
	public Port findOrAddPort(Port port) { return findOrAdd(m_ports, port); }
	public State findOrAddState(State state) { return findOrAdd(m_states, state); }
		
	private List<Port> m_ports;
	private List<State> m_states;
	private List<Transition> m_transitions;
	
	private void autoload() {
		String filename = name() + ".aut";
		information("loading " + filename + "...");

		try {
			InputStream istream = new FileInputStream(filename);
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(istream));
			String line = null;
			while ((line = bufferRead.readLine()) != null) {
				Scanner scanner = new Scanner(line);
				scanner.findInLine("(\\w+) -> (\\w+) : (\\w+)");
				MatchResult result = scanner.match();
				if (result.groupCount() == 3) {
					State source = findOrAddState(new State(result.group(1)));
					Port port = findOrAddPort(new Port(result.group(3)));
					State target = findOrAddState(new State(result.group(2)));
					m_transitions.add(new Transition(source, port, target));
				}
				else  
					information("malformed line '" + line + "', ignored");
				scanner.close();
			} 
			bufferRead.close();
		}
		catch (IOException e) {
			// TODO : not yet implemented
		}
	}
	
}
