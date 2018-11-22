package instanceRunner

import com.beust.klaxon.JsonReader
import com.beust.klaxon.Klaxon
import com.beust.klaxon.KlaxonException
import org.kohsuke.args4j.CmdLineException
import java.io.File
import java.io.FileNotFoundException
import kotlin.system.exitProcess

class Application {
    fun parseCommandLineArguments(args: Array<String>) {
        try {
            simpleParser(args)
            println("Runner parameters: n=$n, k=$k, h=$h, program=`$programToExecute`, index=`${getStudentIndex()}`.")
        } catch(exception : CmdLineException) {
            println("Could not parse arguments. Error message: \n${exception.message}")
        } catch (exception: NumberFormatException) {
            println("Could not parse arguments. Error ${exception.javaClass}, message: \n${exception.message}")
        }

        validateArguments()
    }

    fun getExecutionOptions(addDashInArgsList: Boolean = false): ExecutionOptions {
        executionOption.addDashInArgsList = addDashInArgsList
        return executionOption
    }

    fun getStudentIndex() = if(studentsIndex.isNullOrBlank())
            programToExecute.split(".").first()
        else
            studentsIndex

    fun getInstancesDir() =
            if(instancesDir.isEmpty())
                ""
            else
                instancesDir + File.separatorChar
    fun getOutputDir() =
            if(outputDir.isEmpty())
                ""
            else
                outputDir + File.separatorChar

    private fun validateArguments() {
        assert(n in arrayOf(10, 20, 50, 100, 200, 500, 1000)) {"n=$n is not in the range [10, 20, 50, 100, 200, 500, 1000]."}
        assert(k in 1..10) {"k=$k is not valid. It must be a natural number in range <0,9>"}
        assert(h in 0..1) {"h=$h is not valid. It must be a real number in range <0,1>"}
        //assert(programToExecute.isNotEmpty()) {"Program to execute name cannot be empty."}
    }

    private fun simpleParser(args: Array<String>){
        if(args.size !in 4..6 step 1){
            println("Expected 4 or 5 arguments (programs name is optional). Got ${args.size}.")
            exitProcess(1)
        }

        n = args[0].toInt()
        k = args[1].toInt()
        h = args[2].toDouble()
        programToExecute = if(args.size == 5)
            "${args[3]}.${args[4]}"
        else
            "${args[3]}.$programToExecuteExtension"
        studentsIndex =
            programToExecute.split(".").first()

        checkOutFile = args.size > 4 && args.contains("out")
        batchMode = args.size > 4 && args.contains("batch")
    }

    var programToExecute = ""
        private set

    var n = 0
        private set

    var k = 0
        private set

    var h = 0.0
        private set

    var checkOutFile = false
        private set

    var batchMode = false
        private set

    private var studentsIndex = ""

    private val executionOption by lazy { ExecutionOptions(programToExecute, Instance(n, k, h), getStudentIndex()) }
    //needed if instance or output files are in the different directory than an executable
    private var instancesDir = ""
    private var outputDir = ""
    private var batchSettingsFilename = "batch.json"

    companion object {
        fun loadBatchSettings(executorsFilePath : String) : ArrayList<BatchSettings>{
            val parser = Klaxon()
            val executors = arrayListOf<BatchSettings>()
            try{
                JsonReader(File(executorsFilePath).reader()).use {
                    reader ->
                    reader.beginArray {
                        while (reader.hasNext()){
                            executors += parser.parse<BatchSettings>(reader)!!
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
        const val programToExecuteExtension: String  = "bat"
    }
}

data class BatchSettings(val n: ArrayList<Int>, val k: ArrayList<Int>, val h: ArrayList<Double>)