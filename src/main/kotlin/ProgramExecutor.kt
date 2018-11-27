package singleMachineTaskScheduler

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import kotlin.system.measureTimeMillis

class ProgramRunner(var executionOptions : ExecutionOptions, private val executors : ArrayList<Executor>) {
    fun execute() : ExecutionResult{
        getExecutorBasedFileOnExtension()?.let{
            val executionString = "${it.executorPath} ${executionOptions.getArgumentsList()}"
            println("Trying to execute `$executionString`.")
            //println("CWD is ${System.getProperty("user.dir")}.")
            try{
                val executionTime = measureTimeMillis {executeInner(executionString)}
                moveToOutDirectoryIfNeeded()
                return ExecutionResult(lastLaunchResult, executionTime)
            } catch(e : IOException) {
                if(executionOptions.moveOutputFile){
                    println("Error during executing an external program, error message: ${e.message}")
                } else {
                    println("Couldn't find the executor `${it.executorPath}`.")
                }
            }
        }
        return ExecutionResult(-1, 0)
    }

    private fun moveToOutDirectoryIfNeeded() {
        if(executionOptions.moveOutputFile && lastLaunchResult == 0){
            executionOptions.createOutputDirIfNotExist()
            val outputFilename = Paths.get(executionOptions.outputFileDirectory, executionOptions.getOutputFilename())
            if(!File(executionOptions.getOutputFilename()).exists()){
                throw FileNotFoundException("File ${executionOptions.getOutputFilename()} couldn't be found.")
            }
            Files.move(Paths.get(executionOptions.getOutputFilename()), Paths.get(outputFilename.toUri()), StandardCopyOption.REPLACE_EXISTING)
        }
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