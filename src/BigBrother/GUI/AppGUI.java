package BigBrother.GUI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import BigBrother.Exceptions.EmptyTFException;

public class AppGUI extends JFrame {

	private boolean isExistingApp = false;
	private int appID = -1; //only should be set if app is being edited
	
    private final JTextField aliasTF = new JTextField(20);
    private final JTextField windowTF = new JTextField(20);
    private final JTextField processTF = new JTextField(20);
    private final JCheckBox windowIsRegex = new JCheckBox();
    private final JCheckBox processIsRegex = new JCheckBox();

    private final JButton OKButton = new JButton("OK");
    private final JButton cancelButton = new JButton("Cancel");
    
	public AppGUI() {
		  super("New Appliction");
		  
		  setLayout(new BorderLayout());
	      
	      JPanel northPanel = new JPanel();
	      JPanel centerPanel = new JPanel();
	      JPanel centerTopPanel = new JPanel();
	      JPanel centerBottomPanel = new JPanel();
	      JPanel southPanel = new JPanel();
	      JPanel southTopPanel = new JPanel();
	      JPanel southBottomPanel = new JPanel();
	      JPanel southButtonPanel = new JPanel();
	      
	      northPanel.setLayout(new BorderLayout());
	      centerPanel.setLayout(new GridLayout(1, 2));
	      centerTopPanel.setLayout(new BorderLayout());
	      centerBottomPanel.setLayout(new FlowLayout());
	      southPanel.setLayout(new GridLayout(1, 2));
	      southTopPanel.setLayout(new BorderLayout());
	      southBottomPanel.setLayout(new FlowLayout());
	      
	      JLabel aliasNameLabel = new JLabel("Alias Name:");
	      JLabel windowNameLabel = new JLabel("Window Name:");
	      JLabel processNameLabel = new JLabel("Process Name:");
	      JLabel windowRegexLabel = new JLabel("Window is Regex?");
	      JLabel processRegexLabel = new JLabel("Process is Regex?");
	      
	      northPanel.add(aliasNameLabel, BorderLayout.WEST);
	      northPanel.add(aliasTF, BorderLayout.EAST);
	      
	      centerTopPanel.add(windowNameLabel, BorderLayout.WEST);
	      centerTopPanel.add(windowTF, BorderLayout.EAST);
	      centerBottomPanel.add(windowIsRegex);
	      centerBottomPanel.add(windowRegexLabel);
	      
	      
	      southTopPanel.add(processNameLabel, BorderLayout.WEST);
	      southTopPanel.add(processTF, BorderLayout.EAST);
	      southBottomPanel.add(processIsRegex);
	      southBottomPanel.add(processRegexLabel);
	      southButtonPanel.add(OKButton);
	      southButtonPanel.add(cancelButton);
	      
	      
	      centerPanel.add(centerTopPanel);
	      centerPanel.add(centerBottomPanel);
	      southPanel.add(southTopPanel);
	      southPanel.add(southBottomPanel);
	      southPanel.add(southButtonPanel);
	      
	      add(northPanel, BorderLayout.NORTH);
	      add(centerPanel, BorderLayout.CENTER);
	      add(southPanel, BorderLayout.SOUTH);
	      
	      cancelButton.addActionListener(new ActionListener()
	      {
	        @Override
	        public void actionPerformed(ActionEvent arg0)
	        {
	            closeWindow();
	        }
	      });
	      
	      OKButton.addActionListener(new ActionListener()
	      {
	        
	        @Override
	        public void actionPerformed(ActionEvent arg0)
	        {
	          try
	          {
	        	//check for formatting issues
	            if(aliasTF.getText().equals(""))
	            {
	              throw new EmptyTFException();
	            }
	            else
	            {
	              if(isExistingApp)
	            	  submitEditApp();
	              else
	            	  submitNewApp();
	            }
	          }
	          catch (EmptyTFException e)
	          {
	           JOptionPane.showMessageDialog(null, 
	               "App Alias Field Must Be Filled!", 
	               "Error", JOptionPane.ERROR_MESSAGE); 
	          }
	        }
	      });
	      
	}
	
	public AppGUI(int appID) {
		//call the normal super for a new user, but raise the flag for an existing user
		this();
		isExistingApp = true;
		
		//set the title
		this.setTitle("Edit Existing Application (AppID: " + appID + ")");
		
		//TODO: get info from appID and set fields
	}
	
	private void closeWindow() {
	      WindowEvent winClosingEvent = new WindowEvent(this,
	              WindowEvent.WINDOW_CLOSING);
	          Toolkit.getDefaultToolkit().getSystemEventQueue()
	              .postEvent(winClosingEvent);
	}
	
	private void submitNewApp() {
		//TODO
	}
	
	private void submitEditApp() {
		//TODO
	}
}
