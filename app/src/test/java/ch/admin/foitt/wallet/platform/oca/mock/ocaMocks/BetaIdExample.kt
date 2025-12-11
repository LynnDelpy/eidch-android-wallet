package ch.admin.foitt.wallet.platform.oca.mock.ocaMocks

val betaIdCaptureBase = """
       {
      "type": "spec/capture_base/1.0",
      "digest": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
      "attributes": {
        "family_name": "Text",
        "given_name": "Text",
        "birth_date": "DateTime",
        "age_over_16": "Boolean",
        "age_over_18": "Boolean",
        "age_over_65": "Boolean",
        "age_birth_year": "Numeric",
        "sex": "Numeric",
        "place_of_origin": "Text",
        "birth_place": "Text",
        "nationality": "Text",
        "portrait": "Text",
        "personal_administrative_number": "Text",
        "additional_person_info": "Text",
        "document_number": "Text",
        "issuance_date": "DateTime",
        "expiry_date": "DateTime",
        "reference_id_type": "Text",
        "reference_id_expiry_date": "DateTime",
        "verification_type": "Text",
        "verification_organization": "Text",
        "issuing_authority": "Text",
        "issuing_country": "Text"
      }
    } 
""".trimIndent()

val betaIdExample = """
    {
      "capture_bases": [
      $betaIdCaptureBase
      ],
      "overlays": [
        {
          "type": "extend/overlays/data_source/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "format": "vc+sd-jwt",
          "attribute_sources": {
            "family_name": "${'$'}.family_name",
            "given_name": "${'$'}.given_name",
            "birth_date": "${'$'}.birth_date",
            "age_over_16": "${'$'}.age_over_16",
            "age_over_18": "${'$'}.age_over_18",
            "age_over_65": "${'$'}.age_over_65",
            "age_birth_year": "${'$'}.age_birth_year",
            "sex": "${'$'}.sex",
            "place_of_origin": "${'$'}.place_of_origin",
            "birth_place": "${'$'}.birth_place",
            "nationality": "${'$'}.nationality",
            "portrait": "${'$'}.portrait",
            "personal_administrative_number": "${'$'}.personal_administrative_number",
            "additional_person_info": "${'$'}.additional_person_info",
            "document_number": "${'$'}.document_number",
            "issuance_date": "${'$'}.issuance_date",
            "expiry_date": "${'$'}.expiry_date",
            "reference_id_type": "${'$'}.reference_id_type",
            "reference_id_expiry_date": "${'$'}.reference_id_expiry_date",
            "verification_type": "${'$'}.verification_type",
            "verification_organization": "${'$'}.verification_organization",
            "issuing_authority": "${'$'}.issuing_authority",
            "issuing_country": "${'$'}.issuing_country"
          }
        },
        {
          "type": "spec/overlays/character_encoding/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "default_character_encoding": "utf-8"
        },
        {
          "type": "spec/overlays/meta/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "de-CH",
          "name": "INTG:Beta-ID",
          "description": "Test-Credential für die swiyu Public Beta Umgebung"
        },
        {
          "type": "spec/overlays/meta/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "fr-CH",
          "name": "INTG:Beta-ID",
          "description": "Credential de test pour l'environnement swiyu Public Beta"
        },
        {
          "type": "spec/overlays/meta/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "it-CH",
          "name": "INTG:Beta-ID",
          "description": "Credenziali di test per l'ambiente swiyu Public Beta"
        },
        {
          "type": "spec/overlays/meta/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "rm-CH",
          "name": "INTG:Beta-ID",
          "description": "Credenziali da test per swiyu Public Beta"
        },
        {
          "type": "spec/overlays/meta/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "en-GB",
          "name": "INTG:Beta-ID",
          "description": "Test Credential for swiyu Public Beta environment"
        },
        {
          "type": "aries/overlays/branding/1.1",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "de-CH",
          "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAQAAADa613fAAAAt0lEQVR42u3YMQ4BQRiG4a2p3UMi0Wlcih5n2GYTWS6gcBahdZGfSqHZWdNMxvN+/WSe9m8aSZIkSaqiWMYzcV3ZkFWkdgEBAQEBAQEBAQEBAQEBAQEBKQ4S6zi8tx/Y7rM+GfIYfPV70xzIOcppBlIT5AQCAgICApIB6UFAQEBAQDIgx1og89iMWpv8rXtsR25S5jnoWstdCwQEBAQEBAQEBAQEBAQEBOSfIYu4Ja5tJEmSJP3UC98M2ozRcV9yAAAAAElFTkSuQmCC",
          "primary_background_color": "#4A5F77",
          "primary_field": "{{given_name}} {{family_name}}"
        },
        {
          "type": "aries/overlays/branding/1.1",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "fr-CH",
          "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAQAAADa613fAAAAt0lEQVR42u3YMQ4BQRiG4a2p3UMi0Wlcih5n2GYTWS6gcBahdZGfSqHZWdNMxvN+/WSe9m8aSZIkSaqiWMYzcV3ZkFWkdgEBAQEBAQEBAQEBAQEBAQEBKQ4S6zi8tx/Y7rM+GfIYfPV70xzIOcppBlIT5AQCAgICApIB6UFAQEBAQDIgx1og89iMWpv8rXtsR25S5jnoWstdCwQEBAQEBAQEBAQEBAQEBOSfIYu4Ja5tJEmSJP3UC98M2ozRcV9yAAAAAElFTkSuQmCC",
          "primary_background_color": "#4A5F77",
          "primary_field": "{{given_name}} {{family_name}}"
        },
        {
          "type": "aries/overlays/branding/1.1",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "it-CH",
          "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAQAAADa613fAAAAt0lEQVR42u3YMQ4BQRiG4a2p3UMi0Wlcih5n2GYTWS6gcBahdZGfSqHZWdNMxvN+/WSe9m8aSZIkSaqiWMYzcV3ZkFWkdgEBAQEBAQEBAQEBAQEBAQEBKQ4S6zi8tx/Y7rM+GfIYfPV70xzIOcppBlIT5AQCAgICApIB6UFAQEBAQDIgx1og89iMWpv8rXtsR25S5jnoWstdCwQEBAQEBAQEBAQEBAQEBOSfIYu4Ja5tJEmSJP3UC98M2ozRcV9yAAAAAElFTkSuQmCC",
          "primary_background_color": "#4A5F77",
          "primary_field": "{{given_name}} {{family_name}}"
        },
        {
          "type": "aries/overlays/branding/1.1",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "en-GB",
          "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAGQAAABkCAQAAADa613fAAAAt0lEQVR42u3YMQ4BQRiG4a2p3UMi0Wlcih5n2GYTWS6gcBahdZGfSqHZWdNMxvN+/WSe9m8aSZIkSaqiWMYzcV3ZkFWkdgEBAQEBAQEBAQEBAQEBAQEBKQ4S6zi8tx/Y7rM+GfIYfPV70xzIOcppBlIT5AQCAgICApIB6UFAQEBAQDIgx1og89iMWpv8rXtsR25S5jnoWstdCwQEBAQEBAQEBAQEBAQEBOSfIYu4Ja5tJEmSJP3UC98M2ozRcV9yAAAAAElFTkSuQmCC",
          "primary_background_color": "#4A5F77",
          "primary_field": "{{given_name}} {{family_name}}"
        },
        {
          "type": "spec/overlays/standard/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "attr_standards": {
            "portrait": "urn:ietf:rfc:2397",
            "birth_date": "urn:iso:std:iso:8601",
            "issuance_date": "urn:iso:std:iso:8601",
            "expiry_date": "urn:iso:std:iso:8601",
            "reference_id_expiry_date": "urn:iso:std:iso:8601"
          }
        },
        {
          "type": "spec/overlays/format/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "attribute_formats": {
            "birth_date": "YYYY-MM-DD",
            "issuance_date": "YYYY-MM-DD",
            "expiry_date": "YYYY-MM-DD",
            "reference_id_expiry_date": "YYYY-MM-DD"
          }
        },
        {
          "type": "spec/overlays/label/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "de-CH",
          "attribute_labels": {
            "family_name": "Name",
            "given_name": "Vorname(n)",
            "birth_date": "Geburtsdatum",
            "age_over_16": "Alter über 16 Jahre",
            "age_over_18": "Alter über 18 Jahre",
            "age_over_65": "Alter über 65 Jahre",
            "age_birth_year": "Jahrgang",
            "sex": "Geschlecht",
            "place_of_origin": "Heimatort",
            "birth_place": "Geburtsort",
            "nationality": "Nationalität",
            "portrait": "Gesichtsbild",
            "personal_administrative_number": "AHV-Nummer",
            "additional_person_info": "Zusätzliche Angaben zur Person",
            "document_number": "Beta-ID-Nummer",
            "issuance_date": "Ausgestellt am",
            "expiry_date": "Gültig bis",
            "reference_id_type": "Ausweistyp",
            "reference_id_expiry_date": "Ablaufdatum des Ausweises",
            "verification_type": "Identitätsprüfungsart",
            "verification_organization": "Identitätsprüfung durch",
            "issuing_authority": "Ausgabestelle",
            "issuing_country": "Ausstellungsland"
          }
        },
        {
          "type": "spec/overlays/label/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "fr-CH",
          "attribute_labels": {
            "family_name": "Nom",
            "given_name": "Prénom(s)",
            "birth_date": "Date de naissance",
            "age_over_16": "Age de plus de 16 ans",
            "age_over_18": "Age de plus de 18 ans",
            "age_over_65": "Age de plus de 65 ans",
            "age_birth_year": "Né(e) en",
            "sex": "Sexe",
            "place_of_origin": "Lieu d'origine",
            "birth_place": "Lieu de naissance",
            "nationality": "Nationalité",
            "portrait": "Image faciale",
            "personal_administrative_number": "Numéro AVS",
            "additional_person_info": "Mentions supplémentaires concernant la personne",
            "document_number": "Numéro de Beta-ID",
            "issuance_date": "Délivré le",
            "expiry_date": "Date d'expiration",
            "reference_id_type": "Type de document d'identité",
            "reference_id_expiry_date": "Date d'expiration du document",
            "verification_type": "Processus de vérification d'identité",
            "verification_organization": "Identitätsprüfung durch",
            "issuing_authority": "Autorité de délivrance",
            "issuing_country": "Pays d'émission"
          }
        },
        {
          "type": "spec/overlays/label/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "it-CH",
          "attribute_labels": {
            "family_name": "Cognome",
            "given_name": "Nome(i)",
            "birth_date": "Data di nascita",
            "age_over_16": "Età superiore a 16 anni",
            "age_over_18": "Età superiore a 18 anni",
            "age_over_65": "Età superiore a 65 anni",
            "age_birth_year": "Annata",
            "sex": "Sesso",
            "place_of_origin": "Luogo di attinenza",
            "birth_place": "Luogo di nascita",
            "nationality": "Cittadinanza",
            "portrait": "Immagine facciale",
            "personal_administrative_number": "Numero AVS",
            "additional_person_info": "Informazioni supplementari sulla persona",
            "document_number": "Numero di Beta-ID",
            "issuance_date": "Rilasciato il",
            "expiry_date": "Data di scadenza",
            "reference_id_type": "Tipo di documento",
            "reference_id_expiry_date": "Data di scadenza del documento",
            "verification_type": "Procedura di verifica del'identità",
            "verification_organization": "Procedura di verifica del'identità tramite",
            "issuing_authority": "Autorità di emissione",
            "issuing_country": "Paese di emissione"
          }
        },
        {
          "type": "spec/overlays/label/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "rm-CH",
          "attribute_labels": {
            "family_name": "Num",
            "given_name": "Prenum(s)",
            "birth_date": "Data di naschientscha",
            "age_over_16": "Dapli che 16 onns",
            "age_over_18": "Dapli che 18 onns",
            "age_over_65": "Dapli che 65 onns",
            "age_birth_year": "Onnada",
            "sex": "Schlattaina",
            "place_of_origin": "Lieu d'origin",
            "birth_place": "Lieu da naschientscha",
            "nationality": "Naziunalitad",
            "portrait": "Fotografia da fatscha",
            "personal_administrative_number": "Numer da l'AVS",
            "additional_person_info": "Indicaziuns supplementaras per la persuna",
            "document_number": "Numer Beta-ID",
            "issuance_date": "Emess ils",
            "expiry_date": "Data di scadenza",
            "reference_id_type": "Tip dal document",
            "reference_id_expiry_date": "Data da scadenza dal document",
            "verification_type": "Process da verificaziun d'identitad",
            "verification_organization": "Process da verificaziun tras",
            "issuing_authority": "Autorità da emission",
            "issuing_country": "Plea da emission"
          }
        },
        {
          "type": "spec/overlays/label/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "en-GB",
          "attribute_labels": {
            "family_name": "Surname",
            "given_name": "Given name(s)",
            "birth_date": "Date of birth",
            "age_over_16": "Over the age of 16",
            "age_over_18": "Over the age of 18",
            "age_over_65": "Over the age of 65",
            "age_birth_year": "Born in",
            "sex": "Sex",
            "place_of_origin": "Place of origin",
            "birth_place": "Place of birth",
            "nationality": "Nationality",
            "portrait": "Face image",
            "personal_administrative_number": "Insurance Number",
            "additional_person_info": "Additional information about the person",
            "document_number": "Beta-ID Number",
            "issuance_date": "Date of issue",
            "expiry_date": "Date of expiry",
            "reference_id_type": "Identification type",
            "reference_id_expiry_date": "Identification date of expiry",
            "verification_type": "Identity verification process",
            "verification_organization": "Identity verification by",
            "issuing_authority": "Issuing authority",
            "issuing_country": "Issuing country"
          }
        },
        {
          "type": "extend/overlays/cluster_ordering/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "de-CH",
          "cluster_order": {
            "pid": 1,
            "additional": 2
          },
          "attribute_cluster_order": {
            "pid": {
              "document_number": 1,
              "portrait": 2,
              "family_name": 3,
              "given_name": 4,
              "birth_date": 5,
              "sex": 6,
              "place_of_origin": 7,
              "birth_place": 8,
              "nationality": 9,
              "personal_administrative_number": 10,
              "additional_person_info": 11,
              "age_over_16": 12,
              "age_over_18": 13,
              "age_over_65": 14,
              "age_birth_year": 15
            },
            "additional": {
              "issuance_date": 1,
              "expiry_date": 2,
              "reference_id_type": 3,
              "reference_id_expiry_date": 4,
              "verification_type": 5,
              "verification_organization": 6,
              "issuing_authority": 7,
              "issuing_country": 8
            }
          }
        },
        {
          "type": "extend/overlays/cluster_ordering/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "fr-CH",
          "cluster_order": {
            "pid": 1,
            "additional": 2
          },
          "attribute_cluster_order": {
            "pid": {
              "document_number": 1,
              "portrait": 2,
              "family_name": 3,
              "given_name": 4,
              "birth_date": 5,
              "sex": 6,
              "place_of_origin": 7,
              "birth_place": 8,
              "nationality": 9,
              "personal_administrative_number": 10,
              "additional_person_info": 11,
              "age_over_16": 12,
              "age_over_18": 13,
              "age_over_65": 14,
              "age_birth_year": 15
            },
            "additional": {
              "issuance_date": 1,
              "expiry_date": 2,
              "reference_id_type": 3,
              "reference_id_expiry_date": 4,
              "verification_type": 5,
              "verification_organization": 6,
              "issuing_authority": 7,
              "issuing_country": 8
            }
          }
        },
        {
          "type": "extend/overlays/cluster_ordering/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "it-CH",
          "cluster_order": {
            "pid": 1,
            "additional": 2
          },
          "attribute_cluster_order": {
            "pid": {
              "document_number": 1,
              "portrait": 2,
              "family_name": 3,
              "given_name": 4,
              "birth_date": 5,
              "sex": 6,
              "place_of_origin": 7,
              "birth_place": 8,
              "nationality": 9,
              "personal_administrative_number": 10,
              "additional_person_info": 11,
              "age_over_16": 12,
              "age_over_18": 13,
              "age_over_65": 14,
              "age_birth_year": 15
            },
            "additional": {
              "issuance_date": 1,
              "expiry_date": 2,
              "reference_id_type": 3,
              "reference_id_expiry_date": 4,
              "verification_type": 5,
              "verification_organization": 6,
              "issuing_authority": 7,
              "issuing_country": 8
            }
          }
        },
        {
          "type": "extend/overlays/cluster_ordering/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "rm-CH",
          "cluster_order": {
            "pid": 1,
            "additional": 2
          },
          "attribute_cluster_order": {
            "pid": {
              "document_number": 1,
              "portrait": 2,
              "family_name": 3,
              "given_name": 4,
              "birth_date": 5,
              "sex": 6,
              "place_of_origin": 7,
              "birth_place": 8,
              "nationality": 9,
              "personal_administrative_number": 10,
              "additional_person_info": 11,
              "age_over_16": 12,
              "age_over_18": 13,
              "age_over_65": 14,
              "age_birth_year": 15
            },
            "additional": {
              "issuance_date": 1,
              "expiry_date": 2,
              "reference_id_type": 3,
              "reference_id_expiry_date": 4,
              "verification_type": 5,
              "verification_organization": 6,
              "issuing_authority": 7,
              "issuing_country": 8
            }
          }
        },
        {
          "type": "extend/overlays/cluster_ordering/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "en-GB",
          "cluster_order": {
            "pid": 1,
            "additional": 2
          },
          "attribute_cluster_order": {
            "pid": {
              "document_number": 1,
              "portrait": 2,
              "family_name": 3,
              "given_name": 4,
              "birth_date": 5,
              "sex": 6,
              "place_of_origin": 7,
              "birth_place": 8,
              "nationality": 9,
              "personal_administrative_number": 10,
              "additional_person_info": 11,
              "age_over_16": 12,
              "age_over_18": 13,
              "age_over_65": 14,
              "age_birth_year": 15
            },
            "additional": {
              "issuance_date": 1,
              "expiry_date": 2,
              "reference_id_type": 3,
              "reference_id_expiry_date": 4,
              "verification_type": 5,
              "verification_organization": 6,
              "issuing_authority": 7,
              "issuing_country": 8
            }
          }
        },
        {
          "type": "spec/overlays/entry_code/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "attribute_entry_codes": {
            "sex": [
              "1",
              "2"
            ],
            "age_over_16": [
              "true",
              "false"
            ],
            "age_over_18": [
              "true",
              "false"
            ],
            "age_over_65": [
              "true",
              "false"
            ],
            "reference_id_type": [
              "SWISS_PASS",
              "SWISS_IDK",
              "FOREIGNER_PERMIT",
              "SELF_DECLARED"
            ],
            "verification_type": [
              "ONLINE",
              "ON_SITE",
              "SELF_DECLARED"
            ]
          }
        },
        {
          "type": "spec/overlays/entry/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "de-CH",
          "attribute_entries": {
            "sex": {
              "1": "Männlich",
              "2": "Weiblich"
            },
            "age_over_16": {
              "true": "ja",
              "false": "nein"
            },
            "age_over_18": {
              "true": "ja",
              "false": "nein"
            },
            "age_over_65": {
              "true": "ja",
              "false": "nein"
            },
            "reference_id_type": {
              "SWISS_PASS": "Schweizer Pass",
              "SWISS_IDK": "Schweizer Identitätskarte",
              "FOREIGNER_PERMIT": "Aufenthaltsbewilligung",
              "SELF_DECLARED": "Selbstdeklaration"
            },
            "verification_type": {
              "ONLINE": "Online-Verifikation",
              "ON_SITE": "Identitätsprüfung am Schalter",
              "SELF_DECLARED": "Selbstdeklaration"
            }
          }
        },
        {
          "type": "spec/overlays/entry/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "fr-CH",
          "attribute_entries": {
            "sex": {
              "1": "Homme",
              "2": "Femme"
            },
            "age_over_16": {
              "true": "oui",
              "false": "non"
            },
            "age_over_18": {
              "true": "oui",
              "false": "non"
            },
            "age_over_65": {
              "true": "oui",
              "false": "non"
            },
            "reference_id_type": {
              "SWISS_PASS": "Passeport suisse",
              "SWISS_IDK": "Carte d'identité suisse",
              "FOREIGNER_PERMIT": "Permis de séjour",
              "SELF_DECLARED": "Auto-déclaration"
            },
            "verification_type": {
              "ONLINE": "Vérification en ligne",
              "ON_SITE": "Vérification d'identité au guichet",
              "SELF_DECLARED": "Auto-déclaration"
            }
          }
        },
        {
          "type": "spec/overlays/entry/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "it-CH",
          "attribute_entries": {
            "sex": {
              "1": "Maschio",
              "2": "Femmina"
            },
            "age_over_16": {
              "true": "sì",
              "false": "no"
            },
            "age_over_18": {
              "true": "sì",
              "false": "no"
            },
            "age_over_65": {
              "true": "sì",
              "false": "no"
            },
            "reference_id_type": {
              "SWISS_PASS": "Passaporto svizzero",
              "SWISS_IDK": "Carta d'identità svizzera",
              "FOREIGNER_PERMIT": "Permesso di soggiorno",
              "SELF_DECLARED": "Autodichiarazione"
            },
            "verification_type": {
              "ONLINE": " Verifica online",
              "ON_SITE": " Verifica dell'identità allo sportello",
              "SELF_DECLARED": "Autodichiarazione"
            }
          }
        },
        {
          "type": "spec/overlays/entry/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "rm-CH",
          "attribute_entries": {
            "sex": {
              "1": "Masculin",
              "2": "Feminin"
            },
            "age_over_16": {
              "true": "gea",
              "false": "na"
            },
            "age_over_18": {
              "true": "gea",
              "false": "na"
            },
            "age_over_65": {
              "true": "gea",
              "false": "na"
            },
            "reference_id_type": {
              "SWISS_PASS": "Pass svizzer",
              "SWISS_IDK": "Carta d'identitad svizzer",
              "FOREIGNER_PERMIT": "Permission da dimora",
              "SELF_DECLARED": "Autodecleraziun"
            },
            "verification_type": {
              "ONLINE": "Verificaziun online",
              "ON_SITE": "Verificaziun al spurtegl",
              "SELF_DECLARED": "Autodecleraziun"
            }
          }
        },
        {
          "type": "spec/overlays/entry/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "language": "en-GB",
          "attribute_entries": {
            "sex": {
              "1": "Male",
              "2": "Female"
            },
            "age_over_16": {
              "true": "yes",
              "false": "no"
            },
            "age_over_18": {
              "true": "yes",
              "false": "no"
            },
            "age_over_65": {
              "true": "yes",
              "false": "no"
            },
            "reference_id_type": {
              "SWISS_PASS": "Swiss Passport",
              "SWISS_IDK": "Swiss Identity Card",
              "FOREIGNER_PERMIT": "Foreigner permit",
              "SELF_DECLARED": "Self-declared"
            },
            "verification_type": {
              "ONLINE": "Online verification",
              "ON_SITE": "On-site verification",
              "SELF_DECLARED": "Self-declared"
            }
          }
        },
        {
          "type": "spec/overlays/sensitive/1.0",
          "capture_base": "IHm0TBBExIcpdZexTUzQK7p6NoRCZnJ53at7s6MhHR2z",
          "attributes": [
            "portrait",
            "personal_administrative_number"
          ]
        }
      ]
    }
""".trimIndent()
