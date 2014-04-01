package papers.ADFDPlus.TestClasses.copy;
/**
 * Point Fault Domain example for one argument
 * @author (Mian and Manuel)
 */
public class OneDimensionalPointFailureDomain {

	public static void pointErrors (int x){
		if (x == -66 )
			x = 5/0;

		if (x == -2 )
			x = 5/0;

		if (x == 51 )
			x = 5/0;

		if (x == 23 )
			x = 5/0;
	}
}

