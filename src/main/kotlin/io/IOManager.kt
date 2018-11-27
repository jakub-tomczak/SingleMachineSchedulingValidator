package singleMachineTaskScheduler.io

import singleMachineTaskScheduler.data.BestResult
import singleMachineTaskScheduler.data.Instance
import singleMachineTaskScheduler.data.InstanceData
import singleMachineTaskScheduler.data.Task
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.Paths

class IOManager{
    fun loadInstances(instancesDir: String, getAsResource: Boolean = true) : List<InstanceData> {
        val instances = arrayListOf<InstanceData>()

        try{
            val files = nValues
                    .asSequence()
                    .map { x ->  if(getAsResource)
                        getResource(instancesDir, "sch$x.txt")
                    else
                        File(Paths.get(instancesDir, "sch$x.txt").toUri()) }
                    .filter { x -> x.exists() }
                    .toList()

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
                    instances.add(InstanceData(it.second,i, ArrayList(lines.subList(i*it.second, ((i+1)*it.second)))))
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
    fun loadBestResults(bestResultsDirectory: String, bestResultsFilename: String, getAsResource: Boolean = true): List<BestResult> {
        val file = if(getAsResource){
            getResource(bestResultsDirectory, bestResultsFilename)
        } else {
            File(Paths.get(bestResultsDirectory, bestResultsFilename).toUri())
        }
        if (!file.exists()){
            println("Could not find ${file.absolutePath}.")
            System.exit(1)
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

    private fun getResource(dir: String, filename: String): File {
        val url = this::class.java.classLoader.getResource(Paths.get(dir, filename).toString()) ?:
            throw FileNotFoundException("Resource $filename has not been found")
        val file = File(url.toURI())
        return if(file.exists()) {
            file
        } else {
            throw FileNotFoundException("File $url has not been found.")
        }
    }

    private val numberOfInstancesInFile = 10
    private val nValues = arrayListOf(10, 20, 50, 100, 200, 500, 1000)
    private val hValues = arrayListOf(.2, .4, .6, .8)
}