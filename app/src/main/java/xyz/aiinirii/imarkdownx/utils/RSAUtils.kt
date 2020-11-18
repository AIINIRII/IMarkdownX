package xyz.aiinirii.imarkdownx.utils

import android.util.Base64
import java.security.*
import javax.crypto.Cipher

/**
 * this utils is used for RSA encrypt
 * @author AIINIRII
 */
object RSAUtils {
    // transform algorithm string, default as "RSA/NONE/PKCS1Padding"
    var sTransform = "RSA/NONE/PKCS1Padding"

    // base64 mode, default is Base64.DEFAULT
    var sBase64Mode: Int = Base64.DEFAULT

    /**
     * generate RSA key pair
     * @param keyLength Int
     * @return KeyPair?
     */
    fun generateRSAKeyPair(keyLength: Int): KeyPair? {
        var keyPair: KeyPair? = null
        try {
            val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(keyLength)
            keyPair = keyPairGenerator.generateKeyPair()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return keyPair
    }

    /**
     * method to used when encrypt or decrypt
     * @param srcData ByteArray? data
     * @param key Key? the key
     * @param mode Int encrypt or decrypt
     * @return ByteArray?
     */
    fun processData(srcData: ByteArray?, key: Key?, mode: Int): ByteArray? {
        var resultBytes: ByteArray? = null
        try {
            val cipher: Cipher = Cipher.getInstance("RSA/NONE/PKCS1Padding")
            cipher.init(mode, key)
            resultBytes = cipher.doFinal(srcData)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return resultBytes
    }

    /**
     * encrypt data by public key using Base64
     * @param srcData ByteArray?
     * @param publicKey PublicKey?
     * @return String?
     */
    fun encryptDataByPublicKey(srcData: ByteArray?, publicKey: PublicKey?): String? {
        val resultBytes = processData(srcData, publicKey, Cipher.ENCRYPT_MODE)
        return Base64.encodeToString(resultBytes, sBase64Mode)
    }

    /**
     * decrypt by private string and transform using Base64
     * @param encryptedData String?
     * @param privateKey PrivateKey?
     * @return ByteArray?
     */
    fun decryptDataByPrivate(encryptedData: String?, privateKey: PrivateKey?): ByteArray? {
        val bytes: ByteArray = Base64.decode(encryptedData, sBase64Mode)
        return processData(bytes, privateKey, Cipher.DECRYPT_MODE)
    }

    /**
     * decrypt using private string
     * @param encryptedData String?
     * @param privateKey PrivateKey?
     * @return String?
     */
    fun decryptToStrByPrivate(encryptedData: String?, privateKey: PrivateKey?): String? {
        return String(decryptDataByPrivate(encryptedData, privateKey)!!)
    }
}