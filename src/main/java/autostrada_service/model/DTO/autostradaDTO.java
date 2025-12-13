package autostrada_service.model.DTO;

public class autostradaDTO {

    private Integer id;       // id_autostrada
    private final String sigla;     // nome autostrada/città
    private final Integer idRegione;

    public autostradaDTO(Integer id, String sigla, Integer idRegione) {
        this.id = id;
        this.sigla = sigla;
        this.idRegione = idRegione;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getSigla() { return sigla; }

    public Integer getIdRegione() { return idRegione; }


}
