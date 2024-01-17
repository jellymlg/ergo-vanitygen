<h1>Ergo vanitygen</h1>

<pre>
Usage: java -jar ergo-vanitygen-VERSION.jar [options]
-s, --start              look for pattern at the start of addresses
-e, --end                look for pattern at the end of addresses
-m, --matchCase          match provided pattern with case sensitivity
-b, --batchSize [value]  the number of addresses to check at once in paralell, 1000 by default
-p, --pattern [value]    pattern to look for in addresses
</pre>

Example:
<br>
`java -jar ergo-vanitygen-1.0.jar -e -p heLLo -m`
<br>
This example will try finding an address that ends exactly with "heLLo" (case-sensitive)

<b>WARNING</b>: Randomly guessing seeds is demanding for the CPU: finding a 5 letter value can take millions of guesses!
