package app;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.*;

public class Menu extends Window{
	
	Font  titleFont  = new Font("Rockwell", Font.PLAIN, 40);      
	Font medium = new Font(Font.SERIF,Font.PLAIN,26);
	Font large = new Font(Font.SERIF,Font.PLAIN,35);
	
	private String user;
	private GButton[] diffButtons;
	private GButton playButton;
	private GButton settingsButton;
	private GButton logoutButton;
	private JLabel playText;
	private int difficulty;
	private JLabel bestText;
	private JLabel avText;
	private ImageIcon hoverGear;
	private ImageIcon defaultGear;
	private ImageIcon hoverArrow;
	private ImageIcon defaultArrow;
	private int[] bests;
	private int[] averages;
	
	
	public Menu(WindowManager manager){
		super(manager);
		bests=new int[3];
		averages=new int[3];
		//Setting up mainPanel
		mainPanel.setBackground(Color.BLUE);
		mainPanel.setLayout(new BorderLayout());
		
		//Setting up panels (sections of the window)
		JPanel leftPanel = new JPanel(); leftPanel.setPreferredSize(new Dimension(260,0)); 
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		JPanel bottomPanel = new JPanel(); bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomPanel.setPreferredSize(new Dimension(0,100));
		JPanel topPanel=new JPanel(new BorderLayout(100,0));
		JPanel infoPanel = new JPanel(); infoPanel.setLayout(new BoxLayout(infoPanel,BoxLayout.Y_AXIS));
	    infoPanel.setBackground(Color.LIGHT_GRAY); infoPanel.setPreferredSize(new Dimension(500,50));	     
	    JPanel rightPanel = new JPanel(); rightPanel.setPreferredSize(new Dimension(60,50));
		
		//Text Labels
		JLabel title=new JLabel("MAIN MENU"); title.setFont(titleFont); 
		JLabel infoTitle = new JLabel("STATS"); infoTitle.setFont(large);
		bestText= new JLabel("Best: "); bestText.setFont(medium);
		avText= new JLabel("Average: "); avText.setFont(medium);
		playText=new JLabel("PLAY"); playText.setFont(large); playText.setVisible(false);
		
		//Image Icons
		hoverGear = new ImageIcon("resources/hoverGear.png");
		defaultGear= new ImageIcon("resources/gear.png");
		hoverArrow = new ImageIcon("resources/hoverArrow.png");
		defaultArrow = new ImageIcon("resources/arrow.png");
				
		//Buttons
		logoutButton=new GButton("Logout",Color.cyan);  logoutButton.setFont(medium);logoutButton.setPreferredSize(new Dimension(110, 12));
		logoutButton.addMouseListener(new ButtonListener(this));
		settingsButton = new GButton(defaultGear);
		settingsButton.addMouseListener(new ButtonListener(this));	
		playButton = new GButton(defaultArrow); 
		playButton.addMouseListener(new ButtonListener(this));
		playButton.setVisible(false);
		
		diffButtons = new GButton[3];
		diffButtons[0]=new GButton("EASY",Color.GREEN); 
	    diffButtons[1]=new GButton("MEDIUM", Color.ORANGE); 
	    diffButtons[2]=new GButton("HARD",Color.RED);
	    for (int i=0;i<3;i++) {
	    	diffButtons[i].addMouseListener(new ButtonListener(this));
			leftPanel.add(Box.createRigidArea(new Dimension(0,70)));
			leftPanel.add(diffButtons[i]);
	    }
	    
	    //Adding components to their respective panels
		topPanel.add(title,BorderLayout.CENTER);
		topPanel.add(logoutButton,BorderLayout.LINE_START);
		topPanel.add(settingsButton,BorderLayout.LINE_END);
		bottomPanel.add(playText);
		bottomPanel.add(playButton);
	    infoPanel.add(infoTitle); 
	    infoPanel.add(bestText);
	    infoPanel.add(avText);

	    //Adding sub-panels to main panel
	    mainPanel.add(rightPanel, BorderLayout.LINE_END);
	    mainPanel.add(bottomPanel,BorderLayout.PAGE_END);
	    mainPanel.add(infoPanel, BorderLayout.CENTER);
	    mainPanel.add(leftPanel, BorderLayout.WEST);
	    mainPanel.add(topPanel, BorderLayout.PAGE_START);
	    
	    
	    

		
	}
	

	@Override
	public void mousePress(MouseEvent e) {
		for (int i=0;i<3;i++) {
			if (diffButtons[i]==e.getSource()) {
				diffButtons[i].changeBrightness(true);
				for (int j =0; j<3;j++) {
					 if (j!=i) {
						 diffButtons[j].changeBrightness(false);
						 
					 }
				}
				playButton.setVisible(true); playText.setVisible(true);//Shows play button and accompanying text
				difficulty=i;
				updateInfo();
				refreshValues();
			}
		}
		if (settingsButton==e.getSource()) {
			super.manager.changeState("settings");
		}
		if (logoutButton==e.getSource()) {
			super.manager.changeState("login");
		}
		if (playButton==e.getSource()) {
			super.manager.setDifficulty(difficulty);
			super.manager.changeState("game");
			
		}
	}
	
	@Override 
	public void mouseEnter(MouseEvent e) {
		if (e.getSource()==playButton) {
			playButton.setIcon(hoverArrow);
		}
		else if (e.getSource()==settingsButton) {
			settingsButton.setIcon(hoverGear);
		}
	}
	@Override 
	public void mouseExit(MouseEvent e) {
		if (e.getSource()==playButton) {
			playButton.setIcon(defaultArrow);
		}
		else if (e.getSource()==settingsButton) {
			settingsButton.setIcon(defaultGear);
		}
	}
	
	
	public void updateInfo() {
		
		//Reads file depending on difficulty and changes stats on the stats pane
		String file = "resources/data/"+user+".txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			br.readLine();
			for (int i=0;i<difficulty;i++) {
				br.readLine();
			}
			String[] scores =br.readLine().split(",");
			int max= 0;
			int total=0;
			for (int i=0;i<scores.length;i++) {
				int current = Integer.parseInt(scores[i]);
				total+=current;
				if (current>max) {
					max=current;
				}
			}
			int average = total/scores.length;
			this.averages[difficulty]=average;
			this.bests[difficulty]=max;
			br.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void refreshValues() {
		avText.setText("Average: "+Integer.toString(averages[difficulty]));
		bestText.setText("Best: "+Integer.toString(bests[difficulty]));
	}
	
	public void setUser(String user) {
		this.user=user;
	}
	
	
	
		
}
