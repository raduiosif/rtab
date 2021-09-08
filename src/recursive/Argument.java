package recursive;

import generic.Entity;
import component.Component;

public class Argument extends Entity {
	public enum Kind {FORMAL_UDI, FORMAL_NI, LOCAL};
	
	public Argument(Kind kind, String name) { 
		super(name); 
		m_kind = kind;
		m_component = null;
	}
	public String classname() { return "argument"; }
	public Kind kind() { return m_kind; }
	public void setKind(Kind kind) { m_kind = kind; }
	public Component component() { return m_component; }
	public void setComponent(Component component) { m_component = component; }
	
	private Kind m_kind;
	private Component m_component;

}
