package isc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import javax.imageio.ImageIO;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.multi.GenericMultipleBarcodeReader;


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
    public String[] readBarCode(String path) throws IOException {
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(path)))));
        String[] resultsText = new String[0];
        try {
            Result[] results = reader.decodeMultiple(binaryBitmap, hintsMap);
            resultsText = new String[results.length];
            for (int i = 0; i < results.length; i++) {
                resultsText[i] = results[i].getText();
            }
        } catch (NotFoundException ex) {
        }

        return resultsText;
    }

}

