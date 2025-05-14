# Ergo vanitygen

A tool to generate Ergo addresses with specific patterns.

## Build Instructions

1. Make sure you have [SBT (Scala Build Tool)](https://www.scala-sbt.org/download.html) installed
2. Clone this repository
3. Run the following command in the project directory to build the JAR file:

```
sbt assembly
```

This will create a JAR file in the `target/scala-2.XX/` directory named `ergo-vanitygen-VERSION.jar`

## Usage

```
java -jar ergo-vanitygen-VERSION.jar [options]
-s, --start              look for pattern at the start of addresses
-e, --end                look for pattern at the end of addresses
-m, --matchCase          match provided pattern with case sensitivity
-b, --batchSize [value]  the number of addresses to check at once in parallel, 1000 by default
-p, --pattern [value]    pattern to look for in addresses
```

## Example

```
java -jar ergo-vanitygen-1.1.jar -e -p heLLo -m
```

This example will try finding an address that ends exactly with "heLLo" (case-sensitive)

**WARNING**: Randomly guessing seeds is demanding for the CPU: finding a 5 letter value can take millions of guesses!
