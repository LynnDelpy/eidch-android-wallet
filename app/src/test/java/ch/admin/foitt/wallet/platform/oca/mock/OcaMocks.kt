package ch.admin.foitt.wallet.platform.oca.mock

import ch.admin.foitt.wallet.platform.oca.domain.model.AttributeType
import ch.admin.foitt.wallet.platform.oca.domain.model.CaptureBase1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaBundle
import ch.admin.foitt.wallet.platform.oca.domain.model.OcaClaimData
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.BrandingOverlay1x1
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.CharacterEncodingOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.DataSourceOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.EntryOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.FormatOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.LabelOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.MetaOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.OrderOverlay1x0
import ch.admin.foitt.wallet.platform.oca.domain.model.overlays.StandardOverlay1x0

object OcaMocks {
    val ocaResponse = """
        {
            "oca": "displayData"
        }
    """.trimIndent()

    val elfaCaptureBase = """
    {
      "type": "spec/capture_base/1.0",
      "digest": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
      "attributes": {
        "lastName": "Text",
        "firstName": "Text",
        "dateOfBirth": "DateTime",
        "hometown": "Text",
        "dateOfExpiration": "DateTime",
        "issuerEntity": "Text",
        "issuerEntityDate": "DateTime",
        "signatureImage": "Binary",
        "photoImage": "Binary",
        "policeQRImage": "Binary",
        "categoryCode": "Text",
        "categoryIcon": "Binary",
        "categoryRestrictions": "Text",
        "restrictionsA": "Text",
        "restrictionsB": "Text",
        "faberPin": "Numeric",
        "licenceNumber": "Numeric"
      }
    }
    """.trimIndent()

