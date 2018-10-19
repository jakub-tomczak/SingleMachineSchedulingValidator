package instanceRunner

fun main(args: Array<String>){
    val application = Application().apply {
        this.parseCommandLineArguments(args)
        this.printArgs()
    }

    val executionOptions = application.toProgramOptions()
    val programRunner = ProgramRunner(executionOptions)
    val executors = programRunner.loadExecutors("executors.json")
}