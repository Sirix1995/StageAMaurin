
/*
 * Copyright (C) 2002 - 2007 Lehrstuhl Grafische Systeme, BTU Cottbus
 * 
 * Based on class bsh.util.JConsole, written by the following author:
 * Patrick Niemeyer (pat@pat.net)
 * Author of Learning Java, O'Reilly & Associates
 * http://www.pat.net/~pat/
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package	de.grogra.pf.ui.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.Vector;
import javax.swing.text.*;
import javax.swing.*;
import de.grogra.pf.ui.*;
import de.grogra.pf.ui.Console;
import de.grogra.util.Utils;

// Things that are not in the core packages

/**
	A JFC/Swing based console for the BeanShell desktop.
	This is a descendant of the old AWTConsole.

	Improvements by: Mark Donszelmann <Mark.Donszelmann@cern.ch>
		including Cut & Paste

  	Improvements by: Daniel Leuck
		including Color and Image support, key press bug workaround
		
	Adaptation for GroIMP by Ole Kniemeyer
*/
public class JConsole extends PanelSupport
	implements Console, KeyListener,
	MouseListener, ActionListener, PropertyChangeListener 
{
    private final static String	CUT = "Cut";
    private final static String	COPY = "Copy";
    private final static String	PASTE =	"Paste";
    private final static String	CLEAR =	"Clear";

	private final CWriter out;
	private final CWriter err;
	
	private	final Reader in;
	private	PipedWriter inWriter;

    private int	cmdStart = 0;
	private	Vector history = new Vector();
	private	String startedLine;
	private	int histLine = 0;

    private JPopupMenu menu;
    private JTextPane text;
    
    private int maxBufferLength = 10000;
    
    private final Object lock = new Object ();

	NameCompletion nameCompletion;
	final int SHOW_AMBIG_MAX = 10;

	// hack to prevent key repeat for some reason?
    private boolean gotUp = true;
    
    
    private class ColorWriter extends Writer
	{
		Color color;

		private StringBuffer buffer = new StringBuffer ();
		
		ColorWriter (Color color)
		{
			this.color = color;
		}

    	@Override
		public synchronized void write (char[] buf, int ofs, int len)
    	{
    		buffer.append (buf, ofs, len);
    	}

    	@Override
		public synchronized void flush ()
    	{
    		final String s = buffer.toString ();
    		final Color c = color;
    		buffer.setLength (0);
    		EventQueue.invokeLater (new Runnable ()
    		{
    			public void run ()
    			{
    				JConsole.this.print (s, c);
    				JConsole.this.checkLimit ();
    			}
    		});
    	}

    	@Override
		public void close ()
    	{
    	}
	}
    
    private class CWriter extends ConsoleWriter
	{
    	private final Color defColor;

		CWriter (Color color)
		{
    		super (new ColorWriter (color), true);
    		this.defColor = color;
		}

		@Override
		public void print (Object text, int color)
		{
			flush ();
			((ColorWriter) out).color = Utils.getApproximateColor (color);
			print (text);
			flush ();
			((ColorWriter) out).color = defColor;
		}

		@Override
		public void println (Object text, int color)
		{
			flush ();
			((ColorWriter) out).color = Utils.getApproximateColor (color);
			println (text);
			((ColorWriter) out).color = defColor;
		}
	}


	public JConsole ()
	{
		super (new SwingPanel (null));

		JScrollPane sp = new JScrollPane ();
		// Special TextPane which catches for cut and paste, both L&F keys and
		// programmatic	behaviour
		text = new JTextPane( new DefaultStyledDocument() ) 
			{
				@Override
				public void	cut() {
					if (text.getCaretPosition() < cmdStart)	{
						super.copy();
					} else {
						super.cut();
					}
				}

				@Override
				public void	paste()	{
					forceCaretMoveToEnd();
					super.paste();
				}
			};

		Font font = new	Font("Monospaced",Font.PLAIN,12);
		text.setText("");
		text.setFont( font );
		text.setMargin(	new Insets(7,5,7,5) );
		text.addKeyListener(this);
		sp.setViewportView(text);

		// create popup	menu
		menu = new JPopupMenu("JConsole	Menu");
		menu.add(new JMenuItem(CLEAR)).addActionListener(this);
		menu.add(new JMenuItem(CUT)).addActionListener(this);
		menu.add(new JMenuItem(COPY)).addActionListener(this);
		menu.add(new JMenuItem(PASTE)).addActionListener(this);

		text.addMouseListener(this);

		// make	sure popup menu	follows	Look & Feel
		UIManager.addPropertyChangeListener(this);
		
		out = new CWriter (Color.BLACK);
		err = new CWriter (Color.RED);
		
		try
		{
			inWriter = new PipedWriter ();
			in = new PipedReader (inWriter);
		}
		catch (IOException e)
		{
			throw new AssertionError (e);
		}
		((SwingPanel) getComponent ()).getContentPane ().add (sp);
	}


	@Override
	protected void disposeImpl ()
	{
		super.disposeImpl ();
		synchronized (lock)
		{
			try
			{
				inWriter.close ();
			}
			catch (IOException e)
			{
				e.printStackTrace ();
			}
			inWriter = null;
		}
	}


	public Reader getIn ()
	{
		return in;
	}

	
	public ConsoleWriter getOut ()
	{
		return out;
	}


	public ConsoleWriter getErr ()
	{
		return err;
	}


	public void keyPressed(	KeyEvent e ) {
	    type( e );
	    gotUp=false;
	}

	public void keyTyped(KeyEvent e) {
	    type( e );
	}

    public void	keyReleased(KeyEvent e)	{
		gotUp=true;
		type( e	);
    }

    private synchronized void type( KeyEvent e ) {
		switch ( e.getKeyCode()	) 
		{
			case ( KeyEvent.VK_ENTER ):
			    if (e.getID() == KeyEvent.KEY_PRESSED) {
					if (gotUp) {
						enter();
					}
				}
				e.consume();
				text.repaint();
				break;

			case ( KeyEvent.VK_UP ):
			    if (e.getID() == KeyEvent.KEY_PRESSED) {
				    historyUp();
				}
				e.consume();
				break;

			case ( KeyEvent.VK_DOWN	):
			    if (e.getID() == KeyEvent.KEY_PRESSED) {
					historyDown();
				}
				e.consume();
				break;

			case ( KeyEvent.VK_LEFT	):
			case ( KeyEvent.VK_BACK_SPACE ):
				if (text.getCaretPosition() <= cmdStart) {
					// This doesn't work for backspace.
					// See default case for workaround
					e.consume();
				}
				break;

			case ( KeyEvent.VK_DELETE ):
				if (text.getCaretPosition() < cmdStart) {
					e.consume();
				}
				break;

			case ( KeyEvent.VK_RIGHT ):
				forceCaretMoveToStart();
				break;

			case ( KeyEvent.VK_HOME ):
				text.setCaretPosition(cmdStart);
				e.consume();
				break;

			case ( KeyEvent.VK_U ):	// clear line
				if ( (e.getModifiers() & InputEvent.CTRL_MASK) > 0 ) {
					replaceRange( "", cmdStart, textLength());
					histLine = 0;
					e.consume();
				}
				break;

			case ( KeyEvent.VK_ALT ):
			case ( KeyEvent.VK_CAPS_LOCK ):
			case ( KeyEvent.VK_CONTROL ):
			case ( KeyEvent.VK_META ):
			case ( KeyEvent.VK_SHIFT ):
			case ( KeyEvent.VK_PRINTSCREEN ):
			case ( KeyEvent.VK_SCROLL_LOCK ):
			case ( KeyEvent.VK_PAUSE ):
			case ( KeyEvent.VK_INSERT ):
			case ( KeyEvent.VK_F1):
			case ( KeyEvent.VK_F2):
			case ( KeyEvent.VK_F3):
			case ( KeyEvent.VK_F4):
			case ( KeyEvent.VK_F5):
			case ( KeyEvent.VK_F6):
			case ( KeyEvent.VK_F7):
			case ( KeyEvent.VK_F8):
			case ( KeyEvent.VK_F9):
			case ( KeyEvent.VK_F10):
			case ( KeyEvent.VK_F11):
			case ( KeyEvent.VK_F12):
			case ( KeyEvent.VK_ESCAPE ):

			// only	modifier pressed
			break;

			// Control-C
			case ( KeyEvent.VK_C ):
				if (text.getSelectedText() == null) {
				    if (( (e.getModifiers() & InputEvent.CTRL_MASK) > 0	)
					&& (e.getID() == KeyEvent.KEY_PRESSED))	{
						append("^C");
					}
					e.consume();
				}
				break;

			case ( KeyEvent.VK_TAB ):
			    if (e.getID() == KeyEvent.KEY_RELEASED) {
					String part = text.getText().substring( cmdStart );
					doCommandCompletion( part );
				}
				e.consume();
				break;

			default:
				if ( 
					(e.getModifiers() & 
					(InputEvent.CTRL_MASK 
					| InputEvent.ALT_MASK | InputEvent.META_MASK)) == 0 ) 
				{
					// plain character
					forceCaretMoveToEnd();
				}

				/*
					The getKeyCode function always returns VK_UNDEFINED for
					keyTyped events, so backspace is not fully consumed.
					
					Uwe Mannl 2008/06/12
					The name for backspace is language dependent.
				*/
				if ((e.paramString().indexOf("Backspace") != -1) ||
						(e.paramString().indexOf("Rücktaste") != -1))
				{ 
				  if (text.getCaretPosition() <= cmdStart) {
						e.consume();
						break;
					}
				}

				break;
		}
	}

	void doCommandCompletion( String part ) {
		if ( nameCompletion == null )
			return;

		int i=part.length()-1;

		// Character.isJavaIdentifierPart()  How convenient for us!! 
		while ( 
			i >= 0 && 
				( Character.isJavaIdentifierPart(part.charAt(i)) 
				|| part.charAt(i) == '.' )
		) 
			i--;

		part = part.substring(i+1);

		if ( part.length() < 2 )  // reasonable completion length
			return;

		//System.out.println("completing part: "+part);

		// no completion
		String [] complete = nameCompletion.completeName(part);
		if ( complete.length == 0 ) {
			java.awt.Toolkit.getDefaultToolkit().beep();
			return;
		}

		// Found one completion (possibly what we already have)
		if ( complete.length == 1 && !complete.equals(part) ) {
			String append = complete[0].substring(part.length());
			append( append );
			return;
		}

		// Found ambiguous, show (some of) them

		String line = text.getText();
		String command = line.substring( cmdStart );
		// Find prompt
		for(i=cmdStart; line.charAt(i) != '\n' && i > 0; i--);
		String prompt = line.substring( i+1, cmdStart );

		// Show ambiguous
		StringBuffer sb = new StringBuffer("\n");
		for( i=0; i<complete.length && i<SHOW_AMBIG_MAX; i++)
			sb.append( complete[i] +"\n" );
		if ( i == SHOW_AMBIG_MAX )
			sb.append("...\n");

		print( sb, Color.gray );
		print( prompt ); // print resets command start
		append( command ); // append does not reset command start
	}

	private void resetCommandStart() {
		cmdStart = textLength();
	}

	private	void append(String string) {
		int slen = textLength();
		text.select(slen, slen);
	    text.replaceSelection(string);
    }


	void checkLimit ()
	{
		int len = textLength ();
		int d = len - maxBufferLength;
		if (d > 3)
		{
			replaceRange ("...", 0, d + maxBufferLength / 10);
			d = textLength ();
			text.select (d, d);
			cmdStart = Math.max (0, cmdStart + d - len);
		}
	}
	

    private String replaceRange(Object s, int start, int end) {
		String st = s.toString();
		text.select(start, end);
	    text.replaceSelection(st);
	    //text.repaint();
	    return st;
    }

	private	void forceCaretMoveToEnd() {
		if (text.getCaretPosition() < cmdStart)	{
			// move caret first!
			text.setCaretPosition(textLength());
		}
		text.repaint();
    }

	private	void forceCaretMoveToStart() {
		if (text.getCaretPosition() < cmdStart)	{
			// move caret first!
		}
		text.repaint();
    }

	
	public void enter (final String line)
	{
		EventQueue.invokeLater (new Runnable ()
   		{
			public void run ()
			{
				String s = line.endsWith ("\n") ? line : line + "\n";
				append (s);
				enter0 (s);
			}
   		});
	}

	
	public void clear ()
	{
		EventQueue.invokeLater (new Runnable ()
   		{
			public void run ()
			{
				text.setText ("");
				resetCommandStart ();
			}
   		});
	}


	private	void enter() {
		String s = getCmd();

		if (s.trim ().length () > 0)
		{
			history.addElement( s );
		}
		s = s +"\n";

		append("\n");
		enter0 (s);
	}
	
	private void enter0 (String s)
	{
		histLine = 0;
		acceptLine( s );
		resetCommandStart();
		text.setCaretPosition(cmdStart);
		text.repaint();
	}

    private String getCmd() {
		String s = "";
		try {
			s =	text.getText(cmdStart, textLength() - cmdStart);
		} catch	(BadLocationException e) {
			e.printStackTrace ();
		}
		return s;
    }

	private	void historyUp() {
		if ( history.size() == 0 )
			return;
		if ( histLine == 0 )  // save current line
			startedLine = getCmd();
		if ( histLine <	history.size() ) {
			histLine++;
			showHistoryLine();
		}
	}
	private	void historyDown() {
		if ( histLine == 0 )
			return;

		histLine--;
		showHistoryLine();
	}

	private	void showHistoryLine() {
		String showline;
		if ( histLine == 0 )
			showline = startedLine;
		else
			showline = (String)history.elementAt( history.size() - histLine	);

		replaceRange( showline,	cmdStart, textLength() );
		text.setCaretPosition(textLength());
		text.repaint();
	}

	private	void acceptLine( String	line ) 
	{
		synchronized (lock)
		{
			if (inWriter != null )
				try {
					inWriter.write( line);
					inWriter.flush();
				} catch	( IOException e	) {
					e.printStackTrace ();
				}
		}
		//text.repaint();
	}
	

	private void print (CharSequence str)
	{
	    append ((str == null) ? "null" : str.toString ());
		resetCommandStart ();
		text.setCaretPosition (cmdStart);
	}

	
	private Color curColor;
	private AttributeSet curAtts;

	void print (CharSequence str, Color color)
	{
		AttributeSet a = getStyle ();
		if (color != curColor)
		{
			curColor = color;
			curAtts = setStyle (color);
		}
		else
		{
			setStyle (curAtts);
		}
		print (str);
		setStyle (a);
	}


	public synchronized void print(Icon icon) {
	    if (icon==null) 
			return;

		text.insertIcon(icon);
		resetCommandStart();
		text.setCaretPosition(cmdStart);
	}


    public AttributeSet	setStyle(Font font) {
	    return setStyle(font, null);
    }

    public AttributeSet	setStyle(Color color) {
	    return setStyle(null, color);
    }

    public AttributeSet	setStyle( Font font, Color color) 
	{
	    if (font!=null)
			return setStyle( font.getFamily(), font.getSize(), color, 
				font.isBold(), font.isItalic(), 
				StyleConstants.isUnderline(getStyle()) );
		else
			return setStyle(null,-1,color);
    }

    public synchronized	AttributeSet setStyle (
	    String fontFamilyName, int	size, Color color) 
	{
		MutableAttributeSet attr = new SimpleAttributeSet();
		if (color!=null)
			StyleConstants.setForeground(attr, color);
		if (fontFamilyName!=null)
			StyleConstants.setFontFamily(attr, fontFamilyName);
		if (size!=-1)
			StyleConstants.setFontSize(attr, size);

		setStyle(attr);

		return attr;
    }

    public synchronized	AttributeSet setStyle(
	    String fontFamilyName,
	    int	size,
	    Color color,
	    boolean bold,
	    boolean italic,
	    boolean underline
	    ) 
	{
		MutableAttributeSet attr = new SimpleAttributeSet();
		if (color!=null)
			StyleConstants.setForeground(attr, color);
		if (fontFamilyName!=null)
			StyleConstants.setFontFamily(attr, fontFamilyName);
		if (size!=-1)
			StyleConstants.setFontSize(attr, size);
		StyleConstants.setBold(attr, bold);
		StyleConstants.setItalic(attr, italic);
		StyleConstants.setUnderline(attr, underline);

		setStyle(attr);

		return attr;
    }

    public void	setStyle(AttributeSet attributes) {
		setStyle(attributes, false);
    }

    public void	setStyle(AttributeSet attributes, boolean overWrite) {
		text.setCharacterAttributes(attributes,	overWrite);
    }

    public AttributeSet	getStyle() {
		return text.getCharacterAttributes();
    }

	public void setFont( Font font ) {
			text.setFont( font );
	}


	@Override
	public String toString() {
		return "BeanShell console";
	}

    // MouseListener Interface
    public void	mouseClicked(MouseEvent	event) {
    }

    public void mousePressed(MouseEvent event) {
        if (event.isPopupTrigger()) {
            menu.show(
				(Component)event.getSource(), event.getX(), event.getY());
        }
    }

    public void	mouseReleased(MouseEvent event)	{
		if (event.isPopupTrigger()) {
			menu.show((Component)event.getSource(), event.getX(),
			event.getY());
		}
		text.repaint();
    }

    public void	mouseEntered(MouseEvent	event) { }

    public void	mouseExited(MouseEvent event) { }

    // property	change
    public void	propertyChange(PropertyChangeEvent event) {
		if (event.getPropertyName().equals("lookAndFeel")) {
			SwingUtilities.updateComponentTreeUI(menu);
		}
    }

    // handle cut, copy	and paste
    public void	actionPerformed(ActionEvent event) {
		String cmd = event.getActionCommand();
		if (cmd.equals(CUT)) {
			text.cut();
		} else if (cmd.equals(COPY)) {
			text.copy();
		} else if (cmd.equals(PASTE)) {
			text.paste();
		} else if (cmd.equals(CLEAR)) {
    		text.setText ("");
    		resetCommandStart ();
    	}
    }


	public void setNameCompletion( NameCompletion nc ) {
		this.nameCompletion = nc;
	}


	private int textLength() { return text.getDocument().getLength(); }

}


