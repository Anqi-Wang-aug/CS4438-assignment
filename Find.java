import java.util.Vector;            
public class Find extends Algorithm {
    private int m;                   	// Ring of identifiers has size 2^m
    private int SizeRing;              // SizeRing = 2^m

    public Object run() {
        return find(getID());
    }

	// Each message sent by this algorithm has the form: flag, value, ID 
	// where:
	// - if flag = "GET" then the message is a request to get the document with the given key
	// - if flag = "LOOKUP" then the message is request to forward the message to the closest
	//   processor to the position of the key
	// - if flag = "FOUND" then the message contains the key and processor that stores it
	// - if flag = "NOT_FOUND" then the requested data is not in the system
	// - if flag = "END" the algorithm terminates
	
	/* Complete method find, which must implement the Chord search algorithm using finger tables 
	   and assumming that there are two processors in the system that received the same ring identifier. */ 
	/* ----------------------------- */
    public Object find(String id) {
	/* ------------------------------ */
       try {
       
             /* The following code will determine the keys to be stored in this processor, the keys that this processor
                needs to find (if any), and the addresses of the finger table                                           */
	      Vector<Integer> searchKeys; 		// Keys that this processor needs to find in the P2P system. Only
			                               // for one processor this vector will not be empty
	      Vector<Integer> localKeys;   		// Keys stored in this processor

	      localKeys = new Vector<Integer>();
	      String[] fingerTable;                  // Addresses of the fingers are stored here
	      searchKeys = keysToFind();             // Read keys and fingers from configuration file
	      fingerTable = getKeysAndFingers(searchKeys,localKeys,id);  // Determine local keys, keys that need to be found, and fingers
	      m = fingerTable.length-1;
	      SizeRing = exp(2,m);
	      
		/* Your initialization code goes here */
	      String result = "";
	      Message mssg = null;
	      Message message = null;
	      Vector<String> n = neighbours();
	      String[] in_message_receive;
	      String data;
	      Boolean searched = false;
	      
	      if (searchKeys.size() > 0) { 		// If this condition is true, the processor has keys that need to be found
	      
			/* Your code to look for the keys locally and to create initial messages goes here */
			int k = searchKeys.elementAt(0);
			searchKeys.remove(0);
			if(localKeys.contains(k))
			{
				result = result + k + ":" + id + " ";
				searched = true;
			}
			else
			{

				mssg = messageToClosest(k, id, n, fingerTable);
			}
	      }

			
	      while (waitForNextRound()) { // Synchronous loop
         		/* Your code goes here */
         		if(mssg!=null)
         		{
         			send(mssg);
         			String[] in_my_message = unpack(mssg.data());
         			if(equal(in_my_message[0], "END") && searchKeys.size()==0)
         			{
         				return result;
         			}
         			mssg = null;
         		}
         		
         		message = receive();
         		while(message!=null)
         		{
         			in_message_receive = unpack(message.data());
         			int findee = 0;
         			if(in_message_receive.length>1)
         			{
         				findee = stringToInteger(in_message_receive[1]);
         			}
         			if(equal(in_message_receive[0], "GET"))
         			{
         				if (in_message_receive[2].equals(id)) {
							result = result + in_message_receive[1] + ":not found ";
							searched = true;
						}
						// This processor must contain the key, if it is in the system
						else if (localKeys.contains(findee))
						{
							data = pack("FOUND",in_message_receive[1], id);
							mssg = makeMessage(in_message_receive[2], data);
						}
						else
						{	
								data = pack("NOT_FOUND",in_message_receive[1], in_message_receive[2]);
								if(n.contains(in_message_receive[2]))
								{
									mssg = makeMessage(in_message_receive[2], data);	
								}
								else
								{
									mssg = makeMessage(successor(), data);
								}
						}
         			}
         			else if(equal(in_message_receive[0], "LOOKUP"))
         			{
         				mssg = messageToClosest(findee, in_message_receive[2], n, fingerTable);
         				
         			}
         			else if(equal(in_message_receive[0], "NOT_FOUND"))
         			{
         				if(equal(in_message_receive[2], id))
         				{
         					result = result + in_message_receive[1] + ":not found ";
             				searched = true;
         				}
         				else
         				{
         					mssg = makeMessage(successor(), message.data());
         				}
         			}
         			else if(equal(in_message_receive[0], "FOUND"))
         			{
         				result = result + in_message_receive[1] + ":" + in_message_receive[2] + " ";
         				searched = true;
         			}
         			else if(equal(in_message_receive[0], "END"))
         			{
         				mssg = makeMessage(successor(), "END");
         			}
         			message = receive();
         		}
         		if(searched)
         		{
         			if(searchKeys.size()==0)
         			{
         				mssg = makeMessage(successor(), "END");
         			}
         			else
         			{
         				int k = searchKeys.elementAt(0);
         				searchKeys.remove(0);
         				if(localKeys.contains(k))
         				{
         					result = result + k + ":" + id + " ";
         					searched = true;
         				}
         				else
         				{
         					mssg = messageToClosest(k, id, n, fingerTable);
         				}
         			}
         			if(mssg!=null) searched = false;
         		}
	      }
                
 
        } catch(SimulatorException e){
            System.out.println("ERROR: " + e.toString());
        }
    
        /* At this point something likely went wrong. If you do not have a result you can return null */
        return null;
    }
    
