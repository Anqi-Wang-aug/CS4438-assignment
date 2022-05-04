import java.util.Vector;

/* Algorithm for computing the diameter of a hub-and-spoke network. */

public class Diameter extends Algorithm {

    /* Do not modify this method */
    public Object run() {
        int d = findDiameter(getID());
        return d;
    }

    public int findDiameter(String id) {
        Vector<String> v = neighbours(); // Set of neighbours of this node.
        Vector<Message> mailbox= new Vector<Message>();
        // Your initialization code goes here
        Message m=null;
        String data;
        int count = 0;
        if (v.size()==1)
        {
        	data = "0";
        	m = makeMessage(v, data);
        }
        try {
            while (waitForNextRound()) { // Main loop. All processors wait here for the beginning of the next round.
                // Your code goes here   
            	if(m!=null)
            	{
            		send(m);
            		if(v.size()==1)
            		{
            			return 0;
            		}
            		if(v.size()==2)
            		{
            			return stringToInteger(m.data());
            		}
            	}
            	if(v.size()==2)
            	{
            		Message m1 = receive();
            		if(m1!=null)
            		{
            			int pos = 1+stringToInteger(m1.data());
                		m = makeMessage(v, integerToString(pos));
            		}
            	}
            	else if(v.size()>2)
            	{
            		
            		int[] value = new int[numNeighbours()];
            		Message tmp = null;
            		while((tmp = receive())!=null)
            		{
            			mailbox.add(tmp);
            			count++;
            		}
            		if(count==v.size())
            		{
            			for(int i = 0; i<numNeighbours(); i++)
                		{
                			int tmpv = stringToInteger(mailbox.get(i).data());
                        	value[i] = tmpv;
                			
                		}
            			return value[value.length-1]+value[value.length-2]+2;
            		}            		            		
            	}

            }
        } catch(SimulatorException e){
            System.out.println("ERROR: " + e.toString());
        }
    
        // If we got here, something went wrong! (Exception, node failed, etc.)
        return 0;
    }
}
