package instanceRunner

import java.io.File

data class ExecutionOptions(var programPath : String, var instance: Instance, val studentIndex : String){
    val extension = programPath.split('.').last()

    var copyOutputFile = true
    var outputFileDirectory = ""
    var addDashInArgsList = false

    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programPath -n ${instance.n} -k ${instance.k} -h ${instance.h}"
            else
                "$programPath ${instance.n} ${instance.k} ${instance.h}"

    fun getOutputFilename() = instance.getInstanceOutputFilename(studentIndex)
    fun createOutputDirIfNotExist() {
        val fileHandle = File(outputFileDirectory)
        if (!fileHandle.exists()) {
            fileHandle.mkdirs()
        }
    }
}