
package drinks.domain;

public class DrinkkiAine {
    private Integer id;
    private Integer jarj;
    private String maara;
    private String ohje;
    private Integer drinkkiId;
    private Integer aineId;
    
    public DrinkkiAine(Integer id, Integer drinkki_id, Integer aine_id, Integer jarj, String maara, String ohje) {
        this.id = id;
        this.drinkkiId = drinkki_id;
        this.aineId = aine_id;
        this.jarj = jarj;
        this.maara = maara;
        this.ohje = ohje;
    }
    
    public Integer getId() {
        return id;
    }
    
    public Integer getDrinkkiId() {
        return drinkkiId;
    }
    
    public Integer getAineId() {
        return aineId;
    }
    
    public Integer getJarjestys() {
        return jarj;
    }
    
    public void setJarjestys(Integer jarj) {
        this.jarj = jarj;
    }
    
    public String getMaara() {
        return maara;
    }
    
    public String getOhje() {
        return ohje;
    }
    
}
