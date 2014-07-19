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

import dpf.sp.gpinf.indexer.search.viewer.MultiContentViewer;
import org.openide.modules.ModuleInstall;

/**
 *
 * @author Luis Felipe
 */
public class MultiContentViewerCloser extends ModuleInstall {

    @Override
    public boolean closing() {

        MultiContentViewer.getInstance().dispose();
        return true;

    }
}
