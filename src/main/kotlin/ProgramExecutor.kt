package instanceRunner

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

class ProgramRunner(var executionOptions : ExecutionOptions, val executors : ArrayList<Executor>) {
    fun execute() : ExecutionResult{
        getExecutorBasedFileOnExtension()?.let{
            val executionString = "${it.executorPath} ${executionOptions.getArgumentsList()}"
            println("Trying to execute `$executionString`.")
            println("CWD is ${System.getProperty("user.dir")}.")
            val executionTime = measureTimeMillis {executeInner(executionString)}

            return ExecutionResult(executionOptions.instance, lastLaunchResult, executionTime)
        }
        println("No appropriate executor found for the extension ${executionOptions.extension}.")
        exitProcess(0)
    }

    private fun executeInner(executionString: String){
        //wait for an exit value, otherwise exec returns immediately
        lastLaunchResult =  Runtime.getRuntime()
                .exec(executionString)
                .waitFor()
    }

    private fun getExecutorBasedFileOnExtension() = executors
                .asSequence()
                .filter { x -> executionOptions.extension in x.extension }
                .firstOrNull()


    //stores a value returned during last execution of an external program
    private var lastLaunchResult = 0

    companion object {
        fun loadExecutors(executorsFilePath : String) : ArrayList<Executor>{
            val parser = Klaxon()
            val executors = arrayListOf<Executor>()
            try{
                JsonReader(File(executorsFilePath).reader()).use {
                    reader ->
                    reader.beginArray {
                        while (reader.hasNext()){
                            executors += parser.parse<Executor>(reader)!!
                        }
                    }
                }
            } catch(e: FileNotFoundException)
            {
                println("File $executorsFilePath not found.")
            } catch (e: KlaxonException)
            {
                println("Error occurred when reading JSON file. Error message `${e.message}`")
            } catch (e : NoSuchElementException)
            {
                println("Error while parsing JSON. Check whether JSON file has a valid syntax. Error message `${e.message}`")
            }
            return executors
        }
    }
}

data class Executor(val extension : ArrayList<String>, val executorPath : String)