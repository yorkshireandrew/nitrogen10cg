package cg;

import java.io.*;

public class DeepCloner{
	
	@SuppressWarnings("unchecked")
	static final public <T extends Serializable> T clone(final T source) throws IOException, ClassNotFoundException
	{
		final PipedOutputStream pipeout = new PipedOutputStream();
		PipedInputStream pipein = new PipedInputStream(pipeout);
		
		Thread writer = new Thread(){
			public void run(){
				ObjectOutputStream out = null;
				try{
					out = new ObjectOutputStream(pipeout);
					out.writeObject(source);
				}
				catch(IOException e){}
				finally {
					try{out.close();}catch(Exception e){}
				}
			}
		};
		
		writer.start();
		
		ObjectInputStream in = new ObjectInputStream(pipein);
		return ((T)in.readObject());
	}
	
	static void store(Serializable obj, File f) throws IOException
	{
		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
		out.writeObject(obj);
		out.close();
	}
	
	static Object load(File f) throws IOException, ClassNotFoundException
	{
		ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
		return in.readObject();
	}
	
	

}
