## Using Yeti ##
### From the command-line ###

Yeti is a Java program that tests automatically and at random programs. The reference implementation tests Java programs at random.
To launch Yeti:

```

Yeti Usage:
java yeti.Yeti [-java|-Java] [[-time=Xs|-time=Xmn]|[-nTests=X]][-testModules=M1:M2:...:Mn][-help|-h][-rawlogs]
-help, -h: prints the help out.
-java, -Java : for calling it on Java.
-jml, -JML : for calling it on JML annotated code.
-cofoja, -CoFoJa : for calling it on Java programs annotated with CoFoJa.  Note that if you do not want to pre-process the classes statically, you should call Yeti using the option -javaagent:cofoja.jar (or any other path to a CoFoJa jar).
-dotnet, -DOTNET : for calling it on .NET assemblies developed with Code-Contracts.
-kermeta, -Kermeta : for calling it on Kermeta code.
-time=Xs, -time=Xmn, -time=Xmin : for calling Yeti for a given amount of time (X can be minutes or seconds, e.g. 2mn or 3s ).
-nTests=X : for calling Yeti to attempt X method calls.
-testModules=M1:M2:...:Mn : for testing one or several modules. Sub-packages of a system can also be specified with asteriks e.g. yeti.test.* will include all the classes in yeti.test + all the classes belonging to the sub-packages of yeti.test .
Note: Kermeta requires a different format for testModules: -testModules=M1,M2,...,Mn
-initClass=X : this will use a user class to initialize the system this class will be a subclass of yeti.environments.YetiInitializer .
-outputUnitTestFile=X : this option stores the generated test cases in a file. THis is binding-specific for Java: X=tests/test0.T.java will store a file T.java from package test0 into the directory tests.
-rawlogs: prints the logs directly instead of processing them at the end.
-nologs : does not print logs, only the final result.
-msCalltimeout=X : sets the timeout (in milliseconds) for a method call to X.Note that too low values may result in blocking Yeti (use at least 30ms for good performances).
-yetiPath=X : stores the path that contains the code to test (e.g. for Java the classpath to consider)
-newInstanceInjectionProbability=X : probability to inject new instances at each call (if relevant). Value between 0 and 100, default is 25.
-probabilityToUseNullValue=X : probability to use a null instance at each variable (if relevant). Value between 0 and 100, default is 1.
-random : uses the pure random strategy.
-randomPlus : uses the random+ strategy that injects interesting values every now and then (this is the default strategy).
-randomPlusPeriodic : uses the random+ strategy and periodically change the values of the standard probalilities (null values, new instances, interesting values).
-randomPlusDecreasing : uses the random+ strategy and decreases the values of the standard probalilities (null values, new instances, interesting values).<br>
-evolutionary : uses GA to evolve a testing strategy.
-chromosome : execute Yeti using a strategy chromosome.
-DSSR : initially uses random+ strategy and based on the results of random+ it uses Dirt Spot Sweeping Random strategy.
-gui : shows the standard graphical user interface for monitoring yeti.
-noInstancesCap : removes the cap on the maximum of instances for a given type. Default is there is and the max is 1000.
-instancesCap=X : sets the cap on the number of instances for any given type. Defaults is 1000.
-tracesOutputFile=X : the file where to output traces on disk.
-tracesInputFiles=X : the files where to input traces from disk (file names separated by ':').
Note that if a file whose name finishes by ".class" then the file is loaded and traces are taken from the failures it produces on methods annotated using yeti.test.YetiTrace
For an example of such file check the class yeti.test.TGenString which can be loaded on the command-line using -tracesInputFiles=yeti.test.TGenString.class
-printNumberOfCallsPerMethod : prints the number of calls per method.
-branchCoverage : shows the branch coverage if available (in Java, this implies instrumenting the bytecode).
-makeMethodsVisible: converts all the protected and private methods into public for testing.
-approximate : approximates the number of unique failures per number of tests.
-compactReport=X : adds a line in file X containing the information about the testing session.
-accurateMinimization : makes the test case minimization more accurate, by adding previous static method calls when used to compute a result.
-javaBased:doNotIngoreIllegalArgumentExceptions : this makes sure that we do not treat IllegalArgumentExceptions as precondition violations (we ignore them).
```

As an example, to test YetiTest and String for 10 seconds:

```

java yeti.Yeti -java -testModules=yeti.test.YetiTest:java.lang.String -time=10s
```

Note that Yeti is also compatible with assertion checking turned on, so the "-ea"
flag will help Yeti finding more bugs. Example:

