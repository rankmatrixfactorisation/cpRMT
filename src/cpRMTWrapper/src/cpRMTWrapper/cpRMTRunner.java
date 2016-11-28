package cpRMTWrapper;

import main.scala.cp.MultiBiclustering;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;


public class cpRMTRunner {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Option opRankFile = OptionBuilder.withArgName("RankFile").hasArg().withDescription("Rank file").create("f");
        Option opWorkingDir = OptionBuilder.withArgName("WorkingDir").hasArg().withDescription("Working directory").create("dir");
        Option opK = OptionBuilder.withArgName("k").hasArg().withDescription("Number of pattern sets").create("k");
        Option opN = OptionBuilder.withArgName("n").hasArg().withDescription("Number of repeats").create("n");
        Option opTheta = OptionBuilder.withArgName("theta").hasArg().withDescription("Theta threshold").create("theta");
        Option opCols = OptionBuilder.withArgName("nCols").hasArg().withDescription("Lower bound of the number of columns in a bicluster").create("nCols");
        
                        
        Option help	= new Option("help", "Print help");
        Option opUseConstraint = new Option("useConstraints", "Use highly ranked row/column constraints");
        Option opUseLNS	= new Option("useLNS", "Use large neighbourhood search");
        Option opUseOrderingHeuristics	= new Option("useOrderingHeuristic", "Use ordering variable heuristics");
        Option opColDoubleReification = new Option("colDoubleReif", "Use double reification for columns");
        
        Options options = new Options();
        options.addOption(opRankFile);
        options.addOption(opWorkingDir);
        options.addOption(opK);
        options.addOption(opN);
        options.addOption(opTheta);
        options.addOption(opCols);
        options.addOption(opUseConstraint);
        options.addOption(opUseLNS);
        options.addOption(opUseOrderingHeuristics);
        options.addOption(opColDoubleReification);
        
                       
        options.addOption(help);
        
        // default values                
        String rankFileName = "" ;
        String workingDir	= "./";
        //double colThreshold = 0.5;
        //double rowThreshold = 0.5;
        int k = 5;
        int n = 1;
        double theta = 0.5;
        int nCols = 1;
        //int maxRank = -1; // size of columns
        //int model = 3; //3-LNS, 4-complete search without constraints
        boolean bUseConstraints = false;
        boolean bUseLNS = false;
        boolean bUseHeuristic = false;
        boolean bColDoubleReification = false;
                        
        // parse command line to get values
        CommandLineParser parser = new BasicParser();
        
        try{
        	CommandLine cmd = parser.parse( options, args);
        	if (cmd.hasOption("f")) {
        	    rankFileName = cmd.getOptionValue("f");
        	}
        	
        	if (cmd.hasOption("theta")) {
        		theta = Double.parseDouble(cmd.getOptionValue("theta"));
        	}
        	
        	if (cmd.hasOption("dir")) {
        		workingDir = cmd.getOptionValue("dir");
        	}
        	
        	if (cmd.hasOption("k")) {
        		k = Integer.parseInt(cmd.getOptionValue("k"));
        	}
        	
        	if (cmd.hasOption("n")) {
        		n = Integer.parseInt(cmd.getOptionValue("n"));
        	}
        	
        	if (cmd.hasOption("nCols")) {
        		nCols = Integer.parseInt(cmd.getOptionValue("nCols"));
        	}
        	
        	
        	if (cmd.hasOption("help")) {
        		HelpFormatter formatter = new HelpFormatter();
        		formatter.printHelp( "runMultiBiclustering", options );
        		return;
        	}
        	
        	if (cmd.hasOption("useConstraints")) {
        		bUseConstraints = true;
        	}
        	
        	if (cmd.hasOption("useLNS")) {
        		bUseLNS = true;
        	}
        	
        	if (cmd.hasOption("useOrderingHeuristic")) {
        		bUseHeuristic = true;
        	}
        	
        	if (cmd.hasOption("colDoubleReif")) {
        		bColDoubleReification = true;
        	}
        	
        }catch (ParseException ex){
        	System.out.println( ex.getMessage());
        }
        
        System.out.println("Rank File = " + rankFileName);
        System.out.print("theta = "); System.out.println(theta);
        System.out.print("Lower bound of required columns = "); System.out.println(nCols);
        System.out.print("Working directory = "); System.out.println(workingDir);
        System.out.print("Number of patterns = "); System.out.println(k);
        System.out.print("Number of repeat = "); System.out.println(n);        
        System.out.print("Use highly ranked row/col constraints: "); System.out.println(bUseConstraints);
        System.out.print("Use large neighbourhood search: "); System.out.println(bUseLNS);
        System.out.print("Use double reification for column constraints: "); System.out.println(bColDoubleReification);
        System.out.print("Use ordering variable heuristic: "); System.out.println(bUseHeuristic);
        
        
        if (rankFileName.isEmpty()) {
        	System.out.println("Data file is not provided. Stop.");
        	return;
        }
        
        //RankedBicluster obj = new RankedBicluster(rankFileName, rowThreshold, colThreshold, workingDir);
        MultiBiclustering obj = new MultiBiclustering(rankFileName, theta, nCols, bUseConstraints, bUseLNS, bColDoubleReification, bUseHeuristic, k, n, workingDir);
        obj.execute();
       
	}

}
