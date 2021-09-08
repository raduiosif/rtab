package component;

import generic.Entity;

public class Transition extends Entity {

	public Transition(State source, Port port, State target) { 
		super(null);
		m_source = source;
		m_port = port;
		m_target = target;
		m_source.outgoing().add(this);
		m_target.incoming().add(this);
	}
	
	public String classname() { return "transition"; }
	public State source() { return m_source; }
	public Port port() { return m_port; }
	public State target() { return m_target; }
	
	private State m_source;
	private Port m_port;
	private State m_target;
}
