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

import dpf.sp.gpinf.indexer.util.MultiContentViewerHelper;
import dpf.sp.gpinf.indexer.util.NoInternetPolicy;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Policy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.tika.Tika;
import org.openide.nodes.Node;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.corecomponentinterfaces.DataContentViewer;
import org.sleuthkit.datamodel.AbstractFile;
import org.sleuthkit.datamodel.BlackboardArtifact;
import org.sleuthkit.datamodel.BlackboardAttribute;
import org.sleuthkit.datamodel.ReadContentInputStream;
import org.sleuthkit.datamodel.TskCoreException;

/*
 * DataContentViewer for dozens of file formats.
 */
@ServiceProvider(service = DataContentViewer.class)
public class MultiContentViewer implements ActionListener, DataContentViewer {

    /**
     *
     */
    private static final long serialVersionUID = -2751185904521769139L;
    private final static String PREV_HIT_TIP = "Previous";
    private final static String NEXT_HIT_TIP = "Next";
    private static MultiContentViewer instance;

    public static MultiContentViewer getInstance() {
        return instance;
    }
    ArrayList<AbstractViewer> viewerList = new ArrayList<AbstractViewer>();
    JPanel cardViewer, panel;
    AbstractViewer viewerToUse;
    volatile File file, prevFile1, prevFile2;
    volatile String contentType;
    Set<String> highlightTerms;
    String mimeType;
    JPanel topPanel;
    JButton prevHit, nextHit;
    Tika tika = new Tika();

    public MultiContentViewer() {

        panel = new JPanel(new BorderLayout());
        instance = this;

        prevHit = new JButton("<");
        prevHit.setToolTipText(PREV_HIT_TIP);
        nextHit = new JButton(">");
        nextHit.setToolTipText(NEXT_HIT_TIP);
        prevHit.addActionListener(this);
        nextHit.addActionListener(this);
        JPanel navHit = new JPanel(new GridLayout());
        navHit.add(new JLabel("    Hits:"));
        navHit.add(prevHit);
        navHit.add(nextHit);

        topPanel = new JPanel(new BorderLayout());
        topPanel.add(navHit, BorderLayout.WEST);

        cardViewer = new JPanel(new CardLayout());

        panel.add(cardViewer, BorderLayout.CENTER);

        new MultiContentViewerHelper().addViewers(this);

        Policy.setPolicy(new NoInternetPolicy());

    }

    public void addViewer(AbstractViewer viewer) {
        viewerList.add(viewer);
        cardViewer.add(viewer.getPanel(), viewer.getName());

    }

    public void initViewers() {
        for (AbstractViewer viewer : viewerList) {
            viewer.init();
        }
    }

    public void clear() {
        if (viewerToUse != null) {
            viewerToUse.loadFile(null);
        }
        file = null;
    }

    public void dispose() {
        for (AbstractViewer viewer : viewerList) {
            viewer.dispose();
        }
    }

    public void loadFile(File file, String contentType, Set<String> highlightTerms) {
        this.file = file;
        this.contentType = contentType;
        this.highlightTerms = highlightTerms;

        for (AbstractViewer viewer : viewerList) {
            if (viewer.isSupportedType(contentType)) {
                viewerToUse = viewer;
                break;
            }
        }

        loadFile();


    }

