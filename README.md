Recursive Term Algebra of Behaviors

This repository contains the files needed to reproduce the examples from the paper

    "Verifying Safety Properties of Inductively Defined Parameterized Systems" 
     by Marius Bozga and Radu Iosif

Prerequisites: 

1. Install MONA version 1.4-18 from https://www.brics.dk/mona/
2. Java 11.0.1 or later

Steps to running the examples:

1. Edit the ws2s-encoder script and set the DIR variable to point to the root directory where this repository is cloned. 
   
2. cd examples/<Example> (e.g. examples/tree-linked-leaves) and run: 
   $ ../../ws2s-encoder <name-of-rec-file-without-extension> (e.g.  ./../ws2s-encoder tll-full)

The system is deadlock free if MONA reports that the formula is unsatisfiable.
