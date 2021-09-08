package encoder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import org.stringtemplate.v4.*;

import component.*;
import generic.Entity;
import recursive.*;

public class Ws2sEncoder extends WsEncoder {

	public Ws2sEncoder(recursive.System system, String goalname, String importfilename, int flags) {
		super(system, goalname, importfilename, flags);
		m_bindingIndex = -1;
		m_partitions = new ArrayList<Integer>();
		m_argumentTypeMap = new TreeMap<String, Component>();
		m_stateVarsCount = 0;
		for(Rule rule : m_system.rules())
			for(Argument arg : rule.instances())
				if (!m_argumentTypeMap.containsKey(arg.name())) {
					m_argumentTypeMap.put(arg.name(), arg.component());
					m_stateVarsCount += arg.component().states().size();
				}
	}	
	
    public String target() { return "ws2s"; }
	
	public String generate() {
		return generateMain();
	}
	
	// main
	
	private String generateMain() {		
		ST st = m_stg.getInstanceOf("main");
		addRuleVars(st);
		addStateVars(st);
		st.add("preds", "\n#\n# backbone tree\n#\n\n");
		st.add("preds", generateBackboneTree());
		st.add("preds", "\n#\n# ext / configurations\n#\n\n");
		st.add("preds", generateExtConfiguration());
		st.add("preds", generateConfiguration());
    	st.add("preds", "\n#\n# backbone tree paths\n#\n\n");
		st.add("preds", generatePaths());
		if ((m_flags & TRAP_INVARIANT) != 0) {
			st.add("preds", "\n#\n# trap invariants\n#\n\n");
			st.add("preds", generateTrapInitiallyNotEmpty());
			st.add("preds", generateFlow("trap"));
			st.add("preds", generateTrapInvariant());
			st.add("asserts", "trap_invariant");
		}
		if ( (m_flags & ONE_INVARIANT) != 0) {
			st.add("preds", "\n#\n# one invariants\n#\n\n");
			st.add("preds", generateOneInitiallyExactlyOne());
			st.add("preds", generateFlow("one"));
			st.add("preds", generateOneInvariant());
			st.add("asserts", "one_invariant");
		}
		if (m_goalname.equals("deadlock")) {
			st.add("preds", "\n#\n# deadlock\n#\n\n");
			st.add("preds", generateFlow("disabled"));
			st.add("preds", generateDeadlock());
		}
		st.add("asserts", m_goalname);
		if (m_importfilename != null) {
			st.add("preds", "\n#\n# " + m_importfilename + "\n#\n\n");
			st.add("preds", readFile(m_importfilename));
		}
		// generated at end, however, inserted at beginning...  
		st.add("genericPreds", generateBasePredicate("intersects", m_stateVarsCount));
		// st.add("genericPreds", generateBasePredicate("uniquely_intersects", m_stateVarsCount));
		st.add("genericPreds", generateUniquelyIntersectsPredicate(m_stateVarsCount));
		for(int size : m_partitions)
			st.add("genericPreds", generateBasePredicate("partition", size));
		return st.render();
	}		
	
	// partition / unique intersection
	
	private void addPartition(int size) {
		// record partition size for later generation...
		if (!m_partitions.contains(size))
			m_partitions.add(size);
	}
	
	private String generateBasePredicate(String name, int size) {
		ST st = m_stg.getInstanceOf(name);
		st.add("size", size);
		for(int i = 1; i <= size; i++) {
			st.addAggr("indexes.{i}", i);
			for(int j = i+1; j <= size; j++)
				st.addAggr("pairs.{i,j}", i, j);
		}
		return st.render();
	}
	
