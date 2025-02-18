package com.remake.poki.tets;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class ExtractPetData {
    public static void main(String[] args) {
        try {
            // Đọc file HTML
            File inputFile = new File("C:\\Users\\ASUS\\Downloads\\test.html"); // Đường dẫn file input
            Document doc = Jsoup.parse(inputFile, "UTF-8");

            // Chọn tất cả thẻ <img> có dsrc chứa "peticon"
            Elements images = doc.select("img[dsrc*=peticon]");

            for (Element img : images) {
                // Lấy giá trị của dsrc
                String imgSrc = img.attr("dsrc");
                // Lấy giá trị alt
                String altText = img.attr("alt");

                // Trích xuất số từ "peticonXX.png"
                String number = imgSrc.replaceAll(".*/peticon(\\d+)\\.png", "$1");

                // In kết quả
                System.out.println( number + " -> " + altText);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
