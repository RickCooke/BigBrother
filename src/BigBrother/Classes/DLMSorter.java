package BigBrother.Classes;

import java.util.Arrays;
import javax.swing.DefaultListModel;

public class DLMSorter {

	public static <T> void sort(DefaultListModel <T> dlm) {
		
		//init our array
		T[] temp = (T[]) dlm.toArray();
		
		//sort it
        Arrays.sort(temp);
        
        //clear the list
        dlm.removeAllElements();
        
        //dump it back into the dlm
        for (T obj : temp)
            dlm.addElement(obj);
	}
}
