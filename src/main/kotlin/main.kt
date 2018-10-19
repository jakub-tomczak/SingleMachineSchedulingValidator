package instanceRunner

fun main(args: Array<String>){
    Application().apply {
        this.parseCommandLineArguments(args)
        this.printArgs()
    }
}