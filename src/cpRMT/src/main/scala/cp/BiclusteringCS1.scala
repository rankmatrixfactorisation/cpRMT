package main.scala.cp


import oscar.cp.modeling._
import oscar.cp.core._
import oscar.util._

import main.scala.constraints.IntWSum
import main.scala.matrix.DenseMatrix
import main.scala.util._

/**
 * Biclustering using complete search without constraints
 */

class BiclusteringCS1 (rankFile: String,
    theta: Double,
    //bUseConstraints: Boolean,
    //bUseLNS: Boolean,
    //bColDoubleReification: Boolean,
    prevSols: Array[Solution],
    workingDir: String) extends App with CPTrait  {
  
  def execute(): Solution = {
    
  val startTime = System.currentTimeMillis()
  val delimiter = "\t"      
  val rankMatrix = new DenseMatrix(rankFile, delimiter)
  //maskPrevSolutions(prevSols, rankMatrix)

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
  println("***Mining highly ranked bi-clusters using complete search without imposing any constraints***")
	  
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
    
 cp.maximize(totalSum) subjectTo {
  
 } search {	
   if (!allBounds(varCols)) {
  	 select(varCols)(x => !x.isBound) match {		      
	 	case None => noAlternative
	 	case Some(x) => {	 	  
	        branch { cp.post(x == 0) } { cp.post(x == 1) }
	    }
	 }   
     
   } else {
     select(varRows)(y => !y.isBound) match {		      
	 	case None => noAlternative
	    case Some(y) => {	      
	      //val unBoundedRows = Rows.filter(r => !varRows(r).isBound)	      
	      branch { cp.post(y == 0) } { cp.post(y == 1) }	        
	    }
     }
   }
     
 } //start() 
	  
 val stat = cp.start()
 val endTime = System.currentTimeMillis()
	   	  
	  
 println("\n*Final solution:")
 lastSol.print
 lastSol.saveVar("rows", workingDir + "rows.txt", delimiter, false)
 lastSol.saveVar("cols", workingDir + "cols.txt", delimiter, false)
 saveString(workingDir + "time.txt", (endTime - startTime).toString)
 // println("\n " + stat)
 return lastSol
 
}
  
  /*def maskPrevSolutions(sols: Array[Solution], m: DenseMatrix) = {
    for (k <- 0 until sols.size) {
      val sol = sols(k)
      if (sol != null) {
        val cols = sol.getVarValue("cols")
        val rows = sol.getVarValue("rows")
        
        println("Solution " + k)
        println("cols: " + cols)
        println("rows: " + rows)
        
        for (r <- rows; c <- cols) {
          m.setValue(r, c, 1)
        }
      }
    }
  }*/
  
  
}