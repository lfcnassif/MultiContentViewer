/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dpf.sp.gpinf.indexer.util;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/*
 * Class to collect keyword hits to highlight when the blackboard artifacts are
 * not accessible through the lookup mechanism.
 */
public class NonBlackboardKeywordHit {

    /*
     * TODO: patch autopsy to make classes and methods of keywordsearch package
     * used here public, so we can remove the reflection code.
     */
    public static HashSet getKeywords(Node node) {

        HashSet<String> highlightTerms = new HashSet<String>();

        try {
            Class c = Class.forName("org.sleuthkit.autopsy.keywordsearch.HighlightedTextMarkup");
            Object obj = node.getLookup().lookup(c);
            if(obj == null)
                return highlightTerms;
            
            Field field = obj.getClass().getDeclaredField("hits");
            field.setAccessible(true);
            obj = field.get(obj);

            field = obj.getClass().getDeclaredField("keywordList");
            field.setAccessible(true);
            obj = field.get(obj);

            field = obj.getClass().getDeclaredField("keywords");
            field.setAccessible(true);
            obj = field.get(obj);

            List<Object> list = (List<Object>) obj;
            for (Object o : list) {
                field = o.getClass().getDeclaredField("keywordString");
                field.setAccessible(true);
                obj = field.get(o);

                String keyword = (String) obj;
                highlightTerms.add(keyword);
            }

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return highlightTerms;
    }
}