	private String generateUniquelyIntersectsPredicate(int size) {
		String body = "ex1 x: (\n";
		for(int i = 1; i <= size; i++) {
			for(int j = 1; j <= size; j++) {
				body += (j == 1) ? "(" : " ";
				body += (i == j) ? " " : "~";
				body += "(x in X_" + j + " & x in Y_" + j + ")";
				body += (j < size) ? " &\n" : ")";
			}
			body += (i < size) ? " |\n" : ")";
		}
		body += "\n& (all1 y:\n";
		for(int i = 1; i <= size; i++) {
			body += (i == 1) ? "(" : " ";
			body += "(y in X_" + i + " & y in Y_" + i + ")";
			body += (i < size) ? " |\n" : ")";
		}
		body += " => x = y)";
		
		ST st = m_stg.getInstanceOf("uniquely_intersects");
		st.add("size", size);
		for(int i = 1; i <= size; i++) 
			st.addAggr("indexes.{i}", i);
		st.add("body", body);
		return st.render();
	}
		
	// trap invariants
	
	private String generateTrapInitiallyNotEmpty() {
		ST st = m_stg.getInstanceOf("trap_initially_not_empty");
		addRuleVars(st);
		addStateVars(st);
		for(Rule rule : m_system.rules())
			for(Argument arg : rule.instances())
				st.addAggr("initialStateVars.{rid, cid,sid}", 
						rule.id(), arg.name(), arg.component().initial().name());
		return st.render();
	}
	
	private String generateTrapInvariant() {
		ST st = m_stg.getInstanceOf("trap_invariant");
		addRuleVars(st);
		addStateVars(st);
		st.add("stateVarsCount", m_stateVarsCount);
		return st.render();
	}
	
	// one-invariants 
	
	private String generateOneInitiallyExactlyOne() {
		ST st = m_stg.getInstanceOf("one_initially_exactly_one");
		addRuleVars(st);
		addStateVars(st);
		for(Rule rule : m_system.rules())
			for(Argument arg : rule.instances()) {
				ST st_dep = m_stg.getInstanceOf("one_initially_exactly_one_dependency");
				st_dep.addAggr("initVar.{rid,cid,sid}",
						rule.id(), arg.name(), arg.component().initial().name());
				for(Rule otherRule : m_system.rules())
					for(Argument otherArg : otherRule.instances()) 
						if (rule != otherRule || arg != otherArg)
							st_dep.addAggr("otherInitVars.{rid,cid,sid}",
									otherRule.id(), otherArg.name(), otherArg.component().initial().name());
				st.addAggr("dependencies.{term}",st_dep.render());
			}
		return st.render();
	}
	
	private String generateOneInvariant() {
		ST st = m_stg.getInstanceOf("one_invariant");
		addRuleVars(st);
		addStateVars(st);
		st.add("stateVarsCount", m_stateVarsCount);
		return st.render();
	}
	
	// deadlock
	
	private String generateDeadlock() {
		ST st = m_stg.getInstanceOf("deadlock");
		addRuleVars(st);
		addStateVars(st);
		st.add("stateVarsCount", m_stateVarsCount);
		return st.render();
	}
	
	// flows
	
	private String generateFlow(String basename, Interaction interaction, 
			HashMap<Argument, RuleArgument> binding, HashMap<Argument, Transition> tmap) {
		ST st = m_stg.getInstanceOf(basename + "_transition_flow");
		st.add("multiplicity", tmap.size());
		for(int k = 0; k < interaction.ports().size(); k++) {
			Argument arg_k = interaction.ports().get(k).arg();
			Rule bound_rule_k = binding.get(arg_k).rule();
			Argument bound_arg_k = binding.get(arg_k).argument();
			Transition trans_k = tmap.get(arg_k);
			st.addAggr("items.{var,rid,cid,source,target}",
					arg_k.name(), bound_rule_k.id(), bound_arg_k.name(), 
					trans_k.source().name(), trans_k.target().name());
		}
		return st.render();
	}
	
	private void generateFlowFillTransitionRec(String basename, ST st, Rule rule, Interaction interaction,
			HashMap<Argument,RuleArgument> binding, int k, HashMap<Argument, Transition> tmap) {
		if (k == interaction.ports().size()) {
			st.add("flowTransitions", generateFlow(basename, interaction, binding, tmap));
		}
		else {
			// recursion case, find transition for argument k
			String portname_k = interaction.ports().get(k).portname();
			Argument arg_k = interaction.ports().get(k).arg();
			RuleArgument rule_arg_k = binding.get(arg_k);
			Component comp_k = rule_arg_k.argument().component();
			Port port_k = comp_k.findPort(portname_k);
			if (port_k == null) 
				interaction.information("binding port '" + portname_k + "' failed");
			for(Transition transition: comp_k.transitions())
				if (transition.port() == port_k) {
					tmap.put(arg_k, transition);
					generateFlowFillTransitionRec(basename, st, rule, interaction, binding, k+1, tmap);
					tmap.remove(arg_k, transition);
				}
		}
	}

