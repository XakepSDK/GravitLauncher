package pro.gravit.utils.launch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LaunchOptions {
    public boolean enableHacks;
    public boolean disablePackageDelegateSupport;
    public ModuleConf moduleConf;


    public static final class ModuleConf {
        public List<String> modules = new ArrayList<>();
        public List<String> modulePath = new ArrayList<>();
        public Map<String, String> exports = new HashMap<>();
        public Map<String, String> opens = new HashMap<>();
        public Map<String, String> reads = new HashMap<>();
    }
}
