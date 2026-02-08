package com.uniupo.multa.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagamentoDTO {
    private Long idPagamento;
    private Long idBiglietto;
    private String stato;
    private String timestampOut;
    private Integer caselloOut;
    private String targa;
    private Double importo;
}