    val elfaExample = """
        {
          "capture_bases": [
            $elfaCaptureBase
          ],
          "overlays": [
            {
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "type": "spec/overlays/character_encoding/1.0",
              "default_character_encoding": "utf-8",
              "attribute_character_encoding": {
                "signatureImage": "base64",
                "photoImage": "base64",
                "policeQRImage": "base64",
                "categoryIcon": "base64"
              }
            },
            {
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "type": "spec/overlays/format/1.0",
              "attribute_formats": {
                "dateOfBirth": "YYYY-MM-DD",
                "dateOfExpiration": "YYYY-MM-DD",
                "issuerEntityDate": "YYYY-MM-DD",
                "signatureImage": "image/png",
                "photoImage": "image/png",
                "policeQRImage": "image/png",
                "categoryIcon": "image/png"
              }
            },
            {
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "type": "spec/overlays/standard/1.0",
              "attr_standards": {
                "dateOfExpiration": "urn:iso:std:iso:8601",
                "issuerEntityDate": "urn:iso:std:iso:8601",
                "signatureImage": "urn:ietf:rfc:2083",
                "photoImage": "urn:ietf:rfc:2083",
                "policeQRImage": "urn:ietf:rfc:2083",
                "categoryIcon": "urn:ietf:rfc:2083"
              }
            },
            {
              "type": "extend/overlays/data_source/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "format": "vc+sd-jwt",
              "attribute_sources": {
                "lastName": "${'$'}.lastName",
                "firstName": "${'$'}.firstName",
                "dateOfBirth": "${'$'}.dateOfBirth",
                "hometown": "${'$'}.hometown",
                "dateOfExpiration": "${'$'}.dateOfExpiration",
                "issuerEntity": "${'$'}.issuerEntity",
                "issuerEntityDate": "${'$'}.issuerEntityDate",
                "signatureImage": "${'$'}.signatureImage",
                "photoImage": "${'$'}.photoImage",
                "policeQRImage": "${'$'}.policeQRImage",
                "categoryCode": "${'$'}.categoryCode",
                "categoryIcon": "${'$'}.categoryIcon",
                "categoryRestrictions": "${'$'}.categoryRestrictions",
                "restrictionsA": "${'$'}.restrictionsA",
                "restrictionsB": "${'$'}.restrictionsB",
                "faberPin": "${'$'}.faberPin",
                "licenceNumber": "${'$'}.licenceNumber"
              }
            },
            {
              "type": "spec/overlays/meta/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "de",
              "name": "REF: Lernfahrausweis",
              "description": "Elektronischer Lernfahrausweis"
            },
            {
              "type": "spec/overlays/meta/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "en",
              "name": "REF: Learner-driver permit",
              "description": "Electronic learner-driver permit"
            },
            {
              "type": "spec/overlays/meta/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "fr",
              "name": "REF: Permis d'élève conducteur",
              "description": "Permis d'élève conducteur électronique"
            },
            {
              "type": "spec/overlays/meta/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "it",
              "name": "REF: Licenza per allievo conducente",
              "description": "Licenza per allievo conducente elettronica"
            },
            {
              "type": "spec/overlays/meta/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "rm",
              "name": "REF: Permiss per emprender a manischar",
              "description": "Permiss per emprender a manischar electronic"
            },
            {
              "type": "aries/overlays/branding/1.1",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "de",
              "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color": "#007AFF",
              "primary_field": "Kategorie {{categoryCode}}"
            },
            {
              "type": "aries/overlays/branding/1.1",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "en",
              "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color": "#007AEF",
              "primary_field": "Category {{categoryCode}}"
            },
            {
              "type": "aries/overlays/branding/1.1",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "fr",
              "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color": "#007AEF",
              "primary_field": "Catégorie {{categoryCode}}"
            },
            {
              "type": "aries/overlays/branding/1.1",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "it",
              "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color": "#007AEF",
              "primary_field": "Categoria {{categoryCode}}"
            },
            {
              "type": "aries/overlays/branding/1.1",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "rm",
              "logo": "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color": "#007AEF",
              "primary_field": "Categoria {{categoryCode}}"
            },
            {
              "type": "spec/overlays/label/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "de",
              "attribute_labels": {
                "lastName": "Name",
                "firstName": "Vorname",
                "dateOfBirth": "Geburtsdatum",
                "hometown": "Heimatort",
                "dateOfExpiration": "Ablaufdatum",
                "issuerEntity": "Ausstellende Behörde",
                "issuerEntityDate": "Ausstelldatum",
                "signatureImage": "Unterschrift",
                "photoImage": "Foto",
                "policeQRImage": "Polizeikontrolle, QR-Code",
                "categoryCode": "Kategorie",
                "categoryIcon": "Kategorie Piktogramm",
                "categoryRestrictions": "Zusatzangaben auf der Kategorie",
                "restrictionsA": "Zusatzangaben (A)",
                "restrictionsB": "Zusatzangaben (B)",
                "faberPin": "FABER-PIN",
                "licenceNumber": "Nummer des Lernfahrausweises"
              }
            },
            {
              "type": "spec/overlays/label/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "en",
              "attribute_labels": {
                "lastName": "Family name",
                "firstName": "First name",
                "dateOfBirth": "Date of birth",
                "hometown": "Place of origin",
                "dateOfExpiration": "Expiry date",
                "issuerEntity": "Issuing authority",
                "issuerEntityDate": "Date of issue",
                "signatureImage": "Signature",
                "photoImage": "Photo",
                "policeQRImage": "Police control, QR code",
                "categoryCode": "Category",
                "categoryIcon": "Category pictogram",
                "categoryRestrictions": "Additional information for the category",
                "restrictionsA": "Additional information (A)",
                "restrictionsB": "Additional information (B)",
                "faberPin": "FABER PIN",
                "licenceNumber": "Learner-driver permit number"
              }
            },
            {
              "type": "spec/overlays/label/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "fr",
              "attribute_labels": {
                "lastName": "Nom",
                "firstName": "Prénom",
                "dateOfBirth": "Date de naissance",
                "hometown": "Lieu d'origine",
                "dateOfExpiration": "Date d'échéance",
                "issuerEntity": "Autorité d'émission",
                "issuerEntityDate": "Date de délivrance",
                "signatureImage": "Signature",
                "photoImage": "Photo",
                "policeQRImage": "Contrôle de police, QR-code",
                "categoryCode": "Catégorie",
                "categoryIcon": "Catégorie pictogramme",
                "categoryRestrictions": "Indications complémentaires relatives à la catégorie",
                "restrictionsA": "Indications complémentaires (A)",
                "restrictionsB": "Indications complémentaires (B)",
                "faberPin": "NIP FABER",
                "licenceNumber": "Numéro du permis d'élève conducteur"
              }
            },
            {
              "type": "spec/overlays/label/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "it",
              "attribute_labels": {
                "lastName": "Cognome",
                "firstName": "Nome",
                "dateOfBirth": "Data di nascita",
                "hometown": "Luogo di origine",
                "dateOfExpiration": "Data di scadenza",
                "issuerEntity": "Autorità di rilascio",
                "issuerEntityDate": "Data di rilascio",
                "signatureImage": "Firma",
                "photoImage": "Foto",
                "policeQRImage": "Controllo della polizia, codice QR",
                "categoryCode": "Categoria",
                "categoryIcon": "Categoria pittogramma",
                "categoryRestrictions": "Dati supplementari relativi alla categoria",
                "restrictionsA": "Dati supplementari (A)",
                "restrictionsB": "Dati supplementari (B)",
                "faberPin": "PIN FABER",
                "licenceNumber": "Numero della licenza per allievo conducente"
              }
            },
            {
              "type": "spec/overlays/label/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "language": "rm",
              "attribute_labels": {
                "lastName": "Num",
                "firstName": "Prenum",
                "dateOfBirth": "Data da naschientscha",
                "hometown": "Lieu d’origin",
                "dateOfExpiration": "Data da scadenza",
                "issuerEntity": "Autoridad d’emissiun",
                "issuerEntityDate": "Data d’emissiun",
                "signatureImage": "Suttascripziun",
                "photoImage": "Fotografia",
                "policeQRImage": "Controlla da la polizia, code QR",
                "categoryCode": "Categoria",
                "categoryIcon": "Categoria pictogram",
                "categoryRestrictions": "Indicaziuns supplementaras davart la categoria",
                "restrictionsA": "Indicaziuns supplementaras (A)",
                "restrictionsB": "Indicaziuns supplementaras (B)",
                "faberPin": "PIN FABER",
                "licenceNumber": "Numer dal permiss per emprender a manischar"
              }
            },
            {
              "type": "extend/overlays/order/1.0",
              "capture_base": "ILFKGCCSyscqnYnMYl6QR-zD0UoNHuNqPpm9-5yiGMLz",
              "attribute_orders": {
                  "lastName": 3,
                  "firstName": 4,
                  "dateOfBirth": 5,
                  "hometown": 6,
                  "dateOfExpiration": 9,
                  "issuerEntity": 7,
                  "issuerEntityDate": 8,
                  "signatureImage": 17,
                  "policeQRImage": 1,
                  "photoImage": 2,
                  "categoryCode": 11,
                  "categoryIcon": 10,
                  "categoryRestrictions": 12,
                  "restrictionsA": 13,
                  "restrictionsB": 14,
                  "faberPin": 15,
                  "licenceNumber": 16
              }
            }
          ]
        }
    """.trimIndent()

