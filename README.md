# Single machine scheduler validator
Program executes, measures time and validate obtained result from an external 
program/script solving single machine tasks scheduling. This project is a part of the tasks' scheduling classes.

Problem description and sample instances steams from [J E Beasley's homepage](http://people.brunel.ac.uk/~mastjjb/jeb/orlib/schinfo.html).

## Requirements
* Java 8 (JRE >= 1.8)
* Kotlin >= 1.2.7

If using jar from a zip archive, ensure that Instances folder exists and contains files with test instances.
Check whether `executors.json` exists and contains list with executors. Set commands appropriate to the files extensions.

## Command line arguments
First and foremost, there are 4 arguments:
* `-n` number of tasks [10,20,50,100,200,500,1000]
* `-k` number of instance, natural number in range **<0,9>**
* `-h` due date's coefficient, real number in range <0,1>
* `-p` relative path to the program to be executed
* `-index` that corresponds to the student's index (used in the name of a file generated by the executed program).

## Execution
Another step is loading 
executors file. This is `.json` file that should be placed in the same dir that our program. It keeps an array
of executors - extensions and an executable that interprets or executes program/script in that extension.

An example entry
```json
  {
    "extensions": [
      "py"
    ],
    "executorPath": "python"
  }
```
Based on this entry file, given in `-p` argument,  with `.py` extension is going to be executed using command `python program.py`.

Program to be executed should parse the arguments `-n`,  `-k`,  `-h`,  `-i`. For example:
* `python program.py -n 10 -k 2 -h 0.8 -i 000000`, k=2 points 3rd instance in the file `sch10.txt`.

### Input/output files
Beside the path to the program to be executed we need another 2 files.
The first one is a text file that stores a result calculated by that program and another one is the file with instances.
The naming convention is as follows:
1. for a files with instances `sch[n].txt`, where `n` is in the range stated above. For `n=10` **we should place `sch10.txt` in the same directory as our scheduler validator.**
2. `sch_[index]_[n]_[k]_[int(h*10)].out`, where:
* `index` is a student's index
* `n` corresponds to the `n` from cmd args 
* `k` corresponds to the `k` from cmd args
* `int(h*10)` corresponds to the `h` multiplied by 10 and rounded down to the nearest integer.

**`.out`  files should be placed in the same directory as our scheduler validator.**

## Result validation
Result validation consist in:
* checking the result of the executed program's return value - should be equal to 0
* loading a result from a `.out` file (result achieved and tasks's order)
* loading an instance from `sch[n].txt` file
* checking feasibility of the result based on the tasks' order

Result should be an integer number - due date is rounded down to the nearest integer, all coefficients are integers.
Tasks should be numbered from `0` to `n-1`.

## Time measurement
Time is being measured in `ms`. It includes launching interpreter or virtual machine (like python interpreter or JVM). Compiled programs, like c++,
may have a little smaller result when it comes to the timing. On the other hand small differences are not taken into consideration, so it's not a big deal.


