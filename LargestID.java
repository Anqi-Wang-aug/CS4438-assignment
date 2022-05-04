import java.util.Vector; 

/* Algorithm for solving the leader election problem in a synchronous ring network. */

public class LargestID extends Algorithm {

    /* Do not modify this method */
    public Object run() {
		int largest = findLargest(getID());
        return largest;
    }
 
    public int findLargest(String id) {
        Vector<String> v = neighbours(); // Set of neighbours of this node.


        // Your initialization code goes here
      
        try {
        	String max = getID();
        	String data = pack(getID(), max);
        	String past_receiver = "";
        	boolean return_status = false;
        	Message m = makeMessage(v, data);
        	int count = 1;
            while (waitForNextRound()) { // Main loop. All processors wait here for the beginning of the next round.
                // Your code goes here  
       				if(m!=null)
       				{
       					send(m);
       					String[] mData = unpack(m.data());
       					if(count!=1 && return_status)
       					{
       						return stringToInteger(max);
       					}
       					else
       					{
       						m = null;
       					}
       				}
       				Message m1 = receive();
       				Message m2 = receive();
       				if(m1!= null & m2!= null)
       				{
       				
       					String[] m1data = unpack(m1.data());
       					String[] m2data = unpack(m2.data());
       					int m1id = stringToInteger(m1data[1]);
       					int m2id = stringToInteger(m2data[1]);
       					int tmp_max = 0;
       					
       					if(m1id>m2id)
       					{
       						tmp_max = m1id;
       					}
       					else
       					{
       						tmp_max = m2id;
       					}
       					
       					if(tmp_max>stringToInteger(max))
       					{
       						max = integerToString(tmp_max);
       						data = pack(getID(), max);
       						
       						String receiver = "";
       						int index = 0;
       						if(m1id!=m2id)
       						{
       							if(tmp_max==m1id)
           						{
           							 index = v.indexOf(m1data[0]);
           						}
           						else
           						{
           							index = v.indexOf(m2data[0]);
           						}
           						if(index==0)
           						{
           							receiver = v.get(1);
           						}
           						else
           						{
           							receiver = v.get(0);
           						}
         						m = makeMessage(receiver, data);
         						count = 0;
       						}
       						else
       						{
       							m =makeMessage(v, data);
       							return_status = true;
       						}	     						       						       						
       					}
       					else if (tmp_max==stringToInteger(max))
       					{
       						if(!equal(max, getID()))
       						{
       							m = makeMessage(v, data);
       						}
       						else
       						{
       							return stringToInteger(getID());
       						}	      						    						
       					}
       					else
       					{
       						m = null;
       						count = 0;
       					}
       					
       				}
       				else if(m1==null && m2!=null)
       				{
       					String[] m2data = unpack(m2.data());
       					
       					if(equal(past_receiver, m2data[0]))
       					{
       						return_status = true;
       					}
       					if(larger(m2data[1], max ))
       					{
       						max = m2data[1];
       						data = pack(getID(), max);
       						String receiver;
       						int index = v.indexOf(m2data[0]);
       						if(index==0)
       						{
       							receiver = v.get(1);
       						}
       						else
       						{
       							receiver = v.get(0);
       						}
       						m = makeMessage(receiver, data);
           				}
       					else
       					{
       						int tmp_max = stringToInteger(m2data[1]);
       						if(tmp_max == stringToInteger(max))
       						{
       							data = pack(getID(), max);
       							return_status = true;
       							String receiver;
       							int index = v.indexOf(m2data[0]);
       							if(index==0)
           						{
           							receiver = v.get(1);
           						}
           						else
           						{
           							receiver = v.get(0);
           						}
       							m = makeMessage(receiver, data);
       						}
       					}
       					count = 0;
       				}
       				else if(m2==null && m1!=null)
       				{
       					String[] m1data = unpack(m1.data());
       					
       					
       					if(larger(m1data[1], max ))
       					{
       						max = m1data[1];
       						data = pack(getID(), max);
       						String receiver;
       						int index = v.indexOf(m1data[0]);
       						if(index==0)
       						{
       							receiver = v.get(1);
       						}
       						else
       						{
       							receiver = v.get(0);
       						}
       						m = makeMessage(receiver, data);
           				}
       					else
       					{
       						int tmp_max = stringToInteger(m1data[1]);
       						if(tmp_max == stringToInteger(max))
       						{
       							data = pack(getID(), max);
       							String receiver;
       							int index = v.indexOf(m1data[0]);
           						if(index==0)
           						{
           							receiver = v.get(1);
           						}
           						else
           						{
           							receiver = v.get(0);
           						}
           						m = makeMessage(receiver, data);
           						return_status = true;
       						}
       					}
       					count = 0;
       				}
       				else
       				{
       					m = null;
       					count = 0;
       				}
       				
       			}
            } catch(SimulatorException e){
            System.out.println("ERROR: " + e.toString());
        }   
        // If we got here, something went wrong! (Exception, node failed, etc.)
		return 0;
    }
}
