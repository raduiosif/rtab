package component;

import java.util.ArrayList;
import java.util.List;

import generic.Entity;

public class State extends Entity {

	public State(String name) { 
		super(name);
		m_incoming = new ArrayList<Transition>();
		m_outgoing = new ArrayList<Transition>();
	}
	
	public String classname() { return "state"; }
	public List<Transition> incoming() { return m_incoming; }
	public List<Transition> outgoing() { return m_outgoing; }
	
	private List<Transition> m_incoming;
	private List<Transition> m_outgoing;
	
}
