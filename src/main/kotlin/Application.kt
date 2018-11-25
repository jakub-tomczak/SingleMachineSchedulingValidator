package instanceRunner

import org.kohsuke.args4j.CmdLineException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.Exception
import java.lang.System.exit
import java.nio.file.Paths
import kotlin.system.exitProcess

class Application {
    fun parseCommandLineArguments(args: Array<String>) {
        try {
            simpleParser(args)
            if(!batchMode)
                println("Runner parameters: n=$n, k=$k, h=$h, program=`$programToExecute`, index=`${getStudentIndex()}`.")
        } catch(exception : CmdLineException) {
            println("Could not parse arguments. Error message: \n${exception.message}")
        } catch (exception: NumberFormatException) {
            println("Could not parse arguments. Error ${exception.javaClass}, message: \n${exception.message}")
        }

        validateArguments()
    }

    fun getExecutionOptionsFromCmdArgs(addDashInArgsList: Boolean = false): ExecutionOptions =
        ExecutionOptions(programToExecute, Instance(n, k, h), getStudentIndex()).also {
            it.addDashInArgsList = addDashInArgsList
        }


    private fun getStudentIndex() = if(studentsIndex.isNullOrBlank())
            programToExecute.split(".").first()
        else
            studentsIndex

    fun getInstancesDir() =
            if(instancesDir.isEmpty())
                ""
            else
                instancesDir + File.separatorChar

    fun getInstance(instance: Instance) =
            instancesData.asSequence().filter { x -> x.n == instance.n && x.k == instance.k-1 }.firstOrNull() ?: throw Exception("No instance found")


    private fun validateArguments() {
        if(!batchMode){
            assert(n in arrayOf(10, 20, 50, 100, 200, 500, 1000)) {"n=$n is not in the range [10, 20, 50, 100, 200, 500, 1000]."}
            assert(k in 1..10) {"k=$k is not valid. It must be a natural number in range <0,9>"}
            assert(h in 0..1) {"h=$h is not valid. It must be a real number in range <0,1>"}
            //assert(programToExecute.isNotEmpty()) {"Program to execute name cannot be empty."}
        }
    }

    private fun simpleParser(args: Array<String>){
        if(args.size !in 4..6 step 1){
            println("Expected 4 or 5 arguments (program name is optional). Got ${args.size}.")
            exitProcess(1)
        }
        batchMode =  args[0] == "batch"
        if(!batchMode){
            n = args[1].toInt()
            k = args[2].toInt()
            h = args[3].toDouble()

            programToExecute = if(args.size == 6)
                "${args[4]}.${args[5]}"
            else
                "${args[4]}.$programToExecuteExtension"
            studentsIndex =
                    programToExecute.split(".").first()

            checkOutFile = args.size > 4 && args.contains("out")
        }
    }

    fun getStudentsPrograms(): List<String> = File(studentsProgramsPath).list()
            .filter { x -> x.contains(Regex("\\d{6}.bat$")) }
            .toList()

