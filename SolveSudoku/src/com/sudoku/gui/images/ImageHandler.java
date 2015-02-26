package com.sudoku.gui.images;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;

public class ImageHandler {

	static final Logger logger = Logger.getLogger(com.sudoku.gui.images.ImageHandler.class);
	private static final String APPLICATION_IMAGE = "su_doku.gif";
	private static final String FRAME_LOGO = "s.gif";
	private static final String TRASH_CAN = "trashCan.JPG";
	
	private static ImageHandler imageHandler = null;
	
	/**
	 * ImageHandler Constructor
	 */
	public ImageHandler() {
		
	}
	
	/**
	 * Retrieve image for button
	 */
	public ImageIcon getApplicationImage() {
		ImageIcon icon = null;
		
		URL location = this.getClass().getResource(ImageHandler.APPLICATION_IMAGE);
        		
//		If the URL exists, then return the image		
		if (location != null) {
			icon = new ImageIcon(location);
			icon = new ImageIcon(getScaledImage(icon.getImage(), 60, 72));
		}

//		If the URL does NOT exists, then return null
		else {
			logger.error("The image was not found");
			return null;
		}
		
		return icon;
	}
	
	/**
	 * Retrieve image for button
	 */
	public Image getFrameIcon() {
		ImageIcon icon = null;
		
		URL location = this.getClass().getResource(ImageHandler.FRAME_LOGO);
        		
//		If the URL exists, then return the image		
		if (location != null) {
			icon = new ImageIcon(location);
			icon = new ImageIcon(getScaledImage(icon.getImage(),30,30));
		}

//		If the URL does NOT exists, then return null
		else {
			logger.error("The image was not found");
			return null;
		}
		
		return icon.getImage();
	}
	
	/**
	 * Retrieve image for button
	 */
	public ImageIcon getFrameIconIcon() {
		ImageIcon icon = null;
		
		URL location = this.getClass().getResource(ImageHandler.FRAME_LOGO);
        		
//		If the URL exists, then return the image		
		if (location != null) {
			icon = new ImageIcon(location);
			icon = new ImageIcon(getScaledImage(icon.getImage(),30,30));
		}

//		If the URL does NOT exists, then return null
		else {
			logger.error("The image was not found");
			return null;
		}
		
		return icon;
	}
	
	/**
	 * Retrieve image for button
	 */
	public ImageIcon getTrashCan() {
		ImageIcon icon = null;
		
		URL location = this.getClass().getResource(ImageHandler.TRASH_CAN);
        		
//		If the URL exists, then return the image		
		if (location != null) {
			icon = new ImageIcon(location);
			icon = new ImageIcon(getScaledImage(icon.getImage(),25,25));
		}

//		If the URL does NOT exists, then return null
		else {
			logger.error("The image was not found");
			return null;
		}
		
		return icon;
	}
	
	/**
	 * Retrieve image for panda
	 */
	public ImageIcon getPanda(String panda) {
		ImageIcon icon = null;
		
		URL location = this.getClass().getResource(panda);
        		
//		If the URL exists, then return the image		
		if (location != null) {
			icon = new ImageIcon(location);
		//	icon = new ImageIcon(getScaledImage(icon.getImage(),25,25));
		}

//		If the URL does NOT exists, then return null
		else {
			logger.error("The image was not found");
			return null;
		}
		
		return icon;
	}
	
	 /**
     * Resizes an image using a Graphics2D object backed by a BufferedImage.
     * @param srcImg - source image to scale
     * @param w - desired width
     * @param h - desired height
     * @return - the new resized image
     */
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

	/**
	 * @param imageHandler the imageHandler to set
	 */
	public void setImageHandler(ImageHandler imageHandler) {
		ImageHandler.imageHandler = imageHandler;
	}

	/**
	 * @return the imageHandler
	 */
	public static ImageHandler getImageHandler() {
		if (imageHandler == null) {
			imageHandler = new ImageHandler();
		}
		return imageHandler;
	}
}
