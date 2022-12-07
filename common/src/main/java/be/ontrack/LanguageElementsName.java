package be.ontrack;

public class LanguageElementsName {
    private AcceptedLanguages currentLang = AcceptedLanguages.nl;
    private String van = "Van:";
    private String naar = "Naar:";
    private String zoek = "Zoek";
    private String spoor = "Spoor:";

    public LanguageElementsName(AcceptedLanguages currentLang) {
        this.currentLang = currentLang;
        setVan();
        setNaar();
        setZoek();
        setSpoor();

    }

    public AcceptedLanguages getCurrentLang() {
        return currentLang;
    }

    public void setCurrentLang(AcceptedLanguages currentLang) {
        this.currentLang = currentLang;
        setVan();
        setNaar();
        setZoek();
        setSpoor();
    }

    public String getVan() {
        return van;
    }

    public void setVan() {
        switch (currentLang){
            case de:
                this.van = "Aus:";
                break;
            case en:
                this.van = "From:";
                break;
            case fr:
                this.van = "Départ:";
                break;
            case nl:
                this.van = "Van:";
                break;
            default:
                this.van = "Van:";
                break;
        }
    }

    public String getNaar() {
        return naar;
    }

    public void setNaar() {
        switch (currentLang){
            case de:
                this.naar = "Nach:";
                break;
            case en:
                this.naar = "To:";
                break;
            case fr:
                this.naar = "Arrivée:";
                break;
            case nl:
                this.naar = "Naar:";
                break;
            default:
                this.naar = "Naar:";
                break;
        }
    }

    public String getZoek() {
        return zoek;
    }

    public void setZoek() {
        switch (currentLang){
            case de:
                this.zoek = "Suche:";
                break;
            case en:
                this.zoek = "Search:";
                break;
            case fr:
                this.zoek = "Chercher:";
                break;
            case nl:
                this.zoek = "Zoeken:";
                break;
            default:
                this.zoek = "Zoeken:";
                break;
        }
    }

    public String getSpoor() {
        return spoor;
    }

    public void setSpoor() {
        switch (currentLang){
            case de:
                this.spoor = "Plattform:";
                break;
            case en:
                this.spoor = "Plattform:";
                break;
            case fr:
                this.spoor = "Voie:";
                break;
            case nl:
                this.spoor = "Spoor:";
                break;
            default:
                this.spoor = "Spoor:";
                break;
        }
    }
}
