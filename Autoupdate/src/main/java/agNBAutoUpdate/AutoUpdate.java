package agNBAutoUpdate;

public class AutoUpdate {

    public static void main(String[] args) {

        System.out.print("ag AutoUpdate ");
        System.out.println("v1.0.0");
        System.out.println("This is based on the Java code for the Netburner Autoupdate for OS X (autoupdate-java.zip) from October 2018.");
        System.out.println("Add -? for usage and help");

        boolean noGui = false;

        for (int i = 0; i < args.length; i++) {
            if (args[i].charAt(0) == '-') {
                switch (args[i].charAt(1)) {
                    case 'C':
                    case 'c':
                        noGui = true;
                        break;
                    default:

                }
            }

        }
        if (noGui) {
            AutoUpdateNoGui alng = new AutoUpdateNoGui(args);
        } else {
            AutoUpdateDlg alg = new AutoUpdateDlg(args);
            alg.setTitle("ag AutoUpdate");
            alg.setVisible(true);
        }
    }
}
