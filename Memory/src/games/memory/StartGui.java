/*
 *  @version 1.08    August 2019
 *  
 *  @author Sabine Kopec
 *  
 *  Copyright. All rights reserved.
 */


package games.memory;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class StartGui {

	JButton startGame = new JButton("Start the game");
	JLabel chooseSize = new JLabel("Select a size for the game");
	String memoryDimension;
	
	private String [] sizeSelection = {"", "2x2", "4x4", "6x6"};
	
	
	public String getMemoryDimension() {
		return memoryDimension;
	}
	
	StartGui() {
		
		JPanel windowContent = new JPanel();
		
		GridLayout gl = new GridLayout(1, 3);
		windowContent.setLayout(gl);
		
		JComboBox<String> dropdown = new JComboBox<String>(sizeSelection);
		
		dropdown.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				JComboBox<String> cb = (JComboBox<String>) e.getSource();
				memoryDimension = (String) cb.getSelectedItem();
			}
		});
		
		MemoryGui memoryGui = new MemoryGui(this);
		
		startGame.addActionListener(memoryGui);

		
		windowContent.add(chooseSize);
		windowContent.add(dropdown);
		windowContent.add(startGame);
		
		
		JFrame frame = new JFrame("Let's play a game of Memory");
		
		frame.setContentPane(windowContent);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frame.setSize(600,200);
		frame.setVisible(true);
	}
	
	
	public static void main(String[] args) {
		new StartGui();
	}
}