    val complexNestedOca = """
        {
          "capture_bases":[
            {
              "type":"spec/capture_base/1.0",
              "digest":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "attributes":{
                "capture_base_1":"refs:IACad8m8doJZoyOwmkcSOGD0OKL6JoNtC22I1K4DlFMh",
                "array_capture_base":"Array[refs:ICFYTvQNUDNlVfS7_35nv1YpjDHx1EhlNqncFqd7zmyt]",
                "text_array_claim":"Array[Text]"
              }
            },
            {
              "type":"spec/capture_base/1.0",
              "digest":"IACad8m8doJZoyOwmkcSOGD0OKL6JoNtC22I1K4DlFMh",
              "attributes":{
                "capture_base_1_claim_1":"Text",
                "capture_base_1_claim_2":"Text",
                "capture_base_2":"refs:IMg6sVkwVROTddb1csCbOI83tufFvbMkwpbwImZ89joJ"
              }
            },
            {
              "type":"spec/capture_base/1.0",
              "digest":"IMg6sVkwVROTddb1csCbOI83tufFvbMkwpbwImZ89joJ",
              "attributes":{
                "capture_base_2_claim_1":"Text",
                "capture_base_2_claim_2":"Text",
                "capture_base_2_claim_3":"Text"
              }
            },
            {
              "type":"spec/capture_base/1.0",
              "digest":"ICFYTvQNUDNlVfS7_35nv1YpjDHx1EhlNqncFqd7zmyt",
              "attributes":{
                "array_capture_base_claim_1":"Text",
                "array_capture_base_claim_2":"Text"
              }
            }
          ],
          "overlays":[
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "array_capture_base":"${'$'}.array_capture_base",
                "text_array_claim":"${'$'}.textArrayClaim"
              }
            },
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IACad8m8doJZoyOwmkcSOGD0OKL6JoNtC22I1K4DlFMh",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "capture_base_1_claim_1":"${'$'}.capture_base_1_claim_1",
                "capture_base_1_claim_2":"${'$'}.capture_base_1_claim_2",
                "capture_base_2":"${'$'}.capture_base_2"
              }
            },
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IMg6sVkwVROTddb1csCbOI83tufFvbMkwpbwImZ89joJ",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "capture_base_2_claim_1":"${'$'}.capture_base_2.claim_1",
                "capture_base_2_claim_2":"${'$'}.capture_base_2.claim_2",
                "capture_base_2_claim_3":"${'$'}.capture_base_2.claim_3"
              }
            },
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"ICFYTvQNUDNlVfS7_35nv1YpjDHx1EhlNqncFqd7zmyt",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "array_capture_base_claim_1":"${'$'}.array_capture_base[*].claim_1",
                "array_capture_base_claim_2":"${'$'}.array_capture_base[*].claim_2"
              }
            },
            {
              "type":"aries/overlays/branding/1.1",
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "language":"de",
              "theme":"light",
              "logo":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color":"#007AFF",
              "primary_field":"de light: {{capture_base_1.capture_base_1_claim_1}} {{capture_base_1.capture_base_1_claim_2}}",
              "secondary_field":"de light: {{capture_base_1.capture_base_2.capture_base_2_claim_1}}"
            },
            {
              "type":"aries/overlays/branding/1.1",
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "language":"de",
              "theme":"dark",
              "logo":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color":"#FF8500",
              "primary_field":"de dark: {{capture_base_1.capture_base_1_claim_1}} {{capture_base_1.capture_base_1_claim_2}}",
              "secondary_field":"de dark: {{capture_base_1.capture_base_2.capture_base_2_claim_1}}"
            },
            {
              "type":"aries/overlays/branding/1.1",
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "language":"en",
              "theme":"light",
              "logo":"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABwAAAAcCAYAAAByDd+UAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAACPSURBVHgB7ZbbCYAwDEVvxEHcREdzlI7gCHUDN9AtfEJM0S9BUWwrSA5cWujHgdA2oWFcWhAyxKGjYVpYNpaZawSEiHJZCjhhP84lAuMczpUgMir8Tii32PBGiRdoSVWoQhX+UJjiOfnJf9pIV68QQFjsOWIkXoVGYi/OO9zgtlDKZeEBfRbeiT4IU+xRfwVePD+H6WV/zQAAAABJRU5ErkJggg==",
              "primary_background_color":"#007AEF",
              "primary_field":"en light: primary_field"
            },
            {
              "type":"spec/overlays/meta/1.0",
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "language":"de",
              "name":"Nested OCA bundle de"
            },
            {
              "type":"spec/overlays/meta/1.0",
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "language":"en",
              "name":"Nested OCA bundle en"
            },
            {
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "type":"extend/overlays/order/1.0",
              "attribute_orders":{
                "capture_base_1":1,
                "array_capture_base":2,
                "text_array_claim":3
              }
            },
            {
              "capture_base":"IACad8m8doJZoyOwmkcSOGD0OKL6JoNtC22I1K4DlFMh",
              "type":"extend/overlays/order/1.0",
              "attribute_orders":{
                "capture_base_2":3,
                "capture_base_1_claim_2":2,
                "capture_base_1_claim_1":1
              }
            },
            {
              "capture_base":"IMg6sVkwVROTddb1csCbOI83tufFvbMkwpbwImZ89joJ",
              "type":"extend/overlays/order/1.0",
              "attribute_orders":{
                "capture_base_2_claim_3":3,
                "capture_base_2_claim_1":1,
                "capture_base_2_claim_2":2
              }
            },
            {
              "capture_base":"ICFYTvQNUDNlVfS7_35nv1YpjDHx1EhlNqncFqd7zmyt",
              "type":"extend/overlays/order/1.0",
              "attribute_orders":{
                "array_capture_base_claim_1":1,
                "array_capture_base_claim_2":2
              }
            },
            {
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "type":"spec/overlays/label/1.0",
              "language":"de",
              "attribute_labels":{
                "capture_base_1":"capture_base_1 de",
                "array_capture_base":"array_capture_base de",
                "text_array_claim":"text_array_claim de"
              }
            },
            {
              "capture_base":"IK4ceQ-qvbporNvdFExEAMQrPud9OutHQbB2pc2iXrvW",
              "type":"spec/overlays/label/1.0",
              "language":"en",
              "attribute_labels":{
                "capture_base_1":"capture_base_1 en"
              }
            },
            {
              "capture_base":"IACad8m8doJZoyOwmkcSOGD0OKL6JoNtC22I1K4DlFMh",
              "type":"spec/overlays/label/1.0",
              "language":"de",
              "attribute_labels":{
                "capture_base_1_claim_1":"capture_base_1_claim_1 de",
                "capture_base_1_claim_2":"capture_base_1_claim_2 de",
                "capture_base_2":"capture_base_2 de"
              }
            },
            {
              "capture_base":"IMg6sVkwVROTddb1csCbOI83tufFvbMkwpbwImZ89joJ",
              "type":"spec/overlays/label/1.0",
              "language":"de",
              "attribute_labels":{
                "capture_base_2_claim_1":"capture_base_2_claim_1 de",
                "capture_base_2_claim_2":"capture_base_2_claim_2 de",
                "capture_base_2_claim_3":"capture_base_2_claim_3 de"
              }
            },
            {
              "capture_base":"IMg6sVkwVROTddb1csCbOI83tufFvbMkwpbwImZ89joJ",
              "type":"spec/overlays/label/1.0",
              "language":"fr",
              "attribute_labels":{
                "capture_base_2_claim_1":"capture_base_2_claim_1 fr",
                "capture_base_2_claim_2":"capture_base_2_claim_2 fr",
                "capture_base_2_claim_3":"capture_base_2_claim_3 fr"
              }
            },
            {
              "capture_base":"ICFYTvQNUDNlVfS7_35nv1YpjDHx1EhlNqncFqd7zmyt",
              "type":"spec/overlays/label/1.0",
              "language":"de",
              "attribute_labels":{
                "array_capture_base_claim_1":"array_capture_base_claim_1 de",
                "array_capture_base_claim_2":"array_capture_base_claim_2 de"
              }
            },
            {
              "capture_base":"ICFYTvQNUDNlVfS7_35nv1YpjDHx1EhlNqncFqd7zmyt",
              "type":"spec/overlays/label/1.0",
              "language":"en",
              "attribute_labels":{
                "array_capture_base_claim_1":"array_capture_base_claim_1 en",
                "array_capture_base_claim_2":"array_capture_base_claim_2 en"
              }
            }
          ]
        }
    """.trimIndent()

