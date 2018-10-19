package instanceRunner

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import java.io.File
import kotlin.system.measureTimeMillis

class ProgramRunner(var executionOptions : ProgramOptions) {
    fun execute() = measureTimeMillis {executeInner()}

    fun loadExecutors(executorsFilePath : String) : ArrayList<Executor>{
        val parser = Klaxon()
        val executors = arrayListOf<Executor>()
        JsonReader(File(executorsFilePath).reader()).use {
            reader ->
            reader.beginArray {
                while (reader.hasNext()){
                    executors += parser.parse<Executor>(reader)!!
                }
            }
        }
        return executors
    }

    private fun executeInner(){
        val process = Runtime.getRuntime().exec("python -c \"import time; time.sleep(10)\"")
        //wait for an exit value
        process.waitFor()
        print("executing ${executionOptions.programName}, cwd ${System.getProperty("user.dir")}")
    }
}

data class Executor(val language : String, val extension : String, val executorPath : String)