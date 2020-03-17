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
import java.io.File;
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
    private final int FONT_SIZE = 36;//预设字体大小

    private static int pageNum = 0;//当前页数

    @Override
    public void text(String title, String text, String color) throws Exception {
        //创建Image
        BufferedImage image = new BufferedImage(canvasWidth,canvasHeight,BufferedImage.TYPE_INT_RGB);
        File file = new File("D:\\img-output\\img"+pageNum+".png");
        if(!file.exists()){//没有则创建
            File folder = new File("D:\\img-output");
            folder.mkdir();//创建文件夹
            file.createNewFile();
        }
        //创建输出流
        FileOutputStream out = new FileOutputStream(file);
        //创建画笔
        Graphics g = image.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,canvasWidth,canvasHeight);
        //画标题
        //算出起笔位置
        int beginX = (canvasWidth-FONT_SIZE*(title.length()))/2;
        if(beginX<0){
            System.out.println("标题超限,自动移到行首");
            beginX = 0;
        }
        System.out.println("起笔位置为："+beginX);
        //创建随机数
        Random random = new Random();
        if(title==null){
            title = "";
        }
        for(int i=0;i<title.length();i++){
            char c = title.charAt(i);
            InputStream inputStream;
            int randomNum = random.nextInt(FONT_LIST.length);
            //读取图片
            try {
                inputStream = textToImg.textToImg(c+"",FONT_LIST[randomNum],FONT_SIZE,FONT_SIZE*2,FONT_SIZE*2,color);
            }catch (Exception e){
                //触发重试机制
                System.out.println("3秒后重试。。。");
                Thread.sleep(3000);
                i--;
                continue;
            }

            BufferedImage fontImg = ImageIO.read(inputStream);

            //绘制合成图像
            g.drawImage(fontImg,beginX,0,FONT_SIZE,FONT_SIZE,null);
            beginX+=FONT_SIZE/2;
        }
        int x = 0;
        int y = "".equals(title)?10:(FONT_SIZE/2+20);
        //将正文字符串拆分
        for(int i=0;i<text.length();i++){
            char c = text.charAt(i);
            //判断是否遇到换行符
            if(c=='\n'){
                System.out.println("遇到换行符！");
                x = 0;
                y += (FONT_SIZE/2+10);
                //换纸
                if(y+FONT_SIZE>canvasHeight){
                    pageNum++;
                    //释放资源
                    g.dispose();
                    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                    encoder.encode(image);
                    //关闭流
                    out.close();
                    System.out.println("完成!");
                    text("",text.substring(i+1),color);
                    return;
                }
                continue;
            }
            String str = c+"";
            int flag = 0;//是否遇到数字
            int count = 1;//遇到的数字长度
            //判断是否遇到数字
            if(Character.isDigit(c)){
                System.out.println("遇到数字！");
                flag = 1;
                while(Character.isDigit(text.charAt(i+count))){
                    count++;
                }
                System.out.println("提取长度为"+count);
                str = text.substring(i,i+count);
                System.out.println("提取数字为"+str);
                i+=(count-1);
            }
            //生成随机数
            int randomNum = random.nextInt(FONT_LIST.length);
            int randomX = random.nextInt(5);
            int randomY = random.nextInt(5)-3;
            int fontSize = FONT_SIZE+randomNum;
            int imgWidth = fontSize*2;
            InputStream inputStream;
            //读取图片
            try {
                inputStream = textToImg.textToImg(str,flag==1?462:FONT_LIST[randomNum],flag==1?fontSize/4*3:fontSize,flag==0?imgWidth:imgWidth*count/2,imgWidth,color);
            }catch (Exception e){
                //触发重试机制
                System.out.println("3秒后重试。。。");
                Thread.sleep(3000);
                i--;
                continue;
            }

            BufferedImage fontImg = ImageIO.read(inputStream);

            //绘制合成图像
            g.drawImage(fontImg,count>4?x-30:x+randomX,y+randomY,flag==0?FONT_SIZE:FONT_SIZE*count/2,FONT_SIZE,null);

            if(count>2){
                x+=(imgWidth-FONT_SIZE*3/2)*count/2-10;
            }else{
                x+=(imgWidth-FONT_SIZE*3/2);
            }

            if(x+FONT_SIZE>canvasWidth){
                x = 0;
                y += (imgWidth-FONT_SIZE*3/2+10);
                if(y+FONT_SIZE>canvasHeight){
                    //换纸
                    pageNum++;
                    //释放资源
                    g.dispose();
                    JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                    encoder.encode(image);
                    //关闭流
                    out.close();
                    System.out.println("完成!");
                    text("",text.substring(i+1),color);
                    return;
                }
            }
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
