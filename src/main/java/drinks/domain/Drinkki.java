
package drinks.domain;

public class Drinkki {
    private String nimi;
    private Integer id;
    
    public Drinkki(Integer id, String nimi) {
        this.nimi = nimi;
        this.id = id;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getNimi() {
        return nimi;
    }
}
