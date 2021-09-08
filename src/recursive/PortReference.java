package recursive;

import generic.Entity;

public class PortReference extends Entity {

	public PortReference(Argument arg, String portname) {
		super(null);
		m_arg = arg;
		m_portname = portname;
	}
	
	public String classname() { return "port-reference"; }
	public Argument arg() { return m_arg; }
	public String portname() { return m_portname; }
	
	private Argument m_arg;
	private String m_portname;
	
}
