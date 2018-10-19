package instanceRunner

data class ExecutionOptions(var programName : String, val instance: Instance){
    var studentIndex = ""
    var path = ""
    val extension = programName.split('.').last()
    var addDashInArgsList = false
    fun getArgumentsList() =
            if(addDashInArgsList)
                "$programName ${instance.n} ${instance.k} ${instance.h}"
            else
                "$programName -n ${instance.n} -k ${instance.k} -h ${instance.h}"
}