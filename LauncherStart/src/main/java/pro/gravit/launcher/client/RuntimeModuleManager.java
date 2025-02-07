package pro.gravit.launcher.client;

import pro.gravit.launcher.ClientLauncherWrapper;
import pro.gravit.launcher.ClientWrapperModule;
import pro.gravit.launcher.Launcher;
import pro.gravit.launcher.LauncherTrustManager;
import pro.gravit.launcher.modules.LauncherModule;
import pro.gravit.launcher.modules.impl.SimpleModuleManager;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public final class RuntimeModuleManager extends SimpleModuleManager {
    public RuntimeModuleManager() {
        super(null, null, Launcher.getConfig().trustManager);
    }

    @Override
    public void autoload() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void autoload(Path dir) {
        throw new UnsupportedOperationException();
    }

    @Override
    public LauncherModule loadModule(LauncherModule module) {
        return super.loadModule(module);
    }

    public List<LauncherModule> getModules() {
        return Collections.unmodifiableList(modules);
    }

    @Override
    public final boolean verifyClassCheckResult(LauncherTrustManager.CheckClassResult result) {
        return result.type == LauncherTrustManager.CheckClassResultType.SUCCESS;
    }

    public void callWrapper(ClientLauncherWrapper.ClientLauncherWrapperContext context) {
        for (LauncherModule module : modules) {
            if (module instanceof ClientWrapperModule) {
                ((ClientWrapperModule) module).wrapperPhase(context);
            }
        }
    }
}
