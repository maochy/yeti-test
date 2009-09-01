package yeti;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;

import yeti.environments.YetiInitializer;
import yeti.environments.YetiLoader;
import yeti.environments.YetiProgrammingLanguageProperties;
import yeti.environments.YetiTestManager;
import yeti.environments.csharp.YetiCsharpInitializer;
import yeti.environments.csharp.YetiCsharpLogProcessor;
import yeti.environments.csharp.YetiCsharpProperties;
import yeti.environments.csharp.YetiCsharpTestManager;
import yeti.environments.csharp.YetiServerSocket;
import yeti.environments.java.YetiJavaInitializer;
import yeti.environments.java.YetiJavaLogProcessor;
import yeti.environments.java.YetiJavaPrefetchingLoader;
import yeti.environments.java.YetiJavaProperties;
import yeti.environments.java.YetiJavaTestManager;
import yeti.environments.jml.YetiJMLPrefetchingLoader;
import yeti.monitoring.YetiGUIFaultsOverTime;
import yeti.monitoring.YetiGUINumberOfCallsOverTime;
import yeti.monitoring.YetiGUINumberOfFailuresOverTime;
import yeti.monitoring.YetiGUINumberOfVariablesOverTime;
import yeti.strategies.YetiRandomPlusStrategy;
import yeti.strategies.YetiRandomStrategy;


/**
 * Class that represents the main launching class of Yeti
 * 
 * @author Manuel Oriol (manuel@cs.york.ac.uk)
 * @date Jun 22, 2009
 */
public class Yeti {

	/**
	 * The properties of the programming language.
	 */
	public static YetiProgrammingLanguageProperties pl;

	/**
	 * The strategy being used.
	 */
	public static YetiStrategy strategy = null;

	/**
	 * The tested modules.
	 */
	public static YetiModule testModule = null;


	/**
	 * Stores the path to use for testing.
	 */
	public static String yetiPath = System.getProperty("java.class.path");

	/**
	 * Main method of Yeti. It serves YetiRun the arguments it receives.
	 * Arguments are numerous. Here is a list of the current ones:
	 * <br>
	 * <br>
	 * -java, -Java : for calling it on Java.<br>
	 * -jml, -JML : for calling it on JML annotated code.<br>
	 * -dotnet, -DOTNET : for calling it on .NET assemblies developed with Code-Contracts.<br>
	 * -time=Xs, -time=Xmn : for calling Yeti for a given amount of time (X can be minutes or seconds, e.g. 2mn or 3s ).<br>
	 * -nTests=X : for calling Yeti to attempt X method calls.<br>
	 * -testModules=M1:M2:...:Mn : for testing one or several modules.<br>
	 * -initClass=X : this will use a user class to initialize the system this class will be a subclass of yeti.environments.YetiInitializer<br>
	 * -help, -h: prints the help out.<br>
	 * -rawlogs : prints the logs directly instead of processing them at the end. <br>
	 * -nologs : does not print logs, only the final result.<br>
	 * -msCalltimeout=X : sets the timeout (in milliseconds) for a method call to X. Note that too
	 * low values may result in blocking Yeti (use at least 30ms for good performances).<br>
	 * -yetiPath=X : stores the path that contains the code to test (e.g. for Java the classpath to consider)<br>
	 * -newInstanceInjectionProbability=X : probability to inject new instances at each call (if relevant). Value between 0 and 100. <br>
	 * -probabilityToUseNullValue=X : probability to use a null instance at each variable (if relevant). Value between 0 and 100 default is 1.<br>
	 * -randomPlus : uses the random+ strategy that injects interesting values every now and then.<br>
	 * -gui : shows the standard graphical user interface for monitoring yeti.<br>
	 * -noInstancesCap : removes the cap on the maximum of instances for a given type. Default is there is and the max is 1000.<br>
	 * -instancesCap=X : sets the cap on the number of instances for any given type. Defaults is 1000.<br>
	 * -tracesOutputFile=X : the file where to output traces on disk<br>
	 * -tracesInputFiles=X : the files where to input traces from disk (file names separated by ':').
	 * -printNumberOfCallsPerMehtod : prints the number of calls per method.<br>
	 * @param args the arguments of the program
	 */
	public static void main (String[] args) {

		Yeti.YetiRun(args);

	}

