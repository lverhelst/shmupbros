/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package Game;

import Ai.Rule;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

/**
 *
 * @author Emery
 */
public class Launcher extends javax.swing.JFrame {
    
    /**
     * Creates new form Launcher
     */
    public Launcher() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //initiate
        Settings.loadConfig();
        initComponents();
        
        //set loaded settings
        playerName.setText(Settings.playerName);
        showPath.setSelected(Settings.showPath);
        showRay.setSelected(Settings.showRay);
        showSearchSpace.setSelected(Settings.showSearchSpace);
        numberBots.setValue(Settings.numBots);
    }
    
    public String[] getList() {
        String[] list = new String[Settings.rules.size()];
        int i = 0;
        
        for(Rule rule: Settings.rules) {
            list[i++] = rule.getSet() + " " + rule.getName();
        }
        
        return list;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        launch = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        playerName = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        showPath = new javax.swing.JRadioButton();
        showRay = new javax.swing.JRadioButton();
        showSearchSpace = new javax.swing.JRadioButton();
        numberBots = new javax.swing.JSpinner();
        jComboBox1 = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        launch.setText("Launch");
        launch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                launchActionPerformed(evt);
            }
        });

        jLabel1.setText("Player Name:");

        playerName.setText("Player 1");

        jLabel5.setText("Number of Bots:");

        showPath.setText("Show Path");
        showPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showPathActionPerformed(evt);
            }
        });

        showRay.setText("Show Ray");
        showRay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRayActionPerformed(evt);
            }
        });

        showSearchSpace.setText("Show Search Path");
        showSearchSpace.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showSearchSpaceActionPerformed(evt);
            }
        });

        numberBots.setModel(new javax.swing.SpinnerNumberModel(0, 0, 10, 1));

        jComboBox1.setModel(new DefaultComboBoxModel(getList()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(launch))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(showSearchSpace)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(showRay)
                                            .addComponent(showPath)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel5)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(numberBots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(playerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addGap(657, 657, 657)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(85, 85, 85)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(playerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(numberBots, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showPath)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(showRay)
                .addGap(5, 5, 5)
                .addComponent(showSearchSpace)
                .addGap(18, 18, 18)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 220, Short.MAX_VALUE)
                .addComponent(launch)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void launchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_launchActionPerformed
        Settings.playerName = playerName.getText();
        Settings.showPath = showPath.isSelected();
        Settings.showRay = showRay.isSelected();
        Settings.showSearchSpace = showSearchSpace.isSelected();
        Settings.numBots = (int)numberBots.getValue();        
        
        try {
            AppGameContainer app = new AppGameContainer(new StateManager());

            app.setDisplayMode(1024, 600, false);
            app.setShowFPS(true);
            app.setAlwaysRender(true); // Display all frames even when not in focus

            dispose();
            app.start();
        } catch (SlickException e) {

        }
    }//GEN-LAST:event_launchActionPerformed

    private void showPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showPathActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showPathActionPerformed

    private void showRayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRayActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showRayActionPerformed

    private void showSearchSpaceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showSearchSpaceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showSearchSpaceActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Launcher().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JButton launch;
    private javax.swing.JSpinner numberBots;
    private javax.swing.JTextField playerName;
    private javax.swing.JRadioButton showPath;
    private javax.swing.JRadioButton showRay;
    private javax.swing.JRadioButton showSearchSpace;
    // End of variables declaration//GEN-END:variables
}
