
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;

class VoterCheck{
  
    private String VoterId;
    private String uniqueId;
 
    //constructor
    public VoterCheck(String VoterId, String uniqueId)
    {
       this.uniqueId = uniqueId;
       this.VoterId = VoterId;
    }
 
    public String getVoterId()
    {
       return this.VoterId;
    }  
    public String getUniqueId()
    {
       return this.uniqueId;
    }

 }