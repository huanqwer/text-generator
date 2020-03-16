package com.scj.text.generator.serviceImpl;

import com.scj.text.generator.service.BaseService;
import com.scj.text.generator.util.TextToImg;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

@Service
public class BaseServiceImpl implements BaseService {
    @Autowired
    TextToImg textToImg;
    @Value("${canvas.width}")
    int canvasWidth;
    @Value("${canvas.height}")
    int canvasHeight;

    private final int[] FONT_LIST = {455,464,465,81};//预设字体列表

    @Override
    public void test(String text, String color) throws Exception {
        //创建Image
        BufferedImage image = new BufferedImage(canvasWidth,canvasHeight,BufferedImage.TYPE_INT_RGB);
        //创建输出流
        FileOutputStream out = new FileOutputStream("D:\\img-output\\合成图.png");
        //创建画笔
        Graphics g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,1440,2050);
        //创建随机数
        Random random = new Random();
        int x = 0;
        int y = 0;
        //将字符串拆分
        for(int i=0;i<text.length();i++){
            char c = text.charAt(i);
            //生成随机数
            int randomNum = random.nextInt(FONT_LIST.length);
            int fontSize = 22+randomNum;
            int imgWidth = fontSize*2;
            //读取图片
            InputStream inputStream = textToImg.textToImg(c+"",FONT_LIST[randomNum],fontSize,imgWidth,imgWidth,color);
            BufferedImage fontImg = ImageIO.read(inputStream);

            //绘制合成图像
            g.drawImage(fontImg,x,y,48,48,null);
            x+=(imgWidth-20);
        }

        //释放资源
        g.dispose();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
        encoder.encode(image);
        //关闭流
        out.close();
        System.out.println("完成!");
    }
}
