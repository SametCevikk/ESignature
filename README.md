# E-Signature Spring Boot Project

## Description

The `E-Imza` project is a Spring Boot application that uses the TÜBİTAK UEKAE ESYA API to perform digital signing and verification in CAdES and PAdES formats. With smart card support, it provides a secure digital signature infrastructure.

## Features

- **CAdES (CMS Advanced Electronic Signatures)**
  - BES (Basic Electronic Signature) signing
  - Signed data verification
- **PAdES (PDF Advanced Electronic Signatures)**
  - Signing of PDF documents using BES
  - Verification of signed PDF documents
- Smart card (PKCS#11 or APDU) based signature and certificate management
- ESYA license control and version logging to console

## Requirements

- Java 8 or higher (OpenJDK / Oracle JDK)
- Maven 3.6+
- Spring Boot 2.x
- TÜBİTAK UEKAE ESYA API license (`lisans.xml`)
- Smart card drivers (supporting PKCS#11 or APDU)
- A signing certificate on the card (qualified or non-qualified)

## Configuration

The following constants are defined in the `SampleBase` class:

| Variable         | Description                                      |
| ---------------- | ------------------------------------------------ |
| `ROOT_DIR`       | Project root directory (e.g. `C:/Bimser/eImza`)  |
| `PIN_SMARTCARD`  | Smart card PIN code                             |
| `IS_QUALIFIED`   | Qualified certificate check (true/false)         |
| `policyFile`     | Path to the certificate validation policy file  |
| `testDataFolder` | Folder for test data                            |

You may customize these via environment variables or `application.properties`.

## API Endpoints

### CAdES Signing

- **URL**: `POST /cades/sign`
- **Body**:
  ```json
  {
    "file": "<Base64-encoded content>"
  }
  ```
- **Response**:
  ```json
  {
  "message": "Signing operation successful",
  "encodedSignedDocument": "<Base64-encoded signed content>"
  }

 ### CAdES Verification

- **URL**: `POST /cades/verify`
- **Body**:
  ```json
  {
    "file": "<Base64-encoded signed content>"
  }
  ```
- **Response**:
  Returns a plain text message indicating the verification result.

  ### PAdES Signing

- **URL**: `POST /pades/sign`
- **Body**:
  ```json
  {
    "filePath": "C:/files/sample.pdf"
  }
- **Response**:
  ```json
  {
  "message": "Signing operation successful",
  "encodedSignedDocument": "<Base64-encoded signed PDF>"
  }
- message: A success message confirming that the signing operation was successful.

- encodedSignedDocument: The Base64-encoded content of the signed PDF.

### PAdES Verification

- **URL**: `POST /pades/verify`
- **Body**:
  ```json
  {
    "filePath": "C:/files/signed-bes.pdf"
  }
- **Response**:
   Returns a plain text message indicating the verification result.

## Project Structure
```
├── src/main/java/com/bimser/eImza
│   ├── controller      # REST API endpoints
│   │   └── SignatureController.java: Defines HTTP endpoints for CAdES and PAdES signature operations.
│   ├── requests        # Request DTOs
│   │   ├── CadesSignRequest.java: DTO for CAdES signing. Accepts Base64-encoded file content.
│   │   └── PadesSignRequest.java: DTO for PAdES signing. Accepts a validated file path.
│   ├── responses       # Response DTOs
│   │   └── SignResponse.java: Includes a message and the Base64-encoded signed data.
│   ├── SmartCardManagerBase.java: Abstracts smart card connection and certificate selection operations.
│   ├── SmartCardManager.java: Manages signature creation and verification using smart card APIs (ESYA).
│   ├── SampleBase.java: Loads configuration files, license, and validation policies.
│   └── EImzaApplication.java: Main Spring Boot application entry point.
└── config
    ├── esya-signature-config.xml
    └── certval-policy.xml


