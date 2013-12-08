package com.server.offlineprocess;



import com.common.videoAudioutility.*;

import static com.googlecode.javacv.cpp.opencv_core.*;

import com.googlecode.javacv.cpp.opencv_core;
import com.googlecode.javacv.cpp.opencv_core.CvFileStorage;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;
import com.musicg.wave.Wave;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStream;

import java.io.ObjectOutputStream;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.GCMParameterSpec;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;



public class VideoShotMetaDataExtractor {

	int framerate = 25;

	public static void main(String[] args) throws IOException {

		int width = 352;
		int height = 288;
		List<VideoAudioShot>VideoList = new ArrayList<VideoAudioShot>();
		/*
		 * FOR each video extract rgb framebyframe data
		 */
		int N = Integer.parseInt(args[0]);
		for(int i = 1; i <= N; i++){
			List<BufferedImage>Frames = new ArrayList<BufferedImage>();
			try {
				File file = new File(args[i]+".rgb");
				InputStream is = new FileInputStream(file);
				VideoAudioShot newVideo =  new VideoAudioShot(i);


				long len = file.length();
				byte[] bytes = new byte[(int)len];

				int offset = 0;
				int numRead = 0;
				while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
					offset += numRead;
				}

				int ind = 0;
				int bytesread = (height*width*3); int frameindex = 0;


				while((frameindex*bytesread) < len){
					BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
					for(int y = 0; y < height; y++){
						for(int x = 0; x < width; x++){

							byte a = 0;
							byte r = bytes[(frameindex*bytesread)+ind];
							byte g = bytes[(frameindex*bytesread)+ind+height*width];
							byte b = bytes[(frameindex*bytesread)+ind+height*width*2]; 
							//RGBImg[y][x] = new Pixel(r, g, b);
							int pix = 0xff000000 | ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
							//int pix = ((a << 24) + (r << 16) + (g << 8) + b);
							img.setRGB(x,y,pix);
							ind++;

						}
					}
					Frames.add(img);
					frameindex++;
					ind = 0;
				}
				newVideo.setListofFrames(Frames);
				newVideo.SetFrameSize(Frames.size());
				newVideo.setFileName(args[i]); 
				//newVideo.converttoImgframes(Frames.size());
				VideoList.add(newVideo);
				

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	
		//Video Utility Object created
		VideoUtility util = new VideoUtility();
		util.TODEBUGS.DEBUG_PRINTLN(true, "yappax ayetho maga video reading");


		/*
		 * Perform MetaData extraction on every video
		 */
		for(int Vindex = 0; Vindex < N; Vindex++)
		{
			VideoAudioShot v = util.scenechangedetector.FindScenechangedFrames(VideoList.get(Vindex), width, height);
			util.TODEBUGS.DEBUG_PRINTLN(true,"video "+" vindex "+" scenechangedetectdone");
			VideoList.remove(Vindex);
			VideoList.add(Vindex, v);
			//feature extraction ---> Color and feature
			VideoList.get(Vindex).featureBasedExtractor();
			VideoList.get(Vindex).HSVHistorgramExtractor();
			//Extract Audio MetaData
			String filename = args[Vindex+1];
			Wave sampleWave = new Wave(filename+".wav");
			VideoList.get(Vindex).ExtractAudioFingerPrint(sampleWave);
		}

		WritetoFile(VideoList);
		OpencvWritetoFile(VideoList);
		util.TODEBUGS.DEBUG_PRINTLN(true, "yappa data send ayetha");
	}

	private static void OpencvWritetoFile(List<VideoAudioShot> videoList) {
		
		int i = 0;
		for(int index = 0; index < videoList.size(); index++){
			CvFileStorage cvfile, cvfile2;
			cvfile = opencv_core.cvOpenFileStorage(
					"C:\\DataExtracted2\\FeatColorMetadata"+index+".xml", // filename
					null, // memstorage
					CV_STORAGE_WRITE, // flags
					null); // encoding
			cvfile2 = opencv_core.cvOpenFileStorage(
					"C:\\DataExtracted2\\HistrMetadata"+index+".xml", // filename
					null, // memstorage
					CV_STORAGE_WRITE, // flags
					null); // encoding
			i=0;
			cvWriteInt(cvfile, "size", videoList.get(index).getListofDescriptors().size());
			for (CvMat eachDescriptor : videoList.get(index).getListofDescriptors()) {
				cvWrite(cvfile, "Descriptors"+i, eachDescriptor);
				i++;
			}
			i=0;
			for (CvHistogram eachHistorgram : videoList.get(index).getListofHistograms()) {
				
				cvWrite(cvfile2, "Histogram"+i, eachHistorgram.mat());
				i++;
			}
			cvReleaseFileStorage(cvfile);
			cvReleaseFileStorage(cvfile2);
		}
		

	}

	private static void WritetoFile(List<VideoAudioShot> videoList) throws IOException {

		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
			fout = new FileOutputStream("C:\\DataExtracted2\\Metadata.msp", true);
			oos = new ObjectOutputStream(fout);
			oos.writeObject(videoList);
		} catch (Exception ex) {
			ex.printStackTrace();
		}finally {
			if(oos  != null){
				oos.close();
			} 
		}

	}

}
