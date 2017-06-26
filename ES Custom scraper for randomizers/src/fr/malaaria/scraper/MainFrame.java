package fr.malaaria.scraper;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JRadioButton;
import java.awt.BorderLayout;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class MainFrame {

	private JFrame frmScraperForRandomizers;

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
		frmScraperForRandomizers.setBounds(100, 100, 450, 300);
		frmScraperForRandomizers.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JRadioButton rdbtnEmulationstation = new JRadioButton("EmulationStation");
		rdbtnEmulationstation.setSelected(true);
		frmScraperForRandomizers.getContentPane().add(rdbtnEmulationstation, BorderLayout.NORTH);
		
		JButton btnScrape = new JButton("Scrape");
		btnScrape.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Zelda1Scraper z1s = new Zelda1Scraper();
				z1s.doScrapeSAX();
			}
		});
		frmScraperForRandomizers.getContentPane().add(btnScrape, BorderLayout.SOUTH);
	}

}
