package singleMachineTaskScheduler.data

data class Instance(val n: Int, val k: Int, val h: Double){
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

    companion object {
        fun getInstanceOutputFilename(instance: Instance, studentsIndex : String) =
                "sch_${studentsIndex}_${instance.n}_${instance.k}_${(instance.h * 10).toInt()}.out"
    }
}

data class InstanceData(val n : Int, val k : Int, val tasks: ArrayList<Task>){
    val tasksLength = tasks.sumBy { it.p }
}