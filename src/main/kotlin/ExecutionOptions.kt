package instanceRunner

data class ExecutionOptions(var programName : String, val instance: Instance, val studentIndex : String){
    var path = ""
    val extension = programName.split('.').last()
    var addDashInArgsList = false
    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programName ${instance.n} ${instance.k} ${instance.h} $studentIndex"
            else
                "$programName -n ${instance.n} -k ${instance.k} -h ${instance.h} -i $studentIndex"
}