    val simpleNestedOca = """
        {
          "capture_bases":[
            {
              "type":"spec/capture_base/1.0",
              "digest":"IJngsgWBS-8m5IVEdatgnJkLrC5ftqR6nzLKwvcIh0St",
              "attributes":{
                "capture_base_1":"refs:IJt3I51rFWxKKu1wqDci9R1mpE3b-XJRnqta1NukyTQO",
                "capture_base_2":"refs:IPxI3Nf9dMzv5Q1_EvWbFN09ro1Bg_tMegPZvvqPmysN",
                "capture_base_3":"refs:IFuSBD6W7_wIB0PIX8B-_N1S_fSRUzrz2Aq6jjOTdGTa"
              }
            },
            {
              "type":"spec/capture_base/1.0",
              "digest":"IJt3I51rFWxKKu1wqDci9R1mpE3b-XJRnqta1NukyTQO",
              "attributes":{
                "capture_base_1_claim_1":"Text"
              }
            },
            {
              "type":"spec/capture_base/1.0",
              "digest":"IPxI3Nf9dMzv5Q1_EvWbFN09ro1Bg_tMegPZvvqPmysN",
              "attributes":{
                "capture_base_2_claim_1":"Text"
              }
            },
            {
              "type":"spec/capture_base/1.0",
              "digest":"IFuSBD6W7_wIB0PIX8B-_N1S_fSRUzrz2Aq6jjOTdGTa",
              "attributes":{
                "capture_base_3_claim_1":"Text"
              }
            }
          ],
          "overlays":[
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IJngsgWBS-8m5IVEdatgnJkLrC5ftqR6nzLKwvcIh0St",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "capture_base_3":"${'$'}.capture_base_3",
                "capture_base_2":"${'$'}.capture_base_2"
              }
            },
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IJt3I51rFWxKKu1wqDci9R1mpE3b-XJRnqta1NukyTQO",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "capture_base_1_claim_1":"${'$'}.capture_base_1_claim_1"
              }
            },
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IPxI3Nf9dMzv5Q1_EvWbFN09ro1Bg_tMegPZvvqPmysN",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "capture_base_2_claim_1":"${'$'}.capture_base_2_claim_1"
              }
            },
            {
              "type":"extend/overlays/data_source/1.0",
              "capture_base":"IFuSBD6W7_wIB0PIX8B-_N1S_fSRUzrz2Aq6jjOTdGTa",
              "format":"vc+sd-jwt",
              "attribute_sources":{
                "capture_base_3_claim_1":"${'$'}.capture_base_3_claim_1"
              }
            },
            {
              "capture_base":"IJngsgWBS-8m5IVEdatgnJkLrC5ftqR6nzLKwvcIh0St",
              "type":"extend/overlays/order/1.0",
              "attribute_orders":{
                "capture_base_1":1,
                "capture_base_2":2,
                "capture_base_3":3
              }
            },
            {
              "capture_base":"IJngsgWBS-8m5IVEdatgnJkLrC5ftqR6nzLKwvcIh0St",
              "type":"spec/overlays/label/1.0",
              "language":"de",
              "attribute_labels":{
                "capture_base_1":"capture_base_1 de",
                "capture_base_2":"capture_base_2 de",
                "capture_base_3":"capture_base_3 de"
              }
            }
          ]
        }
    """.trimIndent()

