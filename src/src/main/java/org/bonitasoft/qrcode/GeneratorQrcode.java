package org.bonitasoft.qrcode;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class GeneratorQrcode {

    private static Logger logger = Logger.getLogger(GeneratorQrcode.class.getName());
    /**
     * @param code
     * @param imageFormat
     * @throws WriterException
     * @throws IOException
     */
    public static boolean generate(final String code, final String imageFormat, final OutputStream outputStream)
    {
        try
        {
            final int size = 100;
            final BitMatrix bitMatrix = new QRCodeWriter().encode(code, BarcodeFormat.QR_CODE, size, size);

            MatrixToImageWriter.writeToStream(bitMatrix, imageFormat, outputStream);
            logger.info("Generate code[" + code + "] imageFormat[" + imageFormat + "]");
            return true;
        } catch (final Exception e)
        {
            logger.severe("Generate code[" + code + "] imageFormat[" + imageFormat + "] : " + e.toString());

            return false;
        }
    }

    /**
     * header sould contains http://localhost:8080/bonita
     * @param header
     * @return
     */
    public static boolean generateUriForMobile(final String header, final OutputStream outputStream)
    {
        logger.info("header[" + header + "] =>Generate [" + header + "/mobile]");

        return generate(header + "/mobile", "png", outputStream);
    }

}
