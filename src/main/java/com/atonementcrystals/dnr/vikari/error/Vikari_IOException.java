package com.atonementcrystals.dnr.vikari.error;

public class Vikari_IOException extends Vikari_Exception {

    public Vikari_IOException(String message) {
        super("IO Error", message);
    }

}
