package main.scala.cp

import main.scala.matrix._
import oscar.cp.core._
import main.scala.util._
import java.io.File
import java.io.PrintWriter


trait CPTrait {
  
  def execute(): Solution
  
  def getColSumRankVar(col: Int, theta: Int, rows: IndexedSeq[CPBoolVar], rankMatrix: DenseMatrix): Int = {
    
	  val posRows = (0 until rows.length).filter(r => !rows(r).isBound || (rows(r).isBound && rows(r).getValue == 1))
	  val sumRank = posRows.map(r => rankMatrix.at(r, col) - theta)
	  //
	  sumRank.sum
	}
	  
  	def printSolution(rows: IndexedSeq[CPBoolVar], cols:IndexedSeq[CPBoolVar]) = {
	    val selectedCols = (0 until cols.size).filter(c => cols(c).getValue == 1)
	    val selectedRows = (0 until rows.size).filter(r => rows(r).getValue == 1)
	    println()
	    println("Cols = " + selectedCols + " [" + selectedCols.size + "/" + cols.size + "]")
	    println("Rows = " + selectedRows + " [" + selectedRows.size + "/" + rows.size + "]")	    
	}
	  
	def getVarianceMatrix(m: DenseMatrix): DenseMatrix = {
		//
	  	val mu 	 = java.lang.Math.round(0.5* (m.colSize )).toInt
		val varMatrix = m
		
		for(i <- 0 until m.rowSize) {
		  for(j <- 0 until m.colSize) {
		    varMatrix.setValue(i, j, if (m.at(i, j) - mu >= 0) m.at(i, j) - mu else 2*(m.at(i, j) - mu))
		  }
		}
		
		return varMatrix
	}
	
	def isDirectoryExist(dir: String): Boolean = {
	  val fileHandler = new File(dir);
      fileHandler.exists()
	}
	
	def saveString(filename: String, info: String) = {
	  
	  val writer = new PrintWriter(new File(filename))
      writer.write(info)
      writer.close()
	}
	
	/**
	 * Create a directory. If the directory exists, it will be deleted and created again
	 * 
	 * @param dir absolute path of the directory
	 */
	def createDirectory(dir: String) = {
	  val fileHandler = new File(dir)
	  if (fileHandler.exists()) {
	    fileHandler.delete()
	  } 
	  fileHandler.mkdir()	  
	}
	
	def maskPrevSolutions(sols: Array[Solution], m: DenseMatrix) = {
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
  	}
	
	def maskPrevSolutionsGivenQuery(sols: Array[Solution], query: Set[Int], m: DenseMatrix) = {
	    for (k <- 0 until sols.size) {
	      val sol = sols(k)
	      if (sol != null) {
	        val cols = sol.getVarValue("cols")
	        val rows = sol.getVarValue("rows")
	        
	        println("Solution " + k)
	        println("cols: " + cols)
	        println("rows: " + rows)
	        
	        for (r <- rows; c <- cols.filter(c => !query.contains(c))) {
	          m.setValue(r, c, 1)
	        }
	      }
	    }
  	}

  	/**
  	 * mark previous solutions 
  	 * 
  	 * sols array of Solution, which has three lists: drows for diffusion, erows for expression and cols for patients
  	 * d diffusion matrix
  	 * e expression matrix
  	 */
	def maskPrevSubtypes(sols: Array[Solution], d: DenseMatrix, e: DenseMatrix ) = {
	    for (k <- 0 until sols.size) {
	      val sol = sols(k)
	      if (sol != null) {
	        val cols = sol.getVarValue("cols")
	        val drows = sol.getVarValue("drows")
	        val erows = sol.getVarValue("erows")
	        
	        println("Solution " + k)
	        println("cols: " + cols)
	        println("drows: " + drows)
	        println("erows: " + erows)
	        
	        for (r <- drows; c <- cols) {
	          d.setValue(r, c, 1)
	        }
	        
	        for (r <- erows; c <- cols) {
	          e.setValue(r, c, 1)
	        }
	      }
	    }
  	}
	
	
}