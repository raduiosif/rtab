Recursive Term Algebra of Behaviors

This repository contains the files needed to reproduce the examples from the paper

    "Verifying Safety Properties of Inductively Defined Parameterized Systems" 
     by Marius Bozga and Radu Iosif

Prerequisites: 

1. Install MONA version 1.4-18 (or later) from https://www.brics.dk/mona/
2. Java 11.0.1 or later

To run the examples, type in the local root directory of the repository: 

    $ chmod 755 ws2s-encoder check-all check; ./check
    
The system is deadlock free if MONA reports that the formula is unsatisfiable.
