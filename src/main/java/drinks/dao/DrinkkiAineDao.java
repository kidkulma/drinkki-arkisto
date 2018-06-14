package drinks.dao;

import drinks.database.Database;
import drinks.domain.DrinkkiAine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DrinkkiAineDao implements Dao<DrinkkiAine, Integer> {

    private Database database;

    public DrinkkiAineDao(Database database) {
        this.database = database;
    }

    public void createTable() throws SQLException {
        //Luodaan taulu drinkkiaineille
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception e) {
            return;
        }
        PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS DrinkkiAine (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "jarjestys integer,\n"
                + "maara varchar(20),\n"
                + "ohje varchar(100),\n"
                + "drinkki_id integer,\n"
                + "aine_id integer,\n"
                + "FOREIGN KEY (drinkki_id) REFERENCES Drinkki(id) ON DELETE CASCADE,\n"
                + "FOREIGN KEY (aine_id) REFERENCES Aine(id) ON DELETE CASCADE);");
        stmt.execute();
        
        stmt.close();

        conn.close();
    }

    @Override
    public DrinkkiAine findOne(Integer key) throws SQLException {
        //Palautetaan kaikista drinkkiaineista se, jonka id on sama kuin haluttu
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return null;
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM DrinkkiAine WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        if (!hasOne) {
            return null;
        }

        DrinkkiAine a = new DrinkkiAine(rs.getInt("id"), rs.getInt("drinkki_id"), rs.getInt("aine_id"), rs.getInt("jarjestys"), rs.getString("maara"), rs.getString("ohje"));

        stmt.close();
        rs.close();

        conn.close();

        return a;
    }

    @Override
    public List<DrinkkiAine> findAll() throws SQLException {
        //Luodaan lista, johon lisätään drinkkiaineet
        List<DrinkkiAine> aineet = new ArrayList<>();

        //Lisätään drinkkiaineet tietokannasta listaan
        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, drinkki_id, aine_id, jarjestys, maara, ohje FROM DrinkkiAine").executeQuery()) {

            while (result.next()) {
                aineet.add(new DrinkkiAine(result.getInt("id"), result.getInt("drinkki_id"), result.getInt("aine_id"), result.getInt("jarjestys"), result.getString("maara"), result.getString("ohje")));
            }
        } catch (Exception ex) {
            return null;
        }

        return aineet;
    }

    @Override
    public DrinkkiAine saveOrUpdate(DrinkkiAine object) throws SQLException {
        //Määrä ja ohje eivät voi olla samanaikaisesti tyhjiä
        if (object.getMaara().isEmpty() && object.getOhje().isEmpty()) {
            throw new SQLException("Sekä määrä, että ohje eivät voi olla tyhjiä");
        }

        if (findOne(object.getId()) != null) {
            return update(object);
        } else {
            return save(object);
        }
    }

    private DrinkkiAine save(DrinkkiAine object) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO DrinkkiAine (drinkki_id, aine_id, jarjestys, maara, ohje) VALUES (?, ?, ?, ?, ?)");
            stmt.setInt(1, object.getDrinkkiId());
            stmt.setInt(2, object.getAineId());
            stmt.setInt(3, object.getJarjestys());
            stmt.setString(4, object.getMaara());
            stmt.setString(5, object.getOhje());
            stmt.executeUpdate();
        } catch (Exception ex) {
            return null;
        }

        return null;
    }

    private DrinkkiAine update(DrinkkiAine object) throws SQLException {
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("UPDATE DrinkkiAine SET jarjestys=? WHERE id=?");
            stmt.setInt(1, object.getJarjestys());
            stmt.setInt(2, object.getId());
            stmt.executeUpdate();
        } catch (Exception ex) {
            return null;
        }

        return null;
    }

    public void addAndUpdate(DrinkkiAine object) throws SQLException {
        List<DrinkkiAine> aineet = this.findAllByDrinkkiId(object.getDrinkkiId());
        for (int i = 0; i < aineet.size(); i++) {
            if (aineet.get(i).getJarjestys() >= object.getJarjestys()) {
                aineet.get(i).setJarjestys(aineet.get(i).getJarjestys() + 1);
            }
            this.saveOrUpdate(aineet.get(i));
        }
        this.saveOrUpdate(object);
    }

    @Override
    public void delete(Integer key) throws SQLException {
        //Poistetaan drinkkiaine tietokannasta annetulla id:llä
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return;
        }
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM DrinkkiAine WHERE id = ?");

        stmt.setInt(1, key);
        stmt.executeUpdate();
    }

    public void deleteAndUpdate(DrinkkiAine object) throws SQLException {
        List<DrinkkiAine> aineet = this.findAllByDrinkkiId(object.getDrinkkiId());
        for (int i = 0; i < aineet.size(); i++) {
            if (aineet.get(i).getJarjestys() > object.getJarjestys()) {
                aineet.get(i).setJarjestys(aineet.get(i).getJarjestys() - 1);
            }
            this.saveOrUpdate(aineet.get(i));
        }
        delete(object.getId());
    }

    public List<DrinkkiAine> findAllByAineId(Integer key) throws SQLException {
        //Luodaan lista, johon lisätään haettavat drinkkiaineet
        List<DrinkkiAine> aineet = new ArrayList<>();

        //Lisätään halutut drinkkiaineet tietokannasta listaan
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, drinkki_id, aine_id, jarjestys, maara, ohje FROM DrinkkiAine "
                    + "WHERE aine_id = ?");
            stmt.setInt(1, key);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                aineet.add(new DrinkkiAine(result.getInt("id"), result.getInt("drinkki_id"), result.getInt("aine_id"), result.getInt("jarjestys"), result.getString("maara"), result.getString("ohje")));
            }
        } catch (Exception ex) {
            return null;
        }

        return aineet;
    }

    public List<DrinkkiAine> findAllByDrinkkiId(Integer key) throws SQLException {
        //Luodaan lista, johon lisätään haettavat drinkkiaineet
        List<DrinkkiAine> aineet = new ArrayList<>();

        //Lisätään halutut drinkkiaineet tietokannasta listaan ja järjestä oikeaan järjestykseen
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, drinkki_id, aine_id, jarjestys, maara, ohje FROM DrinkkiAine "
                    + "WHERE drinkki_id = ? \n"
                    + "ORDER BY jarjestys");
            stmt.setInt(1, key);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                aineet.add(new DrinkkiAine(result.getInt("id"), result.getInt("drinkki_id"), result.getInt("aine_id"), result.getInt("jarjestys"), result.getString("maara"), result.getString("ohje")));
            }
        } catch (Exception ex) {
            return null;
        }

        return aineet;
    }

    public String getDrinkkiNimi(Integer key) throws SQLException {
        return new DrinkkiDao(database).findOne(key).getNimi();
    }

    public String getAineNimi(Integer key) throws SQLException {
        return new AineDao(database).findOne(key).getNimi();
    }

    public Integer getAineId(String nimi) throws SQLException {
        return new AineDao(database).findByName(nimi).getId();
    }

}
