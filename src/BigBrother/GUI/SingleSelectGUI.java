package BigBrother.GUI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import BigBrother.Classes.AppLite;
import BigBrother.Classes.UserLite;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.UnknownSelectTypeException;

public class SingleSelectGUI extends JFrame {

	private int selectType = -1;
    private final String[] selectTypeNames = {"User", "Application"};
    private DefaultListModel listModel;
    private JList listBox = null;
    private JScrollPane listBoxScroll = null;

    // selectType determines what the user is selecting from, tied to the index in the
    // selectTypeNames array
    // 0 - User
    // 1 - App
    public SingleSelectGUI(final int _selectType) throws UnknownSelectTypeException {
        super();

        selectType = _selectType;
        
        // set the title of the frame
        if (selectType >= selectTypeNames.length) {
            throw new UnknownSelectTypeException("Unknown value of selectType.");
        }

        setTitle("Edit " + selectTypeNames[selectType]);

        setLayout(new FlowLayout());

        // init the JList
        if(selectType == 0)
        	listModel = new DefaultListModel<UserLite>();
        else if(selectType == 1)
        	listModel = new DefaultListModel<AppLite>();
        else
            throw new UnknownSelectTypeException("Unknown value of selectType.");
        
        updateList();
        
        listBox = new JList(listModel);
        listBoxScroll = new JScrollPane(listBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        listBoxScroll.setPreferredSize(new Dimension(100, 300));

        // buttons row
        JPanel buttonsRow = new JPanel(new FlowLayout());
        JButton OKButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");
        JButton cancelButton = new JButton("Cancel");
        buttonsRow.add(OKButton);
        buttonsRow.add(deleteButton);
        buttonsRow.add(cancelButton);

        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(new JLabel("Select The " + selectTypeNames[selectType] + " You Wish To Edit:"));
        verticalBox.add(listBoxScroll);
        verticalBox.add(buttonsRow);

        add(verticalBox);

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                closeWindow();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                deleteItem();
            }
        });
        
        OKButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                JFrame win = null;
                try {
                    if (selectType == 0) {
                        int id = ((UserLite) listBox.getSelectedValue()).getID();

                        win = new UserGUI(id);
                    } else if (selectType == 1) {
                        int id = ((AppLite) listBox.getSelectedValue()).getID();

                        win = new AppGUI(id);
                    } else {
                        throw new UnknownSelectTypeException("Unknown value of selectType.");
                    }
                    win.pack();
                    win.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    
    private void updateList() {

    	//clear current list
    	if(listModel == null)
    		return;
    	
    	listModel.clear();
    	
    	Object[] newArray = getList().toArray();
    	
    	for(Object e : newArray)
	    	listModel.addElement(e);
    }
    
    private DefaultListModel getList() {
        DefaultListModel dlm = null;

        // create the correct DLM
        if (selectType == 0) {
            dlm = new DefaultListModel<UserLite>();
            MySQL.getUserList(dlm);
        } else if (selectType == 1) {
            dlm = new DefaultListModel<AppLite>();
            MySQL.getActiveAppList(dlm);
        } else {
            System.err.println("Unknown value of selectType.");
        }
        return dlm;
    }

    private void closeWindow() {
        WindowEvent winClosingEvent = new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
        Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(winClosingEvent);
    }

    private void deleteItem() {
        if (listBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "You must first make a selection before you can delete", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int dialogResult = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete:  " + listBox.getSelectedValue(), "Warning", JOptionPane.YES_NO_OPTION);
        if (dialogResult == JOptionPane.YES_OPTION) {
            if (selectType == 0) {
                int userID = ((UserLite) listBox.getSelectedValue()).getID();
                MySQL.deleteUser(userID);
                listBox.clearSelection();
                listBox = new JList(getList());
                AdminGUI.updateUsers();
            }
        }
        if (selectType == 1) {
            // TODO: delete an app from the database
            // just set the app's 'active' to false
        }


    }
}
