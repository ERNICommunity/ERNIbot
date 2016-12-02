package de.erni.confluenceapi;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LoginView {

	private Crawler crawler;
	private JTextField userNameInput;
	private JPasswordField passwordInput;
	private JFrame guiFrame;
	private JPanel userNamePanel;
	private JPanel loginPanel;
	private JButton loginButton;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void init() {
		crawler = new Crawler();
		guiFrame = new JFrame();

		// make sure the program exits when the frame closes
		guiFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		guiFrame.setTitle("Example GUI");
		guiFrame.setSize(300, 130);
		guiFrame.getContentPane().setLayout(new BorderLayout());
		
		userNamePanel = new JPanel();
        guiFrame.getContentPane().add(userNamePanel, BorderLayout.NORTH);
        userNamePanel.setLayout(new GridLayout(0, 2, 0, 0));
        
        userNameLabel = new JLabel("Benutzername");
        userNamePanel.add(userNameLabel);
        
        userNameInput = new JTextField();
        userNameInput.setColumns(15);
        userNamePanel.add(userNameInput);
        
        passwordLabel = new JLabel("Passwort");
        userNamePanel.add(passwordLabel);
        
        passwordInput = new JPasswordField();
        userNamePanel.add(passwordInput);
        passwordInput.setColumns(15);
        
        loginPanel = new JPanel();
        guiFrame.getContentPane().add(loginPanel, BorderLayout.SOUTH);
        loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        loginButton = new JButton("Bestätigen");
        loginButton.setHorizontalAlignment(SwingConstants.LEFT);
        loginPanel.add(loginButton, BorderLayout.EAST);
		
		loginButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {

				crawler.setUsername(userNameInput.getText());
				crawler.setPassword(passwordInput.getText());
				try {
					crawler.crawl();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				guiFrame.dispose();
			}
		});
        
        
		guiFrame.setVisible(true);
	}

}
