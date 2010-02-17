package jdf;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * Copyright 2010 Jonathan Feinberg
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
/**
 * Synchronize "on an equivalence class"; i.e., if you wish to lock not a
 * specific string, but anything that equals that string, you may
 * 
 * <pre>EquivalenceLock<String> equivalenceLock = new EquivalenceLock<String>();
equivalenceLock.lock("frank"); 
try {
    // whatever 
} finally {
    equivalenceLock.release("frank"); 
}</pre>
 
 * @author Jonathan Feinberg &lt;jdf@pobox.com&gt;
*/
public class EquivalenceLock<T>
{
	private static final Logger LOG = Logger.getLogger(EquivalenceLock.class.getName());
	private static final boolean DEBUG = LOG.isLoggable(Level.FINEST);

	private final Set<T> slots = new HashSet<T>();

	public void lock(final T ticket) throws InterruptedException
	{
		final String threadName = DEBUG ? Thread.currentThread().getName() : null;
		if (DEBUG)
			LOG.finest(threadName + " acquiring lock on tickets");

		synchronized (slots)
		{
			if (DEBUG)
				LOG.finest(threadName + " acquired lock on tickets");

			while (slots.contains(ticket))
			{
				if (DEBUG)
					LOG.finest(threadName + " waiting to toss " + ticket);
				slots.wait();
			}

			if (DEBUG)
				LOG.finest(threadName + " accepting " + ticket);
			slots.add(ticket);
		}
	}

	public void release(final T ticket)
	{
		synchronized (slots)
		{
			if (DEBUG)
				LOG.finest(Thread.currentThread().getName() + " tossing " + ticket);
			slots.remove(ticket);
			slots.notifyAll();
		}
	}
}
