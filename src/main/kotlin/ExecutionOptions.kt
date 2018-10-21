package instanceRunner

data class ExecutionOptions(var programPath : String, val instance: Instance, val studentIndex : String){
    val extension = programPath.split('.').last()
    var addDashInArgsList = false
    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programPath ${instance.n} ${instance.k} ${instance.h} $studentIndex"
            else
                "$programPath -n ${instance.n} -k ${instance.k} -h ${instance.h} -i $studentIndex"
}