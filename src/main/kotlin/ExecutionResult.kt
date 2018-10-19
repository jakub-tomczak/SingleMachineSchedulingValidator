package instanceRunner

class ExecutionResult(val instance : Instance, val executionResult: Int, val executionTime : Long){
    var successfulExecution = false
        get() = executionResult == 0
        private set
}