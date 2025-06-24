package ch.admin.foitt.wallet.platform.jsonSchema.domain.usecase.implementation

import ch.admin.foitt.wallet.platform.jsonSchema.domain.model.JsonSchemaError
import ch.admin.foitt.wallet.platform.jsonSchema.domain.model.toJsonSchemaError
import ch.admin.foitt.wallet.platform.jsonSchema.domain.usecase.JsonSchemaValidator
import ch.admin.foitt.wallet.platform.utils.SafeJson
import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.coroutines.coroutineBinding
import com.github.michaelbull.result.coroutines.runSuspendCatching
import com.github.michaelbull.result.mapError
import com.networknt.schema.InputFormat
import com.networknt.schema.JsonSchema
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SchemaId
import com.networknt.schema.SchemaLocation
import com.networknt.schema.SchemaValidatorsConfig
import com.networknt.schema.SpecVersion
import javax.inject.Inject

internal class VcSdJwtJsonSchemaValidatorImpl @Inject constructor(
    val safeJson: SafeJson
) : JsonSchemaValidator {
    override suspend fun invoke(data: String, jsonSchema: String): Result<Unit, JsonSchemaError> = coroutineBinding {
        val jsonSchemaFactory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012)
        val config = SchemaValidatorsConfig.builder().failFast(true).build()

        // validate jsonSchema conforms to Json Schema Draft 2010-12
        val metaSchema = jsonSchemaFactory.buildJsonSchema(schema = SchemaLocation.of(SchemaId.V202012), config = config).bind()
        validateDataWithJsonSchema(data = jsonSchema, jsonSchema = metaSchema).bind()

        // validate jsonSchema can validate a VcSdJwt schema
        val vcSdJwtMetaSchema = jsonSchemaFactory.buildJsonSchema(schema = vcSdJwtMetaSchemaString, config = config).bind()
        validateDataWithJsonSchema(data = jsonSchema, jsonSchema = vcSdJwtMetaSchema).bind()

        // validate the data against the jsonSchema
        val schema = jsonSchemaFactory.buildJsonSchema(schema = jsonSchema, config = config).bind()
        validateDataWithJsonSchema(data = data, jsonSchema = schema).bind()
    }

    private fun validateDataWithJsonSchema(
        data: String,
        jsonSchema: JsonSchema
    ): Result<Unit, JsonSchemaError> = binding {
        val assertions = runSuspendCatching {
            jsonSchema.validate(data, InputFormat.JSON)
        }.mapError { throwable ->
            throwable.toJsonSchemaError()
        }.bind()

        if (assertions.isNotEmpty()) {
            Err(JsonSchemaError.ValidationFailed).bind()
        }
    }

    private fun JsonSchemaFactory.buildJsonSchema(
        schema: SchemaLocation,
        config: SchemaValidatorsConfig
    ): Result<JsonSchema, JsonSchemaError> = runSuspendCatching {
        this.getSchema(schema, config)
    }.mapError { throwable ->
        throwable.toJsonSchemaError()
    }

    private fun JsonSchemaFactory.buildJsonSchema(
        schema: String,
        config: SchemaValidatorsConfig
    ): Result<JsonSchema, JsonSchemaError> = runSuspendCatching {
        this.getSchema(schema, config)
    }.mapError { throwable ->
        throwable.toJsonSchemaError()
    }

    companion object {
        private val vcSdJwtMetaSchemaString = """
            {
              "properties": {
                "additionalItems": true,
                "type": "object",
                "required": ["properties", "required"],
                "required": {
                  "type": "array",
                  "allOf": [
                    { "contains": { "const": "vct" } },
                    { "contains": { "const": "iss" } }
                  ]
                },
                "properties": {
                  "type": "object",
                  "required": [ "iss", "vct", "nbf", "exp", "cnf", "status", "sub", "iat" ],
                  "properties": {
                    "vct": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "string" }
                      }
                    },
                    "iss": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "string" }
                      }
                    },
                    "nbf": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "number" }
                      }
                    },
                    "exp": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "number" }
                      }
                    },
                    "cnf": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "object" }
                      }
                    },
                    "status": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "object" }
                      }
                    },
                    "sub": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "string" }
                      }
                    },
                    "iat": {
                      "type": "object",
                      "required": ["type"],
                      "properties": {
                        "type": { "const": "number" }
                      }
                    }
                  }
                }
              }
            }
        """.trimIndent()
    }
}