    private Message messageToClosest(int k, String s, Vector<String> neighbours, String[] fingerTable) throws SimulatorException
    {
    	Message mssg = null;
    	String receiver;
    	String tmp = s;
    	if(!equal(s, getID()))
    	{
    		tmp = getID();
    	}
    	receiver = findClosestSuccessor(k, fingerTable, tmp); 
    	printMessage(receiver);
		if(neighbours.contains(receiver))
		{
			String data;
			int r = stringToInteger(receiver);
			if(hp(receiver)>=hk(k) && !equal(receiver, Integer.toString(k)) && ((hk(k)!=0) || hp(getID())>hp(receiver)))
			{
				data = pack("GET", k, s);
			}
			else
			{
				//printMessage(receiver);
				data = pack("LOOKUP", k, s);
			}
			
			mssg = makeMessage(receiver, data);
		}
		else
		{
			
			String data = pack("LOOKUP", k, s);
			
			receiver = findClosestNeighbour(receiver, neighbours);
			
			mssg = makeMessage(receiver, data);
		}
    	return mssg;
    }

	

	private String findClosestSuccessor(int k, String[] fingerTable, String id) throws SimulatorException {
		// TODO Auto-generated method stub
		String closest = "";
		int k_pos = hk(k);
		int distance = 0;
		for(int i = 0; i<fingerTable.length; i++)
		{
			int suc_pos = hp(fingerTable[i]);
			if(i==0)
			{
				distance = Math.abs(suc_pos-k_pos);
				closest = fingerTable[i];
			}
			else
			{
				if(!equal(fingerTable[i], id) && Math.abs(suc_pos-k_pos)<distance)
				{
					distance = Math.abs(suc_pos-k_pos);
					closest = fingerTable[i];
				}
			}
		}
		return closest;

	}
	
	private String findClosestNeighbour(String id, Vector<String> neighbours) throws SimulatorException
	{
		String neighbour = "";
		int id_pos = hp(id);
		int distance = 0;
		for(int i = 0; i<neighbours.size(); i++)
		{
			int suc_pos = hp(neighbours.get(i));
			if(i==0)
			{
				neighbour = neighbours.get(i);
				distance = Math.abs(suc_pos-id_pos);
			}
			else
			{
				if(Math.abs(suc_pos-id_pos)<distance)
				{
					distance = Math.abs(suc_pos-id_pos);
					neighbour = neighbours.get(i);
				}
			}
		}
		return neighbour;
	}

	/* Determine the keys that need to be stored locally and the keys that the processor needs to find.
	   Negative keys returned by the simulator's method keysToFind() are to be stored locally in this 
           processor as positive numbers.                                                                    */
	/* ---------------------------------------------------------------------------------------------------- */
	private String[] getKeysAndFingers (Vector<Integer> searchKeys, Vector<Integer> localKeys, String id) throws SimulatorException {
	/* ---------------------------------------------------------------------------------------------------- */
		Vector<Integer>fingers = new Vector<Integer>();
		String[] fingerTable;
		String local = "";
		int m;
			
		if (searchKeys.size() > 0) {
			for (int i = 0; i < searchKeys.size();) {
				if (searchKeys.elementAt(i) < 0) {   	// Negative keys are the keys that must be stored locally
					localKeys.add(-searchKeys.elementAt(i));
					searchKeys.remove(i);
				}
				else if (searchKeys.elementAt(i) > 1000) {
					fingers.add(searchKeys.elementAt(i)-1000);
					searchKeys.remove(i);
				}
				else ++i;  // Key that needs to be searched for
			}
		}
			
		m = fingers.size();
		// Store the finger table in an array of Strings
		fingerTable = new String[m+1];
		for (int i = 0; i < m; ++i) fingerTable[i] = integerToString(fingers.elementAt(i));
		fingerTable[m] = id;
	
		for (int i = 0; i < localKeys.size(); ++i) local = local + localKeys.elementAt(i) + " ";
		showMessage(local); // Show in the simulator the keys stored in this processor
		return fingerTable;
	}

    /* Hash function to map processor ids to ring identifiers. */
    /* ------------------------------- */
    private int hp(String ID) throws SimulatorException{
	/* ------------------------------- */
        return stringToInteger(ID) % SizeRing;
    }

    /* Hash function to map keys to ring identifiers */
    /* ------------------------------- */
    private int hk(int key) {
    /* ------------------------------- */
        return key % SizeRing;
    }

    /* Compute base^exponent ("base" to the power "exponent") */
    /* --------------------------------------- */
    private int exp(int base, int exponent) {
    /* --------------------------------------- */
        int i = 0;
        int result = 1;

		while (i < exponent) {
			result = result * base;
			++i;
		}
		return result;
    }
    
    
}
