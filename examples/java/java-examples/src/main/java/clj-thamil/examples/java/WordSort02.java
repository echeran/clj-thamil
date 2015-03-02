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

import clj_thamil.java.api.format;

public class WordSort02 {

    public static String utf8String(String s) throws UnsupportedEncodingException {
        byte[] array = s.getBytes("UTF-8");
        return new String(array, Charset.forName("UTF-8"));
    }

    public static void main(String[] args) throws UnsupportedEncodingException, 
                                                  FileNotFoundException,
                                                  IOException {
        Comparator wordComp = format.word_comp();

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

                                          // "\u0bae\u0b9f\u0bae\u0bcd",
                                          // "\u0bae\u0b9f\u0bcd\u0b9f\u0bae\u0bcd",
                                          // "\u0bae\u0b9f\u0bcd\u0b9f\u0bc1\u0bae\u0bcd",
                                          // "\u0bae\u0b9f\u0b95\u0bcd\u0b95\u0bc1",
                                          // "\u0bae\u0bc1\u0b9f\u0b95\u0bcd\u0b95\u0bc1",
                                          // "\u0bae\u0bc1\u0b9f\u0bc1\u0b95\u0bcd\u0b95\u0bc1",
                                          // "\u0baa\u0b9f\u0bae\u0bcd",
                                          // "\u0b95\u0bc1\u0b9f\u0bae\u0bcd",
                                          // "\u0ba4\u0b9f\u0bae\u0bcd",
                                          // "\u0ba4\u0b9f\u0bbf",
                                          // "\u0ba4\u0bbf\u0b9f\u0bcd\u0b9f\u0bae\u0bcd"
                                          
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
    }
}
