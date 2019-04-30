package extractor.model;

public class syncinterface {
    private Integer syncinterfaceid;

    private String delay;

    private String paralist;

    private String return;

    public Integer getSyncinterfaceid() {
        return syncinterfaceid;
    }

    public void setSyncinterfaceid(Integer syncinterfaceid) {
        this.syncinterfaceid = syncinterfaceid;
    }

    public String getDelay() {
        return delay;
    }

    public void setDelay(String delay) {
        this.delay = delay == null ? null : delay.trim();
    }

    public String getParalist() {
        return paralist;
    }

    public void setParalist(String paralist) {
        this.paralist = paralist == null ? null : paralist.trim();
    }

    public String getReturn() {
        return return;
    }

    public void setReturn(String return) {
        this.return = return == null ? null : return.trim();
    }
}