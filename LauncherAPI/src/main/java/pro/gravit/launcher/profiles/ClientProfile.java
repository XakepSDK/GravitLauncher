package pro.gravit.launcher.profiles;

import com.google.gson.*;
import pro.gravit.launcher.LauncherNetworkAPI;
import pro.gravit.launcher.hasher.FileNameMatcher;
import pro.gravit.launcher.profiles.optional.OptionalDepend;
import pro.gravit.launcher.profiles.optional.OptionalFile;
import pro.gravit.launcher.profiles.optional.triggers.OptionalTrigger;
import pro.gravit.utils.helper.IOHelper;
import pro.gravit.utils.helper.VerifyHelper;
import pro.gravit.utils.launch.LaunchOptions;

import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.util.*;

public final class ClientProfile implements Comparable<ClientProfile> {
    private static final FileNameMatcher ASSET_MATCHER = new FileNameMatcher(
            new String[0], new String[]{"indexes", "objects"}, new String[0]);

    @LauncherNetworkAPI
    private String title;
    @LauncherNetworkAPI
    private UUID uuid;
    @LauncherNetworkAPI
    private Version version;
    @LauncherNetworkAPI
    private String info;
    @LauncherNetworkAPI
    private String dir;
    @LauncherNetworkAPI
    private int sortIndex;
    @LauncherNetworkAPI
    private String assetIndex;
    @LauncherNetworkAPI
    private String assetDir;
    //  Updater and client watch service
    @LauncherNetworkAPI
    private List<String> update;
    @LauncherNetworkAPI
    private List<String> updateExclusions;
    @LauncherNetworkAPI
    private List<String> updateShared;
    @LauncherNetworkAPI
    private List<String> updateVerify;
    @LauncherNetworkAPI
    private Set<OptionalFile> updateOptional;
    @LauncherNetworkAPI
    private List<String> jvmArgs;
    @LauncherNetworkAPI
    private List<String> classPath;
    @LauncherNetworkAPI
    private List<String> modulePath = new ArrayList<>();
    @LauncherNetworkAPI
    private List<String> modules = new ArrayList<>();
    @LauncherNetworkAPI
    private List<String> altClassPath;
    @LauncherNetworkAPI
    private List<String> clientArgs;
    @LauncherNetworkAPI
    private List<String> compatClasses;
    @LauncherNetworkAPI
    private List<String> loadNatives;
    @LauncherNetworkAPI
    private Map<String, String> properties;
    @LauncherNetworkAPI
    private List<ServerProfile> servers;
    @LauncherNetworkAPI
    private ClassLoaderConfig classLoaderConfig;

    @LauncherNetworkAPI
    private List<CompatibilityFlags> flags;
    @LauncherNetworkAPI
    private int recommendJavaVersion = 8;
    @LauncherNetworkAPI
    private int minJavaVersion = 8;
    @LauncherNetworkAPI
    private int maxJavaVersion = 999;
    @LauncherNetworkAPI
    private ProfileDefaultSettings settings = new ProfileDefaultSettings();
    @LauncherNetworkAPI
    private boolean limited;
    // Client launcher
    @LauncherNetworkAPI
    private String mainClass;
    @LauncherNetworkAPI
    private String mainModule;
    @LauncherNetworkAPI
    private LaunchOptions.ModuleConf moduleConf;

    public ClientProfile() {
        update = new ArrayList<>();
        updateExclusions = new ArrayList<>();
        updateShared = new ArrayList<>();
        updateVerify = new ArrayList<>();
        updateOptional = new HashSet<>();
        jvmArgs = new ArrayList<>();
        classPath = new ArrayList<>();
        modulePath = new ArrayList<>();
        altClassPath = new ArrayList<>();
        clientArgs = new ArrayList<>();
        compatClasses = new ArrayList<>();
        properties = new HashMap<>();
        servers = new ArrayList<>(1);
        classLoaderConfig = ClassLoaderConfig.LAUNCHER;
        flags = new ArrayList<>();
    }

