package games.memory;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.lang.Math;

public class MemoryGui implements ActionListener{
	
	private StartGui startGui;
	
	private int memorySize;
	private JButton[] buttonGrid;
	private JButton buttonMemory;
	
	// number of memory cards
	public int getMemorySize() {
		return memorySize;
	}
	
	// list including all memory buttons
	public JButton[] getButtonGrid() {
		return buttonGrid;
	}
	
	
	private Image icon = null;
	private Image scaled;
	
	/* 
	 * Method to set Icon automatically scaling to the button size
	 */
	public void setScaledImageToButton(JButton button) {
		File sourceIcon = new File("src/Logo_Memory_small.gif");
		
		try {
			icon = ImageIO.read(sourceIcon);
		} catch (IOException e) {
			System.out.println("Game Icon Image not found");
			e.printStackTrace();
		}
		
		buttonMemory.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				JButton button = (JButton) e.getComponent();
				Dimension size = button.getSize();
				Insets insets = button.getInsets();
				size.width -= insets.left + insets.right;
				size.height -= insets.top + insets.bottom;
				if (size.width > size.height) {
					size.width = -1;
				} else {
					size.height = -1;
				}
				scaled = icon.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
				button.setIcon(new ImageIcon(scaled));
			}

		});
	}
	
	
	
	public Image getGameIcon() {
		return scaled;
	}
	
	
	private JFrame gameFrame;
	
	public void setClose() {
		gameFrame.setVisible(false);
	}
	
	MemoryEngine memoryEngine;
	
	/*
	 * enabling or disabling action listener of memory buttons
	 * to prevent buttons from being active in the 2seconds waiting time 
	 * before two not identical cards are set back to gameIcon in Engine class
	 */
	public void buttonEnable(boolean bool) {
		
		for (JButton buttonMemory: buttonGrid) {
			if (buttonMemory != null) {
				if (!bool) { //false: action listener removed (buttons not active)
					buttonMemory.removeActionListener(memoryEngine);
				} else { // true: action listener added (buttons active)
					buttonMemory.addActionListener(memoryEngine);
				}
			}
		}
	}
	
	
	MemoryGui(StartGui startGui) {
		this.startGui = startGui;
	}

	
	/* 
	 * Action Listener for Start button in StartGui
	 * reads string from JComboBox in StartGui (memoryDimension)
	 * translates it to integer (memorySize)
	 * invokes Method that creates the game frame with the memory cards
	 */
	public void actionPerformed(ActionEvent e) {
		System.out.println("OK was pressed");
				
		String memoryDimension = startGui.getMemoryDimension();
				
//		System.out.println(memorySize);
		if (memoryDimension == null) {
			JOptionPane.showConfirmDialog(null, "Please select a size for the game", "How difficult do you want it to become?", JOptionPane.PLAIN_MESSAGE);
		} else {
			if (memoryDimension == "2x2") {
				memorySize = 4;
			} else if (memoryDimension == "4x4") {
				memorySize = 16;
			} else if (memoryDimension == "6x6") {
				memorySize = 36;
			} else if (memoryDimension == "8x8") {
				memorySize = 64;
			}
			
			MemoryGuiCreateGameFrame(memorySize);
			System.out.println("Start Game");
		}
		
	}

	
	/* 
	 * Method that creates the game frame with the memory cards
	 * 
	 */
	public void MemoryGuiCreateGameFrame(int memorySize) {
		
		// only squared Dimension of game possible
		int memoryDimensionInt = (int) Math.sqrt(memorySize);
		
		/*
		 *  the way the buttons are added to the gameFrame makes them untrackable
		 *  the list buttonGrid allows tracking, necessary for the unique assignment of images to the buttons
		 */
		buttonGrid = new JButton[memorySize];
		
		JPanel windowContent = new JPanel();
		GridLayout gl = new GridLayout(memoryDimensionInt,memoryDimensionInt);
		windowContent.setLayout(gl);
		
		
		gameFrame = new JFrame("North Coast Memory Game");
		gameFrame.add(windowContent);
		
		memoryEngine = new MemoryEngine(this);
		
		// creating as many buttons as dimension of the game and put gameIcon (scaled to button size)
		for (int size=0; size < memorySize; size++) {
			buttonMemory = new JButton();
			
			setScaledImageToButton(buttonMemory);
			
			buttonMemory.setBounds(0, 0, 0, 0);
			buttonGrid[size] = buttonMemory;
			
			windowContent.add(buttonMemory);
			// button action listener in class memoryEngine
			buttonMemory.addActionListener(memoryEngine);
		}
		
		//change Frame Icon to gameIcon
		
		gameFrame.setIconImage(icon);
		gameFrame.setContentPane(windowContent);
		gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		gameFrame.setSize(800,800);
		gameFrame.setVisible(true);

	}

}
