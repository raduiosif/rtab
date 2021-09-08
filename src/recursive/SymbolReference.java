package recursive;

import java.util.ArrayList;
import java.util.List;

import generic.Entity;

public class SymbolReference extends Entity {

	public SymbolReference(Symbol symbol) {
		super(null);
		m_symbol = symbol;
		m_actual_args_udi = new ArrayList<Argument>();
		m_actual_args_ni = new ArrayList<Argument>();
	}
	
	public String classname() { return "symbol-reference"; }
	public Symbol symbol() { return m_symbol; }
	public List<Argument> actualArgsUDI() { return m_actual_args_udi; }
	public List<Argument> actualArgsNI() { return m_actual_args_ni; }
	
	public boolean check() {
		boolean result = true;
		information("checking...");
		
		if (!checkUniqueNames(m_actual_args_udi) || !checkUniqueNames(m_actual_args_ni) ||
				!checkDistinctNames(m_actual_args_udi, m_actual_args_ni)) {	
			error("actual name(s) clash");
			result = false;
		}
		if (m_actual_args_udi.size() != m_symbol.formalArgsUDI().size() ||
			m_actual_args_ni.size() != m_symbol.formalArgsNI().size()) {
			error("arity mismatch");
			result = false;
		}

		return result;
	}
	
	private Symbol m_symbol; 
	private List<Argument> m_actual_args_udi;
	private List<Argument> m_actual_args_ni;

	public static SymbolReference Context = null;
	
}
