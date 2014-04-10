/**
 * 
 */
package yeti;

/**
 * Class that represents the context of a call.
 * This is usually used for calls that fail. 
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Sep 14, 2011
 *
 */
public class YetiCallContext {
	
	/**
	 * This counter is used to assign a UID to the call context 
	 */
	private static long counter = 0L;
	
	/**
	 * The routine that was called
	 */
	private YetiRoutine routine = null;
	
	/**
	 * The arguments to the routine
	 */
	private YetiCard[] arguments = null;
	
	
	/**
	 * The trace that was produced
	 */
	@SuppressWarnings("unused")
	private Throwable trace = null;

	/**
	 * The trace log that was produced
	 */
	private String traceLog = null;

	/**
	 * The uid of the failure
	 */
	private long uid = 0;


	/**
	 * A simple constructor for the context
	 * 
	 * @param routine the routine that was called
	 * @param arguments the arguments of the call
	 * @param trace the trace of the error (if any)
	 */
	public YetiCallContext(YetiRoutine routine, YetiCard[] arguments,
			Throwable trace, String traceLog) {
		super();
		this.routine = routine;
		this.arguments = arguments;
		this.trace = trace;
		this.traceLog = traceLog;
	}
	
	
	/**
	 * This method generates a test case out of the call context
	 * By default it is a JUnit-like test case.
	 * 
	 * @return a String containing a test case
	 */
	public String generateTestCase() {
		uid=counter++;
		String testCaseBody=this.routine.toStringWithArguments(this.arguments)+traceLog;
		return YetiLogProcessor.indent("/** Test case automatically generated by YETI **/\n@Test public void test_"+
				uid+"() throws Exception {\n"+YetiLogProcessor.indent(testCaseBody)+"\n}");

		
	}

}
