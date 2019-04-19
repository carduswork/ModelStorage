package extractor.model;

public class Ports {
    private Integer id;

    private String portname;

    private String porttype;

    private Integer componentid;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPortname() {
        return portname;
    }

    public void setPortname(String portname) {
        this.portname = portname == null ? null : portname.trim();
    }

    public String getPorttype() {
        return porttype;
    }

    public void setPorttype(String porttype) {
        this.porttype = porttype == null ? null : porttype.trim();
    }

    public Integer getComponentid() {
        return componentid;
    }

    public void setComponentid(Integer componentid) {
        this.componentid = componentid;
    }
}