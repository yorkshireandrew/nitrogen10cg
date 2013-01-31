package cg;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.Action;

/** a stack that can only hold a given number of elements, loosing the first in on overflow. This class is NOT threadsafe */
final public class FixedSizeStack<E>{
	
	private ArrayDeque<E> deque;
	private int fixedSize;
	private List<Action> undoActions;
	
	public FixedSizeStack(int numElements)
	{
		deque = new ArrayDeque<E>(numElements);
		fixedSize = numElements;
		undoActions = new ArrayList<Action>();
	}
	
	final boolean empty()
	{
		return deque.isEmpty();
	}
	
	final void push(E element)
	{
		if(fixedSize == 0)return;
		if(deque.size() == fixedSize)deque.removeLast();
		deque.addFirst(element);
		enableActions();
	}
	
	final E pop() throws EmptyStackException
	{
		try
		{
			E retval = deque.pop();
			
			if(empty())
			{
				disableActions();
			}
			
			return retval;
		}
		catch(NoSuchElementException e)
		{
			throw new EmptyStackException();
		}
	}
	
	final E peek() throws EmptyStackException
	{
		try{
		 return deque.getFirst();
		}
		catch(NoSuchElementException e)
		{
			throw new EmptyStackException();
		}
	}
	
	final int search(Object o)
	{
		if(deque.contains(o))
		{
			int size = deque.size();
			Object[] asArray = deque.toArray();
			for(int x = 0; x < size; x++)
			{
				if(asArray[x] == o)return(x+1);
			}
			return -1;
		}
		else
		{
			return -1;
		}
	}
	
	final void clear()
	{
		deque.clear();
	}
	
	final boolean contains(Object o)
	{
		return deque.contains(o);
	}
	
	final void addAction(Action action)
	{
		undoActions.add(action);
	}
	
	final void removeAction(Action action)
	{
		undoActions.remove(action);
	}
	
	private final void enableActions()
	{
		for(Action a: undoActions)
		{
			a.setEnabled(true);
		}
	}
	
	private final void disableActions()
	{
		for(Action a: undoActions)
		{
			a.setEnabled(false);
		}
	}
	
	
	
	
	
}
     


