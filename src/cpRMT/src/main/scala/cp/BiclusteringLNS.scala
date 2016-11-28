package main.scala.cp


import oscar.cp.modeling._
import oscar.cp.core._
import oscar.util._

import main.scala.constraints.IntWSum
import main.scala.matrix.DenseMatrix
import main.scala.util._

/**
 * Biclustering using large neighbourhood search
 */

class BiclusteringLNS (rankFile: String,
    theta: Double,
    nCols: Int,   
    bColDoubleReification: Boolean,
    bUseHeuristic: Boolean,
    prevSols: Array[Solution],
    workingDir: String) extends App with CPTrait {
  
  def execute(): Solution = {

    val delimiter = "\t"      
	val rankMatrix = new DenseMatrix(rankFile, delimiter)
    maskPrevSolutions(prevSols, rankMatrix)

	  val nR = rankMatrix.rowSize
	  val nC = rankMatrix.colSize
	  //
	  val Rows = 0 until nR
	  val Cols	= 0 until nC	  	  
	  // decision variables
	  val cp = CPSolver()
	  val varRows =  Rows.map(i => new CPBoolVar(cp))
	  val varCols =  Cols.map(i => new CPBoolVar(cp))
	  val bestRows = Array.fill(nR)(0)
	  val bestCols = Array.fill(nC)(0)
	  val rand = new scala.util.Random(0)
	  
	 
	  val maxValue = rankMatrix.colSize 
	  val iTheta = java.lang.Math.round(theta * maxValue).toInt
	  
	  println("iTheta = " + iTheta)
	  println("***Mining highly ranked biclusters using large neighbourhood search***")
	  
	  // solution
	  val lastSol = new Solution(List("rows", "cols"))
	  
	  //optimization criterion	  	  
	  val totalSum = sum( Rows.map(r => varRows(r) * ( sum(Cols.map(c => varCols(c) * (rankMatrix.at(r, c) - iTheta) )))))
	  
	  cp.onSolution {
	    
	    Rows.foreach(r => bestRows(r) = varRows(r).value)
	    Cols.foreach(c => bestCols(c) = varCols(c).value)
	    lastSol.update("rows", varRows)
	    lastSol.update("cols", varCols)
	    lastSol.setObjValue(totalSum.getValue)
	    lastSol.print
	    //printSolution(varRows, varCols)
	  }
	  
	  cp.maximize(totalSum)  subjectTo {
        // 1. Highly ranked row constraints
	    for(r <- Rows) {
	      val cWeight = Cols.map(c => rankMatrix.at(r,c) - iTheta)		      
	      val rowNoiseConstraint = new IntWSum(cWeight, varCols, 0, varRows(r))
	      cp.post(rowNoiseConstraint)	      
	    }
	    
	    // 2. Highly ranked column constraints
	    for(c <- Cols) {		      
	      val cWeight = Rows.map(r => rankMatrix.at(r,c) - iTheta)	      
	      if (bColDoubleReification) {	    	  
	    	  // two-way reification constraint
	    	  println("Impose two-way reification constraints")
	    	  val colNoiseConstraint = new IntWSum(cWeight, varRows, 0, varCols(c))
	    	  cp.post(colNoiseConstraint)	   
	      } else {
         	  // One way reification
	    	  println("Impose one-way reification constraints")
	    	  val aux = new CPBoolVar(cp)
	    	  val colNoiseConstraint = new IntWSum(cWeight, varRows, 0, aux)
	    	  cp.post(colNoiseConstraint)
	    	  cp.post(varCols(c) ==> aux)
	        
	      }

	    }
	    
	    // sum varRows > 0
	    val w1 = Rows.map(r => 1)
	    val aux = new CPBoolVar(cp)	    
	    val sumRowsConstraint = new IntWSum(w1, varRows, 1, aux)
	    cp.add(aux == 1)
	    cp.add(sumRowsConstraint)

	    // sum varCols > 0
	    //val w2 = Cols.map(c => 1)
	    //val sumColsConstraint = new IntWSum(w2, varCols, nCols, aux)
	    //cp.add(sumColsConstraint)
	    
	  } search {
	    if (bUseHeuristic) {	    
		  selectMin(varCols)(x => !x.isBound)(x => getColSumRankVar(varCols.indexOf(x), iTheta, varRows, rankMatrix)) match {		      
	      	case None => noAlternative
	      	case Some(x) => {
	      		branch { cp.post(x == 0) } { cp.post(x == 1) }
	      	}
		  }
		  
	    } else {
	      select(varCols)(x => !x.isBound) match {		      
	      	case None => noAlternative
	      	case Some(x) => {
	      		branch { cp.post(x == 0) } { cp.post(x == 1) }
	      	}
		  }
	      
	    } // end bUseHeuristic
	  } // end search block
	  
	  
	  // restart block	  
	  val nRestarts = 1
	  val nFailureLimits = 500 // used to be 500 for the paper
	  for (n <- 0 until nRestarts) {
	    println("Restart " + n )
	    val stats = cp.startSubjectTo(failureLimit = nFailureLimits){}
	    println(stats)
	  }
	  
	  // large neighborhood search	  
	 
	  var limit = nFailureLimits
	  for (r <- 1 to 100) {
		  // relax randomly 60% of the variables and run again
	    println("LNS " + r)
	    println("Applying last solution: " + Cols.filter(c => bestCols(c) == 1).toVector.sorted)
		val stat = cp.startSubjectTo(failureLimit = nFailureLimits) {	   
			cp.add((Cols).filter(i => rand.nextInt(100) < 60).map(i => varCols(i) == bestCols(i)))
	    	
		}
		// adapt the backtrack limit for next run *2 is previous run reached the limit /2 otherwise
		//limit = if (stat.completed) (limit/1.2).toInt  else (limit*1.3).toInt
		//println("set limit to " + limit)
	  }
	  
	  
	  println("\n*Final solution:")
	  lastSol.print
	  lastSol.saveVar("rows", workingDir + "rows.txt", delimiter, false)
	  lastSol.saveVar("cols", workingDir + "cols.txt", delimiter, false)
	  
	  return lastSol
  }
  
  
}