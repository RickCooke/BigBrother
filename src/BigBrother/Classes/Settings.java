package BigBrother.Classes;

import javax.swing.JOptionPane;

import BigBrother.Client.MySQL;
import BigBrother.Exceptions.NoSettingsException;

public class Settings {
	
	public final boolean debug = true;
	
    public long polling_interval;
    public int memory_flush_interval;
    public int local_flush_interval;
    public int max_idle_time;
    public int block_time;
    public int start_time;
    public int keyboard_peek_interval = 10;
    public int remote_insert_buffer_size = 100;
    
    public final String MySQL_host = "23.94.98.164";
    public final String MySQL_database = "bigbrother";
    public final String MySQL_username = "bigbrother";
    public final String MySQL_password = "plzletmein";
    
	public void downloadSettings() {
			try {
				MySQL.recieveSettings(this);
	        } catch (NoSettingsException e) {
	            JOptionPane.showMessageDialog(null, e.getMessage(),
	                "Error", JOptionPane.ERROR_MESSAGE);
	            System.exit(1);
	        }
	}

	public void uploadSettings() {
		try {
			MySQL.sendSettings(this);
		} catch (NoSettingsException e) {
	            JOptionPane.showMessageDialog(null, e.getMessage(),
	                "Error", JOptionPane.ERROR_MESSAGE);
	            System.exit(1);
	    }
	}
	
	public String toString() {
		String retStr = "";

		retStr += "Settings:\n";
		retStr += "polling_interval: " + polling_interval + "\n";
		retStr += "memory_flush_interval: " + memory_flush_interval + "\n";
		retStr += "local_flush_interval: " + local_flush_interval + "\n";
		retStr += "max_idle_time: " + max_idle_time + "\n";
		retStr += "block_time: " + block_time + "\n";
		retStr += "start_time: " + start_time + "\n";
		retStr += "keyboard_peek_interval: " + keyboard_peek_interval + "\n";
		retStr += "remote_insert_buffer_size: " + remote_insert_buffer_size + "\n";
		
		return retStr;
	}
    
}
