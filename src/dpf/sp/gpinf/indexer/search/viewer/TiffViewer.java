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

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.TIFFDecodeParam;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import javax.media.jai.PlanarImage;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/*
 * Viewer for TIF file formats.
 */
public class TiffViewer extends AbstractViewer {

    /**
     *
     */
    private static final long serialVersionUID = -7364831780786494299L;
    private JTextField pageCounter2 = new JTextField(3);
    private JLabel pageCounter3 = new JLabel(" of ");
    private JPanel imgPanel;
    private JScrollPane scrollPane;
    private volatile BufferedImage image = null;
    private volatile File currentFile;
    private int currentPage = 0;
    private int numPages = 0;
    private double zoomFactor = 1;

    public TiffViewer() {
        super(new BorderLayout());

        imgPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                if (image != null) {
                    int w = (int) (image.getWidth() * zoomFactor);
                    int h = (int) (image.getHeight() * zoomFactor);
                    g2.drawImage(image, 0, 0, w, h, null);
                }
            }
        };

        this.getPanel().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (image != null) {
                    fitWidth();
                    imgPanel.repaint();
                }
            }
        });

        scrollPane = new JScrollPane(imgPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        JPanel topPanel = initControlPanel();
        this.getPanel().add(topPanel, BorderLayout.NORTH);
        this.getPanel().add(scrollPane, BorderLayout.CENTER);

    }

    @Override
    public String getName() {
        return "TIFF";
    }

    @Override
    public boolean isSupportedType(String contentType) {
        return contentType.equals("image/tiff");
    }

    @Override
    public void copyScreen() {
        super.copyScreen(scrollPane);
    }

    private void fitWidth() {
        if (image != null) {
            zoomFactor = (imgPanel.getVisibleRect().getWidth()) / image.getWidth();
            Dimension d = new Dimension((int) imgPanel.getVisibleRect().getWidth(), (int) (image.getHeight() * zoomFactor));
            imgPanel.setPreferredSize(d);
        }
    }

    @Override
    public void loadFile(File file, Set<String> highlightTerms) {

        currentFile = file;
        currentPage = 1;
        image = null;
        refreshGUI();

        if (file != null) {
            displayPage(file);
        }


    }

    private void displayPage() {
        displayPage(currentFile);
    }

    private void displayPage(final File file) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                ImageDecoder dec = null;
                try {
                    TIFFDecodeParam param = null;
                    dec = ImageCodec.createImageDecoder("tiff", currentFile, param);
                    numPages = dec.getNumPages();
                    RenderedImage ri = dec.decodeAsRenderedImage(currentPage - 1);
                    BufferedImage tmp = PlanarImage.wrapRenderedImage(ri).getAsBufferedImage();
                    tmp = getCompatibleImage(tmp);
                    if (file != currentFile) {
                        return;
                    }
                    image = tmp;

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (dec != null) {
                            dec.getInputStream().close();
                        }
                    } catch (IOException ex) {
                    }
                }

                refreshGUI();

            }
        }).start();

    }

    private void refreshGUI() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                fitWidth();
                imgPanel.scrollRectToVisible(new Rectangle());
                imgPanel.revalidate();
                imgPanel.repaint();
                pageCounter2.setText(String.valueOf(currentPage));
                pageCounter3.setText(" of " + numPages);
            }
        });
    }

    private BufferedImage getCompatibleImage(BufferedImage image) {
        // obtain the current system graphical settings
        GraphicsConfiguration gfx_config = GraphicsEnvironment.
                getLocalGraphicsEnvironment().getDefaultScreenDevice().
                getDefaultConfiguration();

        /*
         * if image is already compatible and optimized for current system 
         * settings, simply return it
         */
        if (image.getColorModel().equals(gfx_config.getColorModel())) {
            return image;
        }

        // image is not optimized, so create a new image that is
        BufferedImage new_image = gfx_config.createCompatibleImage(
                image.getWidth(), image.getHeight(), image.getTransparency());

        // get the graphics context of the new image to draw the old image on
        Graphics2D g2d = (Graphics2D) new_image.getGraphics();

        // actually draw the image and dispose of context no longer needed
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        // return the new optimized image
        return new_image;
    }

    private JPanel initControlPanel() {

        JPanel topBar = new JPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        topBar.setMinimumSize(new Dimension());

        /**
         * back to page 1
         */
        JButton start = new JButton();
        start.setBorderPainted(false);
        start.setText("|<");
        topBar.add(start);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile != null && currentPage != 1) {
                    currentPage = 1;
                    displayPage();
                }
            }
        });


        /**
         * back icon
         */
        JButton back = new JButton();
        back.setBorderPainted(false);
        back.setText("<");
        topBar.add(back);
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile != null && currentPage > 1) {
                    currentPage -= 1;
                    displayPage();
                }
            }
        });

        pageCounter2.setEditable(true);
        pageCounter2.setToolTipText("Page");
        pageCounter2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent a) {

                String value = pageCounter2.getText().trim();
                int newPage;

                //allow for bum values
                try {
                    newPage = Integer.parseInt(value);

                    if ((newPage > numPages) || (newPage < 1)) {
                        return;
                    }

                    currentPage = newPage;
                    displayPage();

                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, '>' + value + "< is Not a valid Value.\nPlease enter a number between 1 and " + numPages);
                }

            }
        });

        topBar.add(new JPanel());
        topBar.add(pageCounter2);
        topBar.add(pageCounter3);
        //topBar.add(new JPanel());

        /**
         * forward icon
         */
        JButton forward = new JButton();
        forward.setBorderPainted(false);
        forward.setText(">");
        topBar.add(forward);
        forward.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile != null && currentPage < numPages) {
                    currentPage += 1;
                    displayPage();
                }
            }
        });

        /**
         * goto last page
         */
        JButton end = new JButton();
        end.setBorderPainted(false);
        end.setText(">|");
        topBar.add(end);
        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentFile != null && currentPage < numPages) {
                    currentPage = numPages;
                    displayPage();
                }
            }
        });


        return topBar;
    }

    @Override
    public void init() {
        // TODO Auto-generated method stub
    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
    }

    @Override
    public void scrollToNextHit(boolean forward) {
        // TODO Auto-generated method stub
    }
}
