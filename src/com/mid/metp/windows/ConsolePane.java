package com.mid.metp.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;

import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * @author defu
 */
public class ConsolePane extends JScrollPane {
	private PipedInputStream piOut;
	private PipedInputStream piErr;
	private PipedOutputStream poOut;
	private PipedOutputStream poErr;

	private JTextPane textPane = new JTextPane();

	private static ConsolePane console = null;

	public static synchronized ConsolePane getInstance() {
		if (console == null) {
			console = new ConsolePane();
		}
		return console;
	}

	private ConsolePane() {

		this.setViewportView(this.textPane);

		this.piOut = new PipedInputStream();
		this.piErr = new PipedInputStream();
		try {
			this.poOut = new PipedOutputStream(this.piOut);
			this.poErr = new PipedOutputStream(this.piErr);
		} catch (IOException e) {
		}

		// Set up System.out
		System.setOut(new PrintStream(this.poOut, true));

		// Set up System.err
		System.setErr(new PrintStream(this.poErr, true));

		this.textPane.setEditable(true);
		this.setPreferredSize(new Dimension(640, 120));

		// Create reader threads
		new ReaderThread(this.piOut).start();
		new ReaderThread(this.piErr).start();
	}

	/**
	 * Returns the number of lines in the document.
	 */
	public final int getLineCount() {
		return this.textPane.getDocument().getDefaultRootElement()
				.getElementCount();
	}

	/**
	 * Returns the start offset of the specified line.
	 * 
	 * @param line
	 *            The line
	 * @return The start offset of the specified line, or -1 if the line is
	 *         invalid
	 */
	public int getLineStartOffset(int line) {
		Element lineElement = this.textPane.getDocument()
				.getDefaultRootElement().getElement(line);
		if (lineElement == null) {
			return -1;
		} else {
			return lineElement.getStartOffset();
		}
	}

	public void replaceRange(String str, int start, int end) {
		if (end < start) {
			throw new IllegalArgumentException("end before start");
		}
		Document doc = this.textPane.getDocument();
		if (doc != null) {
			try {
				if (doc instanceof AbstractDocument) {
					((AbstractDocument) doc).replace(start, end - start, str,
							null);
				} else {
					doc.remove(start, end - start);
					doc.insertString(start, str, null);
				}
			} catch (BadLocationException e) {
				throw new IllegalArgumentException(e.getMessage());
			}
		}
	}

	class ReaderThread extends Thread {
		PipedInputStream pi;

		ReaderThread(PipedInputStream pi) {
			this.pi = pi;
		}

		@Override
		public void run() {
			final byte[] buf = new byte[1024];

			while (true) {
				try {
					final int len = this.pi.read(buf);
					if (len == -1) {
						break;
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							try {

								StyledDocument doc = (StyledDocument) ConsolePane.this.textPane
										.getDocument();

								// Create a style object and then set the style
								// attributes
								Style style = doc.addStyle("StyleName", null);

								Color foreground = ReaderThread.this.pi == ConsolePane.this.piOut ? Color.BLACK
										: Color.RED;
								// Foreground color
								StyleConstants.setForeground(style, foreground);

								// Append to document
								String outstr = new String(buf, 0, len);
								doc.insertString(doc.getLength(), outstr, style);

							} catch (BadLocationException e) {
								// e.printStackTrace();
							}

							// Make sure the last line is always visible
							ConsolePane.this.textPane
									.setCaretPosition(ConsolePane.this.textPane
											.getDocument().getLength());

							// Keep the text area down to a certain line count
							int idealLine = 150;
							int maxExcess = 50;

							int excess = ConsolePane.this.getLineCount()
									- idealLine;
							if (excess >= maxExcess) {
								ConsolePane.this.replaceRange("", 0,
										ConsolePane.this
												.getLineStartOffset(excess));
							}
						}
					});
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}
}
