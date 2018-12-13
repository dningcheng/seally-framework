package org.seally.base.utils;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JLabel;

/**
 * @Date 2018年11月13日
 * @author dnc
 * @Description TODO
 */
public class ImageUtil {

	/**
	 * @Date 2018年10月31日
	 * @author 邓宁城
	 * @Description java下载指定url的图片编码为base64返回
	 * @param picUrl
	 * @throws Exception
	 */
	public static String downloadPictureToBase64(String picUrl) throws Exception {
		URL url = new URL(picUrl);
		DataInputStream dataInputStream = new DataInputStream(url.openStream());
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		while (-1 != (n = dataInputStream.read(buffer))) {
			output.write(buffer, 0, n);
		}
		byte[] byteArray = output.toByteArray();
		dataInputStream.close();
		return Base64.getEncoder().encodeToString(byteArray);
	}

	/**
	 * @Description java给图片加水印
	 * @param sourceImgPath 源图片路径
	 * @param tarImgPath 保存的图片路径
	 * @param waterMarkContent 水印内容
	 * @param fileExt 图片格式
	 * @return 
	 */
	public static void addWatermark(String sourceImgPath, String tarImgPath, String waterMarkContent, String fileExt) {
		Font font = new Font("宋体", Font.BOLD, 36);// 水印字体，大小
		Color markContentColor = Color.gray;// 水印颜色
		Integer degree = 45;// 设置水印文字的旋转角度
		float alpha = 0.2f;// 设置水印透明度
		OutputStream outImgStream = null;
		try {
			File srcImgFile = new File(sourceImgPath);// 得到文件
			Image srcImg = ImageIO.read(srcImgFile);// 文件转化为图片
			int srcImgWidth = srcImg.getWidth(null);// 获取图片的宽
			int srcImgHeight = srcImg.getHeight(null);// 获取图片的高
			// 加水印
			BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bufImg.createGraphics();// 得到画笔
			g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
			g.setColor(markContentColor); // 设置水印颜色
			g.setFont(font); // 设置字体
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));// 设置水印文字透明度
			if (null != degree) {
				g.rotate(Math.toRadians(degree));// 设置水印旋转
			}
			JLabel label = new JLabel(waterMarkContent);
			FontMetrics metrics = label.getFontMetrics(font);
			int width = metrics.stringWidth(label.getText());// 文字水印的宽
			int rowsNumber = srcImgHeight / width;// 打印的行数(以文字水印的宽为间隔)=图片的高 /文字水印的宽 
			int columnsNumber = srcImgWidth / width;// 每行打印的列数(以文字水印的宽为间隔)图片的宽 / 文字水印的宽 
			// 防止图片太小而文字水印太长，所以至少打印一次
			if (rowsNumber < 1) {
				rowsNumber = 1;
			}
			if (columnsNumber < 1) {
				columnsNumber = 1;
			}
			for (int j = 0; j < rowsNumber; j++) {
				for (int i = 0; i < columnsNumber; i++) {
					g.drawString(waterMarkContent, i * width + j * width, -i * width + j * width);// 画出水印,并设置水印位置
				}
			}
			g.dispose();// 释放资源
			// 输出图片
			outImgStream = new FileOutputStream(tarImgPath);
			ImageIO.write(bufImg, fileExt, outImgStream);
		} catch (Exception e) {
			e.printStackTrace();
			e.getMessage();
		} finally {
			try {
				if (outImgStream != null) {
					outImgStream.flush();
					outImgStream.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
				e.getMessage();
			}
		}
	}
	
	/**
	 * @Date 2018年11月14日
	 * @author dnc
	 * @Description 将二进制的图片数据加上水印后返回新的图片的二进制数据
	 * @param source
	 * @param waterMarkContent
	 * @return byte[]
	 * @throws IOException
	 */
	public static byte[] markImg(byte[] source,String waterMarkContent) throws IOException{
		InputStream input = new ByteArrayInputStream(source);
		Image srcImg = ImageIO.read(input);
		int srcImgWidth = srcImg.getWidth(null);// 获取图片的宽
		int srcImgHeight = srcImg.getHeight(null);// 获取图片的高
		
		// 加水印
		Font font = new Font("宋体", Font.BOLD, 36);// 水印字体，大小
		Color markContentColor = Color.gray;// 水印颜色
		Integer degree = 45;// 设置水印文字的旋转角度
		float alpha = 0.2f;// 设置水印透明度
		BufferedImage bufImg = new BufferedImage(srcImgWidth, srcImgHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufImg.createGraphics();// 得到画笔
		g.drawImage(srcImg, 0, 0, srcImgWidth, srcImgHeight, null);
		g.setColor(markContentColor); // 设置水印颜色
		g.setFont(font); // 设置字体
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));// 设置水印文字透明度
		if (null != degree) {
			g.rotate(Math.toRadians(degree));// 设置水印旋转
		}
		JLabel label = new JLabel(waterMarkContent);
		FontMetrics metrics = label.getFontMetrics(font);
		int width = metrics.stringWidth(label.getText());// 文字水印的宽
		int rowsNumber = srcImgHeight / width;  // 打印的行数(以文字水印的宽为间隔)=图片的高 /文字水印的宽 
		int columnsNumber = srcImgWidth / width;// 每行打印的列数(以文字水印的宽为间隔)图片的宽 / 文字水印的宽 
		// 防止图片太小而文字水印太长，所以至少打印一次
		if (rowsNumber < 1) {
			rowsNumber = 1;
		}
		if (columnsNumber < 1) {
			columnsNumber = 1;
		}
		for (int j = 0; j < rowsNumber; j++) {
			for (int i = 0; i < columnsNumber; i++) {
				g.drawString(waterMarkContent, i * width + j * width, -i * width + j * width);// 画出水印,并设置水印位置
			}
		}
		g.dispose();// 释放资源
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		ImageIO.write(bufImg, "gif", out);
		
		return out.toByteArray();
	}
	
	
	public static void main(String[] args) {
		String sourcec = "D:\\clipboard.png";
		
		addWatermark(sourcec, "D:\\clipboard_wattor.png", "测试水印", "png");
	}
	
}
