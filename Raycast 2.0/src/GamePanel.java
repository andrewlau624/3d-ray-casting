import java.awt.AWTException;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

	final int tSize = 90;
	final int c = 10;
	final int r = 10;
	
	final int width = 900;
	final int height = 900;
	
	final int FPS = 60;
	
	final double rad = Math.toRadians(0.1f);
	final double pi = Math.PI;
	
	int pSize = 10;
	int po = pSize / 2;
	double px, py, par;
	int pxi, pyi;
	int prs = 5;
	int ps = 2;

	double ppx1, ppx2, ppy1, ppy2, ppx3, ppx4, ppy3, ppy4;	
	
	int rays = 300;
	
	double rx, ry, ra, rdx, rdy, dist, RX, RY, vrx, vry, hrx, hry;
	int mx, my, mp, vmp, hmp;
	
	double lh, lw, lo;
	boolean mon = true;
	
	double sens = 0.10;
	
	ArrayList<File> textures = new ArrayList<File>();

	KeyHandler kh = new KeyHandler();
	Thread thread;
	Robot robot;
	
	JFrame jframe;
	
	BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
	Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "cursor");
	
	Map map;
	
	public GamePanel(JFrame jFrame) {

		this.jframe = jFrame;
		
		this.setPreferredSize(new Dimension(width * 2, height));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true);
		this.setFocusable(true);
		this.addKeyListener(kh);
	}

	public void start() {
		map = new Map(c, r, tSize, po);
		getTextures();
		
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Get Starting Position
		px = map.getPX();
		py = map.getPY();
		
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		double drawInterval = 1000000000/FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		
		while(thread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			
			if(delta >= 1) {			
				update();
				repaint();
				delta--;
			}
		}
	}
	
	public void update() {			
		movement();
	}
	
	public void movement() {	
		pxi = (int) px;
		pyi = (int) py;

		
		if(par < 0) {
			par += 2 * pi;
		}		
		if(par > 2 * pi) {
			par -= 2 * pi;
		}
		
		if(kh.up) {
			py += ps * Math.sin(par);
			px += ps * Math.cos(par);
		}
		
		if(kh.down) {
			py -= ps * Math.sin(par);
			px -= ps * Math.cos(par);
		}
		
		if(kh.left) {
			par -= pi / 2;
			py += ps * Math.sin(par);
			px += ps * Math.cos(par);
			par += pi / 2;
		}
		
		if(kh.right) {
			par -= pi / 2;
			py -= ps * Math.sin(par);
			px -= ps * Math.cos(par);
			par += pi / 2;
		}
		
		mon = kh.enter;
		
		mouseMovement();
	}
	
	public void mouseMovement() {
		
		if(kh.mouse) {
			 cursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "cursor");
		} else {
			cursor = Cursor.getDefaultCursor();
		}

		jframe.setCursor(cursor);
		
		if(kh.mouse) {
			Point lp = MouseInfo.getPointerInfo().getLocation();
			robot.mouseMove(width, height / 2);

			int p = width;
			
			if(p != lp.x) {	
				par -= (p - lp.x) * rad * sens;
			}		
		
			lp.x = p;	
		}
	}
	
	
	public void drawRays(Graphics2D g2) {
		
		for(int i = -rays / 2; i < rays / 2 + 1; i++) {
			//Horizontal	
			int vd = 0;
			
			ra = par + rad * i;
			
			if(ra < 0) {
				ra += 2 * pi;
			}		
			if(ra > 2 * pi) {
				ra -= 2 * pi;
			}
			
			if(ra == 0 || ra == 2 * pi || ra == pi) {
				ry = 5000;
				rx = 5000;
			} else if(ra > pi) {
				ry = ((pyi + po) / tSize) * tSize;	
				rx = -(pyi + po - ry) / Math.tan(ra) + pxi + po;
				
				rdy = -tSize;
				rdx = rdy / Math.tan(ra);
			} else if(ra < pi) {
				ry = ((pyi + po) / tSize) * tSize + tSize;
				rx = -(pyi + po - ry) / Math.tan(ra) + pxi + po;
				
				rdy = tSize;
				rdx = rdy / Math.tan(ra);
			}
			
			while(vd < Math.max(c, r) + 1) {
				my = (int) ry / tSize;
				mx =  (int) rx / tSize;
				
				if(ra < pi) mp = my * c + mx;
				else if(ra > pi) mp = (my - 1) * c + mx;
				
				if(mp > 0 && mp < c * r && map.mapW[mp] != 0 && map.mapW[mp] != -5) {
					vd = Math.max(c, r) + 1;
					
				} else {
					ry += rdy;
					rx += rdx;
					vd++;
				}
			}
		
			hrx = rx;
			hry = ry;
			hmp = mp;
			
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(3));
			//g2.drawLine(pxi + po, pyi + po, (int) rx, (int) ry);
			
			//Vertical	
			vd = 0;
			
			if(ra == pi / 2 || ra == 3 * pi / 2) {
				rx = 5000;
				ry = 5000;
			} else if(ra < 3 * pi / 2 && ra > pi / 2) {
				rx = ((pxi + po) / tSize) * tSize;
				ry = -(pxi + po - rx) * Math.tan(ra) + pyi + po;
				
				rdx = -tSize;
				rdy = rdx * Math.tan(ra);
			} else if(ra < pi / 2 || ra > 3 * pi / 2) {
				rx = ((pxi + po) / tSize) * tSize + tSize;
				ry = -(pxi - rx + po) * Math.tan(ra) + pyi + po;
				
				rdx = tSize;
				rdy = rdx * Math.tan(ra);
			}
			
			while(vd < Math.max(c, r) + 1) {
				my = (int) ry / tSize;
				mx =  (int) rx / tSize;
				
				if(ra < pi / 2 || ra > 3 * pi / 2) mp = my * c + mx; 
				else if (ra < 3 * pi / 2 && ra > pi / 2) mp = my * c + mx - 1; 
				
				if(mp > 0 && mp < c * r && map.mapW[mp] != 0 && map.mapW[mp] != -5) {
					vd = Math.max(c, r) + 1;
			
				} else {
					ry += rdy;
					rx += rdx;
					vd++;
				}
			}
			
			vrx = rx;
			vry = ry;
			vmp = mp;
			
			g2.setColor(Color.red);
			g2.setStroke(new BasicStroke(3));
			//g2.drawLine(pxi + po, pyi + po, (int) rx, (int) ry);
			
			if(distance(px + po, py + po, vrx, vry) < distance(px + po, py + po, hrx, hry)) {
				rx = vrx;
				ry = vry;
				mp = vmp;
			} else if((distance(px + po, py + po, vrx, vry) > distance(px + po, py + po, hrx, hry))) {
				rx = hrx;
				ry = hry;
				mp = hmp;
			}	
			g2.setStroke(new BasicStroke(1));
			if(mon) g2.drawLine(pxi + po, pyi + po, (int) rx, (int) ry);
		    
		    
		    //Draw 3D
		    double fa = par - ra;
		    
			
			if(fa < 0) {
				fa += 2 * pi;
			}		
			if(fa > 2 * pi) {
				fa -= 2 * pi;
			}
			
		    dist = distance(px + po, py + po, rx, ry) * Math.cos(fa);
			
		    lh = ((height / 2) * tSize) / dist;
		    lo = height / 2 - lh / 2;
		    
		    File file = textures.get(0);
		    
		    try {
		    	file = textures.get(map.mapW[mp]);  
		    }
		    catch(Exception e) {
		    	file = textures.get(0);
		    }
		    
    		if(kh.tyler) file = textures.get(6);
		    
		    BufferedImage img = null;
			try {
				img = ImageIO.read(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		    
		    double ty = 0;
		    double tys = img.getHeight() / lh;
		    
		    double tx = rx / (tSize / img.getWidth()) % img.getWidth();
		    
		    if(rx == vrx)	 {
		    	tx = ry / (tSize / img.getWidth()) % img.getWidth();
		    	
		    	if(ra < 3 * pi / 2 && ra > pi / 2) tx = img.getWidth() - tx;   
		    } else if(rx == hrx) {
		    	tx = rx / (tSize / img.getWidth()) % img.getWidth();
		    	
		    	if(ra < pi) tx = img.getWidth() - tx;
		    }
		    
		    if(tx > img.getWidth()) tx = img.getWidth();
		    
		    g2.setStroke(new BasicStroke((int) lw));
		    	
			if(mon) {			
			    lw = width / rays;
			  
				for(int h = 0; h < lh; h++) {					
					
					try {
						g2.setColor(getDistColor(new Color(img.getRGB((int) tx, (int) ty)), dist, 100));
					}
					catch(Exception e) {
						
					}
					
					ty += tys;
					
					if(h + lo > 0 && h + lo < height) {
						g2.drawLine(((int) lw * (i + rays / 2 + 1) + width), (h + (int) lo), ((int) lw * (i + rays / 2 + 1) + width), (h + (int) lo)); 
					}		
				}
			} else {
				
			    lw = width / rays;
			    
				for(int h = 0; h < lh; h++) {
					
					try {
						g2.setColor(getDistColor(new Color(img.getRGB((int) tx, (int) ty)), dist, 100));
					}
					catch(Exception e) {
						
					}
					
					ty += tys;
			
					
					if(h + lo > 0 && h + lo  < height) {
						g2.drawLine(((int) lw * (i + rays / 2 + 1) + width / 2), (h + (int) lo), ((int) lw * (i + rays / 2 + 1) + width / 2), (h + (int) lo)); 
					}
				}
			}
		}
	}
	
	public void getTextures() {
		File folder = new File("Textures");
		
		for(int i = 0; i < folder.listFiles().length + 1; i++) {
			textures.add(new File("Textures/ Test Wall(5)"));
		}
		
		for(File file : folder.listFiles()) {
			String name = file.getName();
			int fileNum = Integer.parseInt(String.valueOf((name.charAt(name.length() - 2))));

			textures.set(fileNum, file);
		}
	}
	
	public Color getDistColor(Color color, double dist, int br) {
		
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();
		
		int or = color.getRed();
		int og = color.getGreen();
		int ob = color.getBlue();
		
		r *= br / dist;
		g *= br / dist;
		b *= br / dist;
		
		if(r < 0) r = 0;
		if(g < 0) g = 0;
		if(b < 0) b = 0;
		
		if(r > or) r = or;
		if(g > og) g = og;
		if(b > ob) b = ob;
		
		return new Color(r, g, b);
	}
	
	public double distance(double px, double py, double rx, double ry) {
		return Math.sqrt((px - rx) * (px - rx) + (py - ry) * (py - ry));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		if(mon) {
			//Map
			map.drawMap(g2);
			
			//Player		
			g2.setColor(Color.white);
			g2.fillRect(pxi, pyi, pSize, pSize);
			g2.setStroke(new BasicStroke(3));
	
			g2.drawLine(pxi + po, pyi + po, (int) (pxi + po + 20 * Math.cos(par)), (int) (pyi + po + 20 * Math.sin(par)));
		}
		
		//Rays
		drawRays(g2);
	}
}

class KeyHandler implements KeyListener {

	boolean up, down, left, right, enter, tyler, mouse = true;
	
	@Override
	public void keyTyped(KeyEvent e) {

		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W) {
			up = true;
		}
		
		if(key == KeyEvent.VK_S) {
			down = true;
		}
		
		if(key == KeyEvent.VK_A) {
			left = true;
		}
		
		if(key == KeyEvent.VK_D) {
			right = true;
		}
		
		if(key == KeyEvent.VK_ENTER) {
			enter = !enter;
		}
		
		if(key == KeyEvent.VK_T) {
			tyler = !tyler;
		}
		
		if(key == KeyEvent.VK_V) {
			mouse = !mouse;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_W) {
			up = false;
		}
		
		if(key == KeyEvent.VK_S) {
			down = false;
		}
		
		if(key == KeyEvent.VK_A) {
			left = false;
		}
		
		if(key == KeyEvent.VK_D) {
			right = false;
		}
	}
}