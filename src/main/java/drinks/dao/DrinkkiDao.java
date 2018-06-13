package drinks.dao;

import drinks.database.Database;
import drinks.domain.Drinkki;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DrinkkiDao implements Dao<Drinkki, Integer> {

    private Database database;

    public DrinkkiDao(Database database) {
        this.database = database;
    }

    public void createTable() throws SQLException {
        //Luodaan taulu drinkeille
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return;
        }
        PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Drinkki (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "nimi varchar(50));");
        stmt.execute();
        
        stmt.close();

        conn.close();
    }

    @Override
    public Drinkki findOne(Integer key) throws SQLException {
        //Palautetaan kaikista drinkeistä se, jonka id on sama kuin haluttu
        return findAll().stream().filter(u -> u.getId().equals(key)).findFirst().get();
    }

    @Override
    public List<Drinkki> findAll() throws SQLException {
        //Luodaan lista, johon lisätään drinkit
        List<Drinkki> aineet = new ArrayList<>();

        //Lisätään drinkit tietokannasta listaan
        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, nimi FROM Drinkki").executeQuery()) {

            while (result.next()) {
                aineet.add(new Drinkki(result.getInt("id"), result.getString("nimi")));
            }
        } catch (Exception e) {
            return null;
        }

        return aineet;
    }

    @Override
    public Drinkki saveOrUpdate(Drinkki object) throws SQLException {
        //Jos lisättävän drinkin nimi on tyhjä, annetaan virhe
        if (object.getNimi().isEmpty()) {
            throw new SQLException("Drinkin nimi ei voi olla tyhjä.");
        }

        //Ainoastaan uuden nimisen drinkin tallennus mahdollista
        Drinkki byName = findByName(object.getNimi());
        if (byName != null) {
            return byName;
        }

        //Jos saman nimistä drinkkiä ei vielä löydy, lisätään se tietokantaan
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Drinkki (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
        } catch (Exception e) {
            return null;
        }

        return findByName(object.getNimi());
    }

    @Override
    public void delete(Integer key) throws SQLException {
        //Poistetaan drinkki tietokannasta annetulla id:llä
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return;
        }
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Drinkki WHERE id = ?");

        stmt.setInt(1, key);
        stmt.executeUpdate();
    }

    private Drinkki findByName(String nimi) throws SQLException {
        //Etsitään tietokannasta drinkkiä nimellä
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM Drinkki WHERE nimi = ?");
            stmt.setString(1, nimi);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Drinkki(result.getInt("id"), result.getString("nimi"));
        } catch (Exception e) {
            return null;
        }
    }

}
