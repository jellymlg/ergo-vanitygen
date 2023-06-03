<h1>Ergo vanitygen</h1>

Usage:
<br>
`java -jar ergo-vanitygen-<X.Y>.jar <mode> <string>`
<br>
`mode: "-start" or "-end"`
<br>
`string: the value to look for in addresses`
<br>

Example:
<br>
`java -jar ergo-vanitygen-1.0.jar -end hello`
<br>
This example will try finding an address that ends with "hello" (case-insensitive)

<b>WARNING</b>: Randomly guessing seeds is demanding for the CPU: finding a 5 letter value can take millions of guesses!