	/**
	 * The Run Method for Yeti.
	 * This will receive the same arguments as described for method main and process them
	 * @param args the list of arguments passed on either by main or Map Method in YetiMap
	 */	
	public static void YetiRun(String[] args){
		YetiEngine engine;
		YetiInitializer secondaryInitializer = null;
		boolean isJava = false;
		boolean isJML = false;
		boolean isDotNet = false;
		boolean isTimeout = false;
		int timeOutSec=0;
		boolean isNTests = false;
		boolean isRawLog = false;
		boolean isNoLogs = false;
		boolean isRandomPlus = false;
		boolean showMonitoringGui = false;
		boolean printNumberOfCallsPerMehtod = false;
		int nTests=0;
		String []modulesToTest=null;
		int callsTimeOut=75;
		Thread th = null;
		String tracesOutputFile = null;
		String[] traceInputFiles = null;
		YetiLogProcessor logProcessor = null;

		// we parse all arguments of the program
		for (String s0: args) {
			// if it is printing help
			if (s0.equals("-help")||s0.equals("-h")) {
				Yeti.printHelp();
				return;
			}
			// if Java
			if (s0.equals("-java")||s0.equals("-Java")) {
				isJava = true;
				continue;
			}
			// if JML
			//TODO somebody could also set -java
			if (s0.toLowerCase().equals("-jml")) {
				isJML = true;
				continue;
			}

			//if .NET
			if(s0.toLowerCase().equals("-dotnet")){		
				isDotNet = true;
				continue;
			}

			// if testing for time value
			if (s0.startsWith("-time=")) {
				isTimeout=true;
				int size = s0.length();
				// if the time value is in seconds
				if (s0.substring(size-1).equals("s")) {
					timeOutSec=(Integer.parseInt(s0.substring(6, size-1)));
					continue;
				}
				// if the time value is in minutes
				if (s0.substring(size-2).equals("mn")) {
					timeOutSec=60*(Integer.parseInt(s0.substring(6, size-2)));
					continue;	
				}				
			}

			// if testing for time value
			if (s0.startsWith("-msCalltimeout=")) {
				int size = s0.length();
				// if the time value is in seconds
				callsTimeOut=(Integer.parseInt(s0.substring(15, size)));
				if (callsTimeOut<=0) {
					Yeti.printHelp();
					return;
				}
				continue;
			}
			// if testing for new instance injection probability
			if (s0.startsWith("-newInstanceInjectionProbability=")) {
				int size = s0.length();
				// if the time value is in seconds
				YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY=(Integer.parseInt(s0.substring(33, size)))/100d;
				if ((YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY>1.0)||(YetiStrategy.NEW_INSTANCES_INJECTION_PROBABILITY<0)) {
					Yeti.printHelp();
					return;
				}
				continue;
			}

			// if testing for new instance injection probability
			if (s0.startsWith("-probabilityToUseNullValue=")) {
				int size = s0.length();
				// if the time value is in seconds
				YetiVariable.PROBABILITY_TO_USE_NULL_VALUE=(Integer.parseInt(s0.substring(27, size)))/100d;
				if ((YetiVariable.PROBABILITY_TO_USE_NULL_VALUE>1.0)||(YetiVariable.PROBABILITY_TO_USE_NULL_VALUE<0)) {
					Yeti.printHelp();
					return;
				}
				continue;
			}

			// if it is for a number of tests
			if (s0.startsWith("-nTests=")) {
				isNTests=true;
				nTests=(Integer.parseInt(s0.substring(8)));
				continue;
			}
			// we want to test these modules
			if (s0.startsWith("-testModules=")) {
				String s1=s0.substring(13);
				modulesToTest=s1.split(":");
				continue;
			}
			// we want to have only logs in standard form
			if (s0.equals("-rawlogs")) {
				isRawLog = true;
				continue;	
			}

			// we want to have only logs in standard form
			if (s0.equals("-nologs")) {
				isNoLogs = true;
				continue;	
			}

			// we want to have only logs in standard form
			if (s0.equals("-gui")) {
				showMonitoringGui = true;
				continue;	
			}

			// we want to use the following path
			if (s0.startsWith("-yetiPath=")) {				

				String s1=s0.substring(10);
				Yeti.yetiPath = s1;
				System.setProperty("java.class.path", System.getProperty("java.class.path")+":"+s1);

				continue;
			}

			// we can use the randomPlus strategy
			if (s0.equals("-randomPlus")) {
				isRandomPlus = true;
				continue;	
			}

			// we have no limits for the number of instances
			if (s0.equals("-noInstancesCap")) {
				YetiType.defaultTypesHaveCapOnNumberOfDirectInstances = true;
				continue;	
			}


			// we read a new limit for the number of instances (default is 1000)
			if (s0.startsWith("-instancesCap=")) {
				YetiType.defaultMaximumNumberOfDirectInstances=(Integer.parseInt(s0.substring(14)));
				continue;	
			}

			//setting up the secondary initializer
			if(s0.startsWith("-initClass=")){
				try {
					secondaryInitializer= (YetiInitializer)Yeti.class.getClassLoader().loadClass(s0.substring(11)).newInstance();
				} catch (Exception e) {
					System.err.print("Problem while loading user initializer class "+s0.substring(11));
					e.printStackTrace();
					return;
				}
				continue;
			}


			// we want to test these modules
			if (s0.startsWith("-tracesOutputFile=")) {
				tracesOutputFile=s0.substring(18);
				continue;
			}
			// we want to test these modules
			if (s0.startsWith("-tracesInputFiles=")) {
				String s1=s0.substring(18);
				traceInputFiles = s1.split(":");
				continue;
			}

			// If we want to gather the number of calls per method
			if (s0.equals("-printNumberOfCallsPerMehtod")) {
				printNumberOfCallsPerMehtod = true;
				continue;	
			}

			System.out.println("Yeti could not understand option: "+s0);
			Yeti.printHelp();
			return;

		}

		// we read traces if there are some to read and we initialize the list of Errors with it.
		HashMap<String, Object> initialListOfErrors = null;
		if (traceInputFiles!=null) {
			initialListOfErrors = new HashMap<String, Object>();
			// for each file to use, we read the traces and add them to our initial list
			for (String fileName: traceInputFiles) {
				for (String trace: Yeti.readTracesFromFile(fileName)) {
					initialListOfErrors.put(trace, 0);
				}
			}

		}

		//test of options to set up the YetiProperties for Java
		if (isJava) {
			YetiLoader prefetchingLoader = new YetiJavaPrefetchingLoader(yetiPath);
			YetiInitializer initializer = new YetiJavaInitializer(prefetchingLoader);
			YetiTestManager testManager = new YetiJavaTestManager();
			logProcessor = new YetiJavaLogProcessor(initialListOfErrors);
			pl=new YetiJavaProperties(initializer, testManager, logProcessor);
		}

		//test of options to set up the YetiProperties for JML
		if (isJML) {
			YetiLoader prefetchingLoader = new YetiJMLPrefetchingLoader(yetiPath);
			YetiInitializer initializer = new YetiJavaInitializer(prefetchingLoader);
			YetiTestManager testManager = new YetiJavaTestManager();
			logProcessor = new YetiJavaLogProcessor(initialListOfErrors);
			pl=new YetiJavaProperties(initializer, testManager, logProcessor);
		}

		//test of options to set up the YetiProperties for .NET assemblies
		if (isDotNet) {

			System.out.println("****************************************");
			System.out.println("STARTING CsharpReflexiveLayer.exe ");
			System.out.println("****************************************");

			th = new Thread(new Runnable()
			{

				public void run() {
					Runtime run = Runtime.getRuntime();
					String command = yetiPath + "CsharpReflexiveLayer.exe";					
					try {
						Process p = run.exec(command);						
						InputStream in = p.getInputStream();						

						@SuppressWarnings("unused")
						int c;
						while ((c = in.read()) != -1) {
							//System.out.print((char) c);
						}

					} catch (IOException e) {					
						YetiCsharpInitializer.initflag=true;
					}
				}
			} );

			th.start();


			YetiInitializer initializer = new YetiCsharpInitializer();
			YetiTestManager testManager = new YetiCsharpTestManager();
			logProcessor = new YetiCsharpLogProcessor(initialListOfErrors);
			YetiServerSocket socketConnector = new YetiServerSocket();
			pl=new YetiCsharpProperties(initializer, testManager, logProcessor, socketConnector);
			System.out.println("\nMaking the .NET test-case calls...\n");			
			System.out.println("----------------------------------------");

		}

		//if it is raw logs, then set it		
		if (isRawLog) {
			pl.setRawLog(isRawLog);
		}

		//if it is no logs, then set it		
		if (isNoLogs) {
			pl.setNoLogs(isNoLogs);
		}

		// initializing Yeti
		try {
			pl.getInitializer().initialize(args);
		} catch (YetiInitializationException e) {
			//should never happen
			e.printStackTrace();
		}

		// calls the secondary initializer
		if (secondaryInitializer!=null) {
			try {
				secondaryInitializer.initialize(args);
			} catch (YetiInitializationException e1) {
				// if there is an issue with the custom initialization
				System.err.print("Problem while executing user initializer class "+secondaryInitializer.getClass().getName());
				e1.printStackTrace();
				return;

			}
		}

		// create a YetiTestManager and 
		YetiTestManager testManager = pl.getTestManager(); 

		//sets the calls timeout
		if (!(callsTimeOut<=0)) {
			testManager.setTimeoutInMilliseconds(callsTimeOut);
		}

		// We set the strategy
		if (isRandomPlus) {
			strategy= new YetiRandomPlusStrategy(testManager);
		} else {
			strategy= new YetiRandomStrategy(testManager);
		}


		// getting the module(s) to test
		YetiModule mod=null;

		// if the modules to test is actually one module
		if (modulesToTest.length==1) {
			// we get the module
			mod=YetiModule.allModules.get(modulesToTest[0]);

			//check
			YetiLog.printDebugLog("Loading:"+modulesToTest[0], Yeti.class);

			// if it does not exist we stop
			if(mod==null) {
				System.err.println(modulesToTest[0] + " was not found. Please check");
				System.err.println("Testing halted");
				printHelp();
				return;
			}
		} else {
			// if the modules to test are many
			ArrayList<YetiModule> modules=new ArrayList<YetiModule>(modulesToTest.length);
			// we iterate through the modules
			// if the module does not exist we omit it
			for(String moduleToTest : modulesToTest) {
				YetiModule yetiModuleToTest = YetiModule.allModules.get(moduleToTest);
				if(yetiModuleToTest==null) {
					System.err.println(moduleToTest + " was not found. Please check");
					System.err.println(moduleToTest + " is skipped from testing");
				} else {
					modules.add(yetiModuleToTest);
				}
			}
			// if none rests at the end, we stop the program
			if(modules.isEmpty()) {
				System.err.println("Testing halted");
				printHelp();
				return;
			}
			// otherwise, we combine all modules
			mod = YetiModule.combineModules(modules.toArray(new YetiModule[modules.size()]));
		}
		// we let everybody use the tested module
		Yeti.testModule = mod;

		// creating the engine object
		engine= new YetiEngine(strategy,testManager);

		// Creating the log processor
		if (showMonitoringGui) {
			YetiLog.proc=new YetiGUINumberOfVariablesOverTime(new YetiGUINumberOfFailuresOverTime(new YetiGUINumberOfCallsOverTime(new YetiGUIFaultsOverTime(pl.getLogProcessor(),100),100),100),100);
		} else {
			YetiLog.proc=pl.getLogProcessor();
		}

		// logging purposes:
		long startTestingTime = new Date().getTime();
		// depending of the options launch the testing
		if (isNTests)
			// if it is the number of states
			engine.testModuleForNumberOfTests(mod, nTests);
		else if (isTimeout) 
			// if it is according to a timeout
			engine.testModuleForNSeconds(mod, timeOutSec);
		else {
			printHelp();
			return;
		}
		// logging purposes:
		long endTestingTime = new Date().getTime();

		// for logging purposes
		if (isTimeout) {
			System.out.println("\n/** Testing Session finished, time: "+(endTestingTime-startTestingTime)+"ms **/");
		}

		boolean isProcessed = false;
		String aggregationProcessing = "";
		// presents the logs
		System.out.println("/** Testing Session finished, number of tests:"+YetiLog.numberOfCalls+", time: "+(endTestingTime-startTestingTime)+"ms , number of failures: "+YetiLog.numberOfErrors+"**/");	
		if (!Yeti.pl.isRawLog()&&!Yeti.pl.isNoLogs()) {
			isProcessed = true;		
			for (String log: YetiLog.proc.processLogs()) {
				System.out.println(log);
			}
			// logging purposes: (slightly wrong because of printing)
			long endProcessingTime = new Date().getTime();
			aggregationProcessing = "/** Processing time: "+(endProcessingTime-endTestingTime)+"ms **/";
		}
		if (!isProcessed) {

			YetiLogProcessor lp = (YetiLogProcessor)Yeti.pl.getLogProcessor();
			System.out.println("/** Unique relevant bugs: "+lp.getNumberOfUniqueFaults()+" **/");

		}
		if (isProcessed) {
			System.out.println("/** Testing Session finished, number of tests:"+YetiLog.numberOfCalls+", time: "+(endTestingTime-startTestingTime)+"ms , number of failures: "+YetiLog.numberOfErrors+"**/");
			System.out.println(aggregationProcessing);

		}
		// if users want to print the traces in an outputFile, we do it now
		if ((tracesOutputFile!=null)&&(logProcessor!=null)) {
			Yeti.outputTracesToFile(logProcessor.listOfNewErrors, tracesOutputFile,logProcessor.numberOfNonErrors);
		}
		
		if (printNumberOfCallsPerMehtod) {
			System.out.println("Trace of number of calls per method:");
			for (YetiRoutine r: Yeti.testModule.routinesInModule.values()) {
				System.out.println(r.getSignature()+": Called: "+r.getnTimesCalled()+", Successfully: "+r.getnTimesCalledSuccessfully()+", Undecidable: "+r.getnTimesCalledUndecidable()+", Unsuccessfully: "+r.getnTimesCalledUnsuccessfully());
			}
		}
		System.out.println("/** Testing finished **/");


	}

