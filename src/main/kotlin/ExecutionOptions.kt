package instanceRunner

data class ExecutionOptions(var programPath : String, val instance: Instance, val studentIndex : String, val addDashInArgsList: Boolean = false){
    val extension = programPath.split('.').last()
    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programPath -n ${instance.n} -k ${instance.k} -h ${instance.h} -i $studentIndex"
            else
                "$programPath ${instance.n} ${instance.k} ${instance.h} $studentIndex"
}