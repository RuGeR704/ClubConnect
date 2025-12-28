package Application.GestionePagamenti;

import java.io.Serializable;
import java.time.LocalDate;

public class DettagliPagamentoBean implements Serializable {
    private int id_pagamento;
    private int id_club;
    private int id_metodo;
    private double importo;
    private LocalDate data_tansazione;

    public int getId_pagamento() {
        return id_pagamento;
    }

    public void setId_pagamento(int id_pagamento) {
        this.id_pagamento = id_pagamento;
    }

    public int getId_club() {
        return id_club;
    }

    public void setId_club(int id_club) {
        this.id_club = id_club;
    }

    public int getId_metodo() {
        return id_metodo;
    }

    public void setId_metodo(int id_metodo) {
        this.id_metodo = id_metodo;
    }

    public double getImporto() {
        return importo;
    }

    public void setImporto(double importo) {
        this.importo = importo;
    }

    public LocalDate getData_tansazione() {
        return data_tansazione;
    }

    public void setData_tansazione(LocalDate data_tansazione) {
        this.data_tansazione = data_tansazione;
    }

}
