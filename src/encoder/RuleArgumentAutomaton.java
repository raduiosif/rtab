package encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import recursive.*;

public class RuleArgumentAutomaton {

	public RuleArgumentAutomaton(recursive.System system) {
		m_system = system;
		m_states = new ArrayList<RuleArgument>();
		m_map = new HashMap<Rule, HashMap<Argument, RuleArgument>>();
		autobuild();
	}
	
	public List<RuleArgument> states() { return m_states; }
	
	public RuleArgument lookup(Rule rule, Argument argument) {
		return m_map.get(rule).get(argument);
	}
	
	public boolean reachable(RuleArgument init, RuleArgument fin) {
		return forwardBfs(init).contains(fin);
	}
	
	public List<RuleArgument> forwardBfs(RuleArgument init) {
		List<RuleArgument> visited = new ArrayList<RuleArgument>();
		List<RuleArgument> pending = new ArrayList<RuleArgument>();
		pending.add(init);
		while (!pending.isEmpty()) {
			RuleArgument head = pending.remove(0);
			visited.add(head);
			for(RuleArgumentSuccessor succ : head.outgoing())
				if (!visited.contains(succ.ruleArg()) && 
						!pending.contains(succ.ruleArg()))
					pending.add(succ.ruleArg());
		}
		return visited;
	}
	
	public List<RuleArgument> backwardBfs(RuleArgument init) {
		List<RuleArgument> visited = new ArrayList<RuleArgument>();
		List<RuleArgument> pending = new ArrayList<RuleArgument>();
		pending.add(init);
		while (!pending.isEmpty()) {
			RuleArgument head = pending.remove(0);
			visited.add(head);
			for(RuleArgumentSuccessor pred : head.incoming())
				if (!visited.contains(pred.ruleArg()) && 
						!pending.contains(pred.ruleArg()))
					pending.add(pred.ruleArg());
		}
		return visited;
	}
	
	private void autobuild()  {
		// setup states
		for(Rule rule: m_system.rules()) {
			m_map.put(rule,  new HashMap<Argument, RuleArgument>());
			for(Argument arg: rule.formalArgsUDI()) {
				RuleArgument rulearg = new RuleArgument(rule, arg);
				m_map.get(rule).put(arg, rulearg);
				m_states.add(rulearg);
			}
			for(Argument arg: rule.formalArgsNI()) {
				RuleArgument rulearg = new RuleArgument(rule, arg);
				m_map.get(rule).put(arg, rulearg);
				m_states.add(rulearg);
			}
			for(Argument arg : rule.localArgs()) {
				RuleArgument rulearg = new RuleArgument(rule, arg);
				m_map.get(rule).put(arg, rulearg);
				m_states.add(rulearg);
			}
		}
		
		// setup transitions, going through (matching) pairs of rules
		for(Rule rule_1: m_system.rules()) {
			for(SymbolReference child : rule_1.children()) {
				int branch = rule_1.children().indexOf(child);
				for (Rule rule_2 : child.symbol().rules()) {
					// down transitions - for UDI arguments
					for(Argument arg: child.symbol().formalArgsUDI()) {
						int k = child.symbol().formalArgsUDI().indexOf(arg);
						RuleArgument state_1 = lookup(rule_1, child.actualArgsUDI().get(k)); 
						RuleArgument state_2 = lookup(rule_2, rule_2.formalArgsUDI().get(k));
						state_1.outgoing().add( new RuleArgumentSuccessor(branch, state_2) );
						state_2.incoming().add( new RuleArgumentSuccessor(branch, state_1) );
						java.lang.System.out.println(state_1.toString() + 
								" --dn/" + branch + "--> " + state_2.toString());
					}
					// up transitions - for NI arguments
					for(Argument arg: child.symbol().formalArgsNI()) {
						int k = child.symbol().formalArgsNI().indexOf(arg);
						RuleArgument state_1 = lookup(rule_1, child.actualArgsNI().get(k));
						RuleArgument state_2 = lookup(rule_2, rule_2.formalArgsNI().get(k));
						state_2.outgoing().add( new RuleArgumentSuccessor(branch, state_1) );
						state_1.incoming().add( new RuleArgumentSuccessor(branch, state_2) );
						java.lang.System.out.println(state_2.toString() + 
								" --up/" + branch + "--> " + state_1.toString());
					}
				}
			}
		}
	}
		
	private List<RuleArgument> m_states;
	private HashMap<Rule, HashMap<Argument, RuleArgument>> m_map;
	private recursive.System m_system;
	
}
