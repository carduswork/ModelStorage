package extractor.model;

public class linkpoint {
    private Integer linkpointid;

    private String name;

    private String period;

    private String postcondition;

    private String precondition;

    private Integer communicationchannelid;

    private Integer channel;

    private Integer requirementid;

    public Integer getLinkpointid() {
        return linkpointid;
    }

    public void setLinkpointid(Integer linkpointid) {
        this.linkpointid = linkpointid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period == null ? null : period.trim();
    }

    public String getPostcondition() {
        return postcondition;
    }

    public void setPostcondition(String postcondition) {
        this.postcondition = postcondition == null ? null : postcondition.trim();
    }

    public String getPrecondition() {
        return precondition;
    }

    public void setPrecondition(String precondition) {
        this.precondition = precondition == null ? null : precondition.trim();
    }

    public Integer getCommunicationchannelid() {
        return communicationchannelid;
    }

    public void setCommunicationchannelid(Integer communicationchannelid) {
        this.communicationchannelid = communicationchannelid;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Integer getRequirementid() {
        return requirementid;
    }

    public void setRequirementid(Integer requirementid) {
        this.requirementid = requirementid;
    }
}