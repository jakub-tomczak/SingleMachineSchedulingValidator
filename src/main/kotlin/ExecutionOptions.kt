package singleMachineTaskScheduler

import singleMachineTaskScheduler.data.Instance
import java.io.File

data class ExecutionOptions(var programPath : String, var instance: Instance, var studentIndex : String){
    val extension = programPath.split('.').last()

    var moveOutputFile = true
    var outputFileDirectory = "ProgramOutputs"
    var addDashInArgsList = false

    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programPath -n ${instance.n} -k ${instance.k} -h ${instance.h}"
            else
                "$programPath ${instance.n} ${instance.k} ${instance.h}"

    fun getOutputFilename() = Instance.getInstanceOutputFilename(instance, studentIndex)
    fun createOutputDirIfNotExist() {
        val fileHandle = File(outputFileDirectory)
        if (!fileHandle.exists()) {
            fileHandle.mkdirs()
        }
    }
}