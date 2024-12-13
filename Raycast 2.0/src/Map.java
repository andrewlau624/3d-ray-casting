import java.awt.Color;
import java.awt.Graphics2D;

public class Map {
	
	int c, r, ts, po;
	
	int mapW[] = { 
		2, 2, 2, 2, 2, 2, 2, 2, 2, 1,
		2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
		2, 0, 0, 0, 0, 0, 2, 0, 0, 2,
		2, 0, 2, 0, 0, 0, 0, 0, 0, 2,
		2, 0, 0, 0, -5, 0, 0, 0, 0, 2,
		2, 0, 0, 0, 0, 2, 0, 0, 0, 2,
		2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
		2, 0, 0, 0, 0, 2, 0, 1, 0, 2,
		2, 0, 0, 0, 0, 0, 0, 0, 0, 2,
		2, 2, 1, 1, 1, 2, 1, 2, 1, 1,
	};
	
	public Map(int c, int r, int ts, int po) {
		this.c = c;
		this.r = r;
		this.ts = ts;
		this.po = po;
	}
	
	public void drawMap(Graphics2D g2){
		for(int x = 0; x < c; x++) {
			for(int y = 0; y < r; y++) {
				if(mapW[y * c + x] == 1) {
					g2.setColor(Color.white);
					g2.fillRect(x * ts + 1, y * ts + 1, ts - 1, ts - 1);
				} else {
					g2.setColor(Color.black);
					g2.fillRect(x * ts + 1, y * ts + 1, ts - 1, ts - 1);
				}
			}
		}
	}
	
	public float getPX() {
		for(int x = 0; x < c; x++) {
			for(int y = 0; y < r; y++) {
				if(mapW[y * c + x] == -5) {
					return x * ts + ts / 2 - po;
				}
			}
		}
		return 0;
	}
	
	public float getPY() {
		for(int x = 0; x < c; x++) {
			for(int y = 0; y < r; y++) {
				if(mapW[y * c + x] == -5) {
					return y * ts + ts / 2 - po;
				}
			}
		}
		return 0;
	}
}
