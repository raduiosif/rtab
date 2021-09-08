grammar Rec;

@parser::header {		
package parser;

import component.*;
import recursive.*;
}

@lexer::header{ 
package parser;
}

// parser

start : rec_system ;

rec_system: ( rec_rule_set )+ EOF ;

rec_rule_set : 
	a = IDENTIFIER { Symbol.Context = recursive.System.Context.findOrAddSymbol(new Symbol($a.text)); } 
	'(' (    x = argument_decl { Symbol.Context.formalArgsUDI().add($x.arg); $x.arg.setKind(Argument.Kind.FORMAL_UDI); } 
		(',' y = argument_decl { Symbol.Context.formalArgsUDI().add($y.arg); $y.arg.setKind(Argument.Kind.FORMAL_UDI); } )* )?
		('/' u = argument_decl { Symbol.Context.formalArgsNI().add($u.arg);  $u.arg.setKind(Argument.Kind.FORMAL_NI); } 
		(',' v = argument_decl { Symbol.Context.formalArgsNI().add($v.arg);  $v.arg.setKind(Argument.Kind.FORMAL_NI); } )* )?  ')' 
	DEF r = rec_rule { recursive.System.Context.rules().add($r.rule); } 
	    ('|' s = rec_rule { recursive.System.Context.rules().add($s.rule); } )* { Symbol.Context = null; }
	';' ;

rec_rule returns [Rule rule]: 
	'[' id = IDENTIFIER ']' { $rule = new Rule($id.text, Symbol.Context); Rule.Context = $rule; }
	(LET     x = argument_decl { $rule.localArgs().add($x.arg); $x.arg.setKind(Argument.Kind.LOCAL);  } 
		(',' y = argument_decl { $rule.localArgs().add($y.arg); $y.arg.setKind(Argument.Kind.LOCAL);  } )* IN)?
	'(' (    u = interaction { $rule.interactions().add($u.inter); } 
		('+' v = interaction { $rule.interactions().add($v.inter); } )* )? ')'
	'(' ( child (',' child)* )? ')' { Rule.Context = null; }
	;

child : z = argument ':'  compid = IDENTIFIER {
		Component component = recursive.System.Context.findComponent($compid.text);
		if (component == null) {
			component = new Component($compid.text);
			recursive.System.Context.components().add(component);
		} 
	   $z.arg.setComponent(component);
	   Rule.Context.instances().add($z.arg);
    }
	| a = IDENTIFIER {
		Symbol symbol = recursive.System.Context.findOrAddSymbol(new Symbol($a.text));
		SymbolReference.Context = new SymbolReference(symbol); ;
		Rule.Context.children().add(SymbolReference.Context);
	} '(' (    x = argument { SymbolReference.Context.actualArgsUDI().add($x.arg); } 
		  (',' y = argument { SymbolReference.Context.actualArgsUDI().add($y.arg); } )* )?
		  ('/' u = argument { SymbolReference.Context.actualArgsNI().add($u.arg); } 
		  (',' v = argument { SymbolReference.Context.actualArgsNI().add($v.arg); } )* )? ')' 
		{ SymbolReference.Context =  null; } ;

interaction returns [Interaction inter]: { $inter = new Interaction(); }  
	 p = port { $inter.ports().add($p.port_ref); } 
	(q = port { $inter.ports().add($q.port_ref); } )* 
	; 

port returns [PortReference port_ref] : 
	x = argument '.' portid = IDENTIFIER { 
		$port_ref = new PortReference($x.arg, $portid.text); 
	} ;
	
argument_decl returns [Argument arg] : 
	argid = IDENTIFIER {
		$arg = new Argument(Argument.Kind.FORMAL_UDI, $argid.text); 
	} ;

argument returns [Argument arg] : 
	argid = IDENTIFIER {
		$arg = Rule.Context.findArg($argid.text); 
		if ($arg == null) 
			Rule.Context.error("unexpected argument '" + $argid.text + "'", true);
	} ;

// lexer

LET        : '_let_' ;
IN         : '_in_'  ;

IDENTIFIER : [a-zA-Z][a-zA-Z0-9_]*  ;

DEF        : ':=' ;

COMMENT    : '#'[^\n]*  -> skip ; // skip #... comments 
WHITESPACE : [ \t\r\n]+ -> skip ; // skip spaces, tabs, newlines
