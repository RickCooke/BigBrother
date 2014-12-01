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
import BigBrother.Classes.DLMSorter;
import BigBrother.Classes.UserLite;
import BigBrother.Client.MySQL;
import BigBrother.Exceptions.UnknownSelectTypeException;

public class SingleSelectGUI extends JFrame
{

  private static int selectType = -1;
  private final String[] selectTypeNames = { "User", "Application" };
  private static DefaultListModel listModel;
  private JList listBox = null;
  private JScrollPane listBoxScroll = null;

  // selectType determines what the user is selecting from, tied to the index in
  // the
  // selectTypeNames array
  // 0 - User
  // 1 - App

  public SingleSelectGUI(final int _selectType)
      throws UnknownSelectTypeException
  {
    super();

    setSize(new Dimension(400, 400));
    setMinimumSize(new Dimension(400, 400));
    pack();
    setVisible(true);

    selectType = _selectType;

    // set the title of the frame
    if( selectType >= selectTypeNames.length )
    {
      throw new UnknownSelectTypeException("Unknown value of selectType.");
    }

    setTitle("Edit " + selectTypeNames[selectType]);

    setLayout(new FlowLayout());

    // init the JList
    if( selectType == 0 )
      listModel = new DefaultListModel<UserLite>();
    else if( selectType == 1 )
      listModel = new DefaultListModel<AppLite>();
    else
      throw new UnknownSelectTypeException("Unknown value of selectType.");

    updateList();

    listBox = new JList(listModel);
    listBoxScroll = new JScrollPane(listBox,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    listBoxScroll.setPreferredSize(new Dimension(100, 300));

    // buttons row
    JPanel buttonsRow = new JPanel(new FlowLayout());
    JButton editButton = new JButton("Edit");
    JButton deleteButton = new JButton("Delete");
    JButton cancelButton = new JButton("Cancel");
    buttonsRow.add(editButton);
    buttonsRow.add(deleteButton);
    buttonsRow.add(cancelButton);

    Box verticalBox = Box.createVerticalBox();
    verticalBox.add(new JLabel("Select The " + selectTypeNames[selectType]
        + " You Wish To Edit:"));
    verticalBox.add(listBoxScroll);
    verticalBox.add(buttonsRow);

    add(verticalBox);

    cancelButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        closeWindow();
      }
    });

    deleteButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        deleteItem();
      }
    });

    editButton.addActionListener(new ActionListener()
    {
      @Override
      public void actionPerformed(ActionEvent arg0)
      {
        JFrame win = null;
        try
        {
          if( selectType == 0 )
          {
            int id = ((UserLite) listBox.getSelectedValue()).getID();
            win = new UserGUI(id);
          }
          else if( selectType == 1 )
          {
            int id = ((AppLite) listBox.getSelectedValue()).getID();
            win = new AppGUI(id);
          }
          else
          {
            throw new UnknownSelectTypeException("Unknown value of selectType.");
          }
        }
        catch( Exception e )
        {
          JOptionPane.showMessageDialog(null, e.getMessage(), "Error",
              JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }


  static void updateList()
  {

    // clear current list
    if( listModel == null )
    {
      return;
    }

    listModel.clear();

    Object[] newArray = getList().toArray();

    for( Object e : newArray )
    {
      listModel.addElement(e);
    }

    DLMSorter.sort(listModel);
  }

  private static DefaultListModel getList()
  {
    DefaultListModel defaultListModel = null;

    // create the correct DLM
    if( selectType == 0 )
    {
      defaultListModel = new DefaultListModel<UserLite>();
      MySQL.getUserList(defaultListModel);
    }
    else if( selectType == 1 )
    {
      defaultListModel = new DefaultListModel<AppLite>();
      MySQL.getActiveAppList(defaultListModel);
    }
    else
    {
      System.err.println("Unknown value of selectType.");
    }
    return defaultListModel;
  }

  private void closeWindow()
  {
    WindowEvent winClosingEvent = new WindowEvent(this,
        WindowEvent.WINDOW_CLOSING);
    Toolkit.getDefaultToolkit().getSystemEventQueue()
        .postEvent(winClosingEvent);
  }

  private void deleteItem()
  {
    if( listBox.getSelectedIndex() == -1 )
    {
      JOptionPane.showMessageDialog(this,
          "You must first make a selection before you can delete", "Error",
          JOptionPane.ERROR_MESSAGE);
      return;
    }

    if( selectType == 0 )
    {
      int userID = ((UserLite) listBox.getSelectedValue()).getID();
      AdminGUI.deleteUser(userID);
    }

    if( selectType == 1 )
    {
      int appID = ((AppLite) listBox.getSelectedValue()).getID();
      AdminGUI.deleteApp(appID);
    }
  }
}
