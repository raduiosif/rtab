Recursive Term Algebra of Behaviors

This repository contains the files needed to reproduce the examples from the paper

    "Verifying Safety Properties of Inductively Defined Parameterized Systems" 
     by Marius Bozga and Radu Iosif

Prerequisites: 

* Install MONA version 1.4-18 from https://www.brics.dk/mona/

Steps to running the examples:

1. Edit the ws2s-encoder script and set the DIR variable to point to the root directory where this repository is cloned. 
   At this point the command ./ws2s-encoder should yield the message "usage:  ws2s-encoder [-ws1s|-ws2s] model" 
   
2. cd examples/<Example> (e.g. examples/tree-linked-leaves) and run: 
   $ ../../ws2s-encoder <name-of-rec-file-without-extension> (e.g.  ./../ws2s-encoder tll-full)
   $ mona <name-of-rec-file-without-extension.mona> (e.g. mona tll-full.mona)

The system is deadlock free is MONA reports that the formula is unsatisfiable.