	/**
	 * Outputs traces into a file passed as an argument. This method is coded to work with method readTracesFromFile 
	 * 
	 * @param listOfErrors the list of errors to output.
	 * @param fileName the name of the file in which to write them.
	 */
	public static void outputTracesToFile(HashMap<String,Object> listOfErrors,String fileName, int nNonErrors) {
		try {
			// we open the file
			PrintStream ps = new PrintStream(fileName);

			// we get our values
			Iterator<String> traces = listOfErrors.keySet().iterator();
			Iterator<Object> dates = listOfErrors.values().iterator();


			DateFormat df = DateFormat.getDateInstance(DateFormat.LONG, Locale.US);
			// we print all the traces in the file
			for (int i=0; i<listOfErrors.size();i++) {
				Object traceDate = dates.next();
				if (traceDate instanceof Date) {
					ps.println("Trace "+(i+1+nNonErrors)+" discovered on "+df.format(((Date)traceDate))+":\n"+traces.next());
				} else {
					ps.println("Trace "+(i+1+nNonErrors)+" discovered on "+traceDate.toString()+":\n"+traces.next());					
				}
			}

			// we close the stream
			ps.close();
		} catch (FileNotFoundException e) {
			System.err.println("Trying to write exception trace to "+fileName+": cannot open or create file");
		}

	}


