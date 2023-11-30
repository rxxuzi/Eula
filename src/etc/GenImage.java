package etc;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class GenImage {

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            BufferedImage image = createGradientImage(512, 512);
            saveImage(image, "./res/" + i + ".png");
        }
    }

    private static BufferedImage createGradientImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // グラデーションのタイプをランダムに選択
        GradientType[] types = GradientType.values();
        GradientType gradientType = types[new Random().nextInt(types.length)];

        // ランダムな4色のグラデーションを生成
        Paint paint = getRandomGradient(gradientType, width, height);
        g2d.setPaint(paint);
        g2d.fillRect(0, 0, width, height);

        g2d.dispose();
        return image;
    }

    private static Paint getRandomGradient(GradientType type, int width, int height) {
        Random rand = new Random();
        float[] fractions = new float[]{0f, 0.33f, 0.66f, 1f};
        Color[] colors = new Color[]{
                new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
                new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
                new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256)),
                new Color(rand.nextInt(256), rand.nextInt(256), rand.nextInt(256))
        };

        Point2D start = new Point(0, 0);
        Point2D end = new Point(0, height);

        switch (type) {
            case HORIZONTAL:
                start.setLocation(0, height / 2);
                end.setLocation(width, height / 2);
                break;
            case VERTICAL:
                // 既定の設定を使用
                break;
            case DIAGONAL_LEFT:
                start.setLocation(0, 0);
                end.setLocation(width, height);
                break;
            case DIAGONAL_RIGHT:
                start.setLocation(width, 0);
                end.setLocation(0, height);
                break;
            case RADIAL:
                return new RadialGradientPaint(new Point2D.Float(width / 2f, height / 2f), width / 2f, fractions, colors);
        }
        return new LinearGradientPaint(start, end, fractions, colors);
    }

    private static void saveImage(BufferedImage image, String path) {
        try {
            File outputFile = new File(path);
            outputFile.getParentFile().mkdirs();
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    enum GradientType {
        HORIZONTAL, VERTICAL, DIAGONAL_LEFT, DIAGONAL_RIGHT, RADIAL
    }
}
