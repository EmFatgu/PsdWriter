package psd.psdwiter;

import psd.psdwiter.util.RleCompassion;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageDataWriter implements WriterFace {
    @Override
    public byte[] toByte() {
        int allSize = 0;
        List<byte[]> bytesList = new ArrayList<>();
        BufferedImage bimg = null;
        try {
            bimg = ImageIO.read(new File("/Users/guhaibo/Desktop/psd/Screenshot_20180106-175537.jpg"));
        } catch (IOException e) {
            // e.printStackTrace();
        }

        int[] rgbas = new int[bimg.getWidth() * bimg.getHeight()];
        for (int i = 0; i < bimg.getHeight(); i++) {
            for (int j = 0; j < bimg.getWidth(); j++) {
                rgbas[i * bimg.getWidth() + j] = bimg.getRGB(j, i);
            }
        }


        ByteBuffer lengthBuffer = ByteBuffer.allocate(2 * 4 * bimg.getHeight());
        int compassion = 1;
        for (int j = 0; j < 4; j++) {
            byte[] chanelData = new byte[rgbas.length];
            for (int index = 0; index < rgbas.length; index++) {
                Color color = new Color(rgbas[index], true);
                if (j == 0) {
                    chanelData[index] = (byte) color.getRed();
                } else if (j == 1) {
                    chanelData[index] = (byte) color.getGreen();
                } else if (j == 2) {
                    chanelData[index] = (byte) color.getBlue();
                } else if (j == 3) {
                    chanelData[index] = (byte) color.getAlpha();
                }
              //  chanelData[index] = chanelData[index] == 0? (byte) 255 : chanelData[index];
            }
            if (compassion == 1) {
                for (int i = 0; i < bimg.getHeight(); i++) {
                    byte[] colors = RleCompassion.compassion(Arrays.copyOfRange(chanelData, i * bimg.getWidth(), (i + 1) * bimg.getWidth()));
                    lengthBuffer.putShort((short) colors.length);
                    bytesList.add(colors);
                }
            } else {
                bytesList.add(chanelData);
            }
        }

        if (compassion == 1) {
            bytesList.add(0, lengthBuffer.array());
        }

        for (byte[] bytes : bytesList) {
            allSize += bytes.length;
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(2 + allSize);
        byteBuffer.putShort((short) compassion);

        for (byte[] bytes : bytesList) {
            byteBuffer.put(bytes);
        }

        bytesList.clear();
        return byteBuffer.array();
    }


    @Override
    public void writeBytes(BufferedOutputStream fileOutputStream) throws IOException {
        fileOutputStream.write(toByte());
    }
}