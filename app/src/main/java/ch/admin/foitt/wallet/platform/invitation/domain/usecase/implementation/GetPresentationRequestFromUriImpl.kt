package ch.admin.foitt.wallet.platform.invitation.domain.usecase.implementation

import ch.admin.foitt.openid4vc.domain.model.presentationRequest.FetchPresentationRequestError
import ch.admin.foitt.openid4vc.domain.model.presentationRequest.PresentationRequestContainer
import ch.admin.foitt.openid4vc.domain.usecase.FetchPresentationRequest
import ch.admin.foitt.wallet.BuildConfig
import ch.admin.foitt.wallet.platform.invitation.domain.model.GetPresentationRequestError
import ch.admin.foitt.wallet.platform.invitation.domain.model.toGetPresentationRequestError
import ch.admin.foitt.wallet.platform.invitation.domain.usecase.GetPresentationRequestFromUri
import ch.admin.foitt.wallet.platform.utils.getQueryParameter
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import java.net.URI
import java.net.URL
import javax.inject.Inject

internal class GetPresentationRequestFromUriImpl @Inject constructor(
    private val fetchPresentationRequest: FetchPresentationRequest,
) : GetPresentationRequestFromUri {
    override suspend fun invoke(uri: URI): Result<PresentationRequestContainer, GetPresentationRequestError> = coroutineBinding {
        val (requestUrl, clientId) = runSuspendCatching {
            when (uri.scheme) {
                BuildConfig.SCHEME_PRESENTATION_REQUEST -> uri.toURL() to null
                BuildConfig.SCHEME_PRESENTATION_REQUEST_OID,
                BuildConfig.SCHEME_PRESENTATION_REQUEST_SWIYU -> {
                    val clientId = uri.getQueryParameter(QUERY_PARAM_CLIENT_ID)
                        .mapError {
                            it.toGetPresentationRequestError(uri)
                        }.bind()
                    val requestUri = uri.getQueryParameter(QUERY_PARAM_REQUEST_URI)
                        .mapError {
                            it.toGetPresentationRequestError(uri)
                        }.bind()

                    URL(requestUri) to clientId
                }
                else -> error("invalid presentation schema")
            }
        }.mapError {
            it.toGetPresentationRequestError(uri)
        }.bind()

        val presentationRequest = fetchPresentationRequest(requestUrl).mapError(
            FetchPresentationRequestError::toGetPresentationRequestError
        ).bind()

        when (presentationRequest) {
            is PresentationRequestContainer.Jwt -> PresentationRequestContainer.Jwt(jwt = presentationRequest.jwt, clientId = clientId)
            is PresentationRequestContainer.Json -> PresentationRequestContainer.Json(json = presentationRequest.json, clientId = clientId)
        }
    }

    private companion object {
        const val QUERY_PARAM_CLIENT_ID = "client_id"
        const val QUERY_PARAM_REQUEST_URI = "request_uri"
    }
}
