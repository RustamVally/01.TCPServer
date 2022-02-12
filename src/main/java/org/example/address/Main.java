package org.example.address;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Main {
    public static void main(String[] args) {
        // new InetAddress() - шаблон (нет public конструктора)
        // host - устройство у которого есть адресс

        try {
            InetAddress yandex = InetAddress.getByName("ya.ru");
            // name -> ip (resolving)
            System.out.println("yandex = " + yandex);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
