package extractor.model;

public class _exception {
    private Integer exceptionid;

    private String name;

    private String type;

    private Integer communicationchannelid;

    private Integer faultmitigationid;

    private Integer faultdetectiionid;

    private Integer requirementid;

    private Integer stateid;

    private Integer taskid;

    private Integer syncinterfaceid;

    private Integer shareddataaccessid;

    public Integer getExceptionid() {
        return exceptionid;
    }

    public void setExceptionid(Integer exceptionid) {
        this.exceptionid = exceptionid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getCommunicationchannelid() {
        return communicationchannelid;
    }

    public void setCommunicationchannelid(Integer communicationchannelid) {
        this.communicationchannelid = communicationchannelid;
    }

    public Integer getFaultmitigationid() {
        return faultmitigationid;
    }

    public void setFaultmitigationid(Integer faultmitigationid) {
        this.faultmitigationid = faultmitigationid;
    }

    public Integer getFaultdetectiionid() {
        return faultdetectiionid;
    }

    public void setFaultdetectiionid(Integer faultdetectiionid) {
        this.faultdetectiionid = faultdetectiionid;
    }

    public Integer getRequirementid() {
        return requirementid;
    }

    public void setRequirementid(Integer requirementid) {
        this.requirementid = requirementid;
    }

    public Integer getStateid() {
        return stateid;
    }

    public void setStateid(Integer stateid) {
        this.stateid = stateid;
    }

    public Integer getTaskid() {
        return taskid;
    }

    public void setTaskid(Integer taskid) {
        this.taskid = taskid;
    }

    public Integer getSyncinterfaceid() {
        return syncinterfaceid;
    }

    public void setSyncinterfaceid(Integer syncinterfaceid) {
        this.syncinterfaceid = syncinterfaceid;
    }

    public Integer getShareddataaccessid() {
        return shareddataaccessid;
    }

    public void setShareddataaccessid(Integer shareddataaccessid) {
        this.shareddataaccessid = shareddataaccessid;
    }
}