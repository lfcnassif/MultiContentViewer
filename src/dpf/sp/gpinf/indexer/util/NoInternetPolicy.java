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

import java.net.SocketPermission;
import java.security.Permission;
import java.security.Policy;
import java.security.ProtectionDomain;

/*
 * Policy to block socket connections, including Internet connections,
 * except those to LibreOffice server port 8100.
 */
public class NoInternetPolicy extends Policy {

    Permission socketPerm = new SocketPermission("localhost:8100", "connect, resolve");
    Permission socketPerm2 = new SocketPermission("[0:0:0:0:0:0:0:1]:8100", "connect,resolve");

    @Override
    public boolean implies(ProtectionDomain domain, Permission perm) {
        //System.out.println("implying");
        if (perm instanceof SocketPermission) {
            if (socketPerm.implies(perm)) {
                return true;
            } else if (socketPerm2.implies(perm)) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }
}
