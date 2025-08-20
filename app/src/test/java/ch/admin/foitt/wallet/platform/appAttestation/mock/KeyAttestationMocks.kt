package ch.admin.foitt.wallet.platform.appAttestation.mock

import ch.admin.foitt.wallet.platform.appAttestation.domain.model.KeyAttestationJwt

internal object KeyAttestationMocks {

/*
-----BEGIN PUBLIC KEY-----
MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEEVs/o5+uQbTjL3chynL4wXgUg2R9
q9UU8I5mEovUf86QZ7kOBIjJwqnzD1omageEHWwHdBO6B+dFabmdT9POxg==
-----END PUBLIC KEY-----

-----BEGIN PRIVATE KEY-----
MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgevZzL1gdAFr88hb2
OF/2NxApJCzGCEDdfSp6VQO30hyhRANCAAQRWz+jn65BtOMvdyHKcvjBeBSDZH2r
1RTwjmYSi9R/zpBnuQ4EiMnCqfMPWiZqB4QdbAd0E7oH50VpuZ1P087G
-----END PRIVATE KEY-----
*/
    val jwtAttestation01 get() = KeyAttestationJwt(
        "eyJ0eXAiOiJrZXktYXR0ZXN0YXRpb24rand0IiwiYWxnIjoiRVMyNTYiLCJjcnYiOiJFZDQ0OCIsImtpZCI6ImRpZDp0ZHc6ZXhhbXBsZS5jb20ja2V5LTEifQ.eyJpc3MiOiJkaWQ6dGR3OmV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjQ3MDIyLCJleHAiOjIwNjgwMTAxMTQsImtleV9zdG9yYWdlIjpbImlzb18xODA0NV9tb2RlcmF0ZSJdLCJhdHRlc3RlZF9rZXlzIjpbeyJrdHkiOiJFQyIsImNydiI6IlAtMjU2IiwieCI6IlRDQUVSMTladnUzT0hGNGo0VzR2ZlNWb0hJUDFJTGlsRGxzN3ZDZUdlbWMiLCJ5IjoiWnhqaVdXYlpNUUdIVldLVlE0aGJTSWlyc1ZmdWVjQ0U2dDRqVDlGMkhaUSJ9XX0.xF5bwmXw3vgnGI_mid2GrDQzAVB-LxS7WC3q8mDf-dWHvarkGGdeX-QcVUumWNU4z7IQdHiNHK8ZmrDMrUUuAA"
    )
    val jwtAttestation02NoStorage get() = KeyAttestationJwt(
        "eyJ0eXAiOiJrZXktYXR0ZXN0YXRpb24rand0IiwiYWxnIjoiRVMyNTYiLCJraWQiOiJkaWQ6dGR3OmV4YW1wbGUuY29tI2tleS0xIn0.eyJpc3MiOiJkaWQ6dGR3OmV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjQ3MDIyLCJleHAiOjE1NDE0OTM3MjQsImF0dGVzdGVkX2tleXMiOlt7Imt0eSI6IkVDIiwiY3J2IjoiUC0yNTYiLCJ4IjoiVENBRVIxOVp2dTNPSEY0ajRXNHZmU1ZvSElQMUlMaWxEbHM3dkNlR2VtYyIsInkiOiJaeGppV1diWk1RR0hWV0tWUTRoYlNJaXJzVmZ1ZWNDRTZ0NGpUOUYySFpRIn1dfQ.pY6hJgKxqmuA1gCl3aetrHRFxdmieGw5jbrVg2MCVywHq2WdmCndl_Fc82sLoS_cnXpF-jelb-VhgIiReP2TzQ"
    )
    val jwtAttestation03UnknownStorage get() = KeyAttestationJwt(
        "eyJ0eXAiOiJrZXktYXR0ZXN0YXRpb24rand0IiwiYWxnIjoiRVMyNTYiLCJraWQiOiJkaWQ6dGR3OmV4YW1wbGUuY29tI2tleS0xIn0.eyJpc3MiOiJkaWQ6dGR3OmV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjQ3MDIyLCJleHAiOjE1NDE0OTM3MjQsImtleV9zdG9yYWdlIjpbImlzb18xODA0NV9zdXBlcl9kdXBlciJdLCJhdHRlc3RlZF9rZXlzIjpbeyJrdHkiOiJFQyIsImNydiI6IlAtMjU2IiwieCI6IlRDQUVSMTladnUzT0hGNGo0VzR2ZlNWb0hJUDFJTGlsRGxzN3ZDZUdlbWMiLCJ5IjoiWnhqaVdXYlpNUUdIVldLVlE0aGJTSWlyc1ZmdWVjQ0U2dDRqVDlGMkhaUSJ9XX0.bqvunfZRUR0yJtpo4onttxo6WBceT6Zr9SWLilEjwDnfMHqlyl_u6lQxvOnnGXCd4HoIxJzq_jIg_1RvL5iUiw"
    )
    val jwtSimple01 get() = KeyAttestationJwt(
        "eyJ0eXAiOiJrZXktYXR0ZXN0YXRpb24rand0IiwiYWxnIjoiRVMyNTYiLCJraWQiOiJkaWQ6dGR3OmV4YW1wbGUuY29tI2tleS0xIn0.eyJpc3MiOiJkaWQ6dGR3OmV4YW1wbGUuY29tIiwiaWF0IjoxNTE2MjQ3MDIyLCJleHAiOjE1NDE0OTM3MjR9.0bq9BY7Xsnpyd4a3WQf0myACysJ2NY5_83aT7TUKeJTgLv99dwbiHaX7gJO-BH5tkDsM4CKZFS4O9OzsDiA72A"
    )
    val jwkEcP256_01 = """
        {
            "kty":"EC",
            "crv":"P-256",
            "x":"TCAER19Zvu3OHF4j4W4vfSVoHIP1ILilDls7vCeGemc",
            "y":"ZxjiWWbZMQGHVWKVQ4hbSIirsVfuecCE6t4jT9F2HZQ"
        }
    """.trimIndent()

    val jwkEcP256_02 = """
        {
            "kty":"EC",
            "crv":"P-256",
            "x":"MKBCTNIcKUSDii11ySs3526iDZ8AiTo7Tu6KPAqv7D4",
            "y":"4Etl6SRW2YiLUrN5vfvVHuhp7x8PxltmWWlbbM4IFyM",
            "kid":"1"
        }
    """.trimIndent()
}
