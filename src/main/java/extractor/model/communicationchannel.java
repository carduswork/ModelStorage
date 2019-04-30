package extractor.model;

public class communicationchannel {
    private Integer communicationchannelid;

    private String delay;

    private String mode;

    private String name;

    private Integer sourceid;

    private Integer destid;

    private String type;

    public Integer getCommunicationchannelid() {
        return communicationchannelid;
    }

    public void setCommunicationchannelid(Integer communicationchannelid) {
        this.communicationchannelid = communicationchannelid;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay == null ? null : delay.trim();
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode == null ? null : mode.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getSourceid() {
        return sourceid;
    }

    public void setSourceid(Integer sourceid) {
        this.sourceid = sourceid;
    }

    public Integer getDestid() {
        return destid;
    }

    public void setDestid(Integer destid) {
        this.destid = destid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }
}