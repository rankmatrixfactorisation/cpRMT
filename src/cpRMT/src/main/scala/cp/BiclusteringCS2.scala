package main.scala.cp

import oscar.cp.modeling._
import oscar.cp.core._
import oscar.util._

import main.scala.constraints.IntWSum
import main.scala.matrix.DenseMatrix
import main.scala.util._

/**
 * Biclustering using complete search and redundant constraints
 */

class BiclusteringCS2 (rankFile: String,
    theta: Double,
    //bUseConstraints: Boolean,
    //bUseLNS: Boolean,
    //bColDoubleReification: Boolean,
    prevSols: Array[Solution],
    workingDir: String) extends App with CPTrait {
  
  def execute(): Solution = {
    
	val startTime = System.currentTimeMillis()
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
	  
	  
	  val maxValue = rankMatrix.colSize//if (maxRank == -1) rankMatrix.colSize else maxRank 
	  val iTheta = java.lang.Math.round(theta * maxValue).toInt
	  
	  println("iTheta = " + iTheta)
	  println("***Mining highly ranked bi-clusters using complete search and redundant constraints***")
	  
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
	      // use double reification constraints
	      //val colNoiseConstraint = new IntWSum(cWeight, varRows, 0, varCols(c))
	      //cp.post(colNoiseConstraint)
	      val aux = new CPBoolVar(cp)
	      val colNoiseConstraint = new IntWSum(cWeight, varRows, 0, aux)
	      cp.post(colNoiseConstraint)
	      cp.post(varCols(c) ==> aux)
	    }
	    
	  } search {
	    
       select(varCols)(x => !x.isBound) match {		      
	      case None => noAlternative
	      case Some(x) => {
	        branch { cp.post(x == 0) } { cp.post(x == 1) }
	      }
       }
	        
	  } // end search block
	  
	  val stat = cp.start()
	  val endTime = System.currentTimeMillis()
	  println("\n*Final solution:")
	  lastSol.print
	  lastSol.saveVar("rows", workingDir + "rows.txt", delimiter, false)
	  lastSol.saveVar("cols", workingDir + "cols.txt", delimiter, false)
	  saveString(workingDir + "time.txt", (endTime - startTime).toString)
	  println("\n " + stat)
	  
	  return lastSol
  }
  
  
  
  
}