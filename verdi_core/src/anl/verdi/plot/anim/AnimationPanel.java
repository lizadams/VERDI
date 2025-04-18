package anl.verdi.plot.anim;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import anl.verdi.plot.types.TimeAnimatablePlot;
import anl.verdi.plot.util.AnimationListener;
import anl.verdi.util.Utilities;

import com.jgoodies.forms.factories.DefaultComponentFactory;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.Sizes;
//import org.geotools.swing.JMapFrame;
import anl.verdi.data.Axes;
import anl.verdi.data.DataFrameAxis;


/**
 * @author User #2
 */
public class AnimationPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5856679324306579177L;

	private PlotAnimator animator;
	
	private static File lastChosenFolder;


	private class SpinnerListener implements ChangeListener {

		private JLabel label;

		public SpinnerListener(JLabel fld) {
			this.label = fld;
		}

		public void stateChanged(ChangeEvent e) {
			JSpinner source = (JSpinner) e.getSource();

			int val = ((Number) source.getValue()).intValue() - 1;
			if (axes != null) {
				GregorianCalendar date = axes.getDate(val);
				label.setText(Utilities.formatShortDate(date));
			}
		}
	}

	private Axes<DataFrameAxis> axes;
	private JDialog dialog;
	private TimeAnimatablePlot plot;
	private File movieFile, gifFile, aviFile;

	public AnimationPanel() {
		initComponents();
		initListeners();
	}

	private void initListeners() {
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				if (startBtn.getText().equals("Start")) {
					start();
				} else {
					if (animator != null) animator.stop();
					startBtn.setText("Start");
				}
			}
		});

		gifChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				gifFileBtn.setEnabled(gifChk.isSelected());
				gifFileLbl.setEnabled(gifChk.isSelected());
				if (gifChk.isSelected() && gifFile == null) {
					getGifFile();
				}
			}
		});

		gifFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getGifFile();
			}
		});
		
		aviChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				aviFileBtn.setEnabled(aviChk.isSelected());
				aviFileLbl.setEnabled(aviChk.isSelected());
				if (aviChk.isSelected() && aviFile == null) {
					getAviFile();
				}
			}
		});

		aviFileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getAviFile();
			}
		});

		movieChk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				fileBtn.setEnabled(movieChk.isSelected());
				fileLbl.setEnabled(movieChk.isSelected());
				if (movieChk.isSelected() && movieFile == null) {
					getMovieFile();
				}
			}
		});

		fileBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				getMovieFile();
			}
		});
	}

	private void getGifFile() {
		JFileChooser chooser = new JFileChooser() {
			public void approveSelection() {
				File f = getSelectedFile();
				String name = f.getName();
				if (name.indexOf(".") == -1)
					f = new File(f.getAbsolutePath() + ".gif");
				if (f.exists()) {
					int result = JOptionPane.showConfirmDialog(this, f.getName() +  " exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					default:
						return;
					}
				}
				super.approveSelection();				
			}
		};
		
		if (lastChosenFolder != null)
			chooser.setCurrentDirectory(lastChosenFolder);
		
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {

			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				return f.getName().toLowerCase().endsWith(".gif");
			}

			public String getDescription() {
				return "GIF (*.gif)";
			}
		});

		File f = null;
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			f = chooser.getSelectedFile();
			if (f != null) {
				if (!f.getName().toLowerCase().endsWith(".gif")) {
					f = new File(f.getAbsolutePath() + ".gif");
				}
				gifFile = f;
				gifFileLbl.setText(gifFile.getName());
				lastChosenFolder = gifFile.getParentFile();
			}
		}
		if (f == null && gifFile == null) {
			gifFileBtn.setSelected(false);
			gifChk.setSelected(false);
		}
	}
	
	private void getAviFile() {
		JFileChooser chooser = new JFileChooser() {
			public void approveSelection() {
				File f = getSelectedFile();
				String name = f.getName();
				if (name.indexOf(".") == -1)
					f = new File(f.getAbsolutePath() + ".avi");
				if (f.exists()) {
					int result = JOptionPane.showConfirmDialog(this, f.getName() +  " exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					default:
						return;
					}
				}
				super.approveSelection();				
			}
		};
		
		if (lastChosenFolder != null)
			chooser.setCurrentDirectory(lastChosenFolder);
		
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {

			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				return f.getName().toLowerCase().endsWith(".avi");
			}

			public String getDescription() {
				return "Audio Video Interleave (*.avi)";
			}
		});

		File f = lastChosenFolder;
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			f = chooser.getSelectedFile();
			if (f != null) {
				if (!f.getName().endsWith(".avi")) {
					f = new File(f.getAbsolutePath() + ".avi");
				}
				aviFile = f;
				aviFileLbl.setText(aviFile.getName());
				lastChosenFolder = aviFile.getParentFile();
			}
		}
		if (f == null && aviFile == null) {
			aviFileBtn.setSelected(false);
			aviChk.setSelected(false);
		}
	}

	private void getMovieFile() {
		JFileChooser chooser = new JFileChooser() {
			public void approveSelection() {
				File f = getSelectedFile();
				String name = f.getName();
				if (name.indexOf(".") == -1)
					f = new File(f.getAbsolutePath() + ".mp4");
				if (f.exists()) {
					int result = JOptionPane.showConfirmDialog(this, f.getName() +  " exists, overwrite?", "Existing file", JOptionPane.YES_NO_CANCEL_OPTION);
					switch (result) {
					case JOptionPane.YES_OPTION:
						super.approveSelection();
						return;
					case JOptionPane.CANCEL_OPTION:
						cancelSelection();
						return;
					default:
						return;
					}
				}
				super.approveSelection();				
			}
		};
		
		if (lastChosenFolder != null)
			chooser.setCurrentDirectory(lastChosenFolder);
		
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileFilter(new FileFilter() {

			public boolean accept(File f) {
				if (f.isDirectory()) return true;
				return f.getName().toLowerCase().endsWith(".mp4");
			}

			public String getDescription() {
				return "MPEG-4 (*.mp4)";
			}
		});

		File f = null;
		if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			f = chooser.getSelectedFile();
			if (f != null) {
				if (!f.getName().endsWith(".mp4")) {
					f = new File(f.getAbsolutePath() + ".mp4");
				}
				movieFile = f;
				fileLbl.setText(movieFile.getName());
				lastChosenFolder = movieFile.getParentFile();
			}
		}
		if (f == null && movieFile == null) {
			fileBtn.setSelected(false);
			movieChk.setSelected(false);
		}
	}

	private void start() {
		startBtn.setText("Stop");
		int min = ((Integer) minSpinner.getValue()).intValue() - 1;
		int max = ((Integer) maxSpinner.getValue()).intValue() - 1;
		int end = Math.max(min, max) - axes.getTimeAxis().getOrigin();
		int start = Math.min(min, max) - axes.getTimeAxis().getOrigin();

		animator = new PlotAnimator(plot);
		File mFile = movieFile;
		File gFile = gifFile;
		File avFile = aviFile;
		
		if (!movieChk.isSelected()) mFile = null;
		if (!gifChk.isSelected()) gFile = null;
		if (!aviChk.isSelected()) avFile = null;
		
		animator.addAnimationListener(new AnimationListener() {
			public void animationStopped() {
				startBtn.setText("Start");
			}
		});
		animator.start(start, end, mFile, gFile, avFile);
	}

	public void init(Axes<DataFrameAxis> axes, TimeAnimatablePlot plot) {
		this.axes = axes;
		this.plot = plot;
		DataFrameAxis time = axes.getTimeAxis();
		this.axes = axes;
		int min = (int) (time.getRange().getOrigin() + 1);
		int max = min + (int) time.getRange().getExtent() - 1;

		SpinnerNumberModel model = (SpinnerNumberModel) minSpinner.getModel();
		model.setMinimum(min);
		model.setMaximum(max);
		model = (SpinnerNumberModel) maxSpinner.getModel();
		model.setMinimum(min);
		model.setMaximum(max);
		minSpinner.setValue(new Integer(min));
		maxSpinner.setValue(new Integer(max));
		maxDate.setText(Utilities.formatShortDate(axes.getDate(max - 1)));
		minDate.setText(Utilities.formatShortDate(axes.getDate(min - 1)));

		minSpinner.addChangeListener(new SpinnerListener(minDate));
		maxSpinner.addChangeListener(new SpinnerListener(maxDate));

		Window window = SwingUtilities.getWindowAncestor(plot.getPanel());

		// NOTE: a JMapFrame should be an instance of a JFrame because JMapFrame extends JFrame
		if (window instanceof JFrame) 
			dialog = new JDialog((JFrame) window, "Animate Plot", false);
//		if (window instanceof JFrame) dialog = new JDialog((JMapFrame) window, "Animate Plot", false);
		else 
			dialog = new JDialog((JDialog) window, "Animate Plot", false);

		dialog.setLayout(new BorderLayout());
		dialog.add(this, BorderLayout.CENTER);
		dialog.setLocationRelativeTo(plot.getPanel());
		dialog.pack();
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		Point p = dialog.getLocation();
		dialog.setLocation(0, p.y);
		dialog.setVisible(true);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner non-commercial license
		DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
		label1 = compFactory.createLabel("Starting Time Step:");
		minSpinner = new JSpinner();
		label2 = compFactory.createLabel("Ending Time Step:");
		maxSpinner = new JSpinner();
		minDate = compFactory.createLabel("");
		movieChk = new JCheckBox();
		maxDate = compFactory.createLabel("");
		gifChk = new JCheckBox();
		gifFileLbl = new JLabel();
		gifFileBtn = new JButton();
		aviChk = new JCheckBox();
		aviChk.setEnabled(false);
		aviFileLbl = new JLabel();
		aviFileBtn = new JButton();
		fileLbl = new JLabel();
		fileBtn = new JButton();
		separator1 = compFactory.createSeparator("");
		panel1 = new JPanel();
		startBtn = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setBorder(new TitledBorder("Animate Plot"));
		// 2014
		ColumnSpec[] aColumnSpec = ColumnSpec.decodeSpecs("max(pref;40dlu)");
		ColumnSpec bColumnSpec = new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW);
		setLayout(new FormLayout(
						new ColumnSpec[]{
										FormFactory.DEFAULT_COLSPEC,
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										aColumnSpec[0],
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										bColumnSpec,
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										FormFactory.PREF_COLSPEC
						},
						new RowSpec[]{
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.LINE_GAP_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.LINE_GAP_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.LINE_GAP_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.LINE_GAP_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC,
										FormFactory.LINE_GAP_ROWSPEC,
										FormFactory.PREF_ROWSPEC,
										FormFactory.LINE_GAP_ROWSPEC,
										FormFactory.DEFAULT_ROWSPEC
						}));
