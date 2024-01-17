package fanta.vanitygen

import fanta.vanitygen.Util.{argParser, randomAddress}

import scala.collection.parallel.immutable.ParRange
import scala.collection.parallel.mutable.ParArray

object Main {

  def main(args: Array[String]): Unit = {

    val parsedArgs: Option[Args] = argParser.parse(args, Args())
    if(parsedArgs.isEmpty) {
      System.err.println("Failed to parse arguments")
      System.exit(1)
    }

    val param: Args = parsedArgs.get

    println("Looking for addresses that " + param.mode + (if(param.exact) " exactly" else "") + " with \"" + param.pattern + "\"")

    var hits: ParArray[(String,String)] = null
    var i = 0

    do {
      hits = ParRange(0, param.batchSize, 1, inclusive = false)
        .map(_ => randomAddress()).toParArray.filter(x => param.check(x._2, param))
      i += 1
      println(s"Checked ${i * param.batchSize} addresses...")
    }while(hits.isEmpty)

    println("Found " + hits.length + " addresses " + param.mode + "ing " + (if(param.exact) "exactly " else "") + "with \"" + param.pattern + "\"")

    hits.toArray.zipWithIndex.foreach { case ((seed, addr), i) =>
      println("---------------------------")
      println(s"Match ${i + 1}")
      println(s"Seed phrase: $seed")
      println(s"Address: $addr")
      println("---------------------------")
    }

  }

}