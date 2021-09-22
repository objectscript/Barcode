package isc;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import javax.imageio.ImageIO;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import static com.google.zxing.common.CharacterSetECI.UTF8;

public class barcode {
    Map<DecodeHintType, Object> hintsMap = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);
    GenericMultipleBarcodeReader reader = new GenericMultipleBarcodeReader(new MultiFormatReader());

    public barcode() {
        hintsMap.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        hintsMap.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.of(BarcodeFormat.CODE_128));
        hintsMap.put(DecodeHintType.PURE_BARCODE, Boolean.FALSE);
        hintsMap.put(DecodeHintType.CHARACTER_SET, UTF8);
    }

    // Function to read the file with Barcode
    public String[] readBarCode(String path) throws IOException
    {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path)))));
        String[] resultsText = new String[0];
        try {
            Result[] results = reader.decodeMultiple(binaryBitmap, hintsMap);
            resultsText = new String[results.length];
            for (int i = 0; i < results.length; i++) {
                System.out.println(results[i].getText());
                resultsText[i] = results[i].getText();
            }
        } catch (NotFoundException ex)
        {
        }

        return resultsText;
    }

//    public static void main(String[] args) throws WriterException, IOException {
//        String qrCodeText = "Hi";
//        String filePath = "Test.png";
//        int size = 25;
//        String fileType = "png";
//
//        boolean sc = createQRImage(filePath, qrCodeText, size, fileType);
//        barcode barcode = new barcode();
//
//        generateQRCode("Hi",25,25,"new.png");
//        System.out.println(barcode.readBarCode("new.png").length);
//        System.out.println("Ура: "+ sc);
//    }

    public boolean createQRImage(String filePath,String qrCodeText, int size, String fileType) throws WriterException, IOException {
        // Create the ByteMatrix for the QR-Code that encodes the given String
        Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<>();
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix byteMatrix = qrCodeWriter.encode(qrCodeText, BarcodeFormat.QR_CODE, size, size, hintMap);
        // Make the BufferedImage that are to hold the QRCode
        int matrixWidth = byteMatrix.getWidth();
        BufferedImage image = new BufferedImage(matrixWidth, matrixWidth, BufferedImage.TYPE_INT_RGB);
        image.createGraphics();

        Graphics2D graphics = (Graphics2D) image.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, matrixWidth, matrixWidth);
        // Paint and save the image using the ByteMatrix
        graphics.setColor(Color.BLACK);

        for (int i = 0; i < matrixWidth; i++) {
            for (int j = 0; j < matrixWidth; j++) {
                if (byteMatrix.get(i, j)) {
                    graphics.fillRect(i, j, 1, 1);
                }
            }
        }

        File qrFile = new File(filePath);
        return ImageIO.write(image, fileType, qrFile);
    }

    public byte[] generateQRCode(String filePath, String text, int height, String fileType) throws WriterException, IOException
    {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, height, height);

        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(matrix, fileType, path);

        File qrFile = new File(filePath);
        return Files.readAllBytes(qrFile.toPath());
    }
}

