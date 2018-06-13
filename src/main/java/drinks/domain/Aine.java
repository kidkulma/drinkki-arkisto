
package drinks.domain;

public class Aine {
    private String nimi;
    private Integer id;
    
    public Aine(Integer id, String nimi) {
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
