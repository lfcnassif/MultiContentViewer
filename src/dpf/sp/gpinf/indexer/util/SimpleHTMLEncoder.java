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

/*
 * Encodes or decodes non permitted chars to HTML equivalent entities.
 */
public class SimpleHTMLEncoder {

    public static final String htmlDecode(String html) {
        if (html == null || html.length() == 0) {
            return "";
        }

        String result = html.replaceAll("&quot;", "\"");
        result = result.replaceAll("&amp;", "&");
        result = result.replaceAll("&lt;", "<");
        result = result.replaceAll("&gt;", ">");

        return result;

    }

    /**
     * Encode string into HTML
     */
    public static final String htmlEncode(String plainText) {
        if (plainText == null || plainText.length() == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder(plainText.length());

        char[] chars = new char[plainText.length()];
        plainText.getChars(0, chars.length, chars, 0);

        for (int index = 0; index < plainText.length(); index++) {
            //char ch = plainText.charAt(index);
            char ch = chars[index];

            switch (ch) {
                case '"':
                    result.append("&quot;");
                    break;

                case '&':
                    result.append("&amp;");
                    break;

                case '<':
                    result.append("&lt;");
                    break;

                case '>':
                    result.append("&gt;");
                    break;

                default: //if (ch < 128) 
                {
                    result.append(ch);
                }
                /*else 
                 {
                 result.append("&#").append((int)ch).append(";");
                 }*/

            }
        }

        return result.toString();
    }

    public SimpleHTMLEncoder() {
    }

    public String encodeText(String originalText) {
        return htmlEncode(originalText);
    }
}