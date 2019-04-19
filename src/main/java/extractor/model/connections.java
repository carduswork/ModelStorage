package extractor.model;

public class connections {
    private Integer idconnections;

    private String connectiontype;

    private Integer startcomponentid;

    private String startinterface;

    private Integer endcomponentid;

    private String endinterface;

    public Integer getIdconnections() {
        return idconnections;
    }

    public void setIdconnections(Integer idconnections) {
        this.idconnections = idconnections;
    }

    public String getConnectiontype() {
        return connectiontype;
    }

    public void setConnectiontype(String connectiontype) {
        this.connectiontype = connectiontype == null ? null : connectiontype.trim();
    }

    public Integer getStartcomponentid() {
        return startcomponentid;
    }

    public void setStartcomponentid(Integer startcomponentid) {
        this.startcomponentid = startcomponentid;
    }

    public String getStartinterface() {
        return startinterface;
    }

    public void setStartinterface(String startinterface) {
        this.startinterface = startinterface == null ? null : startinterface.trim();
    }

    public Integer getEndcomponentid() {
        return endcomponentid;
    }

    public void setEndcomponentid(Integer endcomponentid) {
        this.endcomponentid = endcomponentid;
    }

    public String getEndinterface() {
        return endinterface;
    }

    public void setEndinterface(String endinterface) {
        this.endinterface = endinterface == null ? null : endinterface.trim();
    }
}