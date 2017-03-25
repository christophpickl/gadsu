package org.openmechanics.htmleditor;

import java.awt.Color;
import java.util.Hashtable;

import javax.swing.JTextField;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: </p>
 *
 * @author unascribed
 * @version 1.0
 */

class Util {

    private static Hashtable<String, Color> HTMLColors = new Hashtable<>();

    static {
        HTMLColors.put("red", Color.red);
        HTMLColors.put("green", Color.green);
        HTMLColors.put("blue", Color.blue);
        HTMLColors.put("cyan", Color.cyan);
        HTMLColors.put("magenta", Color.magenta);
        HTMLColors.put("yellow", Color.yellow);
        HTMLColors.put("black", Color.black);
        HTMLColors.put("white", Color.white);
        HTMLColors.put("gray", Color.gray);
        HTMLColors.put("darkgray", Color.darkGray);
        HTMLColors.put("lightgray", Color.lightGray);
        HTMLColors.put("orange", Color.orange);
        HTMLColors.put("pink", Color.pink);
    }


    static Color getColorForName(String name, Color defaultColor) {
        if (HTMLColors.contains(name.toLowerCase()))
            return (Color) HTMLColors.get(name.toLowerCase());
        return defaultColor;
    }

    static Color decodeColor(String color, Color defaultColor) {
        String colorVal = "";
        if (color.length() > 0) {
            colorVal = color.trim();
            if (colorVal.startsWith("#"))
                colorVal = colorVal.substring(1);
            try {
                colorVal = Integer.toString(Integer.parseInt(colorVal, 16));
                return Color.decode(colorVal.toLowerCase());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else return defaultColor;
        return getColorForName(color, defaultColor);
    }

    static String encodeColor(Color color) {
        return "#" + Integer.toHexString(color.getRGB() - 0xFF000000).toUpperCase();
    }

    static Color decodeColor(String color) {
        return decodeColor(color, Color.white);
    }

    static void setBgcolorField(JTextField field) {
        Color c = Util.decodeColor(field.getText());
        field.setBackground(c);
        field.setForeground(new Color(~c.getRGB()));
    }

    static void setColorField(JTextField field) {
        Color c = Util.decodeColor(field.getText(), Color.black);
        field.setForeground(c);
        //field.setForeground(new Color(~c.getRGB()));
    }

}
