package games.memory;

import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.Timer;

public class MemoryEngine implements ActionListener {
	
	private int memorySize;
	private int countTurnedCards;
	private int countPairsFound;
	private JButton[] buttonGrid;
	private JButton buttonTurnedFirst;
	private int numberOfPairs;
	private int position;
	private int position_buttonTurnedFirst;
	
	private List<Image> listOfImages;
	
	private ImageIcon[] listOfAssignedImages;
	
	MemoryGui memoryGui;
	MemoryEngine(MemoryGui memoryGui) {
		this.memoryGui = memoryGui;
		
		memorySize = memoryGui.getMemorySize();
		numberOfPairs = memorySize / 2;
		
		//start game by assigning images to memory buttons
		listOfAssignedImages = MemoryEngineAssignImagesToButtons();
		
		buttonGrid = memoryGui.getButtonGrid();
	}
	
	
	/*
	 * Action Listener on every memory button
	 */
	
	@Override
	public void actionPerformed(ActionEvent e) {

		JButton memoryButton = (JButton) e.getSource();
		
		Image gameIcon = memoryGui.getGameIcon();

		// determine which button was pressed using list buttonGrid from MemoryGui, that includes all added buttons
		for (int row=0; row<memorySize; row++) {
			
			if (buttonGrid[row] == memoryButton) {
				position = row;
//				System.out.println("Number to be displayed: " + String.valueOf(listOfAssignedIntegers[row]));
				
				// if same button is pressed twice, turning button back to gameIcon
				//and setting number of Turned Cards to -1 (count is only incremented after the for loop!)
				if (countTurnedCards == 1 && position == position_buttonTurnedFirst) {
					memoryButton.setIcon(new ImageIcon(gameIcon));
					countTurnedCards = -1;
				} else {
					// set button icon to respective assigned image
					memoryButton.setIcon(listOfAssignedImages[row]);
					// remove spacing between image and button
					memoryButton.setMargin(new Insets(0,0,0,0));
				}
				break;
			}
		}
		
		// counter for number of turned cards, because every two turned cards are to be compared
		countTurnedCards++;
		
//		System.out.println("Number of turned Cards " + countTurnedCards);
		
		if (countTurnedCards == 1) {
			// for first turned button, its reference is copied to buttonTurnedFirst
			buttonTurnedFirst = memoryButton;
			position_buttonTurnedFirst = position;
			System.out.println("One card turned");
		} else if (countTurnedCards == 2) {
			// for two turned cards, the counter is set back to zero and the images are compared
			countTurnedCards = 0;
			System.out.println("Two cards turned");
			
			
			System.out.println("First button description: " + ((ImageIcon)buttonTurnedFirst.getIcon()).getDescription());
			System.out.println("Second button description: " + ((ImageIcon)memoryButton.getIcon()).getDescription());
			/*
			 * ImageIcons are initialized with a describing string that differs for every image
			 * if images identical: count of pairs found incremented and action Listener from buttons removed
			 * if not identical: disabling all buttons, waiting two seconds (to let the player memorize the images)
			 * then setting buttons back to gameIcon and enabling ActionListener for buttons again
			 */
			if ( ((ImageIcon)buttonTurnedFirst.getIcon()).getDescription().equals(((ImageIcon)memoryButton.getIcon()).getDescription())) {
				System.out.println("Images are identical");
				
				countPairsFound = countPairsFound + 1;
				System.out.println("Number of Pairs Found: " + countPairsFound);
				buttonTurnedFirst.removeActionListener(this);
				memoryButton.removeActionListener(this);
				buttonGrid[position] = null;
				buttonGrid[position_buttonTurnedFirst] = null;
			} else {
				System.out.println("Images are not identical");
				
				memoryGui.buttonEnable(false);
				
				
				// wait 2 seconds before turning back to gameIcon and re-enabling buttons
				Timer timer = new Timer(2000, new ActionListener() {
					  @Override
					  public void actionPerformed(ActionEvent arg0) {
						  
						  memoryButton.setIcon(new ImageIcon(gameIcon));
						  buttonTurnedFirst.setIcon(new ImageIcon(gameIcon));
						  
						  memoryGui.buttonEnable(true);
//						  System.out.println("Turning cards back");
						  
					  }
				});
				timer.setRepeats(false); // Only execute once
				timer.start(); // Go go go! :-D
				
				

			}
		}
		
		// end game condition: number of already found pairs equals total number of pairs
		if (countPairsFound == numberOfPairs) {
			JOptionPane.showConfirmDialog(null, "You won", "Congratulations!", JOptionPane.PLAIN_MESSAGE);
			memoryGui.setClose();
		}

//		}
					
		
	}
	/*
	 * generate a list of images from the folder "src/images"
	 * the ordering of files returned by listFiles() is not guaranteed 
	 * so different images will be selected every time the program is started
	 */
	private List<Image> MemoryEngineListOfImages() {
		
		listOfImages = new ArrayList<Image>();
		Image image = null;
		
		File[] imageFiles = new File("src/images").listFiles();
		//If this pathname does not denote a directory, then listFiles() returns null. 

//		System.out.println("Length of List imageFiles: " + imageFiles.length);
		for (File imageFile : imageFiles) {
		    if (imageFile.isFile()) {
		    	try {
					image = ImageIO.read(imageFile);
				} catch (IOException e) {
					System.out.println("Memory Images could not be found in folder src/images");
					e.printStackTrace();
				}
				listOfImages.add(image);
		    }
		}
		System.out.println("listOfImages generated");
		return listOfImages;
		
	}
	
	
	