    private void loadFile() {

        if (file != prevFile1 && viewerToUse != null) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    CardLayout layout = (CardLayout) cardViewer.getLayout();
                    layout.show(cardViewer, viewerToUse.getName());
                }
            });

            viewerToUse.loadFile(file, contentType, highlightTerms);

            if (prevFile2 != null) {
                prevFile2.delete();
            }
            prevFile2 = prevFile1;

            if (prevFile1 != null) {
                prevFile1.delete();
            }
            prevFile1 = file;
        }

    }

    @Override
    public void actionPerformed(ActionEvent evt) {

        AbstractViewer viewerToScroll = viewerToUse;

        if (evt.getSource() == prevHit) {
            viewerToScroll.scrollToNextHit(false);

        } else if (evt.getSource() == nextHit) {
            viewerToScroll.scrollToNextHit(true);
        }

    }
    
    private String getCustomMimeType(String name){
        
        if(name.endsWith(".cdr"))
            return "image/x-cdr";
        else if(name.endsWith(".dbf"))
            return "text/x-dbf";
        else return null;
    }

    private String getMimeType(Node node) {

        AbstractFile abstractFile = node.getLookup().lookup(AbstractFile.class);
        if (abstractFile != null) {
            String name = abstractFile.getName().toLowerCase();
            mimeType = getCustomMimeType(name);
            if(mimeType != null)
                return mimeType;
            
            ArrayList<BlackboardAttribute> attributes;
            try {
                attributes = abstractFile.getGenInfoAttributes(BlackboardAttribute.ATTRIBUTE_TYPE.TSK_FILE_TYPE_SIG);
                for (BlackboardAttribute attribute : attributes) {
                    return attribute.getValueString();
                }

            } catch (TskCoreException ex) {
                //Exceptions.printStackTrace(ex);
            }
            return tika.detect(name);

        } else {
            return "";
        }
    }

    private void getHighlighTerms(Node node) {

        highlightTerms = new HashSet<String>();

        Collection<? extends BlackboardArtifact> artifacts = node.getLookup().lookupAll(BlackboardArtifact.class);
        for (BlackboardArtifact artifact : artifacts) {
            if (artifact.getArtifactTypeID() == BlackboardArtifact.ARTIFACT_TYPE.TSK_KEYWORD_HIT.getTypeID()) {
                try {
                    for (BlackboardAttribute attr : artifact.getAttributes()) {
                        if (attr.getAttributeTypeID() == BlackboardAttribute.ATTRIBUTE_TYPE.TSK_KEYWORD.getTypeID()) {
                            String keyword = attr.getValueString();
                            highlightTerms.add(keyword);
                        }
                    }
                } catch (TskCoreException ex) {
                    //Exceptions.printStackTrace(ex);
                }
            }
        }

    }

    @Override
    public void setNode(org.openide.nodes.Node node) {

        if (node == null) {
            return;
        }

        AbstractFile abstractFile = node.getLookup().lookup(AbstractFile.class);
        if (abstractFile == null) {
            return;
        }

        getHighlighTerms(node);
        if (highlightTerms.size() > 0) {
            panel.add(topPanel, BorderLayout.NORTH);
        } else {
            panel.remove(topPanel);
        }

        Logger.getLogger(MultiContentViewer.class.getName()).log(Level.INFO, abstractFile.getName());

        try {
            File file = File.createTempFile("autopsy-multiviewer", ".tmp");
            file.deleteOnExit();

            OutputStream fos = new BufferedOutputStream(new FileOutputStream(file));
            InputStream stream = new BufferedInputStream(new ReadContentInputStream(abstractFile));

            byte[] buf = new byte[100000];
            int len = 0;
            while ((len = stream.read(buf, 0, buf.length)) != -1) {
                fos.write(buf, 0, len);
            }

            fos.close();
            stream.close();

            this.loadFile(file, mimeType, highlightTerms);

        } catch (IOException ex) {
            Logger.getLogger(MultiContentViewer.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    @Override
    public String getTitle() {
        return "Preview";
    }

    @Override
    public String getToolTip() {
        return "Multi Data Content Viewer";
    }

    @Override
    public DataContentViewer createInstance() {
        return new MultiContentViewer();
    }

    @Override
    public Component getComponent() {
        return panel;
    }

    @Override
    public void resetComponent() {
        clear();
    }

    @Override
    public boolean isSupported(org.openide.nodes.Node node) {

        mimeType = getMimeType(node);
        for (AbstractViewer viewer : viewerList) {
            if (viewer.isSupportedType(mimeType)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int isPreferred(org.openide.nodes.Node node) {
        return 9;
    }
}