	private String generateFlow(String basename, Rule rule, Interaction interaction, 
			HashMap<Argument,RuleArgument> binding, int bindingIndex) {
		ST st = m_stg.getInstanceOf(basename + "_bound_interaction_flow");
		st.addAggr("interaction.{rid, iid}", rule.id(), interaction.id());
		addStateVars(st);
		addInteractionVars(st, interaction);
		st.add("bindingIndex", bindingIndex);
		// generate all sets of interacting transitions
		HashMap<Argument, Transition> tmap = new HashMap<Argument, Transition>(); 
		generateFlowFillTransitionRec(basename, st, rule, interaction, binding, 0, tmap);
		return st.render();
	}

	private String generateFlowTerm(String basename, Rule rule, Interaction interaction, 
			HashMap<Argument,RuleArgument> binding, int bindingIndex) {
		ST st = m_stg.getInstanceOf(basename + "_bound_interaction_term");
		st.addAggr("interaction.{rid, iid}", rule.id(), interaction.id());
		addRuleVars(st);
		addStateVars(st);
		addInteractionVars(st, interaction);
		st.add("bindingIndex", bindingIndex);
		for(int k = 0; k < interaction.ports().size(); k++) {
			Argument arg_k = interaction.ports().get(k).arg();
			RuleArgument rule_arg_k = m_automaton.lookup(rule, arg_k);
			RuleArgument bind_rule_arg_k = binding.get(arg_k);
			if (rule_arg_k == bind_rule_arg_k)
				st.addAggr("locals.{cid}", arg_k.name());
			else 
				st.addAggr("globals.{cid,bind_rid,bind_cid}", arg_k.name(),
						bind_rule_arg_k.rule().id(), bind_rule_arg_k.argument().name());
		}
		return st.render();
	}

	private void generateFlowFillBindingRec(String basename, ST st, 
			Rule rule, Interaction interaction, 
			int k, HashMap<Argument, RuleArgument> binding) {
		if (k == interaction.ports().size()) {
			// terminal case, a valid binding was found
			st.addAggr("bindings.{term,flowPreds}",
					generateFlowTerm(basename, rule, interaction, binding, m_bindingIndex),
					generateFlow(basename, rule, interaction, binding, m_bindingIndex)); 
			m_bindingIndex++;
		}
		else {
			// recursion case
			Argument arg_k = interaction.ports().get(k).arg();
			RuleArgument rule_arg_k = m_automaton.lookup(rule, arg_k);
			
			if (rule.instances().contains(arg_k)) {
				// argument instantiated locally, fixed 
				binding.put(arg_k, rule_arg_k);
				generateFlowFillBindingRec(basename, st, rule, interaction, k+1, binding);
				binding.remove(arg_k);
			}
			else {
				// argument non-instantiated locally, iterate instantiation choices
				for(Rule a_rule: m_system.rules())
					for(Argument an_arg : a_rule.instances()) {
						RuleArgument a_rule_an_arg = m_automaton.lookup(a_rule, an_arg);
						if (m_automaton.reachable(rule_arg_k, a_rule_an_arg)) {
							binding.put(arg_k, a_rule_an_arg);
							generateFlowFillBindingRec(basename, st, rule, interaction, k+1, binding);							
							binding.remove(arg_k);
						}
					}
			}
		}
	} 
	
	private String generateFlow(String basename, Rule rule, Interaction interaction) {
		ST st = m_stg.getInstanceOf(basename + "_interaction_flow");
		st.addAggr("interaction.{rid, iid}", rule.id(), interaction.id());
		addRuleVars(st);
		addStateVars(st);
		addInteractionVars(st, interaction);
		// generate all possible (instance) bindings for interaction components
		m_bindingIndex = 0;
		HashMap<Argument, RuleArgument> binding = new HashMap<Argument, RuleArgument>();
		generateFlowFillBindingRec(basename, st, rule, interaction, 0, binding);
		return st.render();
	}
	