    const val DIGEST = "digest"
    const val LANGUAGE_EN = "en"
    const val LANGUAGE_DE = "de"
    const val ATTRIBUTE_KEY_FIRSTNAME = "firstname"
    const val ATTRIBUTE_LABEL_FIRSTNAME_EN = "Firstname"
    const val ATTRIBUTE_LABEL_FIRSTNAME_DE = "Vorname"
    const val ATTRIBUTE_KEY_AGE = "age"
    const val ATTRIBUTE_LABEL_AGE_EN = "Age"
    const val ATTRIBUTE_LABEL_AGE_DE = "Alter"
    const val CREDENTIAL_FORMAT = "vc+sd-jwt"
    const val JSON_PATH_FIRSTNAME = "$.firstname"
    const val JSON_PATH_AGE = "$.age"

    private const val VALID_DIGEST_HUMAN = "IDif6Jd863C_YYjp1cHFCTAUr1_TzZSS1l-pv21Q56qs"
    private const val ATTRIBUTE_KEY_LASTNAME = "lastname"
    const val ATTRIBUTE_KEY_ADDRESS_STREET = "address_street"
    private const val ATTRIBUTE_KEY_ADDRESS_CITY = "address_city"
    private const val ATTRIBUTE_KEY_ADDRESS_COUNTRY = "address_country"
    private const val ATTRIBUTE_KEY_PETS = "pets"
    private const val VALID_DIGEST_PET = "IKLvtGx1NU0007DUTTmI_6Zw-hnGRFicZ5R4vAxg4j2j"
    const val ATTRIBUTE_KEY_NAME = "name"
    private const val ATTRIBUTE_KEY_RACE = "race"
    private const val JSON_PATH_LASTNAME = "$.lastname"
    private const val JSON_PATH_ADDRESS_STREET = "$.address.street"
    private const val JSON_PATH_ADDRESS_CITY = "$.address.city"
    private const val JSON_PATH_ADDRESS_COUNTRY = "$.address.country"
    private const val JSON_PATH_PETS = "$.pets"
    private const val JSON_PATH_PETS_NAME = "$.pets[*].name"
    private const val JSON_PATH_PETS_RACE = "$.pets[*].race"
    private const val LOGO =
        "data:image/jpeg;base64,iVBORw0KGgoAAAANSUhEUgAAABoAAAAaCAYAAACpSkzOAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAAXNSR0IArs4c6QAAAARnQU1BAACxjwv8YQUAAAGbSURBVHgBvVaBUcMwDFQ4BjAbmA06QjYgGzQbkA1aJmg3yHWCwgSGCVImcJkg3UDYjVMUI6VxW/g7X+701kuWZTsAI0DEwo0GO9RuKMKpYGvdsH4uXALnqPE3DOFrhs8hFc5pizxyN2YCZyS9+5FYuWCfgYzZJYFUon2UuwMZ+xG7xO3gXKBQ95xwGyHIuxuvbhwY/oPo+WbSQAyKtDCGVtWM3aMifmXENcGnb3uqp6Q2NZHgEpnWDQl5rsJwxgS9NTBZ98ghEdgdcA6t3yOpJQtGSIUVekHN+DwJWsfSWSGLmsm2xWHti2iOEbQav6I3IYtPIqDdZwvDc3K0OY5W5EvQ2vUb2jJZaBLIogxL5pUcf9LC7gzZQPigJXFe4HmsyPw1sRvk9jKsjj4Fc5yOOfFTyDcLcEGfMR0VpACnlUvC4j+C9FjFulkURLuPhdvgIcuy08UbPxMabofBjRMHOsAfIS6db21fOgXXYe/K9kgNgxWFmr7A9dhMmoXD001h8OcvSPpLWkIKsLu3THC2yBxG7B48S5IoJb1vHubbPPxs2qsAAAAASUVORK5CYII="
    const val FORMAT = "utf-8"
    private const val STANDARD = "urn:ietf:rfc:2397"
    const val BASE64_ENCODING = "base64"
    const val UNKNOWN_ENCODING = "default encoding"
    const val META_NAME = "name"
    const val META_DESCRIPTION = "description"
    const val BRANDING_LIGHT_THEME = "light"
    const val BRANDING_DARK_THEME = "dark"
    const val BRANDING_LOGO = "logo"
    const val BRANDING_BACKGROUND_COLOR = "background color"
    const val BRANDING_PRIMARY_FIELD = "primary field"

