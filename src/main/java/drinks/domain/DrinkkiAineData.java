
package drinks.domain;

public class DrinkkiAineData {
    private Integer id;
    private String nimi;
    private Integer jarj;
    private String maara;
    private String ohje;
    
    public DrinkkiAineData(Integer id, String nimi, Integer jarj, String maara, String ohje) {
        this.id = id;
        this.nimi = nimi;
        this.jarj = jarj;
        this.maara = maara;
        this.ohje = ohje;
    }
    
    public Integer getId() {
        return id;
    }
    
    public String getNimi() {
        return nimi;
    }
    
    public Integer getJarjestys() {
        return jarj;
    }
    
    public String getMaara() {
        return maara;
    }
    
    public String getOhje() {
        return ohje;
    }
}
