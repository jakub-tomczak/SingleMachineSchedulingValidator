package singleMachineTaskScheduler

data class FinalResult(val studentsIndex: String, val meanDifference: Double, val time: Double, val numberOfSuccessExecutions: Int, val numberOfRuns: Int) {
    override fun toString(): String {

        return "$studentsIndex;$meanDifference;$time;$numberOfSuccessExecutions;$numberOfRuns"
    }
}