package main;

import recursive.System;
import encoder.Ws1sTweakEncoder;
import encoder.Ws2sEncoder;
import encoder.WsEncoder;

public class Main {
	public enum Encoding {WS2S, WS1S_TWEAK};

	
	public static void main(String[] args) {
		String sysname = null; 
		String goalname = "deadlock";
		String importfilename = null; 
		
		Encoding encoding = Encoding.WS2S; 
		int flags = WsEncoder.TRAP_INVARIANT;
		
		for(int i = 0; i < args.length; i++) {
			if (args[i].startsWith("-")) {
				if (args[i].equals("-ws1s")) 
					encoding = Encoding.WS1S_TWEAK;
				else if (args[i].equals("-ws2s"))
					encoding = Encoding.WS2S;
				else if (args[i].equals("-1"))
					flags |= WsEncoder.ONE_INVARIANT;
				else if (args[i].equals("-i") && i < args.length-1) {
					importfilename = args[i+1]; i++;
				}
				else if (args[i].equals("-g") && i < args.length-1) {
					goalname = args[i+1]; i++;
				}
				else 	
					java.lang.System.out.println("unknown option + '" + args[i] + "', ignored");
			}	
			else	 {
				sysname = args[i];
				break;
			}
		}
					
		if (sysname == null) {
			java.lang.System.out.println("usage:\n\nws2s-encoder [-ws1s|-ws2s] [-1] [-i filename] [-g goal] model");
			return;
		}
				
		System system = new System (sysname);
			
		if (!system.check())
			system.error("sanity check failed", true);
		
		WsEncoder encoder = null;
		switch (encoding) {
		case WS2S: encoder = new Ws2sEncoder(system, goalname, importfilename, flags); break;
		case WS1S_TWEAK: encoder = new Ws1sTweakEncoder(system, goalname, importfilename, flags); break;
		}
			
		encoder.run();
	}
	
}
