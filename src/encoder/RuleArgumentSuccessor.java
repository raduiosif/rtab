package encoder;

public class RuleArgumentSuccessor {

	public RuleArgumentSuccessor(int branch, RuleArgument rulearg) {
			m_branch = branch;
			m_rulearg = rulearg;
	}
	
	public int branch() { return m_branch; }
	public RuleArgument ruleArg() { return m_rulearg; } 
	
	private int m_branch;
	private RuleArgument m_rulearg;
	
}
