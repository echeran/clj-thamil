package clj_thamil.examples.java;

import clojure.java.api.Clojure;
import clojure.lang.IFn;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class WordSort01 {

    public static String utf8String(String s) throws UnsupportedEncodingException {
        byte[] array = s.getBytes("UTF-8");
        return new String(array, Charset.forName("UTF-8"));
    }

    public static void main(String[] args) throws UnsupportedEncodingException, 
                                                  FileNotFoundException,
                                                  IOException {
        // (require 'clj-thamil.format)
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("clj-thamil.format"));
        // access clj-thamil.format/word-comp, a non-fn var
        IFn wordCompVar = Clojure.var("clj-thamil.format", "word-comp");
        IFn deref = Clojure.var("clojure.core", "deref");
        Comparator wordComp = (Comparator) (deref.invoke(wordCompVar));

        List<String> strs = Arrays.asList(
                                          
                                          "மடம்",
                                          "மட்டம்",
                                          "மட்டும்",
                                          "மடக்கு",
                                          "முடக்கு",
                                          "முடுக்கு",
                                          "படம்",
                                          "குடம்",
                                          "தடம்",
                                          "தடி",
                                          "திட்டம்"
                                          
                                          );
        List<String> strs2 = new ArrayList<String>();
        strs2.addAll(strs);
        Collections.sort(strs2, wordComp);
        String list1 = StringUtils.join(strs, "\n");
        String list2 = StringUtils.join(strs2, "\n");

        System.out.println("Original list of strings:");
        System.out.println(list1);
        System.out.println("Sorted   list of strings:");
        System.out.println(list2);

        // String fileName = "out.txt";
        // FileOutputStream fos = new FileOutputStream(new File(fileName));
        // PrintWriter pw = new PrintWriter(fos);
        // pw.println("Original list of strings:");
        // pw.println(list1);
        // pw.println("Sorted   list of strings:");
        // pw.println(list2);
        // pw.close();
        // fos.close();

    }
}
