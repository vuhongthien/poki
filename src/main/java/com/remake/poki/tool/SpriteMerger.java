package com.remake.poki.tool;

/**
 * @author : ASUS ------- ^^!
 * @created : 27/05/2025,
 **/
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpriteMerger {

    // Số sprite tối đa trên 1 hàng (muốn dài mới xuống hàng thì tăng số này lên)
    private static final int FRAMES_PER_ROW = 10;

    public static void main(String[] args) {
        String inputDirPath  = "C:\\Users\\ASUS\\Downloads\\sprites\\sprites\\DefineSprite_146";
        String outputDirPath = "C:\\Users\\ASUS\\Downloads\\sprites\\sprites\\DefineSprite_146\\New folder";

        try {
            String outputFilePath = mergeSpritesAndExport(inputDirPath, outputDirPath);
            System.out.println("✅ Đã xuất sprite sheet tới: " + outputFilePath);
        } catch (IOException e) {
            System.err.println("❌ Lỗi khi xử lý ảnh: " + e.getMessage());
        }
    }

    public static String mergeSpritesAndExport(String inputDirPath, String outputDirPath) throws IOException {
        File inputDir = new File(inputDirPath);
        if (!inputDir.exists() || !inputDir.isDirectory()) {
            throw new IllegalArgumentException("Thư mục đầu vào không hợp lệ: " + inputDirPath);
        }

        File[] imageFiles = inputDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));
        if (imageFiles == null || imageFiles.length == 0) {
            throw new IllegalArgumentException("Không tìm thấy ảnh PNG trong thư mục: " + inputDirPath);
        }

        // Sắp xếp theo số thứ tự trong tên file
        Arrays.sort(imageFiles, Comparator.comparingInt(SpriteMerger::extractNumber));

        BufferedImage firstImage = ImageIO.read(imageFiles[0]);
        int singleWidth  = firstImage.getWidth();
        int singleHeight = firstImage.getHeight();

        int frameCount = imageFiles.length;

        // Tính số cột / hàng
        int columns = Math.min(FRAMES_PER_ROW, frameCount);
        int rows    = (int) Math.ceil((double) frameCount / columns);

        int totalWidth  = singleWidth * columns;
        int totalHeight = singleHeight * rows;

        // Pad cho width/height là bội số của 4 để Unity nén DXT5 được
        int paddedWidth  = makeMultipleOf4(totalWidth);
        int paddedHeight = makeMultipleOf4(totalHeight);

        BufferedImage spriteSheet =
                new BufferedImage(paddedWidth, paddedHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = spriteSheet.getGraphics();

        // Vẽ từng sprite theo grid (nhiều hàng)
        for (int i = 0; i < frameCount; i++) {
            BufferedImage img = ImageIO.read(imageFiles[i]);

            int col = i % columns;
            int row = i / columns;

            int x = col * singleWidth;
            int y = row * singleHeight;

            g.drawImage(img, x, y, null);
        }
        g.dispose();

        String outputFileName = singleWidth + "x" + singleHeight + ".png";

        File outputDir = new File(outputDirPath);
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, outputFileName);
        ImageIO.write(spriteSheet, "PNG", outputFile);

        return outputFile.getAbsolutePath();
    }

    // Trích số từ tên file để sắp xếp (VD: 12 từ "sprite12.png")
    private static int extractNumber(File file) {
        String name = file.getName();
        Matcher matcher = Pattern.compile("(\\d+)").matcher(name);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            return Integer.MAX_VALUE; // Không có số thì đẩy về sau cùng
        }
    }

    // Trả về số gần nhất >= value mà chia hết cho 4
    private static int makeMultipleOf4(int value) {
        int r = value % 4;
        return (r == 0) ? value : value + (4 - r);
    }
}
