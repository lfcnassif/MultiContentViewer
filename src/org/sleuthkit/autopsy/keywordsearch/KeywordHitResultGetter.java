/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.sleuthkit.autopsy.keywordsearch;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.nodes.Node.PropertySet;
import org.openide.util.Exceptions;
import org.sleuthkit.autopsy.datamodel.KeyValueNode;
import org.sleuthkit.autopsy.keywordsearch.KeywordSearchResultFactory.KeyValueQueryContent;

/**
 *
 * @author Luis Felipe
 */
public class KeywordHitResultGetter {
    
    public static HashSet getKeywords(Node node){
        
        HashSet<String> highlightTerms = new HashSet<String>();
        
        System.out.println(node.getClass().getCanonicalName());
        
        /*KeyValueNode key = (KeyValueNode)node.getParentNode();erro
        
        try {
            Field field = key.getClass().getDeclaredField("data");
            field.setAccessible(true);
            Object obj = field.get(key);
            Method method = key.getClass().getDeclaredMethod("getQueryStr");
            method.setAccessible(true);
            String keyword = (String)method.invoke(obj);
            highlightTerms.add(keyword);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
       */
        
        
        try {
            Class c = KeywordList.class.getClassLoader().loadClass("org.sleuthkit.autopsy.keywordsearch.HighlightedTextMarkup");
            Object key = node.getLookup().lookup(c);
            Field field = key.getClass().getDeclaredField("keywordHitQuery");
            field.setAccessible(true);
            String keyword = (String)field.get(key);
            highlightTerms.add(keyword);
            System.out.println(keyword);
            
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return highlightTerms;
    }
    
}
