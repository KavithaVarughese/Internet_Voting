

import java.io.*; 
import java.text.*; 
import java.util.*; 
import java.net.*;

public class VoterData{
   private HashMap<String, VoterInfo> VoterTable = new HashMap<String, VoterInfo>();

   public VoterData(){
   VoterTable.put("B160779CS", new VoterInfo("B160779CS", "kavitha_pwd", "kavithavarughese@gmail.com", "d21jkahfds"));
   VoterTable.put("B160382CS", new VoterInfo("B160382CS", "varshini_pwd", "varshininaikk1998@gmail.com", "a365zqr301"));
   }

   public HashMap<String, VoterInfo> getVoterTable()
   {
      return this.VoterTable;
   }
}

class VoterInfo{
  
   private String VoterId;
   private String password;
   private String email;
   private String salt;
   public boolean uidAssigned;

   //constructor
   public VoterInfo(String VoterId, String password, String email, String salt)
   {
      this.password = password;
      this.VoterId = VoterId;
      this.email = email;
      this.salt = salt;
      this.uidAssigned = false;
   }

   public String getVoterId()
   {
      return this.VoterId;
   }  
   public String getPassword()
   {
      return this.password;
   }
   public String getEmail()
   {
      return this.email;
   }  
   public String getSalt()
   {
      return this.salt;
   }  
   public boolean getUidAssigned()
   {
      return this.uidAssigned;
   }  
   public void changeUidAssigned()
   {
      this.uidAssigned = true;
   }    
}

