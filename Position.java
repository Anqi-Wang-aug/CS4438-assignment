import java.util.Vector; 

/* Algorithm for counting the number of processors to the left of a given processor in a line network. */

public class Position extends Algorithm {

    /* Do not modify this method */
    public Object run() {
        int pos = findPosition(getID());
        return pos;
    }

    public int findPosition(String id) {
        Vector<String> v = neighbours(); // Set of neighbours of this node.


        // Your initialization code goes here
				try {
							Message m = null;
							int pos = 0;
							while(waitForNextRound())
							{
									
									if(equal(v.elementAt(0), "0"))
									{
											m = makeMessage(v.elementAt(1), ""+pos);
											send(m);
											return pos;
									}
									else if (m!=null)
									{
											if(equal(v.elementAt(1), "0"))
											{
													return pos;
											}
											else 
											{
													send(m);
													return pos;
											}
									}
								
										
									m = receive();
									if(m!=null)
									{
											pos = 1+stringToInteger(m.data());
											m = makeMessage(v.elementAt(1), ""+pos);
									}
								}
												
	
        } catch(SimulatorException e){
            System.out.println("ERROR: " + e.toString());
        }

        // If we got here, something went wrong! (Exception, node failed, etc.)    
        return 0;
    }
}
