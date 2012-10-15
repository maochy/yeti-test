package yeti;

import java.io.*;
import java.util.*;
import java.util.Scanner;

public class GraphDataRead {
public static int [] graphDataInt;
public void readDataFromFile(){

File f = null;
Scanner scan = null;
try{
   f = new File("/Users/mian/inclaspath/Results.txt");
   scan = new Scanner(f);
}
catch(Exception e){
   System.exit(0);
}

ArrayList<Integer> x = new ArrayList<Integer>();
//Assuming you know all your data on the file are ints
while(scan.hasNext())
   x.add(scan.nextInt());

graphDataInt = convertIntegers(x);


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