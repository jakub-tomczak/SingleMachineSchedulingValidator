package instanceRunner

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

class ResultValidator(private val application: Application, private val instance: Instance) {
    fun validateResult() =
        if(isOrderingFeasible(loadResults(), loadInstance())) {
            println("OK, feasible solution.")
        } else {
            println("Result is not valid or failed to load a file.")
        }


    private fun loadInstance() : InstanceData {
        val filename = "${application.getInstancesDir()}${instance.getInstanceFilename()}"
        val instanceData = InstanceData(instance.n, instance.k)
        println("Trying to load instance from `$filename`. k = ${instanceData.k}.")
        try{
            val indexedK = instanceData.k - 1
            File(filename)
                    .inputStream()
                    .bufferedReader()
                    .useLines {
                        //2 - first line of the file + first line of the expected instance
                        //k-times first line of an instance
                        //k*n - number of lines in skipped instances
                        it.drop(2 + indexedK + indexedK*instanceData.n).take(instanceData.n).toList()
                    }
                    .forEach {
                        val splitted = it
                                .splitToSequence(" ")
                                .filter { x-> x.isNotEmpty() }
                                .toList()
                                .map { x -> x.toInt() }
                        instanceData.addTask(Task(splitted[0], splitted[1], splitted[2]))
                    }
        } catch(e : FileNotFoundException) {
            //file not found
            println("File $filename has not been found.")
        } catch(e: IOException) {
            //reading out of file
            println("Error while loading an instance from a file $filename.\nError: ${e.message}")
        } catch (e : IndexOutOfBoundsException) {
            println("Malformed instance file.")
        }
        return instanceData
    }

    private fun loadResults() : OrderingResult {
        val filename = "${application.getOutputDir()}${instance.getInstanceOutputFilename(application.studentsIndex)}"
        val loadedResult = OrderingResult(0)
        println("Trying to open results from `$filename`.")
        //load results and save in loadedResult
        try {
            File(filename)
                    .inputStream()
                    .bufferedReader()
                    .use {
                        loadedResult.result = it.readLine().toDouble().toInt()

                        loadedResult.tasksOrder
                                .addAll(
                                        it.readLine()
                                                .split(" ")
                                                .map { x -> x.toInt() })
                    }

        } catch (e: FileNotFoundException) {
            println("File $filename has not been found.")
        } catch(e: IOException) {
            //reading out of file
            println("Error while loading an instance from a file $filename.\nError: ${e.message}")
        }
        return loadedResult
    }

    private fun isOrderingFeasible(orderingResult: OrderingResult, instanceData : InstanceData) : Boolean{
        if(orderingResult.result == 0 || instanceData.tasks.isEmpty())
            return false
        val dueDate =  (instance.h * instanceData.tasksLength).toInt()
        println("Checking feasibility. n=${instanceData.n}, k=${instanceData.k}, h=${instance.h}, dueDate=$dueDate.")
        var sum = 0
        var currentLength = 0
        for (taskIndex in orderingResult.tasksOrder){
            val task = instanceData.tasks[taskIndex]
            if(currentLength + task.p < dueDate)
                sum += (dueDate - currentLength - task.p) * task.a
             else
                sum += (currentLength + task.p - dueDate) * task.b
            currentLength += task.p
        }
        println("Result calculated from task order is $sum, result from program's output is ${orderingResult.result}")
        return orderingResult.result == sum
    }
}