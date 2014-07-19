/*
 * Copyright 2014, Luis Filipe Nassif
 * 
 * This file is part of MultiContentViewer.
 *
 * MultiContentViewer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MultiContentViewer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with MultiContentViewer.  If not, see <http://www.gnu.org/licenses/>.
 */
package dpf.sp.gpinf.indexer.search.viewer;

import java.awt.Component;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.swing.JPanel;

/*
 * Abstract class defining default members and methods to a file viewer.
 * Extend this class to implement a viewer for one or more specific
 * formats/mimetypes.
 */
public abstract class AbstractViewer {

    private static final long serialVersionUID = 1L;

    /*
     * Takes a screenshot of the specifiednd component
     */
    protected static void copyScreen(Component comp) {
        BufferedImage image = new BufferedImage(
                comp.getWidth(), comp.getHeight(), BufferedImage.TYPE_INT_RGB);

        comp.paint(image.getGraphics());
        TransferableImage trans = new TransferableImage(image);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(trans, trans);
    }
    private JPanel panel;
    protected int currentHit, totalHits;

    /*
     * Default constructor.
     */
    public AbstractViewer() {
        panel = new JPanel();
    }

    /*
     * Constructor with a layout parameter.
     */
    public AbstractViewer(LayoutManager layout) {
        panel = new JPanel(layout);
    }

    /*
     * Get the internal panel where the file will be rendered.
     */
    public JPanel getPanel() {
        return panel;
    }

    /*
     * Returns the name of the viewer.
     */
    abstract public String getName();

    /* 
     * Returns true only if the viewer supports the specified mimetype
     */
    abstract public boolean isSupportedType(String mimeType);

    /*
     * Initializes the viewer. This was designed to be called outside of the
     * EDT thread because initialization can be slow. Subclasses must honor
     * this contract.
     */
    abstract public void init();

    /*
     * Dispose the resources used by this viewer.
     */
    abstract public void dispose();

    /*
     * Renders the file and highlights the supplied terms. Must accept
     * a null file to clear the preview. Subclasses may override this if they 
     * can use the file mimetype to help the rendering process.
     */
    public void loadFile(File file, String mimetype, Set<String> highlightTerms) {
        loadFile(file, highlightTerms);
    }

    /*
     * Renders the file. Must accept a null file to clear the preview.
     */
    public void loadFile(File file) {
        loadFile(file, null);
    }

    /*
     * Renders the file and highlights the supplied terms. Must accept
     * a null file to clear the preview.
     */
    abstract public void loadFile(File file, Set<String> highlightTerms);

    /*
     * Navigate to the next hightlighted term if parameter is true, or to the
     * previous one if parameter is false.
     */
    abstract public void scrollToNextHit(boolean forward);

    /*
     * Takes a screenshot of the file preview.
     */
    public void copyScreen() {
        copyScreen(this.getPanel());
    }

    public static class TransferableImage implements Transferable, ClipboardOwner {

        Image i;

        public TransferableImage(Image i) {
            this.i = i;
        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor.equals(DataFlavor.imageFlavor) && i != null) {
                return i;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            DataFlavor[] flavors = new DataFlavor[1];
            flavors[ 0] = DataFlavor.imageFlavor;
            return flavors;
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            DataFlavor[] flavors = getTransferDataFlavors();
            for (int i = 0; i < flavors.length; i++) {
                if (flavor.equals(flavors[ i])) {
                    return true;
                }
            }

            return false;
        }

        @Override
        public void lostOwnership(Clipboard arg0, Transferable arg1) {
            System.out.println("Lost Clipboard Ownership");

        }
    }
}
