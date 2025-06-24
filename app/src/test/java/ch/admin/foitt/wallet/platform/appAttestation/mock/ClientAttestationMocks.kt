package ch.admin.foitt.wallet.platform.appAttestation.mock

internal object ClientAttestationMocks {
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

    val jwtAttestation01 get() =
        "eyJraWQiOiJkaWQ6dGR3Omlzc3VlciNhc3NlcnQta2V5LTAxIiwidHlwIjoib2F1dGgtY2xpZW50LWF0dGVzdGF0aW9uK2p3dCIsImFsZyI6IkVTMjU2In0.eyJpc3MiOiJkaWQ6dGR3Omlzc3VlciIsInN1YiI6ImRpZDpqd2s6ZXlKamNuWWlPaUpRTFRJMU5pSXNJbXQwZVNJNklrVkRJaXdpZUNJNklteG1Rbk5KZEdwQ1ZqaFZkelpOYjAxcVJsQk5helowU2tONFZuRldURmRHTnpoQ2R6aFJlWEpOTFZFaUxDSjVJam9pV1dJeVpVNWpPVFUwUkZkclRqRlpNa05RVkc1dGJrSjBSMVpDYURSR1ptUXRTbFE0YkV0VldXdFFVU0o5IiwiY25mIjp7Imp3ayI6eyJrdHkiOiJFQyIsImNydiI6IlAtMjU2IiwieCI6ImxmQnNJdGpCVjhVdzZNb01qRlBNazZ0SkN4VnFWTFdGNzhCdzhReXJNLVEiLCJ5IjoiWWIyZU5jOTU0RFdrTjFZMkNQVG5tbkJ0R1ZCaDRGZmQtSlQ4bEtVWWtQUSJ9fSwibmJmIjoxNzQ4OTYwOTY5LCJleHAiOjE4MTIwMzI5NjksIndhbGxldF9uYW1lIjoic3dpeXUifQ.E9uXflZAeAuadNwHmY2WSOaVpcOs4o8LreI5Kp1-_Uf0wI7UAfzKOpX-lz65iIUQeiB-CHWa7ZEwsuTUhcXDnQ"

    val jwkEcP256_01_didJwk get() =
        "did:jwk:eyJjcnYiOiJQLTI1NiIsImt0eSI6IkVDIiwieCI6ImxmQnNJdGpCVjhVdzZNb01qRlBNazZ0SkN4VnFWTFdGNzhCdzhReXJNLVEiLCJ5IjoiWWIyZU5jOTU0RFdrTjFZMkNQVG5tbkJ0R1ZCaDRGZmQtSlQ4bEtVWWtQUSJ9"

    val jwkEcP256_01 = """
        {
            "kty": "EC",
            "crv": "P-256",
            "x": "lfBsItjBV8Uw6MoMjFPMk6tJCxVqVLWF78Bw8QyrM-Q",
            "y": "Yb2eNc954DWkN1Y2CPTnmnBtGVBh4Ffd-JT8lKUYkPQ"
        }
    """.trimIndent()

    val jwkEcP256_02_didJwk get() =
        "did:jwk:eyJrdHkiOiJFQyIsImNydiI6IlAtMjU2IiwieCI6ImY4M09KM0QyeEY0eU9kd2pUYk5KeFRBZFVERnpaeWJ4VFVUUityOXNWcVkiLCJ5IjoieF9GRXpSdTluYUZ4ZVd3NldZRjhJUkx3Rko5YzJWOFFQRVpreTgxaVF4MCJ9"

    val jwkEcP256_02 = """
        {
            "kty": "EC",
            "crv": "P-256",
            "x": "f83OJ3D2xF4yOdwjTbNJxTAdUDFzZybxTUTR-f9sVqY",
            "y": "x_FEzRu9naFxeVw6WYF8IRLwFJ9c2V9QPEZkY81iQx0"
        }
    """.trimIndent()
}
