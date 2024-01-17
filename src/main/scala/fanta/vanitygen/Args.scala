package fanta.vanitygen

final case class Args(mode: String,
                      check: (String,Args) => Boolean,
                      exact: Boolean,
                      batchSize: Int,
                      pattern: String)

object Args {

  def START(address: String, param: Args): Boolean =
    if(param.exact)
      address.startsWith(param.pattern, 1)
    else
      address.toLowerCase.startsWith(param.pattern.toLowerCase, 1)

  def END(address: String, param: Args): Boolean =
    if(param.exact)
      address.endsWith(param.pattern)
    else
      address.toLowerCase.endsWith(param.pattern.toLowerCase)

  def apply(): Args = new Args("end", END, false, 1000, "")

}