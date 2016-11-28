package main.scala.constraints

import oscar.cp.core.CPOutcome
import oscar.cp.core.CPPropagStrength
import oscar.cp.core.CPIntVar
import oscar.cp.core.Constraint
import oscar.cp.core.CPBoolVar
import oscar.cp.util.ArrayUtils;
//import oscar.reversible.ReversibleInt
import java.security.InvalidParameterException

/**
 * Sum Constraint: w[0]*x[0]+w[1]*x[1]+...+w[n]*x[n] >= b <-> r
 * @param w weighted vector
 * @param x decision vector
 * @param b lower bound value
 * @param r reified variable
 */

class IntWSum (val w: Array[Int], val x: Array[CPBoolVar], val b: Int, val r: CPBoolVar) extends Constraint(x(0).store, "IntWSum") {
  
		if (w.size != x.size) throw new InvalidParameterException("w and x must have the same size")
		
		val pos = (0 until w.size).filter(i => w(i) >= 0)
		val neg = (0 until w.size).filter(i => w(i) < 0)
		
		val wpos = pos.map(i => w(i))
		val wneg = neg.map(i => w(i))		
		
		def wsum (x: IndexedSeq[Int], y: IndexedSeq[Int]): Int = {
		  // Q: can we perform sum operation in parallel??
			//var total = 0;
 			//for(i <- 0 until x.size)
 			//	total = total + x(i)*y(i)
 			//return total
		  val products = (0 until x.size).map(i => x(i)*y(i))
		  return products.sum
		}  
				
		override def setup(l: CPPropagStrength): CPOutcome =  {
				if ( propagate() == CPOutcome.Failure)
				  return CPOutcome.Failure;
				// register for x
				for (i <- 0 until x.size) {
					if( !x(i).isBound)
					  x(i).callPropagateWhenBoundsChange(this)
				}
				// register for reified variable
				if (!r.isBound)
				    r.callPropagateWhenBind(this)				    
				   // r.callPropagateWhenBoundsChange(this)
				// return    
				CPOutcome.Suspend
		}
		
       override def propagate(): CPOutcome = {
    		   //println("propagate")
    		   val xposmax = pos.map(i => x(i).getMax)
    		   val xposmin = pos.map(i => x(i).getMin)
    		   val xnegmin = neg.map(i => x(i).getMin)
    		   val xnegmax = neg.map(i => x(i).getMax)    		   
    		   
    		   val maxsum = wsum(wpos, xposmax) + wsum(wneg, xnegmin)
    		   val minsum = wsum(wpos, xposmin) + wsum(wneg, xnegmax)
    		      		  
    		   if (maxsum < b) {
    		     //println("maxsum")
    		         if (!r.isBound) {
    		        	 if( r.updateMax(0) == CPOutcome.Failure) {
    		        	   //println("maxsum - CANNOT set " + x.indexOf(r) + " -> 1")
    		        	     return CPOutcome.Failure    		        	  
    		        	 } else {
    		        	     //return CPOutcome.Suspend
    		        	     //println("set " + x.indexOf(r) + "/" + x.length + " -> 0")
    		        	     return CPOutcome.Success
    		        	 }
    		         } else if ( r.isBound && r.getValue == 1) {
    		             //println("maxsum - already bounded !CANNOT set " + x.indexOf(r) + " -> 0")    		           
    		             return CPOutcome.Failure
    		         } else if (r.isBound && r.getValue == 0){
    		             //return CPOutcome.Suspend
    		             return CPOutcome.Success
    		         }   		
    			     //return CPOutcome.Suspend?
    		   }
    		     
    		   if (minsum >= b) {
    		     //println("minsum = " + minsum)
    			   if (!r.isBound) {
    		        	 if( r.updateMin(1) == CPOutcome.Failure) {
    		        		 //println("minsum - CANNOT set " + x.indexOf(r) + " -> 0")
    		        	     return CPOutcome.Failure    		        	  
    		        	 }else {
    		        	     //return CPOutcome.Suspend
    		        	   //println("update r to 1: " + x.indexOf(r))
    		        	   
    		        	     return CPOutcome.Success
    		        	 }
    		       } else if (r.isBound && r.getValue == 0) {
    		    	    //println("minsum - r already bounded- CANNOT set " + x.indexOf(r) + " -> 1")
    		            return CPOutcome.Failure
    		       } else if (r.isBound && r.getValue == 1) {
    		            //return CPOutcome.Suspend
    		             return CPOutcome.Success
    		       }   		
    			     //return CPOutcome.Suspend
    		   }    		     
    		   
    		   //if( (!r.isBound) || (r.isBound && r.getValue == 1) ) {
    		   if( r.isBound && r.getValue == 1) {
    		       // Perform constraint propagations 
    			   for (i <- 0 until w.size) {
	    			   if (maxsum < (b + w(i)) && !x(i).isBound) {
	    				   // println("update " + i + " - remove 0")
	    				   // remove {0}
	    			       if( x(i).updateMin(1) == CPOutcome.Failure)
	    			         return CPOutcome.Failure
	    			   } else if(maxsum < (b - w(i)) && !x(i).isBound) {
	    				   //println("update " + i + " - remove 1")
	    				   // remove {1}
	    				   if ( x(i).updateMax(0) == CPOutcome.Failure)
	    				     return CPOutcome.Failure
	    			   }
    			   }	
    		   } else if (r.isBound && r.getValue == 0) {
    		         // Perform constraint propagations    		         
    		   }
    		   
    		   return CPOutcome.Suspend
       }
        
}