    fun getInstancesForBatch(): List<Instance> {
        val filename = Paths.get(batchSettingsFilename)

        try{
            if (!File(filename.toUri()).exists()){
                println("File with instances ${filename.toUri()} doesn't exist.")
            } else {
                return File(filename.toUri())
                        .inputStream()
                        .bufferedReader()
                        .readLines()
                        .map {
                            val values = it.split(" ").filter { x-> x.isNotEmpty() }
                            if(values.size != 3){
                                throw java.lang.IndexOutOfBoundsException("Malformed instance $it")
                            }
                            Instance(values[0].toInt(), values[1].toInt(), values[2].toDouble())
                        }
            }
        } catch(e : FileNotFoundException) {
            //file not found
            println("File $filename has not been found.")
        } catch(e: IOException) {
            //reading out of file
            println("Error while loading an instance from a file $filename.\nError: ${e.message}")
        } catch (e : IndexOutOfBoundsException) {
            println("Malformed instance file. Error: $e.")
        }
        return arrayListOf()
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

    private val instancesData by lazy { loadInstances(this) }
    val bestResults by lazy { loadBestResults(this) }
    //needed if instance or output files are in the different directory than an executable
    private var instancesDir = ""
    private var outputDir = ""
    private var batchSettingsFilename = "instances.txt"
    private var studentsProgramsPath = "."
    private val programToExecuteExtension = "bat"

    private val bestResultsFilename = "best_results.txt"

    companion object {
        private const val numberOfInstancesInFile = 10
        private val nValues = arrayListOf(10, 20, 50, 100, 200, 500, 1000)
        private val hValues = arrayListOf(.2, .4, .6, .8)
        fun loadInstances(application: Application) : ArrayList<InstanceData> {
            val instances = arrayListOf<InstanceData>()

            val files = nValues
                    .asSequence()
                    .map { x ->  File(Paths.get(application.getInstancesDir(), "sch$x.txt").toUri()) }
                    .filter { x -> x.exists() }
                    .toList()

            try{
                files.zip(nValues).map {
                    val lines = it.first
                            .inputStream()
                            .bufferedReader()
                            .readLines()
                            .asSequence()
                            //split strings by " " and only these where are 3 numbers
                            .map { x -> x.split(" ").filter { item -> item.isNotEmpty() } }
                            .filter { x -> x.size == 3 }
                            .map { data -> Task(data[0].toInt(), data[1].toInt(), data[2].toInt()) }
                            .toList()
                    //add to instances data
                    for(i in 0 until numberOfInstancesInFile)
                        instances.add(
                                InstanceData(it.second, i)
                                        .also { instanceData ->  instanceData.addTasks(lines.subList(i*it.second, ((i+1)*it.second))) })
                }
            } catch(e : FileNotFoundException) {
                //file not found
                println("File has not been found. Error $e")
            } catch(e: IOException) {
                //reading out of file
                println("Error while loading an instance from a file.\nError: ${e.message}")
            } catch (e : IndexOutOfBoundsException) {
                println("Malformed instance file.")
            }
            return instances
        }
        fun loadBestResults(application: Application): ArrayList<BestResult> {
            val file = File(Paths.get(application.bestResultsFilename).toUri())
            if (!file.exists()){
                println("Could not find ${file.absolutePath}.")
                exit(1)
            }

            try{
               val data = file
                       .inputStream()
                       .bufferedReader()
                       .readLines()
                       .asSequence()
                       //split strings by " " and only these where are 4 non-empty elements
                       .map { x -> x.split(" ").filter { item -> item.isNotEmpty() } }
                       .filter { x -> x.size == 4 }
                       .toList()

                val bestValues = arrayListOf<BestResult>()
                for(n in 0 until nValues.size){
                    for(k in 0 until 10){
                        for(h in 0 until hValues.size){
                            val isOptimal = data[n*10+k][h].last() == '*'
                            val result = data[n*10+k][h].dropLastWhile { x -> x == '*' }.toInt()
                            bestValues.add(BestResult(Instance(nValues[n], k+1, hValues[h]), bestResult = result, isOptimal = isOptimal))
                        }
                    }
                }
//               nValues.mapIndexed { nIndex, n -> {
//                   (0 until 10).map { k -> {
//                       hValues.asSequence().mapIndexed { hIndex, h -> {
//                           val isOptimal = data[n * 10 + k][hIndex].last() == '*'
//                           val result = data[nIndex * 10 + k][hIndex].dropLastWhile { x -> x == '*' }
//                           BestResult(Instance(n, k+1, h), result.toInt(), isOptimal)
//                       }
//                       }
//                   }
//                   }
//               }
//               }
                return bestValues
            } catch(e : FileNotFoundException) {
                //file not found
                println("File has not been found. Error $e")
            } catch(e: IOException) {
                //reading out of file
                println("Error while loading an best results from a file.\nError: ${e.message}")
            } catch (e : IndexOutOfBoundsException) {
                println("Malformed best results file.")
            }
            return arrayListOf()
        }
    }

}
data class BestResult(val instance: Instance, val bestResult: Int, val isOptimal: Boolean)