//		setLayout(new FormLayout(
//				new ColumnSpec[]{
//								FormFactory.DEFAULT_COLSPEC,
//								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
//								new ColumnSpec("max(pref;40dlu)"),
//								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
//								new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
//								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
//								FormFactory.PREF_COLSPEC
//				},
//				new RowSpec[]{
//								FormFactory.DEFAULT_ROWSPEC,
//								FormFactory.LINE_GAP_ROWSPEC,
//								FormFactory.DEFAULT_ROWSPEC,
//								FormFactory.LINE_GAP_ROWSPEC,
//								FormFactory.DEFAULT_ROWSPEC,
//								FormFactory.LINE_GAP_ROWSPEC,
//								FormFactory.DEFAULT_ROWSPEC,
//								FormFactory.LINE_GAP_ROWSPEC,
//								FormFactory.DEFAULT_ROWSPEC,
//								FormFactory.LINE_GAP_ROWSPEC,
//								FormFactory.PREF_ROWSPEC,
//								FormFactory.LINE_GAP_ROWSPEC,
//								FormFactory.DEFAULT_ROWSPEC
//				}));
		add(label1, cc.xy(1, 1));
		add(minSpinner, cc.xy(3, 1));
		add(label2, cc.xy(1, 3));
		add(maxSpinner, cc.xy(3, 3));

		//---- minDate ----
		minDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(minDate, cc.xywh(5, 1, 3, 1));

		//---- movieChk ----
		movieChk.setText("Make Movie");
		add(movieChk, cc.xy(1, 9));

		//---- maxDate ----
		maxDate.setFont(new Font("Tahoma", Font.BOLD, 11));
		add(maxDate, cc.xywh(5, 3, 3, 1));

		//---- gifChk ----
		gifChk.setText("Make Animated GIF");
		add(gifChk, cc.xy(1, 5));
		add(gifFileLbl, cc.xywh(3, 5, 3, 1));
		
		//---- aviChk ----
		aviChk.setText("Make AVI");
		add(aviChk, cc.xy(1, 7));
		add(aviFileLbl, cc.xywh(3, 7, 3, 1));

		//---- gifFileBtn ----
		gifFileBtn.setText("...");
		gifFileBtn.setMaximumSize(new Dimension(23, 23));
		gifFileBtn.setMinimumSize(new Dimension(23, 23));
		gifFileBtn.setPreferredSize(new Dimension(23, 23));
		gifFileBtn.setEnabled(false);
		add(gifFileBtn, cc.xy(7, 5));
		
		//---- aviFileBtn ----
		aviFileBtn.setText("...");
		aviFileBtn.setMaximumSize(new Dimension(23, 23));
		aviFileBtn.setMinimumSize(new Dimension(23, 23));
		aviFileBtn.setPreferredSize(new Dimension(23, 23));
		aviFileBtn.setEnabled(false);
		add(aviFileBtn, cc.xy(7, 7));

		//---- fileLbl ----
		fileLbl.setEnabled(false);
		add(fileLbl, cc.xywh(3, 9, 3, 1));

		//---- fileBtn ----
		fileBtn.setText("...");
		fileBtn.setEnabled(false);
		fileBtn.setMaximumSize(new Dimension(23, 23));
		fileBtn.setMinimumSize(new Dimension(23, 23));
		fileBtn.setPreferredSize(new Dimension(23, 23));
		add(fileBtn, cc.xy(7, 9));
		add(separator1, cc.xywh(1, 11, 7, 1));

		//======== panel1 ========
		{
			// 2014
			ColumnSpec cColumnSpec = new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW);
			RowSpec[] aRowSpec = RowSpec.decodeSpecs("default");
			panel1.setLayout(new FormLayout(
							new ColumnSpec[]{
											cColumnSpec,
											FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
											FormFactory.DEFAULT_COLSPEC,
											FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
											FormFactory.DEFAULT_COLSPEC
							},
							aRowSpec));
//			panel1.setLayout(new FormLayout(
//					new ColumnSpec[]{
//									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
//									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
//									FormFactory.DEFAULT_COLSPEC,
//									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
//									FormFactory.DEFAULT_COLSPEC
//					},
//					RowSpec.decodeSpecs("default")));

			//---- startBtn ----
			startBtn.setText("Start");
			panel1.add(startBtn, cc.xy(5, 1));
		}
		add(panel1, cc.xywh(1, 13, 7, 1));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner non-commercial license
	private JLabel label1;
	private JSpinner minSpinner;
	private JLabel label2;
	private JSpinner maxSpinner;
	private JLabel minDate;
	private JCheckBox movieChk;
	private JLabel maxDate;
	private JCheckBox gifChk;
	private JLabel gifFileLbl;
	private JButton gifFileBtn;
	private JLabel fileLbl;
	private JButton fileBtn;
	private JCheckBox aviChk;
	private JLabel aviFileLbl;
	private JButton aviFileBtn;
	private JComponent separator1;
	private JPanel panel1;
	private JButton startBtn;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
