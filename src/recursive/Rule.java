package recursive;

import java.util.ArrayList;
import java.util.List;

import generic.Entity;

public class Rule extends Entity {

	public Rule(String name, Symbol symbol) {
		super(name);
		m_symbol = symbol;
		m_formal_args_udi = new ArrayList<Argument>();
		for(Argument formal_arg : m_symbol.formalArgsUDI())
			m_formal_args_udi.add(new Argument(formal_arg.kind(), formal_arg.name()));
		m_formal_args_ni = new ArrayList<Argument>();
		for(Argument formal_arg : m_symbol.formalArgsNI())
			m_formal_args_ni.add(new Argument(formal_arg.kind(), formal_arg.name()));
		m_local_args = new ArrayList<Argument>();
		m_interactions = new ArrayList<Interaction>();
		m_instances = new ArrayList<Argument>();
		m_children = new ArrayList<SymbolReference>();
		m_symbol.rules().add(this);
		m_id = m_symbol.name() + "_" + name();
	}
	
	public String classname() { return "rule"; }
	public Symbol symbol() { return m_symbol; }
	public List<Argument> formalArgsUDI() { return m_formal_args_udi; }
	public List<Argument> formalArgsNI() { return m_formal_args_ni; }
	public List<Argument> localArgs() { return m_local_args; }
	public List<Interaction> interactions() { return m_interactions; }
	public List<Argument> instances() { return m_instances; }
	public List<SymbolReference> children() { return m_children; }
	public String id() { return m_id; }
	
	public Argument findLocalArg(String name) { return find(m_local_args, name); }
	public Argument findArg(String name) {
		Argument result = findLocalArg(name);
		if (result == null)	result = find(m_formal_args_udi, name);
		if (result == null) result = find(m_formal_args_ni, name);
		return result;
	}
	
	public boolean check() {
		boolean result = true;
		information("checking...");
		
		// formal/local name clash
		if (!checkUniqueNames(m_local_args)) {
			error("local name(s) clash");
			result = false;
		}
		if (!checkDistinctNames(m_local_args, m_formal_args_udi) ||
			!checkDistinctNames(m_local_args, m_formal_args_ni)) {
			error("formal/local name(s) clash");
			result = false;
		}
		
		// name(s) multi-instantiated 
		if (!checkUniqueNames(m_instances)) {
			error("name(s) multi-instantiated locally");
			result = false;
		}
		for(SymbolReference child1 : m_children) {
			if (!checkDistinctNames(m_instances, child1.actualArgsUDI())) {
				error("name(s) multi-instantiated locally / on recursion");
				result = false;
			}
			for(SymbolReference child2 : m_children)
				if (child1 != child2 && !checkDistinctNames(child1.actualArgsUDI(), child2.actualArgsUDI())) {
					error("name(s) multi-instantiated on recursion");
					result = false;
				}
		}
		
		// non/instantiated name clash
		if (!checkDistinctNames(m_formal_args_ni, m_instances)) {
			error("non/instantiated name(s) clash locally");
			result = false;
			
		}
		for(SymbolReference child1 : m_children) {
			if (!checkDistinctNames(m_formal_args_ni, child1.actualArgsUDI())) {
				error("non/instantiated name(s) clash on recursion");
				result = false;
			}
		}
		
		// TODO: check unused formal UDI/NI argument (warning ?)
		// TODO: check unused local argument (warning ?)
		
		if (m_children.size() > 2) {
			error("too many children");
			result = false;
		}
		
		result &= checkAll(m_children);

		result &= checkAll(m_interactions);	
		
		return result;
	}

	private Symbol m_symbol;
	private List<Argument> m_formal_args_udi; // ~ m_symbol.m_formal_args_udi
	private List<Argument> m_formal_args_ni;  // ~ m_symbol.m_formal_args_ni
	private List<Argument> m_local_args;
	private List<Interaction> m_interactions;
	private List<Argument> m_instances;
	private List<SymbolReference> m_children;
	private String m_id;
	
	public static Rule Context = null;	
}
