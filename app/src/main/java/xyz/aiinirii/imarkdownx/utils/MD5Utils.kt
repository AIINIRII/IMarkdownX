package xyz.aiinirii.imarkdownx.utils

import java.security.MessageDigest

/**
 *
 * @author AIINIRII
 */
object MD5Utils {
    fun getMD5Code(info: String): String {
        return try {
            val md5: MessageDigest = MessageDigest.getInstance("MD5")
            md5.update(info.toByteArray(charset("utf-8")))
            val encryption: ByteArray = md5.digest()
            val stringBuffer = StringBuffer()
            for (i in encryption.indices) {
                if (Integer.toHexString(0xff and encryption[i].toInt()).length == 1) {
                    stringBuffer.append("0").append(Integer.toHexString(0xff and encryption[i].toInt()))
                } else {
                    stringBuffer.append(Integer.toHexString(0xff and encryption[i].toInt()))
                }
            }
            stringBuffer.toString()
        } catch (e: Exception) {
            // e.printStackTrace();
            ""
        }
    }

    fun verifyMd5Code(originCode: String, md5Code: String): Boolean {
        val realNd5Code = getMD5Code(originCode)
        return realNd5Code == md5Code
    }
}