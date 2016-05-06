package at.cpickl.gadsu.view.swing

import javax.swing.JRootPane


fun JRootPane.enableSmallWindowStyle() {
    // https://developer.apple.com/library/mac/technotes/tn2007/tn2196.html#//apple_ref/doc/uid/DTS10004439
    putClientProperty("Window.style", "small")
}
