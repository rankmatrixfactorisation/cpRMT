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




Run
------------------------

### Set up enviroment variables for Gurobi
SRF uses Gurobi to solve the optimisation problem. Hence, before running SRF, make sure that
Gurobi is installed and its enviroment variables are setup correctly.

## Parameters

        =============================================================================================================
        | PARAMETER NAME          | DEFAULT            | DESCRIPTION                                                |
        =============================================================================================================
        |-df          	          | None               |Absolute path to the tab-separated ranked diffusion file    |
        |                         |                    |where each row contains                                     |
        -------------------------------------------------------------------------------------------------------------
        |-ef                      | None               |Absolute path to the tab-separated ranked expression file   |
        |                         |                    |                                 			    |
        -------------------------------------------------------------------------------------------------------------
        |-if       		  | None	       |Initialised matrix F obtained by a hierarchical 		  
        |                         |                    |clustering.                   				    |
        -------------------------------------------------------------------------------------------------------------        
        |-k                       | 5                  |Number of ranked factors                                    |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-etheta                  | 0.65               |Rank expression threshold (\theta_2 in the paper)           |
        |                         |                    |Remember that it is a number in the range of [0..1]         |
        |                         |                    |The actual integer threshold that is used in the program is |
        |                         |                    |calculated as followed: \theta_2 * n, where n is the number |
        |                         |                    |of the columns of the rank matrix.                          |
        -------------------------------------------------------------------------------------------------------------
        |-dtheta                  | 0.86               |Rank diffusion threshold (\theta_1 in the paper)            |
        |                         |                    |Remember that it is a number in the range of [0..1]         |
        |                         |                    |The actual integer threshold that is used in the program is |
        |                         |                    |calculated as followed: \theta_1 * maxD, where maxD is the  |
        |                         |                    |user-input value specifying the maximum value of rank       |
        |                         |                    |diffusion.						    |
        -------------------------------------------------------------------------------------------------------------
        |-beta                    | 1                  |The rank imbalance threshold used to specify the relative 
        |                         |                    |importance between mutation data and expression data.       |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-k                       | 5                  |Number of ranked factors                                    |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-nReqMut                 | 2                  |Number of required mutations                                |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-maxD                    | 0                  |Maximum value of ranked diffusion. This should be equal to  |
        |                         |                    |the number of vertices in the graph used to run the         |
        |                         |		       |diffusion. Note that if maxD = 0, the program               |
	|			  |                    |uses the number of rows of the ranked diffusion matrix      |
        |                         |                    |the maximum value of ranked diffusion.			    |
        -------------------------------------------------------------------------------------------------------------
        |-maxE                    | 0                  |Maximum value of ranked expression. This should be equal to |
        |                         |                    |the number of the columns of the ranked expression          |
        |                         |                    |matrix. Note that if maxE = 0 or is not specified,          |
	|                         |                    |the program automatically uses the number of the columns    |
        |                         |                    |of the ranked expression matrix the maximum value of ranked | 		|                         |                    |expression.			                            |
        -------------------------------------------------------------------------------------------------------------
        |-dir                     | ./                 |Working directory which will be used to store the results   |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------
        |-log                     | false              |Log intermediate results into files                         |
        |                         |                    |                                                            |
        -------------------------------------------------------------------------------------------------------------


Citation
------------------------
If you use cpRMT in your work, please cite:

Le Van, T., van Leeuwen, M., Nijssen, S., Fierro, A. C., Marchal, K., and De Raedt, L. Ranked tiling. In ECML/PKDD 2014 (2) (2014), pp. 98–113.
