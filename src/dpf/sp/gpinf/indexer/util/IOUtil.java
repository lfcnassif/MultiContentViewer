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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/*
 * Utility class.
 */
public class IOUtil {

    public static void copiaArquivo(File origem, File destino) throws IOException {
        copiaArquivo(origem, destino, false);
    }

    public static void copiaArquivo(File origem, File destino, boolean append) throws IOException {
        InputStream in = new BufferedInputStream(new FileInputStream(origem));
        OutputStream out = new BufferedOutputStream(new FileOutputStream(destino, append));
        if (append) {
            out.write(0x0A);
        }
        byte[] buf = new byte[1000000];
        int len;
        while ((len = in.read(buf)) >= 0 && !Thread.currentThread().isInterrupted()) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
        if (len != -1) {
            if (!destino.delete()) {
                throw new IOException("Não foi possível apagar " + destino.getPath());
            }
        }
    }

    public static void copiaArquivo(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[1000000];
        int len;
        while ((len = in.read(buf)) >= 0 && !Thread.currentThread().isInterrupted()) {
            out.write(buf, 0, len);
        }
    }

    public static void copiaDiretorio(File origem, File destino, boolean recursive) throws IOException {
        if (!destino.exists()) {
            if (!destino.mkdir()) {
                throw new IOException("Não foi possível criar diretório " + destino.getAbsolutePath());
            }
        }
        String[] subdir = origem.list();
        for (int i = 0; i < subdir.length; i++) {
            File subFile = new File(origem, subdir[i]);
            if (subFile.isDirectory()) {
                if (recursive) {
                    copiaDiretorio(subFile, new File(destino, subdir[i]));
                }
            } else {
                File subDestino = new File(destino, subdir[i]);
                copiaArquivo(subFile, subDestino);
            }
        }
    }

    public static void copiaDiretorio(File origem, File destino) throws IOException {
        copiaDiretorio(origem, destino, true);
    }
}