    public ClientProfile(List<String> update, List<String> updateExclusions, List<String> updateShared, List<String> updateVerify, Set<OptionalFile> updateOptional, List<String> jvmArgs, List<String> classPath, List<String> modulePath, List<String> modules, List<String> altClassPath, List<String> clientArgs, List<String> compatClasses, Map<String, String> properties, List<ServerProfile> servers, ClassLoaderConfig classLoaderConfig, List<CompatibilityFlags> flags, Version version, String assetIndex, String dir, String assetDir, int recommendJavaVersion, int minJavaVersion, int maxJavaVersion, ProfileDefaultSettings settings, int sortIndex, UUID uuid, String title, String info, String mainClass) {
        this.update = update;
        this.updateExclusions = updateExclusions;
        this.updateShared = updateShared;
        this.updateVerify = updateVerify;
        this.updateOptional = updateOptional;
        this.jvmArgs = jvmArgs;
        this.classPath = classPath;
        this.modulePath = modulePath;
        this.modules = modules;
        this.altClassPath = altClassPath;
        this.clientArgs = clientArgs;
        this.compatClasses = compatClasses;
        this.properties = properties;
        this.servers = servers;
        this.classLoaderConfig = classLoaderConfig;
        this.version = version;
        this.assetIndex = assetIndex;
        this.dir = dir;
        this.assetDir = assetDir;
        this.recommendJavaVersion = recommendJavaVersion;
        this.minJavaVersion = minJavaVersion;
        this.maxJavaVersion = maxJavaVersion;
        this.settings = settings;
        this.sortIndex = sortIndex;
        this.uuid = uuid;
        this.title = title;
        this.info = info;
        this.mainClass = mainClass;
        this.flags = flags;
    }

    public ServerProfile getDefaultServerProfile() {
        for (ServerProfile profile : servers) {
            if (profile.isDefault) return profile;
        }
        return null;
    }

    @Override
    public int compareTo(ClientProfile o) {
        return Integer.compare(getSortIndex(), o.getSortIndex());
    }

    public String getAssetIndex() {
        return assetIndex;
    }

    public FileNameMatcher getAssetUpdateMatcher() {
        return getVersion().compareTo(ClientProfileVersions.MINECRAFT_1_7_10) >= 0 ? ASSET_MATCHER : null;
    }

    public String[] getClassPath() {
        return classPath.toArray(new String[0]);
    }

    public List<String> getModulePath() {
        return Collections.unmodifiableList(modulePath);
    }

    public List<String> getModules() {
        return Collections.unmodifiableList(modules);
    }

    public String[] getAlternativeClassPath() {
        return altClassPath.toArray(new String[0]);
    }

