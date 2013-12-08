package com.display.uielement;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.DataLine.Info;
import javax.swing.JLabel;

class RPlaySound extends VideoQueryUI implements Runnable {

	public RPlaySound(InputStream waveStream) {
		this.waveStream = waveStream;

	}

	public void run() {
		try {
			if (result_video_stop == true) {
				// query_video_stop = false;
				replay();
			} else if (result_video_pause == true) {
				// query_video_stop = false;
				this.notifyAll();
			} else {
				// query_video_stop = false;
				this.play();
			}
		} catch (PlayWaveException e) {
			e.printStackTrace();
			return;
		}
	}

	@SuppressWarnings("deprecation")
	public void play() throws PlayWaveException {

		try {
			result_video_stop = false;
			result_video_pause = false;
			InputStream bufferedIn = new BufferedInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		} catch (UnsupportedAudioFileException e1) {
			throw new PlayWaveException(e1);
		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		}

		audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);
		dataLine = null;

		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
			throw new PlayWaveException(e1);
		}
		rissrgetable = true;
		// Starts the music :P
		dataLine.start();

		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];

		try {
			while (readBytes != -1) {

				if (result_video_stop == true) {
					dataLine.stop();
					dataLine.flush();
					inputStream.close();
					return;
				} else if (result_video_pause == true) {
					
					synchronized(this)
					{
						while(result_video_pause == true)
						{
							try {
								this.wait();
								
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
						dataLine.start();
														
				} else {
					
					readBytes = audioInputStream.read(audioBuffer, 0,
							audioBuffer.length);
					if (readBytes >= 0) {
						dataLine.write(audioBuffer, 0, readBytes);

					}
				}

			}

		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		}

		finally {
			// plays what's left and and closes the audioChannel
			dataLine.drain();
			dataLine.close();
			result_video_stop = true;
		}
	}

	private void replay() throws PlayWaveException {
		System.out.println("I can come here too");
		result_video_stop = false;
		result_video_pause = false;
		Rreplay=true;

		try {

			InputStream bufferedIn = new BufferedInputStream(this.waveStream);
			audioInputStream = AudioSystem.getAudioInputStream(bufferedIn);
		} catch (UnsupportedAudioFileException e1) {
			throw new PlayWaveException(e1);
		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		}
		int readBytes = 0;
		byte[] audioBuffer = new byte[this.EXTERNAL_BUFFER_SIZE];
		qissrgetable = true;
		audioFormat = audioInputStream.getFormat();
		Info info = new Info(SourceDataLine.class, audioFormat);
		dataLine = null;

		try {
			dataLine = (SourceDataLine) AudioSystem.getLine(info);
			dataLine.open(audioFormat, this.EXTERNAL_BUFFER_SIZE);
		} catch (LineUnavailableException e1) {
			throw new PlayWaveException(e1);
		}
		// Starts the music :P
		
		dataLine.start();
		try {
			while (readBytes != -1) {
				if (result_video_stop == true) {
					dataLine.stop();
					dataLine.flush();
					inputStream.close();

					return;
				} else if (result_video_pause == true) {
					

					synchronized(this)
					{
						while(result_video_pause == true)
						{
							try {
								this.wait();
								
								
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
						dataLine.start();
//					
				} else {

					readBytes = audioInputStream.read(audioBuffer, 0,
							audioBuffer.length);
					if (readBytes >= 0) {
						dataLine.write(audioBuffer, 0, readBytes);

					}
				}

			}

		} catch (IOException e1) {
			throw new PlayWaveException(e1);
		}

		finally {
			// plays what's left and and closes the audioChannel
			dataLine.drain();

			dataLine.stop();
			try {
				inputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dataLine.flush();

			dataLine.close();
			result_video_stop = true;

		}

	}

	public long getPosition() {
		return dataLine.getLongFramePosition();
	}

	public float getSampleRate() {
		// if (this == null)
		// System.out.print("null=====");
		return audioFormat.getFrameRate();
	}
	public boolean getActive() {
		return dataLine.isActive();
			}
	public SourceDataLine dataLine;
	private AudioFormat audioFormat;
	private InputStream waveStream;
	private final int EXTERNAL_BUFFER_SIZE = 524288;
	static AudioInputStream audioInputStream = null;

	// private boolean Flag;
	// public boolean isFlag() {
	// return Flag;
	// }
	//
	// public void setFlag(boolean flag) {
	// Flag = flag;
	// }
	//
	// private boolean pFLag;
	// public boolean ispFLag() {
	// return pFLag;
	// }
	//
	// public void setpFLag(boolean pFLag) {
	// this.pFLag = pFLag;
	// }
	// }

}
	class PlayWaveException extends Exception {

		public PlayWaveException(String message) {
			super(message);
		}

		public PlayWaveException(Throwable cause) {
			super(cause);
		}

		public PlayWaveException(IOException cause) {
			super(cause);
		}

		public PlayWaveException(String message, Throwable cause) {
			super(message, cause);
		}

	}
