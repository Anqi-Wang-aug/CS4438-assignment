
public class ComputeRouting extends Algorithm {

    public Object run() {
        // Our sumTree algorithm returns the total of distances from the descendants to this node 
        String result = computeRoutingTable(getID());
        return result;
    }

    public String computeRoutingTable(String id) {
        try {
	        RoutingTable table = new RoutingTable(id);
	       
			/* Your initialization code goes here */
	        Message mssg = null;
	        int children = 0;
	        if(numChildren()==0)
	        {
	        	
	        	String data = table.stringRepresentation(getID(), table);
	        	showMessage(data);
	        	mssg = makeMessage(getParent(), data);
	        }
	        else if(isRoot())
	        {
	        	children  = numChildren();
	        }
	       
	        while (waitForNextRound()) {  /* synchronous loop */
                
			/* Your algorithm goes here */
				if(mssg!=null)
				{
					send(mssg);
					return "";
				}
				mssg = null;
				Message tmp;
				
				while((tmp = receive())!=null)
				{
					String tmp_data = tmp.data();
					if(table.emptyRoutingTable(tmp_data))
					{
						table.addEntry(tmp_data, tmp_data);
					}
					else
					{
						String[] content = tmp_data.split("\\s+");
						for(int i = 0; i<content.length; i=i+2)
						{
							table.addEntry(content[0], content[i]);
						}
					}	
				}
				String data = table.stringRepresentation(getID(), table);
				printMessage(data);
			
				if(table.emptyRoutingTable(data)==false)
				{				
					if(isRoot())
					{
						children--;
						table.printTables();
						if(children==0)
						{
							return "";
						}
					}
					else
					{
						mssg = makeMessage(getParent(), data);
					}
				}
            }
        } catch(SimulatorException e){
            System.out.println("ERROR: " + e.toString());
        }
    
        return "";
    }

}
