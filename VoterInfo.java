

import java.util.*; 
import java.io.*;

public class VoterInfo{
   private int uniqueId;
   private String VoterId;
   public String password;


   //constructor
   public VoterInfo(String VoterId, int uniqueId)
   {
      this.uniqueId = uniqueId;
      this.VoterId = VoterId;
   }

   public String getVoterId()
   {
      return this.VoterId;
   }  
}