	private String generateFlow(String basename) {
		ST st = m_stg.getInstanceOf(basename + "_flow");
		addRuleVars(st);
		addStateVars(st);
		for(Symbol symbol : m_system.symbols())
			for(Rule rule : symbol.rules())
				for(Interaction interaction : rule.interactions()) {
					st.addAggr("interactions.{rid,iid,flowPreds}", 
							rule.id(), interaction.id(), 
							generateFlow(basename, rule, interaction));
				}
		return st.render();
	}

	// paths
	
	private String generatePath(RuleArgument init, RuleArgument end) {
		ST st = m_stg.getInstanceOf("path");
		addRuleVars(st);
		st.addAggr("init.{rid,cid}", init.rule().id(), init.argument().name());
		st.addAggr("end.{rid,cid}", end.rule().id(), end.argument().name());
		// compute support variables: reachable(init) and co-reachable(end) 
		List<RuleArgument> fwd = m_automaton.forwardBfs(init);
		List<RuleArgument> bwd = m_automaton.backwardBfs(end);
		List<RuleArgument> support = new ArrayList<RuleArgument>();
		int upCount = 0, downCount = 0;
		for(RuleArgument ra : fwd)
			if (bwd.contains(ra))
				support.add(ra);
		// add support variables: all / up / down
		for(RuleArgument ra : support) {
			st.addAggr("raVars.{rid, cid}", ra.rule().id(), ra.argument().name());
			if (ra.isUp()) {
				st.addAggr("raUpVars.{rid, cid}", ra.rule().id(), ra.argument().name());
				upCount++;
			}
			if (ra.isDown()) {
				st.addAggr("raDownVars.{rid, cid}", ra.rule().id(), ra.argument().name());
				downCount++;
			}
		}
		st.add("raVarsCount", support.size());
		st.add("raUpVarsCount", upCount);
		st.add("raDownVarsCount", downCount);

		// add partition predicate if not yet there
		addPartition(support.size()); 
		if (upCount != 0) addPartition(upCount);		
		addPartition(downCount);		
		
		// add path dependencies, for every support variable ...
		for(RuleArgument ra : support) {
			boolean empty = true;
			ST st_term = ra.isDown() ? m_stg.getInstanceOf("path_down_dependency") : 
				m_stg.getInstanceOf("path_up_dependency");
			
			st_term.addAggr("raVar.{rid, cid}", ra.rule().id(), ra.argument().name());

			for(RuleArgumentSuccessor succ: ra.outgoing())
					if (support.contains(succ.ruleArg())) {
						st_term.addAggr("succRaVars.{rid, cid, branch}",
								succ.ruleArg().rule().id(), succ.ruleArg().argument().name(),
								succ.branch());
						empty = false;
					}
		
			if (!empty)
				st.addAggr("dependencies.{term}", st_term.render());
		}
		return st.render();
	}
	
	private String generatePaths() {
		String result = "";
		for(RuleArgument init : m_automaton.states()) {
			List<RuleArgument> reachable = m_automaton.forwardBfs(init);
			for(RuleArgument end : reachable) 
				if (init != end && 
					end.rule().instances().contains(end.argument())) 
						result += generatePath(init, end);
		}
		return result;
	}
	
	// extended configuration (i.e., sets of states)
	
	private String generateExtConfiguration() {
		ST st = m_stg.getInstanceOf("ext_configuration");
		addRuleVars(st);
		addStateVars(st);
		for(String argname : m_argumentTypeMap.keySet()) {
			Component component = m_argumentTypeMap.get(argname);
			for(State state : component.states()) {
				ST domain_dep = m_stg.getInstanceOf("ext_configuration_dep");
				domain_dep.addAggr("stateVar.{cid,sid}", argname, state.name());
				for(Rule rule : m_system.rules()) {
					if (Entity.find(rule.instances(), argname) != null)
						domain_dep.addAggr("ruleVars.{rid}", rule.id());
				}
				st.addAggr("dependencies.{term}", domain_dep.render());
			}
		}
		return st.render();
	}
	
