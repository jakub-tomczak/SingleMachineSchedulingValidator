package instanceRunner

class ExecutionResult(val executionCode: Int, val executionTime : Long){
    var isSolutionFeasible = false
    var givenResult = 0
    var calculatedResult = 0
    var message = ""
}