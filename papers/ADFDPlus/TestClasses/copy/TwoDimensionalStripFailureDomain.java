package papers.ADFDPlus.TestClasses.copy;

/**
 * Strip Fault Domain example for two arguments
 * @author (Mian and Manuel)
 */
public class TwoDimensionalStripFailureDomain{

	public static void stripErrors (int x, int y){

		if(((x > 0)&&(x < 40)) || ((y > 0) && (y < 40))){
			x = 5/0;
		}

	}

}
