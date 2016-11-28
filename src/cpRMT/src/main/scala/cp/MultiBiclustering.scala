package main.scala.cp

import main.scala.util._
import java.io.File

// Created on Feb 20th, 2014

/**
 * Iteratively mine multiple bi-clusters
 * 
 * @param k required number of biclusters
 * @param nRepeat number of times to repeat the algorithm
 */

class MultiBiclustering (rankFile: String, 
    theta: Double,
    nCols: Int,
    bUseConstraints: Boolean, 
    bUseLNS: Boolean, 
    bColDoubleReification: Boolean,
    bUseHeuristic: Boolean,
    k: Integer, 
    nRepeat: Integer, 
    workingDir: String) extends App {

  def execute() = {
      
	  val thresholdDir = workingDir + "//" + java.lang.Math.round(theta*100).toString
	  createDirectory(thresholdDir)
	  
	  val startTime = System.currentTimeMillis()
	  
	  for (l <- 1 to nRepeat) {
		  val repeatDir = thresholdDir + "//" + "r_" + l
		  createDirectory(repeatDir)
		  val sols = new Array[Solution](k)
		  	  	  
		  for (n <- 1 to k) {
		    val savingDir = repeatDir + "//" + n.toString + "//"		    
		    createDirectory(savingDir)
		    
		    if (bUseLNS) {
		    	// use large neighbour search
		    	val bicMiner = new BiclusteringLNS(rankFile, theta, nCols, bColDoubleReification, bUseHeuristic, sols, savingDir)
		    	val sol = bicMiner.execute
		    	sols.update(n - 1, sol)
		    } else {
		      // perform complete search
		    	if (bUseConstraints) {
		    		// complete search with redundant constraints
		    		val bicMiner = new BiclusteringCS2(rankFile, theta, sols, savingDir)
		    		val sol = bicMiner.execute
		    		sols.update(n - 1, sol)
		    	} else {
		    		// complete search without any constraints
		    		val bicMiner = new BiclusteringCS1(rankFile, theta, sols, savingDir)
		    		val sol = bicMiner.execute
		    		sols.update(n - 1, sol)
		    	}
		    }
		    
		  }
	    
    }
	  val endTime = System.currentTimeMillis()
	  
	  println("Total running time: " + (endTime - startTime)/1000 + " seconds")
  
  }
  
  def isDirectoryExist(dir: String): Boolean = {
	  val fileHandler = new File(dir);
      fileHandler.exists()
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
  
}