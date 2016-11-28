cpRMT - Constraint Programming-based approach to Rank Matrix Tiling
-------------------------------------------------------------------------------

Requirements
------------------------
* Linux/Unix
* Scala
* Java
* OscaR (https://bitbucket.org/oscarlib/oscar/wiki/Home)

Support
------------------------
For support using cpRMT, please contact thanh.levan@cs.kuleuven.be

Setup
------------------------

### Compilation

First, use Eclipse IDE for Scale (http://scala-ide.org/) to import the two projects in the src subfolder, including cpRMT and cpRMTWrapper. Then, use the export tool of the IDE to package file cpRMTWrapper/cpRMTRunner.java into a jar file named cpRMTRunner.jar.

How to compile the project using sbt tool will be added soon.


Run
------------------------

### Help

 /path/to/java -jar /path/to/cpRMTRunner.jar -help

## Parameters

        =============================================================================================================
        | PARAMETER NAME          | DEFAULT            | DESCRIPTION                                                |
        =============================================================================================================
        |-f          	          | None               |Rank file                                     |
        -------------------------------------------------------------------------------------------------------------
        |-useLNS                  | false              |Use large neighbourhood search  |
        |                         |                    |                                 			    |
        -------------------------------------------------------------------------------------------------------------
        |-useOrderingHeuristic    | false	       |Use the heuristic described in [1] to order variables when         searching|                        
        -------------------------------------------------------------------------------------------------------------        
        |-k                       | 5                  |Number of patterns                                          |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-theta                   | 0.5                |Theta threshold                                             |       
        -------------------------------------------------------------------------------------------------------------
        |-dir                     | ./                 |Working directory which will be used to store the results   |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-n                       | 1                  |Number of repeats
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------

Other parameters should be ignored (they will be removed soon).

### Typical run

/path/to/java -Xmx2048m -jar /path/to/cpRMTRunner.jar -f /rankfile.txt -useLNS -useOrderingHeuristic -k 8 -n 1 -theta 0.65 -dir /path/to/working/directory/

Citation
------------------------
If you use cpRMT in your work, please cite:

Le Van, T., van Leeuwen, M., Nijssen, S., Fierro, A. C., Marchal, K., and De Raedt, L. Ranked tiling. In ECML/PKDD 2014 (2) (2014), pp. 98–113.
