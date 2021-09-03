Recursive Term Algebra of Behaviors

This repository contains the files needed to reproduce the examples from the paper

    "Specification and Safety Verification of Parametric Hierarchical Distributed Systems" 
     by Marius Bozga and Radu Iosif, 17th edition of the International Conference on Formal Aspects of Component Software (FACS), October 28– 29, 2021.

Prerequisites: 

1. Install MONA version 1.4-18 (or later) from https://www.brics.dk/mona/ 
    * make sure the 'mona' executable is in $PATH
2. Java 11.0.1 or later

To run the examples, type in the local root directory of the repository: 

    $ chmod 755 ws2s-encoder check-all check; ./check
    
The system is deadlock free if MONA reports that the formula is unsatisfiable.
