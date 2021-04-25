package ipcconnect4.util;

import java.util.Locale;
import java.util.prefs.Preferences;

public class LocalPreferences {
    
    private static final String LANG_KEY = "lang_key";
    private static final String LANG_DEF = new Locale("ca", "ES").toLanguageTag();
    private static final String IS_DARK_MODE_KEY = "dark_key";
    private static final boolean IS_DARK_MODE_DEF = false;

    private static LocalPreferences INSTANCE;

    public static LocalPreferences getInstance() {
        if (INSTANCE == null) {
            synchronized (LocalPreferences.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LocalPreferences();
                }
            }
        }
        return INSTANCE;
    }
    
    private final Preferences prefs;
    
    private LocalPreferences() {
        prefs = Preferences.userRoot().node(this.getClass().getName());
    }
    
    
    public Locale getLang() {
        return Locale.forLanguageTag(prefs.get(LANG_KEY, LANG_DEF));
    }
    
    public void setLang(Locale newValue) {
        prefs.put(LANG_KEY, newValue.toLanguageTag());
    }
    
    public boolean getIsDarkMode() {
        return prefs.getBoolean(IS_DARK_MODE_KEY, IS_DARK_MODE_DEF);
    }
    
    public void setIsDarkMode(boolean newValue) {
        prefs.putBoolean(IS_DARK_MODE_KEY, newValue);
    }
}
