package instanceRunner

data class ExecutionOptions(var programPath : String, val instance: Instance, val studentIndex : String, val addDashInArgsList: Boolean = false){
    val extension = programPath.split('.').last()

    var copyOutputFile = true

    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programPath -n ${instance.n} -k ${instance.k} -h ${instance.h}"
            else
                "$programPath ${instance.n} ${instance.k} ${instance.h}"
    fun getOutputFilename() =
            "${studentIndex}_${instance.n}_${instance.k}_${instance.h}.out"
}