    public String[] getClientArgs() {
        return clientArgs.toArray(new String[0]);
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getAssetDir() {
        return assetDir;
    }

    public List<String> getUpdateExclusions() {
        return Collections.unmodifiableList(updateExclusions);
    }

    public FileNameMatcher getClientUpdateMatcher(/*boolean excludeOptional*/) {
        String[] updateArray = update.toArray(new String[0]);
        String[] verifyArray = updateVerify.toArray(new String[0]);
        List<String> excludeList;
        //if(excludeOptional)
        //{
        //    excludeList = new ArrayList<>();
        //    excludeList.addAll(updateExclusions);
        //    excludeList.addAll(updateOptional);
        //}
        //else
        excludeList = updateExclusions;
        String[] exclusionsArray = excludeList.toArray(new String[0]);
        return new FileNameMatcher(updateArray, verifyArray, exclusionsArray);
    }

    public String[] getJvmArgs() {
        return jvmArgs.toArray(new String[0]);
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getMainModule() {
        return mainModule;
    }

    public LaunchOptions.ModuleConf getModuleConf() {
        return moduleConf;
    }

    public List<ServerProfile> getServers() {
        return servers;
    }

    public String getServerAddress() {
        ServerProfile profile = getDefaultServerProfile();
        return profile == null ? "localhost" : profile.serverAddress;
    }

    public Set<OptionalFile> getOptional() {
        return updateOptional;
    }

    public int getRecommendJavaVersion() {
        return recommendJavaVersion;
    }

    public int getMinJavaVersion() {
        return minJavaVersion;
    }

    public int getMaxJavaVersion() {
        return maxJavaVersion;
    }

    public ProfileDefaultSettings getSettings() {
        return settings;
    }

    public List<String> getLoadNatives() {
        return loadNatives;
    }

    public void updateOptionalGraph() {
        for (OptionalFile file : updateOptional) {
            if (file.dependenciesFile != null) {
                file.dependencies = new OptionalFile[file.dependenciesFile.length];
                for (int i = 0; i < file.dependenciesFile.length; ++i) {
                    file.dependencies[i] = getOptionalFile(file.dependenciesFile[i].name);
                }
            }
            if (file.conflictFile != null) {
                file.conflict = new OptionalFile[file.conflictFile.length];
                for (int i = 0; i < file.conflictFile.length; ++i) {
                    file.conflict[i] = getOptionalFile(file.conflictFile[i].name);
                }
            }
            if(file.groupFile != null) {
                file.group = new OptionalFile[file.groupFile.length];
                for(int i = 0; i < file.groupFile.length; ++i) {
                    file.group[i] = getOptionalFile(file.groupFile[i].name);
                }
            }
        }
    }

    public OptionalFile getOptionalFile(String file) {
        for (OptionalFile f : updateOptional)
            if (f.name.equals(file)) return f;
        return null;
    }

    public Collection<String> getShared() {
        return updateShared;
    }

    public int getServerPort() {
        ServerProfile profile = getDefaultServerProfile();
        return profile == null ? 25565 : profile.serverPort;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    @Deprecated
    public boolean isUpdateFastCheck() {
        return true;
    }

    @Override
    public String toString() {
        return String.format("%s (%s)", title, uuid);
    }

    public UUID getUUID() {
        return uuid;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean hasFlag(CompatibilityFlags flag) {
        return flags.contains(flag);
    }

    public void verify() {
        // Version
        getVersion();
        IOHelper.verifyFileName(getAssetIndex());

        // Client
        VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Profile title can't be empty");
        VerifyHelper.verify(getInfo(), VerifyHelper.NOT_EMPTY, "Profile info can't be empty");

        // Client launcher
        VerifyHelper.verify(getTitle(), VerifyHelper.NOT_EMPTY, "Main class can't be empty");
        if (getUUID() == null) {
            throw new IllegalArgumentException("Profile UUID can't be null");
        }
        for (String s : update) {
            if (s == null) throw new IllegalArgumentException("Found null entry in update");
        }
        for (String s : updateVerify) {
            if (s == null) throw new IllegalArgumentException("Found null entry in updateVerify");
        }
        for (String s : updateExclusions) {
            if (s == null) throw new IllegalArgumentException("Found null entry in updateExclusions");
        }

        for (String s : classPath) {
            if (s == null) throw new IllegalArgumentException("Found null entry in classPath");
        }
        for (String s : jvmArgs) {
            if (s == null) throw new IllegalArgumentException("Found null entry in jvmArgs");
        }
        for (String s : clientArgs) {
            if (s == null) throw new IllegalArgumentException("Found null entry in clientArgs");
        }
        for (String s : compatClasses) {
            if (s == null) throw new IllegalArgumentException("Found null entry in compatClasses");
        }
        for (OptionalFile f : updateOptional) {
            if (f == null) throw new IllegalArgumentException("Found null entry in updateOptional");
            if (f.name == null) throw new IllegalArgumentException("Optional: name must not be null");
            if (f.conflictFile != null) for (OptionalDepend s : f.conflictFile) {
                if (s == null)
                    throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.conflictFile", f.name));
            }
            if (f.dependenciesFile != null) for (OptionalDepend s : f.dependenciesFile) {
                if (s == null)
                    throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.dependenciesFile", f.name));
            }
            if(f.groupFile != null)
                for (OptionalDepend s : f.groupFile) {
                    if (s == null)
                        throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.groupFile", f.name));
            }
            if (f.triggersList != null) {
                for (OptionalTrigger trigger : f.triggersList) {
                    if (trigger == null)
                        throw new IllegalArgumentException(String.format("Found null entry in updateOptional.%s.triggers", f.name));
                }
            }
        }
    }

    public String getProperty(String name) {
        return properties.get(name);
    }

    public void putProperty(String name, String value) {
        properties.put(name, value);
    }

    public boolean containsProperty(String name) {
        return properties.containsKey(name);
    }

    public void clearProperties() {
        properties.clear();
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public List<String> getCompatClasses() {
        return Collections.unmodifiableList(compatClasses);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientProfile profile = (ClientProfile) o;
        return Objects.equals(uuid, profile.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    public ClassLoaderConfig getClassLoaderConfig() {
        return classLoaderConfig;
    }

    public void setClassLoaderConfig(ClassLoaderConfig classLoaderConfig) {
        this.classLoaderConfig = classLoaderConfig;
    }

    public boolean isLimited() {
        return limited;
    }

    public List<CompatibilityFlags> getFlags() {
        return flags;
    }

    public enum ClassLoaderConfig {
        AGENT, LAUNCHER, MODULE, SYSTEM_ARGS
    }

    public enum CompatibilityFlags {
        LEGACY_NATIVES_DIR, CLASS_CONTROL_API, ENABLE_HACKS, WAYLAND_USE_CUSTOM_GLFW
    }

    public static class Version implements Comparable<Version> {
        private final long[] data;
        private final String original;
        private final boolean isObjectSerialized;

        public static Version of(String string) {
            String tmp = string.replaceAll("[^.0-9]", "."); // Replace any non-digit character to .
            String[] list = tmp.split("\\.");
            return new Version(Arrays.stream(list)
                    .filter(e -> !e.isEmpty()) // Filter ".."
                    .mapToLong(Long::parseLong).toArray(), string);
        }

        private Version(long[] data, String str) {
            this.data = data;
            this.original = str;
            this.isObjectSerialized = false;
        }

        public Version(long[] data, String original, boolean isObjectSerialized) {
            this.data = data;
            this.original = original;
            this.isObjectSerialized = isObjectSerialized;
        }

        @Override
        public int compareTo(Version some) {
            int result = 0;
            if(data.length == some.data.length) {
                for (int i = 0; i < data.length; ++i) {
                    result = Long.compare(data[i], some.data[i]);
                    if (result != 0) return result;
                }
            } else if(data.length < some.data.length) {
                for (int i = 0; i < data.length; ++i) {
                    result = Long.compare(data[i], some.data[i]);
                    if (result != 0) return result;
                }
                for(int i = data.length; i < some.data.length;++i) {
                    if(some.data[i] > 0) return -1;
                }
            } else {
                for (int i = 0; i < some.data.length; ++i) {
                    result = Long.compare(data[i], some.data[i]);
                    if (result != 0) return result;
                }
                for(int i = some.data.length; i < data.length;++i) {
                    if(data[i] > 0) return 1;
                }
            }
            return result;
        }

        public String toCleanString() {
            return join(data);
        }

        private static String join(long[] data) {
            return String.join(".", Arrays.stream(data).mapToObj(String::valueOf).toArray(String[]::new));
        }

        @Override
        public String toString() {
            return original;
        }

        public static class GsonSerializer implements JsonSerializer<Version>, JsonDeserializer<Version> {

            @Override
            public Version deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                if(json.isJsonObject()) {
                    JsonObject object = json.getAsJsonObject();
                    String name = object.get("name").getAsString();
                    long[] list = context.deserialize(object.get("data"), long[].class);
                    return new Version(list, name, true);
                } else if(json.isJsonArray()) {
                    long[] list = context.deserialize(json, long[].class);
                    return new Version(list, join(list), false);
                } else {
                    return Version.of(json.getAsString());
                }
            }

            @Override
            public JsonElement serialize(Version src, Type typeOfSrc, JsonSerializationContext context) {
                if(src.isObjectSerialized) {
                    JsonObject object = new JsonObject();
                    object.add("name", new JsonPrimitive(src.original));
                    JsonArray array = new JsonArray();
                    for(long l : src.data) {
                        array.add(l);
                    }
                    object.add("data", array);
                    return object;
                }
                return new JsonPrimitive(src.toString());
            }
        }
    }

    public static class ServerProfile {
        public String name;
        public String serverAddress;
        public int serverPort;
        public boolean isDefault = true;
        public int protocol = -1;
        public boolean socketPing = true;

        public ServerProfile() {
        }

        public ServerProfile(String name, String serverAddress, int serverPort) {
            this.name = name;
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
        }

        public ServerProfile(String name, String serverAddress, int serverPort, boolean isDefault) {
            this.name = name;
            this.serverAddress = serverAddress;
            this.serverPort = serverPort;
            this.isDefault = isDefault;
        }

        public InetSocketAddress toSocketAddress() {
            return InetSocketAddress.createUnresolved(serverAddress, serverPort);
        }
    }

    public static class ProfileDefaultSettings {
        public int ram;
        public boolean autoEnter;
        public boolean fullScreen;
    }
}
