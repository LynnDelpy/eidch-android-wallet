package ch.admin.foitt.wallet.platform.credentialPresentation.mock

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.ClientMetaData
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.ClientName
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Constraints
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.Field
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptor
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.InputDescriptorFormat
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.LogoUri
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationDefinition
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequest

object MockPresentationRequest {

    const val CLIENT_ID = "did:example:12345"
    const val VALID_JWT =
        "eyJraWQiOiJkaWQ6dGR3OjEyMzQ9OmV4YW1wbGUuY29tOmFwaTp2MTpkaWQ6MTIzNDU2I2tleS0wMSIsImFsZyI6IkVTMjU2In0.eyJyZXNwb25zZV91cmkiOiJodHRwczovL2V4YW1wbGUuY29tIiwiY2xpZW50X2lkX3NjaGVtZSI6ImRpZCIsImlzcyI6ImRpZDpleGFtcGxlOjEyMzQ1IiwicmVzcG9uc2VfdHlwZSI6InZwX3Rva2VuIiwicHJlc2VudGF0aW9uX2RlZmluaXRpb24iOnsiaWQiOiIzZmE4NWY2NC0wMDAwLTAwMDAtYjNmYy0yYzk2M2Y2NmFmYTYiLCJuYW1lIjoic3RyaW5nIiwicHVycG9zZSI6InN0cmluZyIsImlucHV0X2Rlc2NyaXB0b3JzIjpbeyJpZCI6IjNmYTg1ZjY0LTU3MTctNDU2Mi1iM2ZjLTJjOTYzZjY2YWZhNiIsIm5hbWUiOiJBIG5hbWUiLCJmb3JtYXQiOnsidmMrc2Qtand0Ijp7InNkLWp3dF9hbGdfdmFsdWVzIjpbIkVTMjU2Il0sImtiLWp3dF9hbGdfdmFsdWVzIjpbIkVTMjU2Il19fSwiY29uc3RyYWludHMiOnsiZmllbGRzIjpbeyJwYXRoIjpbIiQubGFzdE5hbWUiXX1dfX1dfSwibm9uY2UiOiJJMDJGaWJMRjRrNUVzZkRPMmpnakRvb1A0QS9adWtRMyIsImNsaWVudF9pZCI6ImRpZDpleGFtcGxlOjEyMzQ1IiwiY2xpZW50X21ldGFkYXRhIjp7ImNsaWVudF9uYW1lIjoiUmVmIFRlc3QiLCJsb2dvX3VyaSI6Ind3dy5leGFtcGxlLmljbyJ9LCJyZXNwb25zZV9tb2RlIjoiZGlyZWN0X3Bvc3QifQ.CU3nJLohuGR58_I5XhyO6uimT0GRk19KXUH7CRoJvMr8jf8nLt4UaiXecFzH9lUM3CitptnxgDynnwvJe1oT2g"

    const val NOT_YET_VALID_JWT =
        "eyJraWQiOiJkaWQ6dGR3OjEyMzQ9OmV4YW1wbGUuY29tOmFwaTp2MTpkaWQ6MTIzNDU2I2tleS0wMSIsImFsZyI6IkVTMjU2In0.eyJuYmYiOjE5MjQ5ODgzOTksInJlc3BvbnNlX3VyaSI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjbGllbnRfaWRfc2NoZW1lIjoiZGlkIiwiaXNzIjoiZGlkOmV4YW1wbGU6MTIzNDUiLCJyZXNwb25zZV90eXBlIjoidnBfdG9rZW4iLCJwcmVzZW50YXRpb25fZGVmaW5pdGlvbiI6eyJpZCI6IjNmYTg1ZjY0LTAwMDAtMDAwMC1iM2ZjLTJjOTYzZjY2YWZhNiIsIm5hbWUiOiJzdHJpbmciLCJwdXJwb3NlIjoic3RyaW5nIiwiaW5wdXRfZGVzY3JpcHRvcnMiOlt7ImlkIjoiM2ZhODVmNjQtNTcxNy00NTYyLWIzZmMtMmM5NjNmNjZhZmE2IiwibmFtZSI6IkEgbmFtZSIsImZvcm1hdCI6eyJ2YytzZC1qd3QiOnsic2Qtand0X2FsZ192YWx1ZXMiOlsiRVMyNTYiXSwia2Itand0X2FsZ192YWx1ZXMiOlsiRVMyNTYiXX19LCJjb25zdHJhaW50cyI6eyJmaWVsZHMiOlt7InBhdGgiOlsiJC5sYXN0TmFtZSJdfV19fV19LCJub25jZSI6IkkwMkZpYkxGNGs1RXNmRE8yamdqRG9vUDRBL1p1a1EzIiwiY2xpZW50X2lkIjoiZGlkOmV4YW1wbGU6MTIzNDUiLCJjbGllbnRfbWV0YWRhdGEiOnsiY2xpZW50X25hbWUiOiJSZWYgVGVzdCIsImxvZ29fdXJpIjoid3d3LmV4YW1wbGUuaWNvIn0sInJlc3BvbnNlX21vZGUiOiJkaXJlY3RfcG9zdCJ9.rs1Qd_HRVZ6HKB_yJiCH_wDKV3tWGYBRiqU8soUnIHObohMuCGCk-jUuuRkpS9WUaYxJlO3Zz2qCBgdajgxo8Q"

