/*
 * This Java source file was generated by the Gradle 'init' task.
 */
/**
 * This is the no gui version of the AutoUpdater
 * This gets started by AutoUpdate.java when the -G or -g command line argument
 * is provided.
 * The utility needs to be provided with a filename via the -F argument
 * If no IP address is provided it searches for netburner devices for five
 * seconds and then lists the devices found.
 * A device is then selected by entering the number form the list.
 * If an IP address is provided the tool waits for a confirmation or
 * automatically programs the device, if the -A argument is given
 * After device programming is completed and the tool exits.
 */
package agNBAutoUpdate;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.prefs.Preferences;

public class AutoUpdateNoGui {

    private String DefaultPath;

    private String IPAddress;
    private String tfFileName;

    private boolean RebootCheck;

    boolean bAutoRun;

    private String UserName;

    private String PassWord;

    protected void ParseCommandLine(String[] args) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 'I':
                    case 'i':
                        this.IPAddress = args[i].substring(2);
                        break;
                    case 'F':
                    case 'f':
                        this.tfFileName = args[i].substring(2);
                        break;
                    case 'A':
                    case 'a':
                        this.bAutoRun = true;
                        break;
                    case 'R':
                    case 'r':
                        this.RebootCheck = true;
                        break;
                    case 'U':
                    case 'u':
                        this.UserName = args[i].substring(2);
                        break;
                    case 'P':
                    case 'p':
                        this.PassWord = args[i].substring(2);
                        break;
                    case '?':
                        System.out.println("Usage is \n java -Jar Autoupdate.jar <options>\nOptions are:");
                        System.out.println("-Ixxx.xxx.xxx.xxx Set IP Address");
                        System.out.println("-FFilename Set File name ");
                        System.out.println("-C         Start without GUI");
                        System.out.println("-R         Set reboot");
                        System.out.println("-A         Run Automatically");
                        System.out.println("-Uusername  Set a username");
                        System.out.println("-Ppassword Set a password");
                        break;
                }
            }
        }
    }

    protected void GetDefaults() {
        Preferences prefsRoot = Preferences.userRoot();
        Preferences myPrefFile = prefsRoot.node("com.Autoupdate.preference");
        this.DefaultPath = myPrefFile.get("FilePath", "C:\\nburn\\bin");
        this.IPAddress = myPrefFile.get("LastIp", "0.0.0.0");
    }

    protected void SaveDefaults() {
        Preferences prefsRoot = Preferences.userRoot();
        Preferences myPrefFile = prefsRoot.node("com.Autoupdate.preference");
        myPrefFile.put("FilePath", this.DefaultPath);
        String s = this.IPAddress;
        myPrefFile.put("LastIp", s);
    }

    public AutoUpdateNoGui(String[] args) {
        //GetDefaults();
        this.bAutoRun = false;
        this.UserName = null;
        this.PassWord = null;
        ParseCommandLine(args);

        if (tfFileName != null && tfFileName.endsWith("_APP.s19") && Files.exists(Paths.get(tfFileName))) {
            System.out.println("Filename: " + tfFileName);
        } else {
            System.out.println("No valid file(name) (" + tfFileName + ") provided.");
            System.out.println("Filename needs to end in _APP.s19 and file needs to exist.");
            System.out.println("Aborting!");
            System.exit(0);
        }

        if (IPAddress != null && !"0.0.0.0".equals(IPAddress)) {
            System.out.println("Using IP Address: " + IPAddress);
        } else {
            System.out.println("No IP Address provided. Searching devices");

            ArrayList<NetBurnerDevice> foundDevices;
            foundDevices = NetBurnerCoreFinder.CoreFinderAction(new DummyDoFinder());
            for (int i = 0; i < foundDevices.size(); i++) {
                System.out.println(new DecimalFormat("000").format(i) + ": MAC " + foundDevices.get(i).GetRootMac() + " " + foundDevices.get(i).toString());
            }
            System.out.println("Please select a number from above list. Any other number to abort.");
            Scanner kbd = new Scanner(System.in);
            int select = kbd.nextInt();
            if (select >= 0 && select < foundDevices.size()) {
                System.out.println("Selected: " + new DecimalFormat("000").format(select) + ": MAC " + foundDevices.get(select).GetRootMac() + " " + foundDevices.get(select).toString());
                IPAddress = foundDevices.get(select).GetRxIpAddress().toString();
                if (IPAddress.indexOf("/") == 0) {
                    IPAddress = IPAddress.substring(1);
                }
                System.out.println("Using IP Address: " + IPAddress);
            } else {
                System.out.println("Invalid selection. Aborting!");
                System.exit(0);
            }
            //kbd.close();
        }

        if (bAutoRun) {
            System.out.println("Autorun active.");
            System.out.println("Programming!");
                    NetBurnerCoreUpdate.CoreUpdateAction(new DummyDoUpdate(),
                            IPAddress, tfFileName, RebootCheck, UserName, PassWord);
        } else {
            System.out.println("Start programming [Y/n]?");
            Scanner kbd = new Scanner(System.in);
            switch (kbd.nextLine()) {
                case "y":
                case "Y":
                case "":
                    System.out.println("Programming!");
                    NetBurnerCoreUpdate.CoreUpdateAction(new DummyDoUpdate(),
                            IPAddress, tfFileName, RebootCheck, UserName, PassWord);
                    break;
                default:
                    System.out.println("Aborting!");
                    System.exit(0);
            }
            //kbd.close();
        }

        //SaveDefaults();
        System.exit(0);
    }

    protected class DummyDoUpdate implements NetBurnerCoreUpdate.UpdateNotify {

        public Boolean doInBackground() {
            return Boolean.valueOf(true);
        }

        public void SetPercentDone(int percent_done) {
            System.out.print("\r"+percent_done+" %");
        }

        public void NotifyError(String message, int cause) {
            System.err.println(message);
        }

        public boolean ShouldAbort() {
            return false;
        }

        public void done() {
        }
    }

    protected class DummyDoFinder implements NetBurnerCoreFinder.FindNotify {

        public void FoundADevice(NetBurnerDevice param1NetBurnerDevice) {
        }

        public void FindAllDone() {
        }
    }

    /*class CheckFileExtension extends FileFilter {
    public boolean accept(File pathname) {
      if (pathname.isDirectory())
        return true; 
      
        return true; 
      return false;
    }
    
    public String getDescription() {
      return new String("NetBurner App Files");
    }
  }*/
 /*private void FINDActionPerformed(ActionEvent evt) {
    FindNetBurnerDevicesDlg find_dlg = new FindNetBurnerDevicesDlg(this, true);
    find_dlg.setVisible(true);
    if (find_dlg.IsValid()) {
      String s = find_dlg.SelectedDevice();
      int index = s.indexOf(" at ");
      if (index > 0)
        index += 4; 
      s = s.substring(index);
      index = s.indexOf(" ");
      s = s.substring(0, index);
      if (s.indexOf("/") == 0)
        s = s.substring(1); 
      this.IPAddress.setText(s);
    } 
  }*/
 /* private void UpdateActionPerformed(ActionEvent evt) {
    UpdateActionDialog udlg = new UpdateActionDialog(this, this.IPAddress.getText(), this.tfFileName.getText(), this.RebootCheck.isSelected(), this.UserName, this.PassWord);
    udlg.setVisible(true);
    if (udlg.Error_Cause == 5) {
      PassWordRequest pReq = new PassWordRequest(this, true);
      pReq.setVisible(true);
      if (pReq.m_bValid) {
        this.UserName = pReq.m_UserName;
        this.PassWord = pReq.m_Password;
        UpdateActionDialog udlgp = new UpdateActionDialog(this, this.IPAddress.getText(), this.tfFileName.getText(), this.RebootCheck.isSelected(), this.UserName, this.PassWord);
        udlgp.setVisible(true);
      } 
    } 
    if (this.bAutoRun && udlg.Error_Cause == 0) {
      SaveDefaults();
      System.exit(0);
    } 
  }*/
}
