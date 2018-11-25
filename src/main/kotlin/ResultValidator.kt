package instanceRunner

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Paths
import kotlin.system.exitProcess

class ResultValidator(private val application: Application, private val executionOptions: ExecutionOptions) {
    fun validateResult(executionResult: ExecutionResult) = executionResult.also {
        val resultsFromFile = loadResults()
        it.givenResult = resultsFromFile.result
        if(resultsFromFile.tasksOrder.size != executionOptions.instance.n)
        {
            it.message = "Number of tasks is not correct, ${resultsFromFile.tasksOrder.size} != ${executionOptions.instance.n}"
        } else {
            it.calculatedResult = calculateCost(resultsFromFile, application.getInstance(executionOptions.instance))
            if(it.calculatedResult != it.givenResult){
                it.message = "Cost from result file is not correct, from file ${it.givenResult}, expected ${it.calculatedResult}"
            } else {
                it.isSolutionFeasible = true
            }
        }
    }

    private fun loadResults() : OrderingResult {
        val filename = Paths.get(executionOptions.outputFileDirectory, executionOptions.getOutputFilename())
        val loadedResult = OrderingResult(0)
        println("Trying to open results from `$filename`.")
        //load results and save in loadedResult
        try {
            File(filename.toUri())
                    .inputStream()
                    .bufferedReader()
                    .use {
                        loadedResult.result = it.readLine().toDouble().toInt()
                        val its = it.readLine()
                        loadedResult.tasksOrder
                                .addAll(
                                        its
                                                .split(" ")
                                                .asSequence()
                                                .filter { x -> !x.isNullOrBlank() }
                                                .map { x -> x.toInt() }.toList())
                    }

        } catch (e: FileNotFoundException) {
            println("File $filename has not been found.")
        } catch(e: IOException) {
            //reading out of file
            println("Error while loading an instance from a file $filename.\nError: ${e.message}")
        } catch (e: NumberFormatException) {
            println("Error while parsing tasks list.\nError: ${e.message}")
            exitProcess(-1)
        }
        return loadedResult
    }

    private fun calculateCost(orderingResult: OrderingResult, instanceData : InstanceData) : Int{
        val dueDate =  (executionOptions.instance.h * instanceData.tasksLength).toInt()
        var currentLength = 0
        return orderingResult.tasksOrder.asSequence().map {
            val task = instanceData.tasks[it]
            currentLength += task.p
            if(currentLength < dueDate)
                (dueDate - currentLength) * task.a
            else
                (currentLength - dueDate) * task.b
        }.sum()
    }
}