	/**
	 * Method that reads exception traces from a file where they were stored.
	 * The exceptions are supposed to be of the format:<br>
	 * "Trace ...\n..."
	 * 
	 * The method also supports comments inserted in the form of a line starting with "//"
	 * 
	 * @param fileName the file to read
	 * @return an ArrayList containing all decoded traces 
	 */
	public static ArrayList<String> readTracesFromFile(String fileName) {
		ArrayList<String> result = new ArrayList<String>();
		BufferedReader br=null;
		// we first check that the file exists
		if (new File(fileName).exists()) {
			try {
				// we create a reader to read line by line the traces
				br = new BufferedReader(new FileReader(fileName));
				boolean isValid = true;

				// we read line by line
				String currentLine = br.readLine();
				while(isValid) {
					if (currentLine==null) 
						break;
					// we have to remove comments
					if (!currentLine.startsWith("//")) {
						// the first line of the exception trace should start with "Trace "
						if (currentLine.startsWith("Trace ")) {
							currentLine=br.readLine();
							String trace = null;
							// we read the trace itself
							while((currentLine!=null)&&!currentLine.startsWith("Trace ")) {
								if (currentLine.startsWith("\t")) {
									if (trace == null) {
										trace = currentLine;
									} else {
										trace = trace + "\n" + currentLine;
									}
								}
								currentLine = br.readLine();
							}
							// once read we add it to the result
							result.add(trace);
							YetiLog.printDebugLog("Imported trace:\n"+trace, Yeti.class);
						} else {
							isValid = false;
						}
					}else {
						// in case the file started by a comment
						currentLine = br.readLine();
					}
				}
			} catch (FileNotFoundException e) {
				// Should never happen unless somebody removed it between our two tests!!!
				// e.printStackTrace();
			} catch (IOException e) {
				// Should never happen either
				// e.printStackTrace();
			}
		} else {
			// just in case the file cannot be open, we print a message
			System.err.println("Trying to read exception trace from "+fileName+": file not found, continuing with execution");
		}
		// we close the streams when we are finished
		if (br!=null) {
			try {
				br.close();
			} catch (IOException e) {
				// Should never happen
				// e.printStackTrace();
			}
		}
		YetiLog.printDebugLog("Imported "+result.size()+" traces", Yeti.class,true);
		return result;

	}

