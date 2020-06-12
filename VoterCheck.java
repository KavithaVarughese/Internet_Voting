
import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;

class VoterCheck{
  
    private String VoterId;
    private String uniqueId;
    public Boolean voteCasted;
 
    //constructor
    public VoterCheck(String VoterId, String uniqueId)
    {
       this.uniqueId = uniqueId;
       this.VoterId = VoterId;
       this.voteCasted = false;
    }
 
    public String getVoterId()
    {
       return this.VoterId;
    }  
    public String getUniqueId()
    {
       return this.uniqueId;
    }
    public Boolean getVoteCasted()
    {
       return this.voteCasted;
    }
    public void setVoteCasted(){
       this.voteCasted = true;
    }

 }