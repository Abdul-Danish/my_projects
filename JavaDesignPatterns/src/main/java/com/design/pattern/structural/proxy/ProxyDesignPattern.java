package com.design.pattern.structural.proxy;

import java.util.Objects;

/*
 * Lazy Loading:
 * One of the primary use cases for proxies is lazy loading. In situations where creating or initializing 
 * an object is resource-intensive, the proxy delays the creation of the real object until it is actually needed.
 * 
 * Access Control:
 * They can ensure that the client code has the necessary permissions before allowing access to the 
 * real object.
 * 
 * Caching:
 * Proxies can implement caching mechanisms to store results or resources.
 */
public class ProxyDesignPattern {

    public static void main(String[] args) {
        Image image = new ImageProxy("/home/test/file.txt");
        image.display();
        image.display();
    }
}

interface Image {
    void display();
}

class RealImage implements Image {
    private String fileName;

    public RealImage(String fileName) {
        this.fileName = fileName;
        loadImageFromDisk();
    }

    private void loadImageFromDisk() {
        System.out.println("Loading Image from Disk: " + fileName);
    }

    @Override
    public void display() {
        System.out.println("Displaying Image: " + fileName);
    }
}

class ImageProxy implements Image {
    private RealImage realImage;
    private String fileName;

    public ImageProxy(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void display() {
        if (Objects.isNull(realImage)) {
            realImage = new RealImage(fileName);
        }
        System.out.println("Displaying Image: " + fileName);
    }
}
