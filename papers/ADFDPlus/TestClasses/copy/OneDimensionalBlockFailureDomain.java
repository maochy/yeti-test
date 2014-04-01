package papers.ADFDPlus.TestClasses.copy;


/**
 * Block Fault Domain example for one arguments
 * @author (Mian and Manuel)
 */

public class OneDimensionalBlockFailureDomain{

	public static void blockErrors (int x){

		if((x > -2) && (x < 2))
			x = 5/0;

		if((x > -30) && (x < -25))
			x = 5/0;

		if((x > 50) && (x < 55))
			x = 5/0;

	}
}