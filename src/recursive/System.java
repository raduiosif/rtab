package recursive;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;

import component.Component;
import generic.Entity;
import parser.RecLexer;
import parser.RecParser;

public class System extends Entity {

	public System(String name) {
		super(name);
		m_components = new ArrayList<Component>();
		m_symbols = new ArrayList<Symbol>();
		m_rules = new ArrayList<Rule>();
		autoload();
	}

	public String classname() { return "system"; }
	public List<Component> components() { return m_components; }
	public List<Symbol> symbols() { return m_symbols; }
	public Symbol start() { return m_symbols.get(0); }
	public List<Rule> rules()  { return m_rules; }
	
	public Component findComponent(String name) { return find(m_components, name); }
	public Symbol findOrAddSymbol(Symbol symbol) { return findOrAdd(m_symbols, symbol); }  
	
	public boolean check() {
		boolean result = true;
		information("checking...");

		if (start().arity() > 0) {
			error("wrong arity for start symbol");
			result = false;
		}

		result &= checkAll(m_symbols);

		for(Rule r1: rules()) 
			for(Rule r2: rules()) { 
				if (r1 == r2) continue;
				for(Argument arg1 : r1.instances())
					for(Argument arg2 : r2.instances()) {
						if (!arg1.name().equals(arg2.name())) continue;
						if (arg1.component() != arg2.component()) {
							error("name '" + arg1.name() + "' instantiated with different types");
							result = false;
						}	
					}
			}
		
		return result;
	}
		
	private List<Component> m_components;
	private List<Symbol> m_symbols;
	private List<Rule> m_rules;
	
	private void autoload() {
		String filename = name() + ".rec";

		information("loading " + filename + "...");
		try {
			InputStream istream = new FileInputStream(filename);
			CharStream charstream = new ANTLRInputStream(istream);           
			RecLexer lexer = new RecLexer(charstream);
			RecParser parser = new RecParser(new CommonTokenStream(lexer));
			
			Context = this;
			parser.start();
			Context = null;
		}
		catch (IOException e) {
			// TODO: not yet implemented
		}
 	}
	
	public static System Context = null;
}
