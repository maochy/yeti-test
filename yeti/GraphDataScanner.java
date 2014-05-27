package yeti;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;


/**
 * This class reads the two files Pass and Fail line by line.
 * From each line it gets the value and assign it to the one of the two arrays.
 * The values from Pass.txt are stored in graphDataIntPass array
 * The values from Fail.txt are stored in graphDataIntFail array 
 * 
 * @author Mian Asbat Ahmad (mian.ahmad@york.ac.uk)
 * @date 10 Apr 2014
 *
 */
public class GraphDataScanner {
	
	/**
	 * The values from Fail.txt are stored in graphDataIntFail array
	 */
	static int [] graphDataIntFail;
	
	/**
	 * The values from Pass.txt are stored in graphDataIntPass array
	 */
	static int [] graphDataIntPass;

	/**
	 * This method reads the failing values from the Fail.txt file.
	 * 
	 * @return all the failing values as integer type stored in an int array.
	 */
	public static int[] readFailDataFromFile(){
		// It is for testing purpose only
		//JOptionPane.showInputDialog(null, "Enter your full name: ");

		File f = null;
		Scanner scan = null;
		try{
			//f = new File(ADFDLauncher.testFilePathInitial + "Fail.txt");
			//testing purpose.
			// Trying to solve the path issue
			//f = new File("." , "Fail.txt");
			//JOptionPane.showMessageDialog(null, f, "the file f", JOptionPane.CANCEL_OPTION);
			f = new File("Fail.txt");

			scan = new Scanner(f);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		ArrayList<Integer> fail = new ArrayList<Integer>();
		//Assuming you know all your data on the file are ints
		while(scan.hasNext())
			fail.add(scan.nextInt());

		return graphDataIntFail = convertIntegers(fail);


		//for(int str : graphDataIntFail)
		// System.out.println(str);

	}

	/**
	 * This method reads the passing values from the Pass.txt file.
	 * 
	 * @return all the passing values as integer type stored in an int array.
	 */
	public static int[] readPassDataFromFile(){

		File f = null;
		Scanner scan = null;
		try{
			//   f = new File(ADFDLauncher.testFilePathInitial + "Pass.txt");
			// Trying to solve the path issue
			//f = new File("." , "Pass.txt");
			f = new File("Pass.txt");
			
			scan = new Scanner(f);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		ArrayList<Integer> pass = new ArrayList<Integer>();
		//Assuming you know all your data on the file are ints
		while(scan.hasNext())
			pass.add(scan.nextInt());

		return graphDataIntPass = convertIntegers(pass);


		//for(Integer str : x)
		//System.out.println(str);

	}

	/**
	 * Converts a list of integers as an array of ints
	 * 
	 * @param integers the list to convert
	 * @return the generated array
	 */
	public static int[] convertIntegers(List<Integer> integers)
	{
		int[] ret = new int[integers.size()];
		for (int i=0; i < ret.length; i++)
		{
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

}