	/**
	 * This is a simple help printing utility function.
	 */
	public static void printHelp() {
		System.out.println("Yeti Usage:\n java yeti.Yeti [-java|-Java] [[-time=Xs|-time=Xmn]|[-nTests=X]][-testModules=M1:M2:...:Mn][-help|-h][-rawlog]");
		System.out.println("\t-java, -Java : for calling it on Java.");
		System.out.println("\t-jml, -JML : for calling it on JML annotated code.");
		System.out.println("\t-dotnet, -DOTNET : for calling it on .NET assemblies developed with Code-Contracts.");
		System.out.println("\t-time=Xs, -time=Xmn : for calling Yeti for a given amount of time (X can be minutes or seconds, e.g. 2mn or 3s ).");
		System.out.println("\t-nTests=X : for calling Yeti to attempt X method calls.");
		System.out.println("\t-testModules=M1:M2:...:Mn : for testing one or several modules.");
		System.out.println("\t-help, -h: prints the help out.");
		System.out.println("\t-initClass=X : this will use a user class to initialize the system this class will be a subclass of yeti.environments.YetiInitializer\n");
		System.out.println("\t-rawlogs: prints the logs directly instead of processing them at the end.");
		System.out.println("\t-nologs : does not print logs, only the final result.");
		System.out.println("\t-msCalltimeout=X : sets the timeout (in milliseconds) for a method call to X.Note that too low values may result in blocking Yeti (use at least 30ms for good performances)");
		System.out.println("\t-yetiPath=X : stores the path that contains the code to test (e.g. for Java the classpath to consider)");
		System.out.println("\t-newInstanceInjectionProbability=X : probability to inject new instances at each call (if relevant). Value between 0 and 100, default is 25.");
		System.out.println("\t-probabilityToUseNullValue=X : probability to use a null instance at each variable (if relevant). Value between 0 and 100, default is 1.");
		System.out.println("\t-randomPlus : uses the random+ strategy that injects interesting values every now and then.");
		System.out.println("\t-gui : shows the standard graphical user interface for monitoring yeti.");
		System.out.println("\t-noInstancesCap : removes the cap on the maximum of instances for a given type. Default is there is and the max is 1000.");
		System.out.println("\t-instancesCap=X : sets the cap on the number of instances for any given type. Defaults is 1000.");
		System.out.println("\t-tracesOutputFile=X : the file where to output traces on disk.");
		System.out.println("\t-tracesInputFiles=X : the files where to input traces from disk (file names separated by ':').");
		System.out.println("\t-printNumberOfCallsPerMehtod : prints the number of calls per method.");

	}

}