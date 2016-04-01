package com.bonitasoft.qrcode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;


public class JUnitQrcode {

    @Test
    public void test() {
        // final ByteArrayOutputStream byteArrayOuputStream = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(new File("c:/temp/qrcode.png"));

        GeneratorQrcode.generateUriForMobile("http://localhost:8080/bonita", fileOutputStream);
        fileOutputStream.close();
        } catch (final FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
