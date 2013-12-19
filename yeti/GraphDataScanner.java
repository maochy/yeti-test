package yeti;

import java.io.*;
import java.util.*;

import javax.swing.JOptionPane;

import yeti.ADFDPlus;

/*
 * This class reads the two files Pass and Fail line and by line.
 * From each line it gets the value and assign it to the one of the two arrays.
 * The values from Pass.txt are stored in graphDataIntPass array
 * The values from Fail.txt are stored in graphDataIntFail array
 * 
 */



public class GraphDataScanner {
static int [] graphDataIntFail;
static int [] graphDataIntPass;

public static int[] readFailDataFromFile(){
	// It is for testing purpose only
	//JOptionPane.showInputDialog(null, "Enter your full name: ");

File f = null;
Scanner scan = null;
try{
   //f = new File(ADFDLauncher.testFilePathInitial + "Fail.txt");
   //testing purpose.
	f = new File("." , "Fail.txt");
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

public static int[] readPassDataFromFile(){

File f = null;
Scanner scan = null;
try{
//   f = new File(ADFDLauncher.testFilePathInitial + "Pass.txt");
	 f = new File("." , "Pass.txt");
   scan = new Scanner(f);
}
catch(Exception e1){
   e1.printStackTrace();
}

ArrayList<Integer> temp = new ArrayList<Integer>();
//Assuming you know all your data on the file are ints
while(scan.hasNext())
   temp.add(scan.nextInt());

return graphDataIntPass = convertIntegers(temp);


//for(Integer str : x)
 //System.out.println(str);

}

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