	// configuration 
	
	private String generateConfiguration() {
		ST st = m_stg.getInstanceOf("configuration");
		addRuleVars(st);
		addStateVars(st);
		for(Rule rule : m_system.rules()) {
			for(Argument arg : rule.instances()) {
				Component component = arg.component();
				ST configuration_dep = m_stg.getInstanceOf("configuration_dep");
				configuration_dep.addAggr("ruleVar.{rid}", rule.id());
				for(State state : component.states()) 
					configuration_dep.addAggr("stateVars.{cid,sid}",
							arg.name(), state.name());
				st.addAggr("dependencies.{term}", configuration_dep.render());
			}
		}
		for(String argname : m_argumentTypeMap.keySet()) {
			Component component = m_argumentTypeMap.get(argname);
			for(State state_1 : component.states()) 
				for(State state_2 : component.states())
					if (state_1.name().compareTo(state_2.name()) < 0)
						st.addAggr("disjointPairs.{cid, sid1, sid2}", 
							argname, state_1.name(), state_2.name());
		}
		return st.render();
	}
	
	// backbone tree
	
	private String generateBackboneTree() {
		ST st = m_stg.getInstanceOf("backbone_tree");
		
		addPartition(m_system.rules().size());
		
		addRuleVars(st);
		st.add("rulesCount", m_system.rules().size());
		
		for(Rule rule : m_system.rules()) {
			if (rule.children().size() == 0) {
				ST tree_0_dep = m_stg.getInstanceOf("backbone_tree_0_dep");
				tree_0_dep.addAggr("ruleVar.{rid}", rule.id());
				st.addAggr("dependencies.{term}", tree_0_dep.render());
			}
			if (rule.children().size() == 1) {
				ST tree_1_dep = m_stg.getInstanceOf("backbone_tree_1_dep");
				tree_1_dep.addAggr("ruleVar.{rid}", rule.id());
				Symbol child_symbol = rule.children().get(0).symbol();
				for(Rule child_rule : child_symbol.rules())
					tree_1_dep.addAggr("children.{rid}", child_rule.id());
				st.addAggr("dependencies.{term}", tree_1_dep.render());
			}
			if (rule.children().size() == 2) {
				ST tree_2_dep = m_stg.getInstanceOf("backbone_tree_2_dep");
				tree_2_dep.addAggr("ruleVar.{rid}", rule.id());
				Symbol child_symbol_0 = rule.children().get(0).symbol();
				Symbol child_symbol_1 = rule.children().get(1).symbol();
				for(Rule child_rule_0 : child_symbol_0.rules())
					for(Rule child_rule_1 : child_symbol_1.rules())
					tree_2_dep.addAggr("childrenPairs.{rid0, rid1}", 
							child_rule_0.id(), child_rule_1.id());
				st.addAggr("dependencies.{term}", tree_2_dep.render());
			}
		}
		
		Symbol start = m_system.start();
		for(Rule rule : start.rules())
			st.addAggr("startRuleVars.{rid}", rule.id());
		
		return st.render();
	}
	
	// lists of variables
	
	private void addInteractionVars(ST st, Interaction interaction) {
		for(PortReference port : interaction.ports())
			st.addAggr("interactionVars.{cid}", port.arg().name());
	}
	
	private void addStateVars(ST st) {
		for(String argname : m_argumentTypeMap.keySet()) {
			Component component = m_argumentTypeMap.get(argname);
			for(State state : component.states())
				st.addAggr("stateVars.{cid,sid}", argname, state.name());
		}
	}
	
	private void addRuleVars(ST st) {
		for(Symbol symbol : m_system.symbols())
			for(Rule rule : symbol.rules())
				st.addAggr("ruleVars.{rid}", rule.id());
	}

	private TreeMap<String, Component> m_argumentTypeMap; // ordered keys!

	private int m_stateVarsCount;
	
	private int m_bindingIndex;

	private List<Integer> m_partitions;
	
}
