This repository contains the files needed to reproduce the examples from the paper

    "Specification and Safety Verification of Parametric Hierarchical Distributed Systems" 

Prerequisites: 

1. Install MONA version 1.4-18 (or later) from https://www.brics.dk/mona/ 

    * make sure the 'mona' executable is in $PATH

2. Java 11.0.1 or later

Running the examples: 

Type the following in the local root directory of the repository:

    $ chmod 755 ws2s-encoder check-all check; ./check
    
Interpreting the results: 

    * The safety property is proved if MONA reports that the formula is "unsatisfiable". 
    * Getting a "satisfying example" means that the property was not proved.
