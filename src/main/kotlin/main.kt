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
            val finalResult = arrayListOf<FinalResult>()
            programs.forEach {
                program ->
                run {
                    var totalSuccess = 0
                    var totalTime = arrayListOf<Long>()
                    val errors = arrayListOf<Double>()
                    //Naive, assumes, that file has `.bat` or a different extensions at the end
                    val studentIndex = program.take(program.length-4)
                    result.addAll( instances.asSequence().map {
                            val execOptions = ExecutionOptions(program, it, studentIndex)
                            println("Running $studentIndex: ${execOptions.instance.n} ${execOptions.instance.k} ${execOptions.instance.h}")
                        executeInstance(application, execOptions, executors).also {
                            println("Result ${it.bestResult}")
                            if(it.isSolutionFeasible)
                            {
                                totalSuccess++
                            } else {
                                println("Solution error: ${it.message}")
                            }
                            if(it.isSolutionFeasible || it.executionCode == 0 && it.calculatedResult != it.givenResult){
                                errors.add(100.0*(it.calculatedResult - it.bestResult)/it.bestResult)
                                totalTime.add( it.executionTime)
                            } else {
                                errors.add(1e6)
                            }
                        }
                    }.toList())

                    var meanTime = if(totalTime.size == 0) {
                        0.0
                    } else {
                        (totalTime.sum() /  totalTime.size).toDouble()
                    }
                    val studentsFinalResult = FinalResult(studentIndex, errors.sum()/errors.size, meanTime, totalSuccess, errors.size)
                    finalResult.add(studentsFinalResult)
                    println("$studentIndex, total successes $totalSuccess/${errors.size}\n")
                    println(studentsFinalResult)
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

fun exportResults(finalResult: ArrayList<FinalResult>) {
    finalResult.sortBy { x -> x.meanDifference}
    val toWrite = finalResult.asSequence().joinToString(separator = "\n") //.map { x -> "${x.studentsIndex};${x.meanDifference};${x.time};${x.numberOfRuns};${x.numberOfSuccessExecutions}" }
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

//    if(executionResult.isSolutionFeasible){
//
//        //println("Error is ${100*(executionResult.calculatedResult - executionResult.bestResult)/executionResult.bestResult} %")
//    } else {
////        println("Error: ${executionResult.message}")
//    }
    if(!application.batchMode)
        println("Execution code: ${executionResult.executionCode}, execution time ${executionResult.executionTime} ms.")
    if (executionResult.executionCode != 0)
        println("Check whether program's path is correct or it ends up correctly.")
    return executionResult
}