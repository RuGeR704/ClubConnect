package Storage;

import Application.GestioneGruppo.AssociazioneBean;
import Application.GestioneGruppo.ClubBean;
import Application.GestioneGruppo.GruppoBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class GruppoDAO {
    public Collection<GruppoBean> doRetrieveAll(Connection con) throws SQLException {
        Collection<GruppoBean> gruppi = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM Gruppo")){
             try(ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                GruppoBean c;
                boolean tipo=rs.getBoolean("tipoGruppo");
                if(tipo){
                    ClubBean club= new ClubBean();
                    club.setImporto_retta(rs.getDouble("importo_retta"));
                    club.setFrequenza(rs.getInt("frequenza"));
                    c=club;
                }else{
                    c = new AssociazioneBean();
                }
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setNome(rs.getString("nome"));
                    c.setDescrizione(rs.getString("descrizione"));
                    c.setLogo(rs.getString("logo"));
                    c.setSede(rs.getString("sede"));
                    c.setSettore(rs.getString("settore"));
                    c.setRegole(rs.getString("regole"));
                    c.setSlogan(rs.getString("slogan"));
                    c.setStato(rs.getBoolean("stato"));
                    c.setTipoGruppo(tipo);
                gruppi.add(c);
            }
        }
        }
        return gruppi;
    }

    public GruppoBean doRetrieveByid(Connection con,int id_gruppo) throws SQLException {
        GruppoBean c=null;
        try (PreparedStatement ps = con.prepareStatement("SELECT * FROM Gruppo WHERE id_gruppo = ?")) {
            ps.setInt(1, id_gruppo);
            try(ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    boolean tipo=rs.getBoolean("tipoGruppo");
                    if(tipo){
                        ClubBean club= new ClubBean();
                        club.setImporto_retta(rs.getDouble("importo_retta"));
                        club.setFrequenza(rs.getInt("frequenza"));
                        c=club;
                    }else{
                        c = new AssociazioneBean();
                    }
                    c.setId_gruppo(rs.getInt("id_gruppo"));
                    c.setNome(rs.getString("nome"));
                    c.setDescrizione(rs.getString("descrizione"));
                    c.setLogo(rs.getString("logo"));
                    c.setSede(rs.getString("sede"));
                    c.setSettore(rs.getString("settore"));
                    c.setRegole(rs.getString("regole"));
                    c.setSlogan(rs.getString("slogan"));
                    c.setStato(rs.getBoolean("stato"));
                    c.setTipoGruppo(tipo);
                }
            }
        }
        return c;
    }

    public void doUpdate(Connection con, GruppoBean gruppo) throws SQLException {
        String sql = "UPDATE Gruppo SET nome=?, descrizione=?, logo=?, sede=?, settore=?, regole=?, slogan=?, stato=?, tipoGruppo=?, importo_retta=?, frequenza=? WHERE id_gruppo=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, gruppo.getNome());
            ps.setString(2, gruppo.getDescrizione());
            ps.setString(3, gruppo.getLogo());
            ps.setString(4, gruppo.getSede());
            ps.setString(5, gruppo.getSettore());
            ps.setString(6, gruppo.getRegole());
            ps.setString(7, gruppo.getSlogan());
            ps.setBoolean(8, gruppo.isStato());
            ps.setBoolean(9, gruppo.isTipoGruppo());
            if (gruppo instanceof ClubBean) {
                ClubBean c = (ClubBean) gruppo;
                ps.setDouble(10, c.getImporto_retta());
                ps.setInt(11, c.getFrequenza());
            } else {
                ps.setNull(10, java.sql.Types.DOUBLE);
                ps.setNull(11, java.sql.Types.INTEGER);
            }
            ps.setInt(12, gruppo.getId_gruppo());
            ps.executeUpdate();
        }
    }

    public void doDelete(Connection con, int id) throws SQLException {
        String sql = "DELETE FROM Gruppo WHERE id_gruppo = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public void doSave(Connection con, GruppoBean gruppo) throws SQLException {
        try(PreparedStatement ps = con.prepareStatement("INSERT INTO Gruppo(nome,descrizione,logo,sede,settore,regole,slogan,stato,tipoGruppo,importo_retta,frequenza) VALUES(?,?,?,?,?,?,?,?,?,?,?)")) {
                ps.setString(1, gruppo.getNome());
                ps.setString(2, gruppo.getDescrizione());
                ps.setString(3, gruppo.getLogo());
                ps.setString(4, gruppo.getSede());
                ps.setString(5, gruppo.getSettore());
                ps.setString(6, gruppo.getRegole());
                ps.setString(7, gruppo.getSlogan());
                ps.setBoolean(8, gruppo.isStato());
                ps.setBoolean(9, gruppo.isTipoGruppo());
            if (gruppo instanceof ClubBean) {
                ClubBean c = (ClubBean) gruppo;
                ps.setDouble(10,c.getImporto_retta());
                ps.setInt(11, c.getFrequenza());
            }
            if(gruppo instanceof AssociazioneBean) {
                ps.setNull(10,java.sql.Types.DOUBLE);
                ps.setNull(11,java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
        }

    }
}
