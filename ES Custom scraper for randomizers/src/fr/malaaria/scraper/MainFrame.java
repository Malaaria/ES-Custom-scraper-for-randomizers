package fr.malaaria.scraper;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class MainFrame {

	private JFrame frmScraperForRandomizers;	
	private JTextArea textAreaLog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainFrame window = new MainFrame();
					window.frmScraperForRandomizers.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmScraperForRandomizers = new JFrame();
		frmScraperForRandomizers.setTitle("Scraper for Randomizers");
		frmScraperForRandomizers.setBounds(100, 100, 654, 300);
		frmScraperForRandomizers.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JRadioButton rdbtnEmulationstation = new JRadioButton("EmulationStation");
		rdbtnEmulationstation.setSelected(true);
		frmScraperForRandomizers.getContentPane().add(rdbtnEmulationstation, BorderLayout.NORTH);

		JButton btnScrape = new JButton("Scrape");
		btnScrape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {				   
				SWCustom worker = new SWCustom(textAreaLog);					
				
				worker.execute();

			}
		});
		frmScraperForRandomizers.getContentPane().add(btnScrape, BorderLayout.SOUTH);
		
		textAreaLog = new JTextArea();
		JScrollPane scrollPane = new JScrollPane(textAreaLog);		
		frmScraperForRandomizers.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		

		
	}

	protected void addLogtext(String ptext){
		textAreaLog.append(ptext + "\n");
	}

}
