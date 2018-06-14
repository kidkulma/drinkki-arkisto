package drinks.dao;

import drinks.database.Database;
import drinks.domain.Aine;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AineDao implements Dao<Aine, Integer> {

    private Database database;

    public AineDao(Database database) {
        this.database = database;
    }

    public void createTable() throws SQLException {
        //Luodaan taulu aineksille
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return;
        }
        PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS Aine (\n"
                + "id SERIAL PRIMARY KEY,\n"
                + "nimi varchar(50));");
        stmt.execute();
        
        stmt.close();

        conn.close();
    }

    @Override
    public Aine findOne(Integer key) throws SQLException {
        //Palautetaan kaikista aineista se, jonka id on sama kuin haluttu
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return null;
        }
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Aine WHERE id = ?");
        stmt.setInt(1, key);

        ResultSet rs = stmt.executeQuery();
        boolean hasOne = rs.next();
        System.out.println("Haettavan aineen id: " + key);
        if (!hasOne) {
            throw new SQLException("EI KIVA");
        }

        Aine a = new Aine(rs.getInt("id"), rs.getString("nimi"));

        stmt.close();
        rs.close();

        conn.close();

        return a;
    }

    @Override
    public List<Aine> findAll() throws SQLException {
        //Luodaan lista, johon lisätään aineet
        List<Aine> aineet = new ArrayList<>();

        //Lisätään aineet tietokannasta listaan
        try (Connection conn = database.getConnection();
                ResultSet result = conn.prepareStatement("SELECT id, nimi FROM Aine").executeQuery()) {

            while (result.next()) {
                aineet.add(new Aine(result.getInt("id"), result.getString("nimi")));
            }
        } catch (Exception e) {
            return null;
        }

        return aineet;
    }

    @Override
    public Aine saveOrUpdate(Aine object) throws SQLException {
        //Jos lisättävän aineen nimi on tyhjä, annetaan virhe
        if (object.getNimi().isEmpty()) {
            throw new SQLException("Aineen nimi ei voi olla tyhjä.");
        }

        //Ainoastaan uuden nimisen aineen tallennus mahdollista
        Aine byName = findByName(object.getNimi());
        if (byName != null) {
            return byName;
        }

        //Jos saman nimistä ainetta ei vielä löydy, lisätään se tietokantaan
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO Aine (nimi) VALUES (?)");
            stmt.setString(1, object.getNimi());
            stmt.executeUpdate();
        } catch (Exception e) {
            return null;
        }

        return findByName(object.getNimi());
    }

    @Override
    public void delete(Integer key) throws SQLException {
        //Poistetaan Aine tietokannasta annetulla id:llä
        Connection conn;
        try {
            conn = database.getConnection();
        } catch (Exception ex) {
            return;
        }
        PreparedStatement stmt = conn.prepareStatement("DELETE FROM Aine WHERE id = ?");

        stmt.setInt(1, key);
        stmt.executeUpdate();
    }

    public Aine findByName(String nimi) throws SQLException {
        //Etsitään tietokannasta ainetta nimellä
        try (Connection conn = database.getConnection()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nimi FROM Aine WHERE nimi = ?");
            stmt.setString(1, nimi);

            ResultSet result = stmt.executeQuery();
            if (!result.next()) {
                return null;
            }

            return new Aine(result.getInt("id"), result.getString("nimi"));
        } catch (Exception e) {
            return null;
        }
    }

}
