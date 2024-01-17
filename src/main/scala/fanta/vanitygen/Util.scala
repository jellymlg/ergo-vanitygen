package fanta.vanitygen

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.ergoplatform.wallet.Constants
import org.ergoplatform.wallet.Constants.eip3DerivationPath
import org.ergoplatform.wallet.mnemonic.Mnemonic.{BitsGroupSize, Pbkdf2Algorithm, Pbkdf2Iterations, Pbkdf2KeyLength}
import org.ergoplatform.wallet.mnemonic.WordList
import org.ergoplatform.wallet.secrets.ExtendedSecretKey.deriveMasterKey
import org.ergoplatform.{ErgoAddressEncoder, P2PKAddress}
import scodec.bits.BitVector
import scopt.OptionParser

import java.security.Security
import java.text.Normalizer.Form.NFKD
import java.text.Normalizer.normalize
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object Util {

  private val skf: SecretKeyFactory = {
    Security.addProvider(new BouncyCastleProvider())
    SecretKeyFactory.getInstance(Pbkdf2Algorithm, BouncyCastleProvider.PROVIDER_NAME)
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

  def randomAddress(): (String,String) = {
    val mnemonic = newMnemonic()
    val addr = ErgoAddressEncoder.Mainnet.toString(
      P2PKAddress(
        deriveMasterKey(toSeed(mnemonic), usePre1627KeyDerivation = false).derive(eip3DerivationPath).publicImage
      )(ErgoAddressEncoder.Mainnet)
    )
    mnemonic -> addr
  }

  val argParser: OptionParser[Args] = new OptionParser[Args]("java -jar ergo-vanitygen-VERSION.jar") {
    opt[Unit]("start")
      .abbr("s")
      .action((_, c) => c.copy(check = Args.START).copy(mode = "start"))
      .text("look for pattern at the start of addresses")
      .optional()
    opt[Unit]("end")
      .abbr("e")
      .action((_, c) => c.copy(check = Args.END).copy(mode = "end"))
      .text("look for pattern at the end of addresses")
      .optional()
    opt[Unit]("matchCase")
      .abbr("m")
      .action((_, c) => c.copy(exact = true))
      .text("match provided pattern with case sensitivity")
      .optional()
    opt[Int]("batchSize")
      .abbr("b")
      .action((x, c) => c.copy(batchSize = x))
      .text("the number of addresses to check at once in paralell, 1000 by default")
      .optional()
    opt[String]("pattern")
      .abbr("p")
      .action((x, c) => c.copy(pattern = x))
      .text("pattern to look for in addresses")
      .required()
  }

}
