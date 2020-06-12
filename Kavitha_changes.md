# To run the code.. Following Files must be compiled
VoterData.java <br __>
VoterCheck.java <br __>
CandidateData.java <br __>
S2.java <br __>
S1.java <br __>
Webserver.java <br __>

Running code is same as READMe.md

# Still to be fixed

1. Nonces have to be randomly generated. Not predefined <br __>
2. getSecretKey : Shared key right now is a temperory string converted to AES SecretKey format. Needs to be discussed with Ritika.
3. Polling Time
4. Cross checking of the nonce sent during communication
5. Secret which is RSA encrypted with vote has to be unique.

# Changes Brought
<br __>
Packet's values are separated by space <br __>


## In Webserver.java

1. getmessagePacket1 : creates packet 1 <br __>
2. printMenu : prints the candidate list in the terminal for the Voter to view <br __>
3. Note Worthy Changes in the main function : <br __>
    1. Candidate list is recieved as an HashMap Object named "CandidateTable" . < Candidate Name , Candidate UniqueId > <br __>
       Candidate uniqueID can be accessed using - CandidateTable.get(< Candidate Name >) <br __>
    2. Program, after recieving packet 2, simply ends with a break statement. 
    
## In S1.java
1. getAlphaNumericString : generates a random alphanumeric string for a specified length n. <br __>
2. getmessagePacket2 : creates packet 2 , the secret for RSA encryption is created here right now. <br __>
3. Note Worthy Changes in VoterHandler : <br __>
    1. VoterTable <br __>
        - In the form of a hashmap < String, Class of the name VoterInfo >  
        - The following functions are available
            > VoterTable.get(VoterId).getPassword() <br __>
            > VoterTable.get(VoterId).getEmail() <br __>
            > VoterTable.get(VoterId).getSalt() <br __>
            > VoterTable.get(VoterId).getUidAssigned() : returns boolean to check if uniqueId is assigned to corresponding voter or not <br __>
            > VoterTable.get(VoterId).getUniqueId() <br __>
            > VoterTable.get(VoterId).changeUidAssigned() : changes the boolean value to true <br __> 
            > VoterTable.get(VoterId).setUniqueId(String uniqueId) <br __>
        - These changes can be seen in VoterData.java 
    2. CandidateTable 
        - In the form of a hashmap <String, String>
        - Stored only the candidate name and the corresponding unique id
        - These changes can be seen in CandidateData.java
    3. VoterCheckTable 
        - This is a table is currently to make sure to have unique UIDs for every voter. Perhaps, can be used for other checklists later on.
        - Changes related to this can be seen in VoterCheck.java
        - This table can even be deleted later on if not necessary.. Chill :P
    4. Sending Candidate Table as an Hashmap object in between Packet 1 and packet 2.
