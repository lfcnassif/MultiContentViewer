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
package dpf.sp.gpinf.indexer.util;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import dpf.sp.gpinf.indexer.search.viewer.EmailViewer;
import dpf.sp.gpinf.indexer.search.viewer.HtmlViewer;
import dpf.sp.gpinf.indexer.search.viewer.IcePDFViewer;
import dpf.sp.gpinf.indexer.search.viewer.LibreOfficeViewer;
import dpf.sp.gpinf.indexer.search.viewer.MultiContentViewer;
import dpf.sp.gpinf.indexer.search.viewer.TiffViewer;
import java.io.File;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.openide.modules.InstalledFileLocator;

/*
 * Helper class to add specific viewers to MultiContentViewer.
 */
public class MultiContentViewerHelper {

    private static String NO_LO4_MSG = "LibreOffice 4 installation not detected!\n "
            + "Preview of office and many other file formats will be disabled.";
    String packageName = MultiContentViewer.class.getPackage().getName();

    private String getLocalLibreOfficePath(String libPath) {
        try {
            IApplicationAssistant ass = new ApplicationAssistant64Bit(libPath);
            ILazyApplicationInfo[] ila = ass.getLocalApplications();
            if (ila.length != 0 && ila[0].getMajorVersion() >= 4) {
                System.out.println("Detected LO " + ila[0].getMajorVersion() + " " + ila[0].getHome());
                return ila[0].getHome();
            } else {
                JOptionPane.showMessageDialog(null, NO_LO4_MSG);
            }

        } catch (OfficeApplicationException e1) {
            //e1.printStackTrace();
        }
        return null;
    }

    public void addViewers(final MultiContentViewer compositeViewer) {

        new Thread() {
            @Override
            public void run() {

                File modulePath = InstalledFileLocator.getDefault().locate("modules", packageName, false);
                final String nativeLibsPath = new File(modulePath, "lib").getAbsolutePath();

                String embeddedLO = null, systemLO = null;
                if (System.getProperty("os.name").startsWith("Windows")) {
                    embeddedLO = new File(modulePath, "ext/libreoffice").getAbsolutePath();
                } else {
                    systemLO = getLocalLibreOfficePath(nativeLibsPath);
                }

                final String pathLO = embeddedLO != null ? embeddedLO : systemLO;

                try {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {

                            compositeViewer.addViewer(new HtmlViewer());
                            compositeViewer.addViewer(new EmailViewer());
                            compositeViewer.addViewer(new IcePDFViewer());
                            compositeViewer.addViewer(new TiffViewer());

                            if (pathLO != null) {
                                compositeViewer.addViewer(new LibreOfficeViewer(nativeLibsPath, pathLO));
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

                compositeViewer.initViewers();

            }
        }.start();
    }
}
