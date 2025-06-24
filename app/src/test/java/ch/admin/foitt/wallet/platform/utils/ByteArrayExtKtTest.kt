package ch.admin.foitt.wallet.platform.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ByteArrayExtKtTest {
    @Test
    fun `compressing a string and uncompressing it should result in the same string`() {
        val input = "some random string to compress with ä, è, \u1234 or even ${Character.toString(0x1F44D)}"
        val compressed = input.toByteArray().compress()
        val uncompressed = compressed.decompress().decodeToString()
        assertEquals(input, uncompressed)
    }
}
