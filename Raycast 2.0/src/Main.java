import javax.swing.JFrame;

public class Main {

	public static void main(String[] args) {
		JFrame system = new JFrame();

		system.setResizable(false);
		system.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		system.setTitle("Raycast 2.0");
		
		GamePanel gamePanel = new GamePanel(system);
		system.add(gamePanel);
		gamePanel.start();
		
		system.pack();
		
		system.setCursor(gamePanel.cursor);	
		
		system.setLocationRelativeTo(null);
		system.setVisible(true);
	}

}
