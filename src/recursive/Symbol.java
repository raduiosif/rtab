package recursive;

import java.util.ArrayList;
import java.util.List;

import generic.Entity;

public class Symbol extends Entity {

	public Symbol(String name) { 
		super(name); 
		m_formal_args_udi = new ArrayList<Argument>();
		m_formal_args_ni = new ArrayList<Argument>();
		m_rules = new ArrayList<Rule>();
	}
	
	public String classname() { return "symbol"; }
	public List<Argument> formalArgsUDI() { return m_formal_args_udi; }
	public List<Argument> formalArgsNI() { return m_formal_args_ni; }
	public List<Rule> rules() { return m_rules; }
	
	public int arity() { return m_formal_args_udi.size() + m_formal_args_ni.size(); }
	public Argument findFormalArg(String name) { 
		Argument arg = find(m_formal_args_udi, name); 
		if (arg == null) arg = find(m_formal_args_ni, name);
		return arg;
		}
	
	public boolean check() {
		boolean result = true;
		information("checking...");
		
		if (!checkUniqueNames(m_formal_args_udi) ||	!checkUniqueNames(m_formal_args_ni) ||
				!checkDistinctNames(m_formal_args_udi, m_formal_args_ni)) {
			error("parameter name(s) clash");
			result = false;
		}
		if (!checkUniqueNames(m_rules)) {
			error("rule name(s) clash");
			result = false;
		}
		if (m_rules.isEmpty()) {
			error("no rule(s) defined");
			result = false;
		}
		
		result &= checkAll(m_rules);
		
		return result;
	}
	
	private List<Argument> m_formal_args_udi; // unique downward instantiation
	private List<Argument> m_formal_args_ni; // not/never instantiated 
	private List<Rule> m_rules;

	public static Symbol Context = null;
	
}
