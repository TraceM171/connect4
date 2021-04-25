package ipcconnect4.util;

import DBAccess.Connect4DAOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import model.Connect4;
import model.Player;

public class LocalPreferences {
    
    // Language
    private static final String LANG_KEY = "lang_key";
    private static final String LANG_DEF = new Locale("ca", "ES").toLanguageTag();
    // Dark mode
    private static final String IS_DARK_MODE_KEY = "dark_key";
    private static final boolean IS_DARK_MODE_DEF = false;
    // Player1
    private static final String P1_NICKNAME = "p1_nickname";
    private static final String P1_NICKNAME_DEF = "";
    // Player 2
    private static final String P2_NICKNAME = "p2_nickname";
    private static final String P2_NICKNAME_DEF = "";

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
    
    public Player getPlayer1() {
        String nickname = prefs.get(P1_NICKNAME, P1_NICKNAME_DEF);
        if (nickname.equals(P1_NICKNAME_DEF)) {
            return null;
        }
        try {
            return Connect4.getSingletonConnect4().getPlayer(nickname);
        } catch (Connect4DAOException ex) {
            Logger.getLogger(LocalPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void setPlayer1(Player newValue) {
         prefs.put(P1_NICKNAME, newValue == null ? "" : newValue.getNickName());
    }
    
    public Player getPlayer2() {
        String nickname = prefs.get(P2_NICKNAME, P2_NICKNAME_DEF);
        try {
            return Connect4.getSingletonConnect4().getPlayer(nickname);
        } catch (Connect4DAOException ex) {
            Logger.getLogger(LocalPreferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void setPlayer2(Player newValue) {
        prefs.put(P2_NICKNAME, newValue == null ? "" : newValue.getNickName());
    }
}
