package com.csvgenerator.java;

import java.awt.Color;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JList;
import java.awt.Font;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import java.awt.Insets;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.optionalusertools.DateChangeListener;
import com.github.lgooddatepicker.zinternaltools.DateChangeEvent;
import java.awt.SystemColor;
import javax.swing.UIManager;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Frame1 {
	private JFrame frmDailyAttendanceAssistant;
	private JFileChooser openFileChooser;
	private int threshold;
	private JList jList;
	private BufferedReader bf2;
	private JLabel lblFileChosen;
	private JLabel lblAbsent;
	LocalDate startDate = LocalDate.now();
	LocalDate endDate = LocalDate.now();
	String fullPathToExportedEdhesiveFile="";
	DefaultListModel<String> dlm = new DefaultListModel<String>();
	String classSelected = "";


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Frame1 window = new Frame1();
					window.frmDailyAttendanceAssistant.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Frame1() {
		initialize();
		openFileChooser = new JFileChooser();
		openFileChooser.setFileFilter(new FileNameExtensionFilter("Comma Seperator Values", "csv"));
		dlm.addElement("CSA");
		dlm.addElement("CSP");
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDailyAttendanceAssistant = new JFrame();
		frmDailyAttendanceAssistant.setBackground(SystemColor.menu);
		frmDailyAttendanceAssistant.getContentPane().setBackground(new Color(248, 248, 255));
		frmDailyAttendanceAssistant.setTitle("Edhesive - Schoology CSV Generator");
		frmDailyAttendanceAssistant.setResizable(false);
		frmDailyAttendanceAssistant.setBounds(100, 100, 450, 361);
		frmDailyAttendanceAssistant.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmDailyAttendanceAssistant.getContentPane().setLayout(null);

		JButton btnNewButton = new JButton("Open File...");	/////////////////////// open file
		btnNewButton.setBackground(SystemColor.activeCaption);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int returnValue = openFileChooser.showOpenDialog(frmDailyAttendanceAssistant);
				
				if (returnValue == JFileChooser.APPROVE_OPTION) {
					lblFileChosen.setText(openFileChooser.getSelectedFile().getAbsolutePath());
					fullPathToExportedEdhesiveFile = openFileChooser.getSelectedFile().getAbsolutePath();
				}else {
					lblFileChosen.setText("No File Chosen!");
				}
			}
		});
		btnNewButton.setBounds(181, 150, 180, 23);
		frmDailyAttendanceAssistant.getContentPane().add(btnNewButton);

		lblFileChosen = new JLabel("No File Chosen Yet!");
		lblFileChosen.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblFileChosen.setForeground(new Color(128, 128, 128));
		lblFileChosen.setHorizontalAlignment(SwingConstants.CENTER);
		lblFileChosen.setBounds(181, 176, 180, 16);
		frmDailyAttendanceAssistant.getContentPane().add(lblFileChosen);

		jList = new JList();
		jList.setModel(dlm);
		jList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				classSelected = dlm.get(jList.getSelectedIndex());
				System.out.println(dlm.get(jList.getSelectedIndex()));
			}
		});
		
		
		jList.setVisibleRowCount(15);
		jList.setFont(new Font("Tahoma", Font.PLAIN, 14));
		jList.setBounds(181, 203, 180, 44);
		frmDailyAttendanceAssistant.getContentPane().add(jList);

		JLabel lblNewLabel = new JLabel("Class");
		lblNewLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel.setBounds(39, 213, 97, 23);
		frmDailyAttendanceAssistant.getContentPane().add(lblNewLabel);

		lblAbsent = new JLabel("Edhesive Csv File");
		lblAbsent.setHorizontalAlignment(SwingConstants.RIGHT);
		lblAbsent.setBounds(39, 153, 97, 16);
		frmDailyAttendanceAssistant.getContentPane().add(lblAbsent);
		
		final DatePicker datePickerStart = new DatePicker();
		datePickerStart.getComponentToggleCalendarButton().setBackground(UIManager.getColor("InternalFrame.inactiveTitleGradient"));
		datePickerStart.addDateChangeListener(new DateChangeListener() {
			public void dateChanged(DateChangeEvent arg0) {                                                       // start date
				startDate = LocalDate.parse(datePickerStart.getDateStringOrSuppliedString("(null)"));
			}
		});
		datePickerStart.setText("Start Date");
		datePickerStart.setBounds(181, 39, 180, 30);
		frmDailyAttendanceAssistant.getContentPane().add(datePickerStart);
		
		JLabel lblStartDate = new JLabel("Start Date");
		lblStartDate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblStartDate.setBounds(39, 45, 97, 14);
		frmDailyAttendanceAssistant.getContentPane().add(lblStartDate);
		
		final DatePicker datePickerEnd = new DatePicker();
		datePickerEnd.getComponentDateTextField().setColumns(3);
		datePickerEnd.getComponentToggleCalendarButton().setBackground(UIManager.getColor("InternalFrame.inactiveTitleGradient"));
		datePickerEnd.addDateChangeListener(new DateChangeListener() {											// end date
			public void dateChanged(DateChangeEvent arg0) {
				endDate = LocalDate.parse(datePickerEnd.getDateStringOrSuppliedString("(null)"));
			}
		});
		datePickerEnd.getComponentDateTextField().setText("enter start date");
		datePickerEnd.setText("End Date");
		datePickerEnd.setBounds(181, 95, 180, 30);
		frmDailyAttendanceAssistant.getContentPane().add(datePickerEnd);
		
		JLabel lblEndDate = new JLabel("End Date");
		lblEndDate.setHorizontalAlignment(SwingConstants.RIGHT);
		lblEndDate.setBounds(39, 102, 97, 14);
		frmDailyAttendanceAssistant.getContentPane().add(lblEndDate);
		
		JLabel lblNewLabel_1 = new JLabel("@Demirel");
		lblNewLabel_1.setForeground(new Color(105, 105, 105));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.RIGHT);
		lblNewLabel_1.setBounds(353, 307, 81, 14);
		frmDailyAttendanceAssistant.getContentPane().add(lblNewLabel_1);
		
		JButton btnGenerate = new JButton("Generate");
		btnGenerate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CsvController csv = new CsvController(startDate,endDate,fullPathToExportedEdhesiveFile, classSelected);

			}
		});
		btnGenerate.setBounds(181, 258, 180, 34);
		frmDailyAttendanceAssistant.getContentPane().add(btnGenerate);
//		takeAttendanceByFileLatestModified(null);
	}
	
	@SuppressWarnings("resource")
	private boolean isParticipantListFileValid(String fullPath) {
		return true;
	}
	
	private String getJarPath() {
		String rosterPath = System.getProperty("java.class.path").substring(0,
				System.getProperty("java.class.path").lastIndexOf("\\") + 1);
		String[] rosterPaths = rosterPath.split(";");
		rosterPath = rosterPaths[0].substring(0, rosterPaths[0].lastIndexOf("\\") + 1);
		return rosterPath;
	}
}