    const val EXPIRED_JWT =
        "eyJraWQiOiJkaWQ6dGR3OjEyMzQ9OmV4YW1wbGUuY29tOmFwaTp2MTpkaWQ6MTIzNDU2I2tleS0wMSIsImFsZyI6IkVTMjU2In0.eyJleHAiOjAsInJlc3BvbnNlX3VyaSI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJjbGllbnRfaWRfc2NoZW1lIjoiZGlkIiwiaXNzIjoiZGlkOmV4YW1wbGU6MTIzNDUiLCJyZXNwb25zZV90eXBlIjoidnBfdG9rZW4iLCJwcmVzZW50YXRpb25fZGVmaW5pdGlvbiI6eyJpZCI6IjNmYTg1ZjY0LTAwMDAtMDAwMC1iM2ZjLTJjOTYzZjY2YWZhNiIsIm5hbWUiOiJzdHJpbmciLCJwdXJwb3NlIjoic3RyaW5nIiwiaW5wdXRfZGVzY3JpcHRvcnMiOlt7ImlkIjoiM2ZhODVmNjQtNTcxNy00NTYyLWIzZmMtMmM5NjNmNjZhZmE2IiwibmFtZSI6IkEgbmFtZSIsImZvcm1hdCI6eyJ2YytzZC1qd3QiOnsic2Qtand0X2FsZ192YWx1ZXMiOlsiRVMyNTYiXSwia2Itand0X2FsZ192YWx1ZXMiOlsiRVMyNTYiXX19LCJjb25zdHJhaW50cyI6eyJmaWVsZHMiOlt7InBhdGgiOlsiJC5sYXN0TmFtZSJdfV19fV19LCJub25jZSI6IkkwMkZpYkxGNGs1RXNmRE8yamdqRG9vUDRBL1p1a1EzIiwiY2xpZW50X2lkIjoiZGlkOmV4YW1wbGU6MTIzNDUiLCJjbGllbnRfbWV0YWRhdGEiOnsiY2xpZW50X25hbWUiOiJSZWYgVGVzdCIsImxvZ29fdXJpIjoid3d3LmV4YW1wbGUuaWNvIn0sInJlc3BvbnNlX21vZGUiOiJkaXJlY3RfcG9zdCJ9.gf70qkRBEjb8DDBNbSdh-ibkQL7xVnzQzQ6LV3Rparboamz2Y5Uh8quRPR_wS3LHikVNvGFgOA94uVWV5vrgnA"

    private val constraints = Constraints(
        fields = listOf(
            Field(
                path = listOf()
            )
        )
    )

    private val inputDescriptorFormatVcSdJwt = InputDescriptorFormat.VcSdJwt(
        sdJwtAlgorithms = listOf(),
        kbJwtAlgorithms = listOf(),
    )

    val inputDescriptor = InputDescriptor(
        id = "id",
        name = "name",
        formats = listOf(
            inputDescriptorFormatVcSdJwt
        ),
        constraints = constraints,
        purpose = "constraintPurpose",
    )

    private val presentationDefinition = PresentationDefinition(
        id = "diam",
        inputDescriptors = listOf(inputDescriptor),
        purpose = "definitionPurpose",
        name = "name",
    )

    val presentationRequest = PresentationRequest(
        nonce = "iusto",
        presentationDefinition = presentationDefinition,
        responseUri = "tincidunt",
        responseMode = "direct_post",
        clientId = CLIENT_ID,
        clientIdScheme = "did",
        responseType = "vp_token",
        clientMetaData = null
    )

    fun invalidPresentationRequest(paths: List<String>) = presentationRequest.copy(
        presentationDefinition = presentationDefinition.copy(
            inputDescriptors = listOf(
                inputDescriptor.copy(
                    constraints = Constraints(
                        fields = listOf(
                            Field(path = paths)
                        ),
                    )
                )
            )
        )
    )

    val presentationRequestWithDisplays = PresentationRequest(
        nonce = "iusto",
        presentationDefinition = presentationDefinition,
        responseUri = "tincidunt",
        responseMode = "suscipit",
        clientId = "clientId",
        clientIdScheme = "clientIdScheme",
        responseType = "responseType",
        clientMetaData = ClientMetaData(
            clientNameList = listOf(
                ClientName(
                    clientName = "firstClientName",
                    locale = "en"
                ),
                ClientName(
                    clientName = "secondClientName",
                    locale = "fr"
                ),
                ClientName(
                    clientName = "clientName",
                    locale = "fallback"
                )
            ),
            logoUriList = listOf(
                LogoUri(
                    logoUri = "firstLogoUri",
                    locale = "en"
                ),
                LogoUri(
                    logoUri = "secondLogoUri",
                    locale = "de"
                ),
                LogoUri(
                    logoUri = "logoUri",
                    locale = "fallback"
                )
            )
        )
    )
}
