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
public class GraphDataScannerForADFD {
	
	/**
	 * The values from FailX.txt are stored in graphDataIntFailX array
	 */
	static int [] graphDataIntFailX;
	
	/**
	 * The values from Pass.txt are stored in graphDataIntPassX array
	 */
	static int [] graphDataIntPassX;
	
	/**
	 * The values from FailY.txt are stored in graphDataIntFailY array
	 */
	static int [] graphDataIntFailY;
	
	/**
	 * The values from PassY.txt are stored in graphDataIntPassY array
	 */
	static int [] graphDataIntPassY;

	/**
	 * This method reads the failing values from the Fail.txt file.
	 * 
	 * @return all the failing values as integer type stored in an int array.
	 */
	public static int[] readFailDataFromFileX(){

		File f = null;
		Scanner scan = null;
		try{
			f = new File("FailX.txt");
			scan = new Scanner(f);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		ArrayList<Integer> failx = new ArrayList<Integer>();
		while(scan.hasNext())
			failx.add(scan.nextInt());
		


		return graphDataIntFailX = convertIntegers(failx);



	}
	
	/**
	 * This method reads the failing values from the Fail.txt file.
	 * 
	 * @return all the failing values as integer type stored in an int array.
	 */
	public static int[] readFailDataFromFileY(){

		File f = null;
		Scanner scan = null;
		try{
			f = new File("FailY.txt");
			scan = new Scanner(f);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		ArrayList<Integer> faily = new ArrayList<Integer>();
		while(scan.hasNext())
			faily.add(scan.nextInt());

		
		return graphDataIntFailY = convertIntegers(faily);

	}

	/**
	 * This method reads the passing values from the Pass.txt file.
	 * 
	 * @return all the passing values as integer type stored in an int array.
	 */
	public static int[] readPassDataFromFileX(){

		File f1 = null;
		Scanner scan1 = null;
		try{

			f1 = new File("PassX.txt");
			
			scan1 = new Scanner(f1);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		ArrayList<Integer> passx = new ArrayList<Integer>();
		while(scan1.hasNext())
			passx.add(scan1.nextInt());
		

		return graphDataIntPassX = convertIntegers(passx);

	}
	
	
	/**
	 * This method reads the passing values from the Pass.txt file.
	 * 
	 * @return all the passing values as integer type stored in an int array.
	 */
	public static int[] readPassDataFromFileY(){

		File f2 = null;
		Scanner scan2 = null;
		try{

			f2 = new File("PassY.txt");
			
			scan2 = new Scanner(f2);
		}
		catch(Exception e1){
			e1.printStackTrace();
		}

		ArrayList<Integer> passy = new ArrayList<Integer>();
		while(scan2.hasNext())
			passy.add(scan2.nextInt());

		

		return graphDataIntPassY = convertIntegers(passy);


	}

	/**
	 * Converts a list of integers as an array of ints
	 * 
	 * @param integers the list to convert
	 * @return the generated array
	 */
	public static int[] convertIntegers(List<Integer> integers)
	{
		int k = 1;
		System.out.println("call " + k++);
		int[] ret = new int[integers.size()];
		for (int i=0; i < ret.length; i++)
		{
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}

}