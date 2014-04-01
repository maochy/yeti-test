package papers.ADFDPlus.TestClasses.copy;

/**
 * Block Fault Domain example for two arguments
 * @author (Mian and Manuel)
 */
public class TwoDimensionalBlockFailureDomain{

	public static void blockErrors (int x, int y){

		if(((x > 0)&&(x < 20)) || ((y > 0)&&(y < 20))){
			x = 5/0;
		}

	}

}