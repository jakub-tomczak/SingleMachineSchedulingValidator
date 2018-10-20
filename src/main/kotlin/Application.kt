package instanceRunner

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.CmdLineException
import java.io.File

class Application {
    fun parseCommandLineArguments(args: Array<String>) {
        val parser = CmdLineParser(this)

        try {
            parser.parseArgument(args.toList())
        } catch(exception : CmdLineException) {
            println("Could not parse arguments:\n${exception.message}")
        }

        //TODO
        //add loading options from JSON
        validateArguments()
    }

    fun printArgs() = println(String.format("$n, $k, $h, index: `$studentsIndex`, program: `$programToExecute`"))

    fun getExecutionOptions() = ExecutionOptions(programToExecute, Instance(n, k, h), studentsIndex)

    fun getInstancesDir() =
            if(instancesDir.isEmpty())
                ""
            else
                instancesDir + File.separatorChar
    fun getOutputDir() =
            if(outputDir.isEmpty())
                ""
            else
                outputDir + File.separatorChar

    private fun validateArguments() {
        assert(n in arrayOf(10, 20, 50, 100, 200, 500, 1000)) {"n=$n is not in the range [10, 20, 50, 100, 200, 500, 1000]."}
        assert(k in 0..9) {"k=$k is not valid. It must be a natural number in range <0,9>"}
        assert(h in 0..1) {"h=$h is not valid. It must be a real number in range <0,1>"}
        assert(programToExecute.isNotEmpty()) {"Program to execute name cannot be empty."}
    }

    @Option(name = "-p", usage = "Name of the program to be executed.")
    var programToExecute = ""
        private set

    @Option(name="-n", usage = "Number of tasks.")
    var n = 0
        private set

    @Option(name="-k", usage = "Instance number.")
    var k = 0
        private set

    @Option(name = "-h", usage = "Due date's coefficient.")
    var h = 0.0
        private set

    @Option(name="-index", usage = "Index of a student that written the program.")
    var studentsIndex = ""
        private set

    //needed if instance or output files are in the different directory than an executable
    private var instancesDir = ""
    private var outputDir = ""


}