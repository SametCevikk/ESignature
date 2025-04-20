package com.bimser.eImza.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CadesSignResponse {
    private String massage;
    private String encodedSignedDocument;
}
