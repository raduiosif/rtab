package encoder;

import java.util.ArrayList;
import java.util.List;

import recursive.Argument;
import recursive.Rule;

public class RuleArgument {
	
	public RuleArgument(Rule rule, Argument argument) {
		m_rule = rule;
		m_argument = argument;
		m_outgoing = new ArrayList<RuleArgumentSuccessor>();
		m_incoming = new ArrayList<RuleArgumentSuccessor>();
	}
	
	public Rule rule() { return m_rule; }
	public Argument argument() { return m_argument; }
	public List<RuleArgumentSuccessor> outgoing() { return m_outgoing; }
	public List<RuleArgumentSuccessor> incoming() { return m_incoming; }
	
	public String toString() {
		return "(" + m_rule.id() + ", " + m_argument.name() + ")";
	}
	public boolean isDown() {
		return m_argument.kind() == Argument.Kind.FORMAL_UDI ||
			m_argument.kind() == Argument.Kind.LOCAL;
	}
	public boolean isUp() { 
		return m_argument.kind() == Argument.Kind.FORMAL_NI; 
	}
	
	private Rule m_rule;
	private Argument m_argument; 
	private List<RuleArgumentSuccessor> m_outgoing;
	private List<RuleArgumentSuccessor> m_incoming;
	
}
