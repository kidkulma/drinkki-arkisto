package drinks.drinkkilista;

import java.util.HashMap;
import spark.ModelAndView;
import spark.Spark;
import spark.template.thymeleaf.ThymeleafTemplateEngine;
import drinks.database.*;
import drinks.dao.*;
import drinks.domain.*;
import java.sql.SQLException;
import java.util.*;

public class Drinkkilista {

    public static void main(String[] args) throws Exception {

        if (System.getenv("PORT") != null) {
            Spark.port(Integer.valueOf(System.getenv("PORT")));
        }

        Database database = new Database("jdbc:sqlite:drinkit.db");
        AineDao aineet = new AineDao(database);
        DrinkkiDao drinkit = new DrinkkiDao(database);
        DrinkkiAineDao drinkkiAineet = new DrinkkiAineDao(database);
        aineet.createTable();
        drinkit.createTable();
        drinkkiAineet.createTable();
        List<String> virheet = new ArrayList<>();
        virheet.add("Aineksen nimi ei voi olla tyhjä.");
        virheet.add("Sekä ohje, että määrä eivät voi olla samanaikaisesti tyhjiä.");

        Spark.get("/", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("drinkit", drinkit.findAll());

            return new ModelAndView(map, "drinkit");
        }, new ThymeleafTemplateEngine());

        Spark.post("/", (req, res) -> {
            drinkit.saveOrUpdate(new Drinkki(-1, req.queryParams("nimi")));

            res.redirect("/");

            return "";
        });

        Spark.get("/drinkit/:id/delete", (req, res) -> {
            drinkit.delete(Integer.parseInt(req.params("id")));

            res.redirect("/");

            return "";
        });

        Spark.get("/drinkit/:id", (req, res) -> {
            HashMap map = new HashMap<>();

            List<DrinkkiAineData> drinkkiainedatat = new ArrayList<>();

            for (DrinkkiAine a : drinkkiAineet.findAllByDrinkkiId(Integer.parseInt(req.params("id")))) {
                drinkkiainedatat.add(new DrinkkiAineData(a.getId(), drinkkiAineet.getAineNimi(a.getAineId()), a.getJarjestys(), a.getMaara(), a.getOhje()));
            }

            map.put("drinkkiainedata", drinkkiainedatat);
            map.put("drinkki", drinkit.findOne(Integer.parseInt(req.params("id"))));
            map.put("aineet", aineet.findAll());

            return new ModelAndView(map, "drinkki");
        }, new ThymeleafTemplateEngine());

        Spark.post("/drinkit/:id", (req, res) -> {
            try {
                drinkkiAineet.addAndUpdate(new DrinkkiAine(-1, Integer.parseInt(req.params("id")), Integer.parseInt(req.queryParams("aine")), Integer.parseInt(req.queryParams("jarj")), req.queryParams("maara"), req.queryParams("ohje")));
            } catch (SQLException e) {
                res.redirect("/virhe/1");
            }

            res.redirect("/drinkit/" + req.params("id"));

            return "";
        });

        Spark.get("/drinkit/:id1/:id2/delete", (req, res) -> {
            drinkkiAineet.deleteAndUpdate(drinkkiAineet.findOne(Integer.parseInt(req.params("id2"))));
            res.redirect("/drinkit/" + req.params("id1"));

            return "";
        });

        Spark.get("/ainekset", (req, res) -> {
            HashMap map = new HashMap<>();
            map.put("aineet", aineet.findAll());

            return new ModelAndView(map, "ainekset");
        }, new ThymeleafTemplateEngine());

        Spark.post("/ainekset", (req, res) -> {
            try {
                aineet.saveOrUpdate(new Aine(-1, req.queryParams("nimi")));
            } catch (SQLException e) {
                res.redirect("/virhe/0");
            }

            res.redirect("/ainekset");

            return "";
        });

        Spark.get("/virhe/:id", (req, res) -> {
            HashMap map = new HashMap<>();

            map.put("virhe", virheet.get(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "virhe");
        }, new ThymeleafTemplateEngine());

        Spark.get("/ainekset/:id", (req, res) -> {
            HashMap map = new HashMap<>();

            List<DrinkkiAineData> drinkkiainedatat = new ArrayList<>();

            for (DrinkkiAine a : drinkkiAineet.findAllByAineId(Integer.parseInt(req.params("id")))) {
                drinkkiainedatat.add(new DrinkkiAineData(a.getDrinkkiId(), drinkkiAineet.getDrinkkiNimi(a.getDrinkkiId()), 1, "", ""));
            }

            map.put("drinkkiainedata", drinkkiainedatat);
            map.put("aine", aineet.findOne(Integer.parseInt(req.params("id"))));

            return new ModelAndView(map, "aines");
        }, new ThymeleafTemplateEngine());

        Spark.get("/ainekset/:id/delete", (req, res) -> {
            aineet.delete(Integer.parseInt(req.params("id")));

            res.redirect("/ainekset");

            return "";
        });

    }
}
