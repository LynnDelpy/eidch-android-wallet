package ch.admin.foitt.wallet.platform.utils

import java.io.ByteArrayOutputStream
import java.util.Base64
import java.util.zip.DeflaterOutputStream
import java.util.zip.InflaterOutputStream

fun ByteArray.toBase64StringUrlEncodedWithoutPadding(): String =
    Base64.getUrlEncoder().withoutPadding().encodeToString(this)

fun ByteArray.toNonUrlEncodedBase64String(): String =
    Base64.getEncoder().encodeToString(this)

fun String.base64StringToByteArray(): ByteArray =
    Base64.getUrlDecoder().decode(this)

fun String.base64NonUrlStringToByteArray(): ByteArray =
    Base64.getDecoder().decode(this)

fun ByteArray.compress(): ByteArray {
    val output = ByteArrayOutputStream()
    DeflaterOutputStream(output).apply {
        write(this@compress)
        close()
    }
    return output.toByteArray()
}

fun ByteArray.decompress(): ByteArray {
    val output = ByteArrayOutputStream()
    InflaterOutputStream(output).apply {
        write(this@decompress)
        close()
    }
    return output.toByteArray()
}
