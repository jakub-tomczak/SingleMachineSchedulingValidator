package instanceRunner

import kotlin.system.exitProcess

fun main(args: Array<String>){
    val application = Application().apply {
        this.parseCommandLineArguments(args)
    }

    val executionOptions = application.getExecutionOptionsFromCmdArgs(addDashInArgsList = false)

    executionOptions.copyOutputFile = true
    executionOptions.outputFileDirectory = "calculatedResults"

    val executors = ProgramRunner.loadExecutors("executors.json")
    if(executors.isEmpty() && !application.programToExecute.isEmpty())
    {
        println("Cannot run an external program, there are no executors provided.")
        exitProcess(0)
    }

    if(application.checkOutFile) {
        //check only .out file
        val result = ExecutionResult(0, 0)
        ResultValidator(application, executionOptions)
                .validateResult(result)
    } else {
        var totalSuccess = 0
        if(application.batchMode){
            val programs = application.getStudentsPrograms().take(1)
            val instances = application.getInstancesForBatch().take(1)
            val result = arrayListOf<ExecutionResult>()
            programs.forEach {
                program ->
                run {
                    result.addAll( instances.asSequence().map {
                        val execOptions = ExecutionOptions(program, it, program.take(6))
                        executeInstance(application, execOptions, executors).also {
                            totalSuccess += if(it.isSolutionFeasible)  1 else 0
                        }
                    }.toList())
                }
            }
            print("Total successes $totalSuccess/${result.size}")
        } else {
            executeInstance(application, executionOptions, executors)
        }

    }
}

fun executeInstance(application: Application, executionOptions: ExecutionOptions, executors: ArrayList<Executor>) : ExecutionResult {
    val executionResult = ProgramRunner(executionOptions, executors).execute()

    ResultValidator(application, executionOptions)
            .validateResult(executionResult)

    if(executionResult.isSolutionFeasible){
        println("OK, feasible solution.")
    } else {
        println(executionResult.message)
    }
    println("Execution code: ${executionResult.executionCode}, execution time ${executionResult.executionTime} ms.")
    if (executionResult.executionCode != 0)
        println("Check whether program's path is correct or it ends up correctly.")
    return executionResult
}
