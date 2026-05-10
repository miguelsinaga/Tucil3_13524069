package gui;

import java.awt.*;


public class Theme {


    private static boolean dark = false;

    public static void setDark(boolean isDark) { dark = isDark; }
    public static boolean isDark() { return dark; }
    public static void toggle() { dark = !dark; }


    private static final Color L_BG         = new Color(0xF7F3FF); 
    private static final Color L_SURFACE    = new Color(0xFFFFFF);
    private static final Color L_SURFACE2   = new Color(0xEEE8FA); 
    private static final Color L_BORDER     = new Color(0xD6C9F0);


    private static final Color L_PURPLE     = new Color(0xB39DDB); 
    private static final Color L_TEAL       = new Color(0x80CBC4); 
    private static final Color L_PINK       = new Color(0xF48FB1); 
    private static final Color L_AMBER      = new Color(0xFFCC80); 
    private static final Color L_GREEN      = new Color(0xA5D6A7); 

    private static final Color L_TEXT       = new Color(0x2D2640); 
    private static final Color L_TEXT_SUB   = new Color(0x6B5F8A); 
    private static final Color L_TEXT_MUTED = new Color(0x9E8FBB);


    private static final Color L_SUCCESS    = new Color(0x81C784);
    private static final Color L_ERROR      = new Color(0xEF9A9A);
    private static final Color L_WARNING    = new Color(0xFFCC80);

    private static final Color D_BG         = new Color(0x1A1528); 
    private static final Color D_SURFACE    = new Color(0x241D38); 
    private static final Color D_SURFACE2   = new Color(0x2E2548); 
    private static final Color D_BORDER     = new Color(0x3D3358);

    private static final Color D_PURPLE     = new Color(0xCE93D8); 
    private static final Color D_TEAL       = new Color(0x80DEEA); 
    private static final Color D_PINK       = new Color(0xF48FB1); 
    private static final Color D_AMBER      = new Color(0xFFD54F); 
    private static final Color D_GREEN      = new Color(0xA5D6A7); 

    private static final Color D_TEXT       = new Color(0xEDE7F6); 
    private static final Color D_TEXT_SUB   = new Color(0xB39DDB); 
    private static final Color D_TEXT_MUTED = new Color(0x7E6FA8); 

    private static final Color D_SUCCESS    = new Color(0xA5D6A7);
    private static final Color D_ERROR      = new Color(0xEF9A9A);
    private static final Color D_WARNING    = new Color(0xFFCC80);


    public static Color BG()         { return dark ? D_BG         : L_BG;         }
    public static Color SURFACE()    { return dark ? D_SURFACE    : L_SURFACE;    }
    public static Color SURFACE2()   { return dark ? D_SURFACE2   : L_SURFACE2;   }
    public static Color BORDER()     { return dark ? D_BORDER     : L_BORDER;     }

    public static Color PURPLE()     { return dark ? D_PURPLE     : L_PURPLE;     }
    public static Color TEAL()       { return dark ? D_TEAL       : L_TEAL;       }
    public static Color PINK()       { return dark ? D_PINK       : L_PINK;       }
    public static Color AMBER()      { return dark ? D_AMBER      : L_AMBER;      }
    public static Color GREEN()      { return dark ? D_GREEN      : L_GREEN;      }

    public static Color TEXT()       { return dark ? D_TEXT       : L_TEXT;       }
    public static Color TEXT_SUB()   { return dark ? D_TEXT_SUB   : L_TEXT_SUB;   }
    public static Color TEXT_MUTED() { return dark ? D_TEXT_MUTED : L_TEXT_MUTED; }

    public static Color SUCCESS()    { return dark ? D_SUCCESS    : L_SUCCESS;    }
    public static Color ERROR()      { return dark ? D_ERROR      : L_ERROR;      }
    public static Color WARNING()    { return dark ? D_WARNING    : L_WARNING;    }

    
    public static Color tileWall()   { return dark ? new Color(0x3D3358) : new Color(0xC5B8E8); }
    public static Color tilePath()   { return dark ? new Color(0x2E2548) : new Color(0xF0EBFF); }
    public static Color tileLava()   { return dark ? new Color(0x7B2D2D) : new Color(0xFFCDD2); }
    public static Color tileGoal()   { return dark ? new Color(0x1B4A3A) : new Color(0xC8E6C9); }
    public static Color tileStart()  { return dark ? new Color(0x2D3A5A) : new Color(0xBBDEFB); }
    public static Color tileCP()     { return dark ? new Color(0x4A3B1E) : new Color(0xFFF9C4); }
    public static Color tileActor()  { return dark ? D_TEAL               : L_TEAL;             }
    public static Color tilePath2()  { return dark ? new Color(0x243040) : new Color(0xE3F2FD); } 

    private static Font _display = null;
    private static Font _body    = null;
    private static Font _mono    = null;

    static {
        _display = new Font("Georgia", Font.BOLD, 20);
        _body    = new Font("Segoe UI", Font.PLAIN, 13);
        _mono    = new Font("Consolas", Font.PLAIN, 13);

        if (!_body.getFamily().equals("Segoe UI"))
            _body = new Font("SansSerif", Font.PLAIN, 13);
        if (!_mono.getFamily().equals("Consolas"))
            _mono = new Font("Monospaced", Font.PLAIN, 13);
    }

    public static Font fontDisplay(float size) { return _display.deriveFont(Font.BOLD, size); }
    public static Font fontBody(float size)    { return _body.deriveFont(Font.PLAIN, size);   }
    public static Font fontBodyBold(float size){ return _body.deriveFont(Font.BOLD, size);    }
    public static Font fontMono(float size)    { return _mono.deriveFont(Font.PLAIN, size);   }

    public static final int RADIUS       = 14;   
    public static final int RADIUS_SM    = 8;
    public static final int PAD          = 16;   
    public static final int PAD_SM       = 10;
    public static final int PAD_XS       = 6;
    public static Color shadowColor() {
        return dark ? new Color(0, 0, 0, 80) : new Color(180, 160, 220, 60);
    }
    public static Color withAlpha(Color c, int alpha) {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public static GradientPaint bgGradient(int w, int h) {
        if (dark) {
            return new GradientPaint(0, 0, new Color(0x1A1528),
                                     w, h, new Color(0x0F1A2E));
        } else {
            return new GradientPaint(0, 0, new Color(0xF7F3FF),
                                     w, h, new Color(0xECF4FF));
        }
    }
}