	/*
	 * Assigning images pairwise to memory buttons:
	 * list of positions of memory buttons (listOfPositions)
	 * using random entry in listOfPositions and assign entry from listOfImages,
	 * afterwards delete position and repeat once (the same image is assigned twice)
	 * repeat with consecutive entry from listOfImages until number of pairs is reached
	 * list of assigned Images is returned, in which the index refers to position of the button in the memory game (Array buttonGrid)
	 * 
	 * returning list of ImageIcons because JButton doesn't support Image
	 * ImageIcons are initialized with an image and a describing String denoting the image's index in the list of images
	 * this allows an easy comparison by the describing String to see if two images are identical (used in the Action Listener)
	 */
	private ImageIcon[] MemoryEngineAssignImagesToButtons() {
		
		
		listOfImages = MemoryEngineListOfImages(); // list with memory button images
		int randomNumber;
		
		listOfAssignedImages = new ImageIcon[memorySize]; 
		List<Integer> listOfPositions = new ArrayList<Integer>(memorySize); // list with index numbers in size of memory
		
		for (int dimension=0; dimension<memorySize; dimension++) {
			listOfPositions.add(dimension); 
		}
		
//		System.out.println("listOfPositions before for loop " + listOfPositions);
		
		for (int dimension=0; dimension<numberOfPairs; dimension++) {
//			System.out.println("Dimension" + dimension);
			
			for (int i=0; i<2; i++) {
				randomNumber = ThreadLocalRandom.current().nextInt(0,listOfPositions.size());
				// random entry in listOfPositions, assign image to that position
				listOfAssignedImages[listOfPositions.get(randomNumber)] = new ImageIcon(listOfImages.get(dimension).getScaledInstance(200, 200, Image.SCALE_SMOOTH), String.valueOf(dimension));
//				System.out.println("Random Number " + randomNumber);
//				System.out.println("listOfAssignedImages" + Arrays.toString(listOfAssignedImages));
//				System.out.println("Position " + listOfPositions.get(randomNumber) + " gets image at position " + dimension);

				listOfPositions.remove(randomNumber);
//				System.out.println("listOfPositions after random number position removed " + listOfPositions);
			}
//			System.out.println("listOfAssignedImages" + Arrays.toString(listOfAssignedImages));			
		}
		
//		System.out.println("Final listOfAssignedImages " + Arrays.toString(listOfAssignedImages));		
		return listOfAssignedImages;
	}
	
}
