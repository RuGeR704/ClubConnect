package Application;

import java.io.Serializable;

public class GestioneSistemaBean implements Serializable{
    private boolean manutenzione;

    public boolean isManutenzione() {
        return manutenzione;
    }

    public void setManutenzione(boolean manutenzione) {
        this.manutenzione = manutenzione;
    }
}
