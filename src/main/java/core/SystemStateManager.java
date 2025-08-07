package core;

/**
 * Verwaltet den globalen Status des Systems (gesperrt/freigeschaltet).
 * Dies ist ein Singleton, um einen konsistenten Status über die gesamte Anwendung hinweg zu gewährleisten.
 */
public class SystemStateManager {

    private static SystemStateManager instance;
    private boolean isSystemLocked = false;

    private SystemStateManager() {
        // Privater Konstruktor für das Singleton-Muster
    }

    public static synchronized SystemStateManager getInstance() {
        if (instance == null) {
            instance = new SystemStateManager();
        }
        return instance;
    }

    public synchronized boolean isSystemLocked() {
        return isSystemLocked;
    }

    public synchronized void setSystemLocked(boolean isLocked) {
        this.isSystemLocked = isLocked;
    }
}