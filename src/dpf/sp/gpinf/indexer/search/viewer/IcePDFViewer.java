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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.icepdf.core.search.DocumentSearchController;
import org.icepdf.ri.common.SwingController;
import org.icepdf.ri.common.SwingViewBuilder;
import org.icepdf.ri.common.views.DocumentViewController;
import org.icepdf.ri.common.views.DocumentViewControllerImpl;
import org.icepdf.ri.util.PropertiesManager;

/*
 * Viewer for PDF file formats.
 */
public class IcePDFViewer extends AbstractViewer {

    /**
     *
     */
    private static final long serialVersionUID = -4538119351386926692L;
    private volatile SwingController pdfController, prevController;
    private volatile JPanel viewerPanel;
    volatile int fitMode = DocumentViewController.PAGE_FIT_WINDOW_WIDTH;
    volatile int viewMode = DocumentViewControllerImpl.ONE_COLUMN_VIEW;
    private int delta = 1;
    private ArrayList<Integer> hitPages;

    public IcePDFViewer() {
        super(new BorderLayout());

        System.setProperty("org.icepdf.core.imageReference", "scaled");
        System.setProperty("org.icepdf.core.ccittfax.jai", "true");
        System.setProperty("org.icepdf.core.minMemory", "150M");
        System.setProperty("org.icepdf.core.views.page.text.highlightColor", "0xFFFF00");
        //may cause jvm crash with malformed fonts
        //System.setProperty("org.icepdf.core.awtFontLoading", "true");

    }

    @Override
    public boolean isSupportedType(String contentType) {
        return contentType.equals("application/pdf");
    }

    @Override
    public String getName() {
        return "Pdf";
    }

    @Override
    public void init() {

        new File(System.getProperties().getProperty("user.home"), ".icesoft/icepdf-viewer").mkdirs();

        pdfController = new SwingController();
        pdfController.setIsEmbeddedComponent(true);

        PropertiesManager propManager = new PropertiesManager(System.getProperties(), pdfController.getMessageBundle());
        propManager.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ANNOTATION, "false");
        propManager.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_UTILITY, "false");
        propManager.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_TOOL, "false");
        propManager.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_ZOOM, "true");
        propManager.set(PropertiesManager.PROPERTY_SHOW_STATUSBAR, "false");
        propManager.set(PropertiesManager.PROPERTY_HIDE_UTILITYPANE, "true");
        propManager.set(PropertiesManager.PROPERTY_DEFAULT_PAGEFIT, Integer.toString(fitMode));
        //propManager.set(PropertiesManager.PROPERTY_SHOW_TOOLBAR_PAGENAV, "true");
        //propManager.set("application.showLocalStorageDialogs", "NO");

        final SwingViewBuilder factory = new SwingViewBuilder(pdfController, propManager, null, false, SwingViewBuilder.TOOL_BAR_STYLE_FIXED, null,
                viewMode, fitMode);

        //SwingViewBuilder factory = new SwingViewBuilder(pdfController, viewMode, fitMode);

        final JPanel panel = this.getPanel();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                viewerPanel = factory.buildViewerPanel();

                panel.add(viewerPanel, BorderLayout.CENTER);
                panel.setMinimumSize(new Dimension());
            }
        });

        //System.out.println("Viewer PDF ok");

    }

    @Override
    public void copyScreen() {
        super.copyScreen(pdfController.getDocumentViewController().getViewContainer());
    }

    @Override
    public void dispose() {
        if (pdfController != null) {
            pdfController.dispose();
        }

    }

    @Override
    public void loadFile(final File file, final Set<String> highlightTerms) {

        pdfController.closeDocument();

        if (file == null) {
            return;
        }

        new Thread() {
            @Override
            public void run() {

                prevController = pdfController;
                fitMode = pdfController.getDocumentViewController().getFitMode();

                pdfController.openDocument(file.getAbsolutePath());

                if (fitMode != pdfController.getDocumentViewController().getFitMode()) {
                    pdfController.setPageFitMode(fitMode, true);
                }

                if (pdfController.isUtilityPaneVisible()) {
                    pdfController.setUtilityPaneVisible(false);
                }

                //getPanel().setSize(getPanel().getWidth() + delta, getPanel().getHeight());
                //delta *= -1;

                highlightText(highlightTerms);

            }
        }.start();


    }

    private void highlightText(Set<String> highlightTerms) {
        try {
            DocumentSearchController search = pdfController.getDocumentSearchController();
            search.clearAllSearchHighlight();
            if (highlightTerms.size() == 0) {
                return;
            }

            boolean caseSensitive = false, wholeWord = false;
            for (String term : highlightTerms) {
                search.addSearchTerm(term, caseSensitive, wholeWord);
            }

            currentHit = -1;
            totalHits = 0;
            hitPages = new ArrayList<Integer>();
            for (int i = 0; i < pdfController.getDocument().getNumberOfPages(); i++) {
                int hits = search.searchHighlightPage(i);
                if (hits > 0) {
                    totalHits++;
                    hitPages.add(i);
                    if (totalHits == 1) {
                        pdfController.getDocumentViewController().setCurrentPageIndex(i);
                        //pdfController.updateDocumentView();
                        currentHit = 0;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro/Interrupção do Highlight");
        }

    }

    @Override
    public void scrollToNextHit(boolean forward) {

        if (forward) {
            if (currentHit < totalHits - 1) {
                pdfController.getDocumentViewController().setCurrentPageIndex(hitPages.get(++currentHit));
            }

        } else {
            if (currentHit > 0) {
                pdfController.getDocumentViewController().setCurrentPageIndex(hitPages.get(--currentHit));
            }

        }
        //pdfController.updateDocumentView();

    }
}
