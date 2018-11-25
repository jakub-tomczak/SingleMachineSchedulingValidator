package instanceRunner

data class Instance(val n: Int, val k: Int, val h: Double){
    fun getInstanceOutputFilename(studentsIndex : String) =
            "sch_${studentsIndex}_${n}_${k}_${(h * 10).toInt()}.out"

    override fun equals(other: Any?): Boolean =
        when(other){
            is Instance -> other.n == this.n && other.k == this.k && other.h == this.h
            else -> false
        }

    override fun hashCode(): Int {
        var result = n
        result = 31 * result + k
        result = 31 * result + h.hashCode()
        return result
    }
}

data class OrderingResult(var result: Int, val tasksOrder : ArrayList<Int> = arrayListOf())

data class InstanceData(val n : Int, val k : Int){
    fun addTasks(tasks: List<Task>)
    {
        this.tasks.addAll(tasks)
        tasksLength += tasks.sumBy { it.p }
    }
    var tasks  = arrayListOf<Task>()
        private set
    var tasksLength = 0
        private set
}

data class Task(val p : Int, val a: Int, val b: Int)