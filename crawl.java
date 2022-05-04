import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;
import java.util.Collection;
import java.util.Collections;

public class crawl{

  /* Do not change this base URL. All URLs for ths assignmetn are relative to this address */
   private static String baseURL = "https://www.csd.uwo.ca/faculty/solis/cs9668/test/";
   
   public static void main (String[] args)
   {

   /* Write your code here for the simple search engine */

      //This program uses BFS to search a word
      Queue<URL> seeds = new LinkedList<>(); //all urls goint to search
      Vector<URL> downloadedURL= new Vector<>(); // urls downloaded. This is to check we have "visited" the page
      String[] query = InOut.readQuery(); //the query, obviously
      Vector<String> validQueryWord = new Vector<>(); // query word contain in a file. Work as a counter 
      Vector<URL> containAll = new Vector<>(); //urls containing all words in the query
      String s; // holder for each line in pages
      URL u;
      try
      {
         //starting at test.html
         URL start = new URL(baseURL+"test.html");
         //add urls to seeds (we are going to visit this in a moment)
         seeds.add(start);
         //to show that we have visited this url

         while(seeds.peek()!=null)
         {
            //remove the head
            URL head = seeds.remove();
            downloadedURL.add(head); //mark it as we have visited this url
            //read page
            BufferedReader input= new BufferedReader(new InputStreamReader(head.openStream()));

            // as long as we have not finished this page
            while((s=input.readLine())!=null)
            {
               u = extractURL(s);
               if(downloadedURL.indexOf(u)==-1)
               {
                  if (u != null) //if url embeded and we have not visited this url
                  {
                     seeds.add(u); //add url to "to be visited list"
                  }
                  for (int i = 0; i < query.length; ++i)
                  {
                     String queryWord = query[i].toLowerCase();
                     s = s.toLowerCase();
                     /*In case if a word will appear multiple times in a query
                     *if query word is in line and we have not meet this query word yet in this line, add this word
                     */
                     if (s.indexOf(queryWord) != -1 && validQueryWord.indexOf(queryWord)==-1) 
                     {
                        validQueryWord.add(queryWord);
                     }
                  }
                   //The document contains exactly all words
                  if (validQueryWord.size()==query.length && containAll.indexOf(head)==-1)
                  {
                     containAll.add(head);
                  }
               } 
            }
            validQueryWord = new Vector<>();
         }
         System.out.println(Integer.toString(containAll.size()));
         for(URL url : containAll)
         {
            InOut.printFileName(url);
         }

         //end 
         InOut.endListFiles();
      }
      catch (MalformedURLException mfe)
      {
         System.out.println("Malformed URL");
      }
     
      catch (IOException ioe)
      {
         System.out.println("IOException: "+ioe.toString());
      }
   } 
   
   /* If there is an URL embedded in the text passed as parameter, the URL will be extracted and
      returned; if there is no URL in the text, the value null is returned                       */   
   public static URL extractURL(String text) throws MalformedURLException {
   	String textUrl;
      	int index = text.lastIndexOf("a href=");
   	if (index > -1) {
   		textUrl = baseURL+text.substring(index+8,text.length()-2);   // Form the complete URL	
   		return new URL(textUrl);
   	}
   	else return null;
   }

 
} 

