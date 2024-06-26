/*
 * BufferOutput.java - Output to buffer implementation
 * :tabSize=4:indentSize=4:noTabs=false:
 * :folding=explicit:collapseFolds=1:
 *
 * Copyright (C) 2001 Slava Pestov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
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

package console;

//{{{ Imports
import java.awt.Color;

import javax.swing.text.AttributeSet;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.Mode;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

import org.gjt.sp.util.ThreadUtilities;



//}}}


/**
 *
 *  Implementation of Console "Shell Command, Output to New Buffer" 
 *
 */

public class BufferOutput implements Output
{
	
	/** Guess the edit mode of the output based on the command line executed. */
	static public String guessMode(String command) {
		command = command.toLowerCase();
		if (command.contains("diff") || command.contains("patch"))
			return "patch";	
		if (command.contains("html")) return "html";
		if (command.contains("xml")) return "xml";
		return "text";		
	}
	
	//{{{ BufferOutput constructors
	public BufferOutput(Console console)
	{
		this(console,"text");
	} 

	public BufferOutput(Console console, String mode)
	{
		this.console = console;
		this.view = console.getView();
		this.mode = mode;
		buf = new StringBuffer();
	} //}}}

	//{{{ print() method
	public void print(Color color, String msg)
	{
		buf.append(msg);
		buf.append('\n');
	} //}}}

	//{{{ write() method
	public void writeAttrs(AttributeSet attrs, String msg)
	{
		buf.append(msg);
	} //}}}


	//{{{ setAttrs() method
	public void setAttrs(int length, AttributeSet attrs)
	{
		// Do nothing.
	} //}}}

	//{{{ commandDone() method
	public void commandDone()
	{

		ThreadUtilities.runInDispatchThread(new Runnable()
		{
			public void run()
			{
				final Buffer buffer = jEdit.newFile(view);
				// What does this do? recursive?
				console.getOutput().commandDone();
				Mode _mode = jEdit.getMode(mode);
				if(_mode != null)
					buffer.setMode(_mode);
				buffer.insert(0,buf.toString());
			}
		});
	} //}}}

	//{{{ Private members
	private Console console;
	private View view;
	private String mode;
	private StringBuffer buf;
	//}}}

	/**
	 * Should this run through error handlers and print in colors?
	 */
	public void printColored(String message)
	{
		throw new RuntimeException("Not Implemented");

	}
}
