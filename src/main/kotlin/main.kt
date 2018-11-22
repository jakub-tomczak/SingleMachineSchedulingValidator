package instanceRunner

import kotlin.system.exitProcess

fun main(args: Array<String>){
    val n = arrayOf(10, 20, 50, 100, 200, 500, 1000)
    val k = 1..10
    val h = arrayOf(.2, .4, .6, .8)

    val application = Application().apply {
        this.parseCommandLineArguments(args)
    }

    val executionOptions = application.getExecutionOptions()

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
        ResultValidator(application, executionOptions.instance)
                .validateResult()
    } else {
        val programRunner = ProgramRunner(executionOptions, executors)

        var totalSuccess = 0
        if(application.batchMode){
            val instances = Application.loadBatchSettings("batch.json")
            for(instance in instances){
                for(n in instance.n)
                    for(k in instance.k)
                        for(h in instance.h){
                            executionOptions.instance = Instance(n, k, h)
                            totalSuccess += if(executeInstance(application, executionOptions, programRunner)) 1 else 0
                        }
            }
            print("Total successes $totalSuccess/${instances.asSequence().sumBy { x -> x.n.size * x.k.size * x.h.size }}")
        } else {
            executeInstance(application, executionOptions, programRunner)
        }

    }
}

fun executeInstance(application: Application, executionOptions: ExecutionOptions, programRunner: ProgramRunner): Boolean {
    val executionResult = programRunner.execute()

    val result = ResultValidator(application, executionOptions.instance)
            .validateResult()
    if(result){
        println("OK, feasible solution.")
    } else {
        println("Result is not valid or failed to load a file.")
    }
    println("Execution code: ${executionResult.executionCode}, execution time ${executionResult.executionTime} ms.")
    if (executionResult.executionCode != 0)
        println("Check whether program's path is correct or it ends up correctly.")
    return result
}
