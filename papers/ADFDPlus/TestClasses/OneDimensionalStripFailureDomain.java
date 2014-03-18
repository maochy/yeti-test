package papers.ADFDPlus.TestClasses;

/**
 * Strip Fault Domain example for one argument
 * @author (Mian and Manuel)
 */

public class OneDimensionalStripFailureDomain{

	public static void stripErrors (int x){

		if((x > -5) && (x < 35))
			x = 5/0;
	}
}