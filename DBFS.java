import java.util.Vector; //We need this for the Vector class.

public class DBFS extends Algorithm {

    public Object run() {
        // Invoke the main algorithm, passing as parameter the node's id.
        String lastmsg = bfsTrees(getID());
        return lastmsg;
    }

    public String bfsTrees(String id) {
        try {

			/* Your initialization code goes here */
        	String parent1, parent2;
        	Message mssg1, mssg2;
        	Message tmp = null;
        	Message ack1 = null;
        	Message ack2 = null;
        	int rounds_left1 = -1;
        	int rounds_left2 = -1;
          	Vector<String> children1 = new Vector<String>();
			Vector<String> children2 = new Vector<String>();
        				
        	if(equal(getID(), "1"))
			{
				parent1 = "null";
				parent2 = "udf";
				String data1 = pack(getID(), "1", "req");
				mssg1 = makeMessage(neighbours(), data1);
				mssg2 = null;
			}
        	else if(equal(getID(), "2"))
        	{
        		parent1 = "udf";
        		parent2 = "null";
        		String data2 = pack(getID(), "2", "req");
        		mssg2 = makeMessage(neighbours(), data2);
        		mssg1 = null;
        	}
        	else
        	{
        		parent1 = "udf";
        		parent2 = "udf";
        		mssg1 = null;
        		mssg2 = null;
        	}
        	
            while (waitForNextRound()) {  
            
			/* Your algorithm goes here */
	           if(mssg1!=null)
	           {
	        	   send(mssg1);
	        	   rounds_left1 = 1;
	           }
   	          	
	           if(mssg2!=null)
	           {
	        	   send(mssg2);
	        	   rounds_left2 = 1;
	           }
	           
	           if(ack1!=null)
	           {
	        	   send(ack1);
	           }
	           if(ack2!=null)
	           {
	        	   send(ack2);
	           }
	           
	           mssg1 = null;
	           mssg2 = null;
	           ack1 = null;
	           ack2 = null;
	           
	           while((tmp = receive())!=null)
	           {
	        	   String[] r_data = unpack(tmp.data());
	        	   if(equal(r_data[1], "1"))
	        	   {
	        		   if(equal(r_data[2], "ack"))
	        		   {
	        			   children1.add(r_data[0]);
	        		   }
	        		   else
	        		   {
	        			   if(equal(parent1, "udf"))
	        			   {
	        				   parent1 = r_data[0];
	        				   
	        				   String ack_data1 = pack(getID(), "1", "ack");
	        				   String req_data1 = pack(getID(), "1", "req");
	        				   Vector v = neighbours();
	        				   v.remove(parent1);
	        				   ack1 = makeMessage(parent1, ack_data1);
	        				   mssg1 = makeMessage(v, req_data1);
	        			   }
	        		   }
	        		   
	        		 
	        	   }
	        	   if(equal(r_data[1], "2"))
	        	   {
	        		   if(equal(r_data[2], "ack"))
	        		   {
	        			   children2.add(r_data[0]);
	        		   }
	        		   else
	        		   {
	        			   if(equal(parent2, "udf"))
	        			   {
	        				   parent2 = r_data[0];
	        				   String ack_data2 = pack(getID(), "2", "ack");
	        				   String req_data2 = pack(getID(), "2", "req");
	        				   ack2 = makeMessage(parent2, ack_data2);
	        				   Vector v = neighbours();
	        				   v.remove(parent2);
	        				   mssg2 = makeMessage(v, req_data2);
	        			   }
	        		   }
	        	   }
	           }
	           if(rounds_left1==1 || rounds_left2==1)
	           {
	        	   if(rounds_left1==1)
	        	   {
	        		   rounds_left1 = 0;
	        	   }
	        	   if(rounds_left2==1)
	        	   {
	        		   rounds_left2 = 0;	        	   
	        	   }
	           }  
	           else if(rounds_left1==0 && rounds_left2==0)
	           {
	        	   printParentsChildren(parent1, parent2, children1, children2);
	        	   return "";
	           }
            }
        } catch(SimulatorException e){
            System.out.println("ERROR: "+e.getMessage());
        }
        
        return null;
    }
    
    /* Print information about the parent and children of this processor in both BFS trees */
    private void printParentsChildren(String parent1, String parent2, Vector<String>children1,
                                      Vector<String>children2) {
	String outMssg = "["+parent1+":";
	for (int i = 0; i < children1.size()-1; ++i) 
		outMssg = outMssg + children1.elementAt(i)+" ";
	if (children1.size() > 0)
		outMssg = outMssg + children1.elementAt(children1.size()-1) + "] [" + parent2 + ":";
	else outMssg = outMssg + "] ["+parent2+":";
	for (int i = 0; i < children2.size()-1; ++i) 
		outMssg = outMssg + children2.elementAt(i)+" ";	
	if (children2.size() > 0)					
		outMssg = outMssg + children2.elementAt(children2.size()-1)+ "]";	
	else outMssg = outMssg + "]";
	showMessage(outMssg);
	printMessage(outMssg);        
	Check.verify(getID(),parent1,parent2,children1,children2);
    }
}
