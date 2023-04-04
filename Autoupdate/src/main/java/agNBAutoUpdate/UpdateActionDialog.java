package agNBAutoUpdate;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.LayoutStyle;
import javax.swing.SwingWorker;

public class UpdateActionDialog extends JDialog {

    String m_sIP;

    String m_sFileName;

    boolean m_bReboot;

    String m_sUserName;

    String m_sPassWord;

    SwingWorker TheWorker;

    String Error_Msg;

    int Error_Cause;

    private JButton CancelButton;

    private JLabel MessageLabel;

    private JProgressBar ProgressBar;

    protected class DoUpdate extends SwingWorker<Boolean, Integer> implements NetBurnerCoreUpdate.UpdateNotify {

        public Boolean doInBackground() {
            NetBurnerCoreUpdate.CoreUpdateAction(this, UpdateActionDialog.this.m_sIP, UpdateActionDialog.this.m_sFileName, UpdateActionDialog.this.m_bReboot, UpdateActionDialog.this.m_sUserName, UpdateActionDialog.this.m_sPassWord);
            return Boolean.valueOf(true);
        }

        public void SetPercentDone(int percent_done) {
            setProgress(percent_done);
            UpdateActionDialog.this.MessageLabel.setText(String.valueOf(percent_done) + " Percent complete");
        }

        public void NotifyError(String message, int cause) {
            UpdateActionDialog.this.Error_Msg = message;
            UpdateActionDialog.this.Error_Cause = cause;
            UpdateActionDialog.this.MessageLabel.setText(message);
            UpdateActionDialog.this.TheWorker.cancel(true);
        }

        public boolean ShouldAbort() {
            return isCancelled();
        }

        public void done() {
            UpdateActionDialog.this.AllDoneCleanup();
            if (UpdateActionDialog.this.Error_Msg == null) {
                JOptionPane.showMessageDialog(null, "Programming completed without Error");
            } else if (UpdateActionDialog.this.Error_Cause != 5) {
                JOptionPane.showMessageDialog(null, UpdateActionDialog.this.Error_Msg);
            }
        }
    }

    public UpdateActionDialog(Frame parent, String sIP, String sFileName, boolean bReboot, String sUserName, String sPassWord) {
        super(parent, true);
        this.m_sIP = sIP;
        this.m_sFileName = sFileName;
        this.m_bReboot = bReboot;
        this.m_sUserName = sUserName;
        this.m_sPassWord = sPassWord;
        this.Error_Cause = 0;
        initComponents();
        this.ProgressBar.setValue(0);
        this.TheWorker = new DoUpdate();
        this.TheWorker.addPropertyChangeListener(
                new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if ("progress".equals(evt.getPropertyName())) {
                    UpdateActionDialog.this.ProgressBar.setValue(((Integer) evt.getNewValue()).intValue());
                }
            }
        });
        this.TheWorker.execute();
    }

    private void initComponents() {
        this.CancelButton = new JButton();
        this.ProgressBar = new JProgressBar();
        this.MessageLabel = new JLabel();
        setDefaultCloseOperation(2);
        this.CancelButton.setText("Cancel");
        this.CancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                UpdateActionDialog.this.CancelButtonActionPerformed(evt);
            }
        });
        this.MessageLabel.setHorizontalAlignment(0);
        this.MessageLabel.setText("Status");
        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(452, 32767)
                                .addComponent(this.CancelButton))
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(this.MessageLabel, -1, 483, 32767)
                                .addContainerGap())
                        .addGroup(GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(24, 24, 24)
                                .addComponent(this.ProgressBar, -1, 483, 32767)
                                .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(this.ProgressBar, -2, 27, -2)
                                .addGap(7, 7, 7)
                                .addComponent(this.MessageLabel, -1, 19, 32767)
                                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(this.CancelButton)
                                .addContainerGap()));
        pack();
    }

    protected void AllDoneCleanup() {
        setVisible(false);
        dispose();
    }

    private void CancelButtonActionPerformed(ActionEvent evt) {
        if (this.Error_Msg == null) {
            this.Error_Msg = "User Aborted";
        }
        this.TheWorker.cancel(true);
    }
}
