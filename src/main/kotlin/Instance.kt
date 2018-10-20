package instanceRunner

data class Instance(val n: Int, val k: Int, val h: Double){
    fun getInstanceOutputFilename(studentsIndex : String) =
            "sch_${studentsIndex}_${n}_${k}_${(h * 10).toInt()}.out"
    fun getInstanceFilename() = "sch$n.txt"
}

data class OrderingResult(var result: Int, val tasksOrder : ArrayList<Int> = arrayListOf())

data class InstanceData(val n : Int, val k : Int){
    fun addTask(task: Task)
    {
        tasks.add(task)
        tasksLength += task.p
    }
    var tasks  = arrayListOf<Task>()
        private set
    var tasksLength = 0
        private set
}

data class Task(val p : Int, val a: Int, val b: Int)