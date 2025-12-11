package ch.admin.foitt.wallet.platform.credential.domain.usecase.implementation.mock

import ch.admin.foitt.openid4vc.domain.model.vcSdJwt.VcSdJwtCredential

object MockCredential {
    val vcSdJwtCredentialProd = VcSdJwtCredential(
        id = 1L,
        payload = "eyJhbGciOiJFUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6ImtleUlkIn0.eyJpc3MiOiJkaWQ6dGR3OmZvbz06aWRlbnRpZmllci1yZWctYS50cnVzdC1pbmZyYS5zd2l5dS5hZG1pbi5jaDpiYXIiLCJ2Y3QiOiJ2Y3QifQ.3z_53uQe_4CyCSkPyU7uu-jsNatK07N7LtlhIfmS2hqnQagab9iJZODfsd4ahsCvoZS58B-7ySypV_rU7sGFMA"
    )

    val vcSdJwtCredentialBeta = VcSdJwtCredential(
        id = 1L,
        payload = "ewogICJhbGciOiJFUzI1NiIsCiAgInR5cCI6IkpXVCIsCiAgImtpZCI6ImtleUlkIgp9.ewogICJpc3MiOiJkaWQ6dGR3OmZvbz06aWRlbnRpZmllci1yZWctYS50cnVzdC1pbmZyYS5zd2l5dS1pbnQuYWRtaW4uY2g6YmFyIiwKICAidmN0IjoidmN0Igp9.ZXdvZ0lDSmhiR2NpT2lKRlV6STFOaUlzQ2lBZ0luUjVjQ0k2SWtwWFZDSXNDaUFnSW10cFpDSTZJbXRsZVVsa0lncDkuLjRVZTlHZWNrZ21kUjA0Z2dSS3dVX21Ua1RaSVh1Nk5OSUdZb1U5U0duN2tMdkxlQXBMaHhzNUEyYmdtT1NKTV9QSHNBRTdsZEtHS01GNUtzaUY3OGhn"
    )
}
