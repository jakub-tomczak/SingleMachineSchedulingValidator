package singleMachineTaskScheduler

import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>){
    val application = Application().apply {
        this.parseCommandLineArguments(args)
    }

    val executionOptions = application.getExecutionOptionsFromCmdArgs(addDashInArgsList = false)

    executionOptions.moveOutputFile = true
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
        if(application.batchMode){
            val programs = application.getStudentsPrograms()
            val instances = application.getInstancesForBatch()
            val result = arrayListOf<ExecutionResult>()
            val finalResult = arrayListOf<Pair<String, Double>>()
            programs.forEach {
                program ->
                run {
                    var totalSuccess = 0
                    val errors = arrayListOf<Double>()
                    val studentIndex = program.take(6)
                    result.addAll( instances.asSequence().map {
                            val execOptions = ExecutionOptions(program, it, studentIndex)
                        executeInstance(application, execOptions, executors).also {
                            totalSuccess += if(it.isSolutionFeasible){
                                1
                            } else {
                                0
                            }
                            if(it.isSolutionFeasible || it.executionCode == 0 && it.calculatedResult != it.givenResult){
                                errors.add(100.0*(it.calculatedResult - it.bestResult)/it.bestResult)
                            } else {
                                errors.add(1e6)
                            }
                            println("${execOptions.instance.n} ${execOptions.instance.k} ${execOptions.instance.h} ${it.bestResult}")
                        }
                    }.toList())
                    finalResult.add(Pair(studentIndex, errors.sum()/errors.size))
                    println("$studentIndex, total successes $totalSuccess/${instances.size}\n")
                }
            }
            exportResults(finalResult)
            println(finalResult)

        } else {
            val executionResult = executeInstance(application, executionOptions, executors)
            if(executionResult.isSolutionFeasible){
                println("Feasible solution, cost = ${executionResult.calculatedResult}")
            } else {
                println("Execution error, message: ${executionResult.message}")
            }
        }

    }
}

fun exportResults(finalResult: ArrayList<Pair<String, Double>>) {
    finalResult.sortBy { x -> x.second }
    //val file =
    val toWrite = finalResult.asSequence().map { x -> "${x.first};${x.second}" }.joinToString(separator = "\n")
    File("finalResults.csv").writeText(toWrite)
}


fun executeInstance(application: Application, executionOptions: ExecutionOptions, executors: ArrayList<Executor>) : ExecutionResult {
    val executionResult = ProgramRunner(executionOptions, executors).execute()

    if(executionResult.executionCode != 0)
    {
        println("Failed to execute program ${executionOptions.programPath}")
        return executionResult
    }

    ResultValidator(application, executionOptions)
            .validateResult(executionResult)

    if(executionResult.isSolutionFeasible){

        //println("Error is ${100*(executionResult.calculatedResult - executionResult.bestResult)/executionResult.bestResult} %")
    } else {
        println(executionResult.message)
    }
    if(!application.batchMode)
        println("Execution code: ${executionResult.executionCode}, execution time ${executionResult.executionTime} ms.")
    if (executionResult.executionCode != 0)
        println("Check whether program's path is correct or it ends up correctly.")
    return executionResult
}