package recursive;

import java.util.ArrayList;
import java.util.List;

import generic.Entity;

public class Interaction extends Entity {

	public Interaction() {
		super(null);
		m_ports = new ArrayList<PortReference>();
		m_id = null;
	}
	
	public String classname() { return "interaction"; }
	public List<PortReference> ports()  { return m_ports; }
	public String id() { if (m_id == null) computeId(); return m_id; }
	
	public boolean check() {
		boolean result = true;
		information("checking...");
		List<Argument> args = new ArrayList<Argument>();
		for(PortReference port : m_ports) 
			if ( !args.contains(port.arg()))
				args.add(port.arg());
			else
				result = false; 

		if (!result) {
			error("replicated name(s)");
			result = false;
		}
		
		return result;
	}
	
	private void computeId() {
		m_id = "";
		for(PortReference port : m_ports) {
			if (!m_id.contentEquals("")) m_id += "_";
			m_id += port.arg().name() + "_" + port.portname();
		}
	}
	
	private List<PortReference> m_ports;
	private String m_id;
}
