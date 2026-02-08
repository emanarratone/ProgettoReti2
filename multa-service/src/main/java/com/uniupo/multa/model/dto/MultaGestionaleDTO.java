package com.uniupo.multa.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MultaGestionaleDTO {
    private Integer id;          // id_multa
    private String nomeRegione;  // Preso da Regione
    private String nomeCasello;  // Preso da casello
    private String targa;        // Preso da Multa
    private String data;         // timestamp_out da Pagamento
    private Double importo;      // importo da Multa
    private String stato;        // stato da Pagamento
}