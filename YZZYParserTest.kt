package main.java.test

import main.java.parser.SZPUParser
import java.io.File
import java.nio.file.Paths

fun main(){
    val path = Paths.get("").toAbsolutePath().toString()
    println(path)
    val file = File("学生课程表.html")
    val parser = SZPUParser(file.readText()) 
    parser.saveCourse()
}