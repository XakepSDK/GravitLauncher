package pro.gravit.launcher.events.request;

import pro.gravit.launcher.events.ExtendedTokenRequestEvent;
import pro.gravit.launcher.events.RequestEvent;

public class HardwareReportRequestEvent extends RequestEvent implements ExtendedTokenRequestEvent {
    public String extendedToken;
    public long expire;

    public HardwareReportRequestEvent() {
    }

    public HardwareReportRequestEvent(String extendedToken, long expire) {
        this.extendedToken = extendedToken;
        this.expire = expire;
    }

    @Override
    public String getType() {
        return "hardwareReport";
    }

    @Override
    public String getExtendedTokenName() {
        return "hardware";
    }

    @Override
    public String getExtendedToken() {
        return extendedToken;
    }

    @Override
    public long getExtendedTokenExpire() {
        return expire;
    }
}
