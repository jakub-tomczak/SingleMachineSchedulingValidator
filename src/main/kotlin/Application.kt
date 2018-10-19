package instanceRunner

import org.kohsuke.args4j.CmdLineParser
import org.kohsuke.args4j.Option
import org.kohsuke.args4j.CmdLineException

class Application {
    fun parseCommandLineArguments(args: Array<String>) {
        val parser = CmdLineParser(this)

        try {
            parser.parseArgument(args.toList())
        } catch(exception : CmdLineException) {
            print("Could not parse cmd\n${exception.message}")
        }

        validateArguments()
    }

    fun printArgs() = print(String.format("$n, $k, $h, index: `$studentsIndex`, program: `$programToExecute`"))

    fun toProgramOptions() = ProgramOptions(programToExecute, n, k, h)

    private fun validateArguments() {
        assert(n in arrayOf(10, 20, 50, 100, 200, 500, 1000)) {"n=$n is not in the range [10, 20, 50, 100, 200, 500, 1000]."}
        assert(k in 1..10) {"k=$k is not valid. It must be a natural number in range <1,10>"}
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
}