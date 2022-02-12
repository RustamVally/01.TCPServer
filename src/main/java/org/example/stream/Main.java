package org.example.stream;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        try (
                FileInputStream in = new FileInputStream("pom.xml");
        ) {
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) {
                String string = new String(buffer, 0, read, StandardCharsets.UTF_8);
                System.out.println("string = " + string);
            }


            // read - кол-во прочитанных байт
            // read = -1 (когда дочитаем)
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