```

java -ea yeti.Yeti -java -testModules=yeti.test.YetiTest:java.lang.String -time=10s
```

Some of the issues one might face are with the paths. Here are a couple of advices:
**Make sure that all the .jar files in lib are in your CLASSPATH when building the project. (More than ever as the javassist jar is necessary even when not using branch coverage)** When testing, make sure that the CLASSPATH allows you to find all necessary classes.
**When testing, define the yetiPath option to the top of the hierarchies of classes that you want to test. In short, YETI loads classes from all entry points in the yetiPath and will use them as helper classes. Other classes can be loaded in the VM but YETI will not know about them. This avoids performance issues.**

### From a Junit test ###

When willing to use Yeti from a unit test, add the following code to your unit test suite (e.g. JUnit):

```

…
import yeti.Yeti;
import yeti.YetiLog;
…
@Test
public void testRandom(){
String []args = { "-java", "-time=5s", "-yetiPath=.", "-testModules=myClassToTest" };
Yeti.main(args);
assertEquals(0,YetiLog.proc.getNumberOfUniqueFaults());
}
```

### By writing a wrapper ###

When some code is difficult to test because it has either complex initialization or known bugs that pollute
the output, it can generally be a good idea to write a wrapper. Such wrappers will perform some filtering
through (for example) contracts and ease the initialization. For example for java.lang.String (using
[CoFoJa](http://code.google.com/p/cofoja/) contracts) :

```

import com.google.java.contract.*;
public class StringWrap{

String s=null;
public StringWrap(String s)
this.s = s;
}
@Requires( "0<=min<=max" )
public String subString(int min, int max){
return s.subString(min,max);
}
```

## Getting YETI ##

### Downloading YETI ###
A precompiled/pre-checked out version of YETI is available in the [[donwload](http://yeti.origo.ethz.ch/download)] section. To use it, do the following tasks in order:
**download the archive** unzip it
**add the root of the created folder to your classpath variable**

As a help, you can use the following variables and definitions in your .bashrc or .bash\_profile:

```

export YETI=yeti_root_dir
export YETILIB=$YETI/lib
export CLASSPATH=.:$YETILIB/cofoja.jar:$CLASSPATH:$YETI:$YETILIB/hadoop-0.19.2-core.jar:$YETILIB/javassist.jar:$YETILIB/jgap.jar:$YETILIB/jmljunitruntime.jar:$YETILIB/jmlmodels.jar:$YETILIB/jmlmodelsnonrac.jar:$YETILIB/jmlruntime.jar:$YETILIB/kermeta-simplified-interpreter.jar:$YETILIB/kermeta-interpreter-lite.jar:$YETILIB/commons-io-2.0.1.jar
```


Once this has been done, you can use YETI as described above.

### Compiling YETI ###
The best way to compile YETI is to check out the trunk in Eclipse (e.g. by using SVNclipse) and then to compile it using the Build all feature.
All libraries are available in the lib subdirectory and this is found automatically by the Eclipse Java view.
In case you want to compile it by hand, you will need to indicate them in the classpath.

Alternatively, an ant file is available to compile YETI. In this case, simply type `ant build`.

When you only want to use a subset of the options of YETI (e.g. only CoFoJa or Kermeta), you can use `ant trim-`_?_ where _?_ can be `jul`, `ga`, `kermeta`, `dot net`, `cloud`, `cofoja`, `doc`, `all-but-cofoja`, or `all-but-kermeta`.
This removes the associated code as well as the associated libraries.

## Yeti Infrastructure ##
The Yeti Infrastructure can be separated in two main parts:
  * The main infrastructure which includes the classes that manage the system while executing as well as post-processing of the tests.
  * The language modelling part which comprises the classes that make the meta-model of  programming language that we consider.


### Main infrastructure classes ###

The main infrastructure includes the following classes:

  * Yeti: the class containing the main method, the parsing of options, the help.
  * YetiLog: a helper class for printing debug logs and testing logs.
  * YetiLogProcessor: an interface for processing logs.
  * YetiJavaLogProcessor: a specialization to process logs for the Java reference implementation of Yeti.
  * YetiEngine: a simple engine that makes calls for a given number of times.
  * YetiStrategy: an abstract class for providing an interface for all standard strategies.
  * YetiRandomStrategy: the basic random strategy.
  * YetiTestManager: class called by the engine in order to make the actual calls.
  * YetiJavaTestManager: a class that handles calls made in the Java standard implementation.
  * YetiProgrammingLanguageProperties: a placeholder for all the instances language related.
  * YetiJavaProperties: specialisation of the properties for the Java language.
  * YetiInitializer: an abstract class for using initializers.
  * YetiJavaInitializer: initializes the class.

![http://yeti-test.googlecode.com/git/ClassDiagram-InfrastructurePart.png](http://yeti-test.googlecode.com/git/ClassDiagram-InfrastructurePart.png)
<a href='Hidden comment: 
[img_assist|nid=15|title=|desc=|link=node|align=center|width=640|height=118]
'></a>

### Language modelling infrastructure classes ###
The classes in this class are modelling the language part.
The parent classes are generally language independent, while the subclasses show what is Java-specific.
Note that Yeti makes a difference between types (which can be instantiated using either constructors or routines returning a value of that type), modules (that are lists of routines to test), and variables (that belong to several types).


  * YetiCard: represents a a card. A card is a Yeti-specific concept that can either map onto a wildcard (a value late bound) or a YetiVariable.
  * YetiVariable: a regular variable in Yeti.
  * YetiIdentifier: an identifier for a variable/a type
  * YetiName: a name in Yeti.
  * YetiType: a type of data. Note that types form a graph (all parent types and children types). Each parent  type has also the constructors of the child type as well as available instances.
  * YetiJavaSpecificType: the way to represent Java types. Has specific representations for primitive types.
  * YetiRoutine: a super type for routines (functions, methods, constructors).
  * YetiJavaRoutine: a class for representing routines in Java (object/class methods and constructors).
  * YetiJavaMethod: a class/object method (note that the arguments are then either including the target object or not).
  * YetiJavaConstructor: a constructor (in the Java term).
  * YetiJavaPrefetchingClassLoader: a loader that inspects the loaded bytecode.
  * YetiModule: a class to store list of routines to test, can be used to combine them.
  * YetiJavaModule: a basic Java module consists of a class.

![http://yeti-test.googlecode.com/git/ClassDiagram-ModelingLanguagePart.png](http://yeti-test.googlecode.com/git/ClassDiagram-ModelingLanguagePart.png)

<a href='Hidden comment: 
[img_assist|nid=16|title=Class Diagram, Modelling language Part|desc=|link=node|align=center|width=640|height=155]
'></a>

### How YETI works in general ###

1. Yeti initializes information at load time (it references loaded methods and types of data)
  * Each time it loads a class, it generates the type information of the loaded types and adds them into the type hierarchy
  * Each time it loads a class, it has a look at the constructors  and adds them as routines that generate the instances
  * Each time it loads a class it also looks at the methods of the class and generates the corresponding routine objects,
it adds the routine to the corresponding module.
(all this is triggered in the YetiJavaPrefetchingClassLoader)

2. When the testing starts: Yeti picks a routine at random in the module to test
  * It tries to find instances that match the needed types. (each type maintains a list of its instances)
  * If they do not match, it looks at constructors for such a type and calls them in a recursive manner to create them.
(each type maintains a list of its constructors)
  * When it makes a call, it adds the result as an instance of that type.
  * Go back to point 2.

The above is a schematical algorithm

## Extending Yeti ##

Yeti enforces strong decoupling between strategies and the actual language constructs.
To extend Yeti no strategy should have to be modified.
The best way to start with any extension is to have a look at the Java binding and understand how it works in terms of structure.

  * A first step is to set up a new environment in the Yeti package system. As an example all classes in the new language should belong to `yeti.environment`._nameOfLanguage_.
  * A new subclass of `YetiProgrammingLanguageProperties` should be defined to store helper classes for the new language.
  * A new subclass of `YetiInitializer` should be defined to initialize the program. This class can be used to read all reflexive information about the system.
  * A new subclass of `YetiTestManager` should be defined to communicate with the target language and proceed with the actual calls.
  * A new subclass of `YetiRoutine` should be created to actually store the definition of routines in the target language.
  * A new subclass of `YetiModule` can be defined to handle the type of modules to test.

Any helper class may be added in the correct package (e.g. `YetiJavaPrefetchingLoader`)!

When you are ready to do so, modify `Yeti.java` and add your option to it. Base your implementation on what is available for JML or CoFoJa.
For each line added that way, please add a comment `//@Yeti`_nameOfLanguage_ `Binding` at the end of each line. This can then be used to trim your code out of Yeti if needs be.

## Conclusions ##

Yeti is still evolving at a fast pace. Stay tuned for updates on this page!