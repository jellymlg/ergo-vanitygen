package fanta.spectrum

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ergoplatform.wallet.Constants
import org.ergoplatform.wallet.Constants.eip3DerivationPath
import org.ergoplatform.{ErgoAddressEncoder, P2PKAddress}
import org.ergoplatform.wallet.mnemonic.WordList
import org.ergoplatform.wallet.mnemonic.Mnemonic._
import org.ergoplatform.wallet.secrets.ExtendedSecretKey.deriveMasterKey
import scodec.bits.BitVector

import java.security.Security
import java.text.Normalizer.Form.NFKD
import java.text.Normalizer.normalize
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import scala.collection.parallel.immutable.ParRange
import scala.collection.parallel.mutable.ParArray

object Main {

  private val skf: SecretKeyFactory = {
    Security.addProvider(new BouncyCastleProvider())
    SecretKeyFactory.getInstance(Pbkdf2Algorithm, BouncyCastleProvider.PROVIDER_NAME)
  }

  case class Check(mode: String, target: String) {

    private val fn: String => Boolean = {
      mode.drop(1) match {
        case "start" =>
          (address: String) => address.toLowerCase.startsWith(target, 1)
        case "end" =>
          (address: String) => address.toLowerCase.endsWith(target)
        case _ =>
          (_: String) => true
      }
    }

    def apply(mnemonic_address: (String,String)): Boolean = fn(mnemonic_address._2)
  }

  def main(args: Array[String]): Unit = {

    if(args.length != 2 || (args(0) != "-start" && args(0) != "-end")) {
      System.err.println("Usage: java -jar ergo-fancy-address-<X.Y.Z>.jar <mode> <string>")
      System.err.println("\tmode: \"-start\" or \"-end\"")
      System.err.println("\tstring: the value to look for in addresses")
      System.exit(1)
    }

    val check: Check = Check(args(0), args(1).toLowerCase)

    println("Looking for addresses that " + check.mode.drop(1) + " with \"" + check.target + "\"")

    val size = 1000
    var hits: ParArray[(String,String)] = null
    var i = 0

    do {
      hits = ParRange(0, size, 1, inclusive = false)
        .map(_ => randomAddress()).toParArray.filter(check(_))
      i += 1
      println(s"Checked ${i * size} addresses...")
    }while(hits.isEmpty)

    println("Found " + hits.length + " addresses ending with \"" + check.target + "\"")

    hits.toArray.foreach { hit =>
      println("---------------------------")
      println(s"Seed phrase is: ${hit._1}")
      println(s"Address is: ${hit._2}")
      println("---------------------------")
    }

  }

  private def randomAddress(): (String,String) = {
    val mnemonic = newMnemonic()
    val addr = ErgoAddressEncoder.Mainnet.toString(
      P2PKAddress(
        deriveMasterKey(toSeed(mnemonic), usePre1627KeyDerivation = false).derive(eip3DerivationPath).publicImage
      )(ErgoAddressEncoder.Mainnet)
    )
    mnemonic -> addr
  }

  private val wordList = WordList.load("english").get

  private def newMnemonic(): String = {
    val entropy = scorex.utils.Random.randomBytes(128 / 8)
    val checksum = BitVector(scorex.crypto.hash.Sha256.hash(entropy))
    val entropyWithChecksum = BitVector(entropy) ++ checksum.take(entropy.length / 4)
    entropyWithChecksum.grouped(BitsGroupSize).map { wordIndex =>
      wordList.words(wordIndex.toInt(signed = false))
    }.mkString(wordList.delimiter)
  }

  private def toSeed(mnemonic: String): Array[Byte] =
    skf.generateSecret(
      new PBEKeySpec(
        normalize(mnemonic, NFKD).toCharArray,
        normalize("mnemonic", NFKD).getBytes(Constants.Encoding),
        Pbkdf2Iterations,
        Pbkdf2KeyLength
      )
    ).getEncoded

}