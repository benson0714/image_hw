import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class image_processing {
	// 宣告gray image as global
	private static BufferedImage gray_image;
	
	//save file after processing
	public static void save_file(BufferedImage result, String address) {
		try {
			File output = new File(address);
			ImageIO.write(result, "jpg", output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//save file which use gamma
	public static void save_file(BufferedImage result, String address, double gamma) {
		try {
			File output = new File(address+"_"+gamma+".jpg");
			ImageIO.write(result, "jpg", output);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// 讀檔案
	public static BufferedImage read_file(String path) {
		BufferedImage image = null;
        File input = new File(path);
        try {
        	image = ImageIO.read(input);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return image;
	}
	
	// 轉灰階
    public static void gray(String path) {
    	BufferedImage result = null;
        BufferedImage image = read_file(path);
           result = new BufferedImage(
                   image.getWidth(),
                   image.getHeight(),
                   BufferedImage.TYPE_INT_RGB);
        //創造圖片
        Graphics2D graphic = result.createGraphics();
        //0,0表從0,0開始對image做處理
        graphic.drawImage(image, 0, 0,result.getWidth(),result.getHeight(), null);

        for (int i = 0; i < result.getHeight(); i++) {
            for (int j = 0; j < result.getWidth(); j++) {
                Color c = new Color(result.getRGB(j, i));
                // 紅色要乘0.299，綠色乘0.587，藍色乘0.114，相加後就是灰階值
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);
                Color newColor = new Color(
                        red + green + blue,
                        red + green + blue,
                        red + green + blue);
                // 設定(Width,Height)的rgb是newColor
                result.setRGB(j, i, newColor.getRGB());
            }
        }
		save_file(result,"D:\\java\\image\\gray.jpg");
		gray_image = result;
    }
    
    // 負片
    public static BufferedImage negative(){
        BufferedImage result = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());
            for (int i = 0; i < result.getWidth(); i++) {
                for (int j = 0; j < result.getHeight(); j++) {
                	// 每一個pixel都做XOR
                    result.setRGB(i, j, (gray_image.getRGB(i, j) ^ 0xffffff));
                }
            }
		save_file(result,"D:\\java\\image\\negative.jpg");
        return result;
    }
    
    //gammaCorrection
    public static BufferedImage gammaCorrection(double gamma) {

        int alpha, red, green, blue;
        int newPixel;

        double gamma_new = 1 / gamma;
        
        //call gamma_LUT function
        int[] gamma_LUT = gamma_LUT(gamma_new);

        BufferedImage gamma_cor = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());

        for (int i = 0; i < gray_image.getWidth(); i++) {
            for (int j = 0; j < gray_image.getHeight(); j++) {

                // Get pixels by R, G, B
                alpha = new Color(gray_image.getRGB(i, j)).getAlpha();
                red = new Color(gray_image.getRGB(i, j)).getRed();
                green = new Color(gray_image.getRGB(i, j)).getGreen();
                blue = new Color(gray_image.getRGB(i, j)).getBlue();

                red = gamma_LUT[red];
                green = gamma_LUT[green];
                blue = gamma_LUT[blue];

                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                gamma_cor.setRGB(i, j, newPixel);
            }
        }
		save_file(gamma_cor,"D:\\java\\image\\gammaCorrection",gamma);
        return gamma_cor;
        
    }

    // Create the gamma correction lookup table
    private static int[] gamma_LUT(double gamma_new) {
        int[] gamma_LUT = new int[256];

        for (int i = 0; i < gamma_LUT.length; i++) {
            gamma_LUT[i] = (int) (255 * (Math.pow((double) i / (double) 255, gamma_new)));
        }

        return gamma_LUT;
    }
    
    // 對比拉伸
    public static BufferedImage ContrastStretch(double input_r1, double output_s1) {
    	BufferedImage result = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());
    	int alpha, red, green, blue;
    	int newPixel;
    	int[] contrast_LUT = contrast_LUT(input_r1, output_s1);
    	
    	for(int i = 0; i < result.getWidth();i++) {
    		for(int j = 0 ; j < result.getHeight();j++) {
                // Get pixels by R, G, B
                alpha = new Color(gray_image.getRGB(i, j)).getAlpha();
                red = new Color(gray_image.getRGB(i, j)).getRed();
                green = new Color(gray_image.getRGB(i, j)).getGreen();
                blue = new Color(gray_image.getRGB(i, j)).getBlue();

                red = contrast_LUT[red];
                green = contrast_LUT[green];
                blue = contrast_LUT[blue];

                // Return back to original format
                newPixel = colorToRGB(alpha, red, green, blue);

                // Write pixels into image
                result.setRGB(i, j, newPixel);
    		}
    	}
    	save_file(result,"D:\\java\\image\\ContrastStretch.jpg");
    	return result;
    }
    
    // Create the contrast stretch lookup table
    private static int[] contrast_LUT(double input_r1, double output_s1) {
        double input_r2 = 255-input_r1;
        double output_s2 = 255-output_s1;
        
    	int[] contrast_LUT = new int[256];

        for (int i = 0; i < contrast_LUT.length; i++) {
        	if(i<=input_r1) {
        		contrast_LUT[i] = (int)((input_r1-0)/(output_s1-0)*(output_s1-0));
        	}
        	else if(i<=input_r2) {
        		contrast_LUT[i] = (int)((i-input_r1)/(input_r2-input_r1)*(output_s2-output_s1)+output_s1);
        	}
        	else {
        		contrast_LUT[i] = (int)((i-input_r2)/(255-input_r2)*(255-output_s2)+output_s2);
        	}
        }

        return contrast_LUT;
    }
      

    
    // percent是看要幾%的胡椒鹽
    public static BufferedImage salt_and_pepper(double percent) {
    	BufferedImage result = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());
        
    	for (int i = 0; i < result.getWidth(); i++) {
            for (int j = 0; j < result.getHeight(); j++) {
            	
            		int random = (int)((Math.random()*20)+1);
            		if(random<=(percent*20)/2) {
            			result.setRGB(i, j, (gray_image.getRGB(i, j) & 0x000000)); 
            		}
            		else if(random<=(percent*20)){
            			result.setRGB(i, j, (gray_image.getRGB(i, j) | 0xffffff));	
            		}
            		else {
            			result.setRGB(i, j, (gray_image.getRGB(i, j)));	
            		}
            }
        }
    	save_file(result,"D:\\java\\image\\salt_and_pepper.jpg");
    	return result;

    }
    

    public static BufferedImage laplace(BufferedImage contrast_image) {
        BufferedImage picture1 = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());   // original
        BufferedImage result = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());   // filtered
        int width  = picture1.getWidth();
        int height = picture1.getHeight();
        for (int i = 1; i < width - 1; i++) {
        	for (int j = 1; j < height - 1; j++) {
                int c00 = contrast_image.getRGB(i-1, j-1) & 0xff;
                int c01 = contrast_image.getRGB(i-1, j) & 0xff;
                int c02 = contrast_image.getRGB(i-1, j+1) & 0xff;
                int c10 = contrast_image.getRGB(i, j-1) & 0xff;
                int c11 = contrast_image.getRGB(i, j) & 0xff;
                int c12 = contrast_image.getRGB(i, j+1) & 0xff;
                int c20 = contrast_image.getRGB(i+1, j-1) & 0xff;
                int c21 = contrast_image.getRGB(i+1, j) & 0xff;
                int c22 = contrast_image.getRGB(i+1, j+1) & 0xff;
                System.out.println(c11);
                int pixel = -c00 -   c01 - c02 +
                        	-c10 + 8*c11 - c12 +
                        	-c20 -   c21 - c22;
                pixel = Math.min(255, Math.max(0, pixel));
                int alpha = new Color(gray_image.getRGB(i, j)).getAlpha();
                // Return back to original format
                int newPixel = colorToRGB(alpha, pixel, pixel, pixel);

                // Write pixels into image
                result.setRGB(i, j, newPixel);
            }
        } 
        save_file(result,"D:\\java\\image\\laplacian.jpg");
        return result;
    }   

    public static BufferedImage max_filter(BufferedImage laplacian_image) {
    	int pixel = 0;
        BufferedImage picture1 = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());   // original
        BufferedImage result = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());   // filtered
        int width  = picture1.getWidth();
        int height = picture1.getHeight();
        for (int i = 1; i < width - 1; i++) {
        	for (int j = 1; j < height - 1; j++) {
                int c00 = laplacian_image.getRGB(i-1, j-1) & 0xff;
                int c01 = laplacian_image.getRGB(i-1, j) & 0xff;
                int c02 = laplacian_image.getRGB(i-1, j+1) & 0xff;
                int c10 = laplacian_image.getRGB(i, j-1) & 0xff;
                int c11 = laplacian_image.getRGB(i, j) & 0xff;
                int c12 = laplacian_image.getRGB(i, j+1) & 0xff;
                int c20 = laplacian_image.getRGB(i+1, j-1) & 0xff;
                int c21 = laplacian_image.getRGB(i+1, j) & 0xff;
                int c22 = laplacian_image.getRGB(i+1, j+1) & 0xff;

                pixel = Math.max(c00, Math.max(c01, Math.max(c02, Math.max(c10, Math.max(c11, Math.max(c12, Math.max(c20, Math.max(c21, c22))))))));
                pixel = Math.min(255, Math.max(0, pixel));
                int alpha = new Color(gray_image.getRGB(i, j)).getAlpha();
                // Return back to original format
                int newPixel = colorToRGB(alpha, pixel, pixel, pixel);

                // Write pixels into image
                result.setRGB(i, j, newPixel);
            }
        } 
        save_file(result,"D:\\java\\image\\max_filter.jpg");
        return result;
    }  
    
    public static BufferedImage median_filter(BufferedImage salt_and_pepper) {
    	int pixel = 0;
        BufferedImage picture1 = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());   // original
        BufferedImage result = new BufferedImage(gray_image.getWidth(), gray_image.getHeight(), gray_image.getType());   // filtered
        int width  = picture1.getWidth();
        int height = picture1.getHeight();
        int []max = new int[9];
        for (int i = 1; i < width - 1; i++) {
        	for (int j = 1; j < height - 1; j++) {
                max[0] = salt_and_pepper.getRGB(i-1, j-1) & 0xff;
                max[1] = salt_and_pepper.getRGB(i-1, j) & 0xff;
                max[2] = salt_and_pepper.getRGB(i-1, j+1) & 0xff;
                max[3] = salt_and_pepper.getRGB(i, j-1) & 0xff;
                max[4] = salt_and_pepper.getRGB(i, j) & 0xff;
                max[5] = salt_and_pepper.getRGB(i, j+1) & 0xff;
                max[6] = salt_and_pepper.getRGB(i+1, j-1) & 0xff;
                max[7] = salt_and_pepper.getRGB(i+1, j) & 0xff;
                max[8] = salt_and_pepper.getRGB(i+1, j+1) & 0xff;

                Arrays.sort(max);
                pixel = max[4];
                int alpha = new Color(gray_image.getRGB(i, j)).getAlpha();
                // Return back to original format
                int newPixel = colorToRGB(alpha, pixel, pixel, pixel);

                // Write pixels into image
                result.setRGB(i, j, newPixel);
            }
        } 
        save_file(result,"D:\\java\\image\\median_filter.jpg");
        return result;
    }  
    
    // Convert R, G, B, Alpha to standard 8 bit
    private static int colorToRGB(int alpha, int red, int green, int blue) {

        int newPixel = 0;
        newPixel += alpha;
        newPixel = newPixel << 8;
        newPixel += red;
        newPixel = newPixel << 8;
        newPixel += green;
        newPixel = newPixel << 8;
        newPixel += blue;

        return newPixel;
    }
    
    public static void main(String... args) {
    	gray("D:\\java\\image\\Lenna.jpg");
    	negative();
    	gammaCorrection(2.2);
    	gammaCorrection(0.3);
    	BufferedImage contrast = ContrastStretch(55,20);
    	BufferedImage salt_and_pepper = salt_and_pepper(0.2);
    	BufferedImage laplacian = laplace(contrast);
    	max_filter(laplacian);
    	median_filter(salt_and_pepper);
    }
}