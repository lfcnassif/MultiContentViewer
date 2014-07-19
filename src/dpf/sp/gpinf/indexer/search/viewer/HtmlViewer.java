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

import com.sun.javafx.application.PlatformImpl;
import java.awt.GridLayout;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Set;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * Viewer for HTML file formats.
 */
public class HtmlViewer extends AbstractViewer {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private static int MAX_SIZE = 50000000;
    private static String LARGE_FILE_MSG = "<html>File too big to safely render internally!<br>"
            + "Use an external viewer!</html>";
    private JFXPanel jfxPanel;
    private WebView htmlViewer;
    WebEngine webEngine;
    protected volatile File file, htmlFile;
    protected Set<String> highlightTerms;
    volatile Document doc;
    volatile String[] queryTerms;
    int currTerm = -1;
    protected ArrayList<Object> hits;

    public HtmlViewer() {
        super(new GridLayout());

        Platform.setImplicitExit(false);
        jfxPanel = new JFXPanel();

        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {
                htmlViewer = new WebView();
                webEngine = htmlViewer.getEngine();
                webEngine.setJavaScriptEnabled(false);
                addHighlighter();

                StackPane root = new StackPane();
                root.getChildren().add(htmlViewer);

                Scene scene = new Scene(root);
                jfxPanel.setScene(scene);
            }
        });

        this.getPanel().add(jfxPanel);
    }

    @Override
    public boolean isSupportedType(String contentType) {
        return contentType.equals("text/html") || contentType.equals("application/xhtml+xml")
                || contentType.equals("text/asp") || contentType.equals("text/aspdotnet");
    }

    @Override
    public void loadFile(final File file, Set<String> highlightTerms) {

        this.file = file;
        this.highlightTerms = highlightTerms;

        if (htmlFile != null) {
            htmlFile.delete();
        }

        PlatformImpl.runLater(new Runnable() {
            @Override
            public void run() {

                webEngine.load(null);

                if (file != null) {
                    try {
                        if (file.length() <= MAX_SIZE) {
                            htmlFile = new File(file.getAbsolutePath() + ".html");
                            htmlFile.deleteOnExit();
                            file.renameTo(htmlFile);
                            webEngine.load(htmlFile.toURI().toURL().toString());

                        } else {
                            webEngine.loadContent(LARGE_FILE_MSG);
                        }

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    protected void addHighlighter() {
        PlatformImpl.runLater(new Runnable() {
            @Override
            public void run() {
                final WebEngine webEngine = htmlViewer.getEngine();
                webEngine.getLoadWorker().stateProperty().addListener(
                        new ChangeListener<Worker.State>() {
                    @Override
                    public void changed(ObservableValue ov, Worker.State oldState, Worker.State newState) {
                        if (newState == Worker.State.SUCCEEDED || newState == Worker.State.FAILED) {

                            doc = webEngine.getDocument();

                            if (file != null) {
                                if (doc != null) {
                                    //System.out.println("Highlighting");
                                    currentHit = -1;
                                    totalHits = 0;
                                    hits = new ArrayList<Object>();
                                    if (highlightTerms != null && highlightTerms.size() > 0) {
                                        highlightNode(doc, false);
                                    }

                                } else {
                                    System.out.println("Null DOM to highlight!");
                                    queryTerms = highlightTerms.toArray(new String[0]);
                                    currTerm = queryTerms.length > 0 ? 0 : -1;

                                    //complete highlight with javascript (slower)
                                    /*webEngine.executeScript("document.designMode = \"on\"");
                                     for(String term : queryTerms){ 
                                     while((Boolean)webEngine.executeScript("window.find(\"" + term + "\")"))
                                     webEngine.executeScript("document.execCommand(\"BackColor\", false, \"Yellow\")");
                                     webEngine.executeScript("window.getSelection().collapse(document,0)");
                                     }
                                     webEngine.executeScript("document.designMode = \"off\"");
                                     webEngine.executeScript("window.scrollTo(0,0)");
                                     */
                                    scrollToNextHit(true);

                                }
                            }
                        }
                    }
                });

            }
        });

    }

    protected void highlightNode(Node node, boolean parentVisible) {
        if (node == null) {
            return;
        }

        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = node.getNodeName();
            if (nodeName.equalsIgnoreCase("body")) {
                parentVisible = true;
            } else if (nodeName.equalsIgnoreCase("script") || nodeName.equalsIgnoreCase("style")) {
                parentVisible = false;
            }
        }

        Node subnode = node.getFirstChild();

        if (subnode != null) {
            do {

                if (parentVisible && (subnode.getNodeType() == Node.TEXT_NODE /*|| subnode.getNodeType() == Node.CDATA_SECTION_NODE*/)) {
                    String term;
                    do {
                        String value = subnode.getNodeValue();

                        //remove accents, etc
                         /*char[] input = value.toLowerCase().toCharArray();
                         char[] output = new char[input.length * 4];
                         int outLen = ASCIIFoldingFilter.foldToASCII(input, 0, output, 0, input.length);
                         String fValue = new String(output, 0, outLen);
                         */
                        String fValue = value.toLowerCase();

                        int idx = Integer.MAX_VALUE;
                        term = null;
                        for (String t : highlightTerms) {
                            int j = 0, i;
                            do {
                                i = fValue.indexOf(t, j);
                                if (i != -1 && i < idx) {
                                    /*if( (i == 0 || !Character.isLetterOrDigit(fValue.charAt(i - 1)))
                                     && (i == fValue.length() - t.length() || !Character.isLetterOrDigit(fValue.charAt(i + t.length())))
                                     )*/
                                    {
                                        idx = i;
                                        term = t;
                                        break;
                                    }
                                    //j = i + 1;
                                }
                            } while (i != -1 && i < idx);

                        }
                        if (term != null) {
                            Node preNode = subnode.cloneNode(false);
                            preNode.setNodeValue(value.substring(0, idx));
                            node.insertBefore(preNode, subnode);

                            Element termNode = doc.createElement("b");
                            termNode.setAttribute("style", "color:black; background-color:yellow");
                            termNode.appendChild(doc.createTextNode(value.substring(idx, idx + term.length())));
                            termNode.setAttribute("id", "indexerHit-" + totalHits);
                            hits.add(termNode);
                            totalHits++;
                            node.insertBefore(termNode, subnode);

                            subnode.setNodeValue(value.substring(idx + term.length()));
                            if (totalHits == 1) {
                                termNode.setAttribute("style", "color:white; background-color:blue");
                                webEngine.executeScript("document.getElementById(\"indexerHit-" + ++currentHit + "\").scrollIntoView(false);");
                            }

                        }
                    } while (term != null);

                }

                highlightNode(subnode, parentVisible);

            } while ((subnode = subnode.getNextSibling()) != null);
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void dispose() {
    }

    @Override
    public String getName() {
        return "Html";
    }

    @Override
    public void scrollToNextHit(final boolean forward) {

        PlatformImpl.runLater(new Runnable() {
            @Override
            public void run() {

                if (forward) {
                    if (doc != null) {
                        if (currentHit < totalHits - 1) {
                            Element termNode = (Element) hits.get(currentHit);
                            termNode.setAttribute("style", "color:black; background-color:yellow");
                            termNode = (Element) hits.get(++currentHit);
                            termNode.setAttribute("style", "color:white; background-color:blue");
                            webEngine.executeScript("document.getElementById(\"indexerHit-" + (currentHit) + "\").scrollIntoView(false);");
                        }
                    } else {
                        while (currTerm < queryTerms.length && queryTerms.length > 0) {
                            if (currTerm == -1) {
                                currTerm = 0;
                            }
                            if ((Boolean) webEngine.executeScript("window.find(\"" + queryTerms[currTerm] + "\")")) {
                                break;
                            } else {
                                currTerm++;
                                if (currTerm != queryTerms.length) {
                                    webEngine.executeScript("window.getSelection().collapse(document.body,0)");
                                }
                            }

                        }
                    }

                } else {
                    if (doc != null) {
                        if (currentHit > 0) {
                            Element termNode = (Element) hits.get(currentHit);
                            termNode.setAttribute("style", "color:black; background-color:yellow");
                            termNode = (Element) hits.get(--currentHit);
                            termNode.setAttribute("style", "color:white; background-color:blue");
                            webEngine.executeScript("document.getElementById(\"indexerHit-" + (currentHit) + "\").scrollIntoView(false);");
                        }
                    } else {
                        while (currTerm > -1) {
                            if (currTerm == queryTerms.length) {
                                currTerm = queryTerms.length - 1;
                            }
                            if ((Boolean) webEngine.executeScript("window.find(\"" + queryTerms[currTerm] + "\", false, true)")) {
                                break;
                            } else {
                                currTerm--;
                                if (currTerm != -1) {
                                    webEngine.executeScript("window.getSelection().selectAllChildren(document)");
                                    webEngine.executeScript("window.getSelection().collapseToEnd()");
                                }

                            }

                        }
                    }
                }

            }
        });

    }
}
