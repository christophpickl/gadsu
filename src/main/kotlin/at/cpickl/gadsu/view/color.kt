package at.cpickl.gadsu.view

import java.awt.Color

/*
Color aColor = new Color(0xFF0096); // Use the hex number syntax
// Alternatively, use Color.decode
Color bColor = Color.decode("FF0096");
 */

fun Color.toHexString() = Integer.toHexString(this.rgb).substring(2)
// String hex = String.format("#%02x%02x%02x", r, g, b);
