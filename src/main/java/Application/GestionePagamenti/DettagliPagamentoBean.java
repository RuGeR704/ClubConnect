package Application.GestionePagamenti;

import java.io.Serializable;
import java.time.LocalDateTime;

public class DettagliPagamentoBean implements Serializable {
    private int id_pagamento;
    private int id_gruppo;
    private int id_metodo;
    private double importo;
    private LocalDateTime data_tansazione;

    public int getId_pagamento() {
        return id_pagamento;
    }

    public void setId_pagamento(int id_pagamento) {
        this.id_pagamento = id_pagamento;
    }

    public int getId_gruppo() {
        return id_gruppo;
    }

    public void setId_gruppo(int id_gruppo) {
        this.id_gruppo = id_gruppo;
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

    public LocalDateTime getData_tansazione() {
        return data_tansazione;
    }

    public void setData_tansazione(LocalDateTime data_tansazione) {
        this.data_tansazione = data_tansazione;
    }

}
