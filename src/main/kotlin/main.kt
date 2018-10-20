package instanceRunner

import kotlin.system.exitProcess

fun main(args: Array<String>){
    val application = Application().apply {
        this.parseCommandLineArguments(args)
    }
    val executionOptions = application.getExecutionOptions()
    val executors = ProgramRunner.loadExecutors("executors.json")
    if(executors.isEmpty())
    {
        println("Cannot run an external program, there are no executors provided.")
        exitProcess(0)
    }

    val programRunner = ProgramRunner(executionOptions, executors)

    val executionResult = programRunner.execute()

    ResultValidator(application, executionOptions.instance)
            .validateResult()
    println("Execution code: ${executionResult.executionResult}, execution time ${executionResult.executionTime} ms.")
}