    const val ENTRY_CODE_A = "a"
    const val ENTRY_CODE_B = "b"

    const val ENTRY_CODE_A_EN = "a en"
    const val ENTRY_CODE_A_DE = "a de"
    const val ENTRY_CODE_B_EN = "b en"
    const val ENTRY_CODE_B_DE = "b de"

    val simpleCaptureBase = CaptureBase1x0(
        digest = DIGEST,
        attributes = mapOf(
            ATTRIBUTE_KEY_FIRSTNAME to AttributeType.Text,
            ATTRIBUTE_KEY_AGE to AttributeType.Numeric,
        )
    )

    val ocaSimpleLabel = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            LabelOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_EN,
                attributeLabels = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to ATTRIBUTE_LABEL_FIRSTNAME_EN,
                    ATTRIBUTE_KEY_AGE to ATTRIBUTE_LABEL_AGE_EN,
                )
            ),
            LabelOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_DE,
                attributeLabels = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to ATTRIBUTE_LABEL_FIRSTNAME_DE,
                    ATTRIBUTE_KEY_AGE to ATTRIBUTE_LABEL_AGE_DE,
                )
            )
        )
    )

    val ocaSimpleDataSource = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            DataSourceOverlay1x0(
                captureBaseDigest = DIGEST,
                format = CREDENTIAL_FORMAT,
                attributeSources = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to JSON_PATH_FIRSTNAME,
                    ATTRIBUTE_KEY_AGE to JSON_PATH_AGE,
                )
            )
        )
    )

    val ocaSimpleFormat = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            FormatOverlay1x0(
                captureBaseDigest = DIGEST,
                attributeFormats = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to FORMAT
                )
            )
        )
    )

    val ocaSimpleMeta = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            MetaOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_EN,
                name = META_NAME,
                description = META_DESCRIPTION
            )
        )
    )

    val ocaSimpleBranding = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            BrandingOverlay1x1(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_EN,
                theme = BRANDING_LIGHT_THEME,
                logo = BRANDING_LOGO,
                primaryBackgroundColor = BRANDING_BACKGROUND_COLOR,
                primaryField = BRANDING_PRIMARY_FIELD,
            )
        )
    )

    val ocaSimpleStandard = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            StandardOverlay1x0(
                captureBaseDigest = DIGEST,
                attributeStandards = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to STANDARD
                )
            )
        )
    )

    val ocaSimpleEncoding = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            CharacterEncodingOverlay1x0(
                captureBaseDigest = DIGEST,
                defaultCharacterEncoding = UNKNOWN_ENCODING,
                attributeCharacterEncoding = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to BASE64_ENCODING
                )
            )
        )
    )

    val ocaSimpleEncodingNoDefault = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            CharacterEncodingOverlay1x0(
                captureBaseDigest = DIGEST,
                attributeCharacterEncoding = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to BASE64_ENCODING
                )
            )
        )
    )

    val ocaSimpleOrder = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            OrderOverlay1x0(
                captureBaseDigest = DIGEST,
                attributeOrders = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to 2,
                    ATTRIBUTE_KEY_AGE to 1,
                )
            )
        )
    )

    val ocaSimpleEntry = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            EntryOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_EN,
                attributeEntries = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to mapOf(ENTRY_CODE_A to ENTRY_CODE_A_EN),
                    ATTRIBUTE_KEY_AGE to mapOf(ENTRY_CODE_B to ENTRY_CODE_B_EN),
                )
            ),
            EntryOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_DE,
                attributeEntries = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to mapOf(ENTRY_CODE_A to ENTRY_CODE_A_DE),
                    ATTRIBUTE_KEY_AGE to mapOf(ENTRY_CODE_B to ENTRY_CODE_B_DE),
                )
            )
        )
    )

    val ocaSimple = OcaBundle(
        captureBases = listOf(simpleCaptureBase),
        overlays = listOf(
            LabelOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_EN,
                attributeLabels = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to ATTRIBUTE_LABEL_FIRSTNAME_EN,
                    ATTRIBUTE_KEY_AGE to ATTRIBUTE_LABEL_AGE_EN,
                )
            ),
            LabelOverlay1x0(
                captureBaseDigest = DIGEST,
                language = LANGUAGE_DE,
                attributeLabels = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to ATTRIBUTE_LABEL_FIRSTNAME_DE,
                    ATTRIBUTE_KEY_AGE to ATTRIBUTE_LABEL_AGE_DE,
                )
            ),
            DataSourceOverlay1x0(
                captureBaseDigest = DIGEST,
                format = CREDENTIAL_FORMAT,
                attributeSources = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to JSON_PATH_FIRSTNAME,
                    ATTRIBUTE_KEY_AGE to JSON_PATH_AGE,
                )
            )
        ),
        ocaClaimData = listOf(
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = DIGEST,
                flagged = false,
                name = ATTRIBUTE_KEY_FIRSTNAME,
                characterEncoding = null,
                dataSources = mapOf(
                    CREDENTIAL_FORMAT to JSON_PATH_FIRSTNAME
                ),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(
                    LANGUAGE_EN to ATTRIBUTE_LABEL_FIRSTNAME_EN,
                    LANGUAGE_DE to ATTRIBUTE_LABEL_FIRSTNAME_DE
                ),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Numeric,
                captureBaseDigest = DIGEST,
                flagged = false,
                name = ATTRIBUTE_KEY_AGE,
                characterEncoding = null,
                dataSources = mapOf(
                    CREDENTIAL_FORMAT to JSON_PATH_AGE
                ),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(
                    LANGUAGE_EN to ATTRIBUTE_LABEL_AGE_EN,
                    LANGUAGE_DE to ATTRIBUTE_LABEL_AGE_DE
                ),
                standard = null
            ),
        )
    )

    val ocaNested = OcaBundle(
        captureBases = listOf(
            CaptureBase1x0(
                digest = VALID_DIGEST_HUMAN,
                attributes = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to AttributeType.Text,
                    ATTRIBUTE_KEY_LASTNAME to AttributeType.Text,
                    ATTRIBUTE_KEY_ADDRESS_STREET to AttributeType.Text,
                    ATTRIBUTE_KEY_ADDRESS_CITY to AttributeType.Text,
                    ATTRIBUTE_KEY_ADDRESS_COUNTRY to AttributeType.Text,
                    ATTRIBUTE_KEY_PETS to AttributeType.Array(AttributeType.Reference(VALID_DIGEST_PET)),
                ),
            ),
            CaptureBase1x0(
                digest = VALID_DIGEST_PET,
                attributes = mapOf(
                    ATTRIBUTE_KEY_NAME to AttributeType.Text,
                    ATTRIBUTE_KEY_RACE to AttributeType.Text,
                )
            )
        ),
        overlays = listOf(
            DataSourceOverlay1x0(
                captureBaseDigest = VALID_DIGEST_HUMAN,
                format = CREDENTIAL_FORMAT,
                attributeSources = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to JSON_PATH_FIRSTNAME,
                    ATTRIBUTE_KEY_LASTNAME to JSON_PATH_LASTNAME,
                    ATTRIBUTE_KEY_ADDRESS_STREET to JSON_PATH_ADDRESS_STREET,
                    ATTRIBUTE_KEY_ADDRESS_CITY to JSON_PATH_ADDRESS_CITY,
                    ATTRIBUTE_KEY_ADDRESS_COUNTRY to JSON_PATH_ADDRESS_COUNTRY,
                    ATTRIBUTE_KEY_PETS to JSON_PATH_PETS,
                )
            ),
            DataSourceOverlay1x0(
                captureBaseDigest = VALID_DIGEST_PET,
                format = CREDENTIAL_FORMAT,
                attributeSources = mapOf(
                    ATTRIBUTE_KEY_NAME to JSON_PATH_PETS_NAME,
                    ATTRIBUTE_KEY_RACE to JSON_PATH_PETS_RACE,
                )
            ),
            LabelOverlay1x0(
                captureBaseDigest = VALID_DIGEST_HUMAN,
                language = LANGUAGE_EN,
                attributeLabels = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to "Firstname",
                    ATTRIBUTE_KEY_LASTNAME to "Lastname",
                    ATTRIBUTE_KEY_ADDRESS_STREET to "street name",
                    ATTRIBUTE_KEY_ADDRESS_CITY to "city name",
                    ATTRIBUTE_KEY_ADDRESS_COUNTRY to "country name",
                    ATTRIBUTE_KEY_PETS to "pets"
                )
            ),
            LabelOverlay1x0(
                captureBaseDigest = VALID_DIGEST_PET,
                language = LANGUAGE_EN,
                attributeLabels = mapOf(
                    ATTRIBUTE_KEY_NAME to "name",
                    ATTRIBUTE_KEY_RACE to "race",
                )
            ),
            BrandingOverlay1x1(
                captureBaseDigest = VALID_DIGEST_HUMAN,
                language = LANGUAGE_EN,
                logo = LOGO,
                primaryBackgroundColor = "#2C75E3",
                primaryField = "{{firstname}} {{lastname}} from {{address_country}}",
            ),
            MetaOverlay1x0(
                captureBaseDigest = VALID_DIGEST_HUMAN,
                language = LANGUAGE_EN,
                name = "Pet permit",
            ),
            OrderOverlay1x0(
                captureBaseDigest = VALID_DIGEST_HUMAN,
                attributeOrders = mapOf(
                    ATTRIBUTE_KEY_FIRSTNAME to 1,
                    ATTRIBUTE_KEY_LASTNAME to 2,
                    ATTRIBUTE_KEY_ADDRESS_STREET to 3,
                    ATTRIBUTE_KEY_ADDRESS_CITY to 4,
                    ATTRIBUTE_KEY_ADDRESS_COUNTRY to 5,
                    "pets" to 6,
                )
            ),
            OrderOverlay1x0(
                captureBaseDigest = VALID_DIGEST_PET,
                attributeOrders = mapOf(
                    ATTRIBUTE_KEY_RACE to 1,
                    ATTRIBUTE_KEY_NAME to 2,
                ),
            )
        ),
        ocaClaimData = listOf(
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_HUMAN,
                flagged = false,
                name = ATTRIBUTE_KEY_FIRSTNAME,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_FIRSTNAME),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_LABEL_FIRSTNAME_EN),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_HUMAN,
                flagged = false,
                name = ATTRIBUTE_KEY_LASTNAME,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_LASTNAME),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_KEY_LASTNAME),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_HUMAN,
                flagged = false,
                name = ATTRIBUTE_KEY_ADDRESS_STREET,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_ADDRESS_STREET),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_KEY_ADDRESS_STREET),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_HUMAN,
                flagged = false,
                name = ATTRIBUTE_KEY_ADDRESS_CITY,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_ADDRESS_CITY),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_KEY_ADDRESS_CITY),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_PET,
                flagged = false,
                name = ATTRIBUTE_KEY_PETS,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_PETS),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_KEY_PETS),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_PET,
                flagged = false,
                name = ATTRIBUTE_KEY_NAME,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_PETS_NAME),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_KEY_NAME),
                standard = null
            ),
            OcaClaimData(
                attributeType = AttributeType.Text,
                captureBaseDigest = VALID_DIGEST_PET,
                flagged = false,
                name = ATTRIBUTE_KEY_RACE,
                characterEncoding = null,
                dataSources = mapOf(CREDENTIAL_FORMAT to JSON_PATH_PETS_RACE),
                entryMappings = emptyMap(),
                format = null,
                labels = mapOf(LANGUAGE_EN to ATTRIBUTE_KEY_RACE),
                standard = null
            )
        )
    )
}
