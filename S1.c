#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PORT 4444
#define PORT_S2 2004

int main(){

	//______/ CREATION OF PERMANENT SOCKET FOR S2____________________________________________/

	//____________________________/ SOCKET CREATION __________________________________________
	int S2Socket, ret2;
	struct sockaddr_in server2Addr;
	char buffer2[1024];

	S2Socket = socket(AF_INET, SOCK_STREAM, 0);
	if(S2Socket < 0){
		printf("[-]Error in connection.\n");
		exit(1);
	}
	printf("[+]S2 Socket is created.\n");
	//________________________________________________________________________________________

	//____________________________/ CONFIGURING SOCKET ______________________________________
	memset(&server2Addr, '\0', sizeof(server2Addr));
	server2Addr.sin_family = AF_INET;
	server2Addr.sin_port = htons(PORT_S2);
	server2Addr.sin_addr.s_addr = inet_addr("127.0.0.1");
	//________________________________________________________________________________________


	//____________________________/ CONNECTINGG ..... ________________________________________
	ret2 = connect(S2Socket, (struct sockaddr*)&server2Addr, sizeof(server2Addr));
	if(ret2 < 0){
		printf("[-]Error in connection.\n");
		exit(1);
	}
	printf("[+]Connected to S2.\n");
	//______________________________________________________________________________________/

	//______________________/ END OF CREATION FOR S2 ____________________


	//____________________________/ SOCKET CREATION FOR VOTER ______________________________/
	int sockfd, ret;
	struct sockaddr_in serverAddr;

	int newSocket;
	struct sockaddr_in newAddr;

	socklen_t addr_size;

	char buffer[1024];
	pid_t childpid;

	sockfd = socket(AF_INET, SOCK_STREAM, 0);
	if(sockfd < 0){
		printf("[-]Error in connection.\n");
		exit(1);
	}
	printf("[+]Server Socket is created.\n");
	//______________________________________________________________________________________/

	//____________________________/ CONFIGURING SOCKET ______________________________________
	memset(&serverAddr, '\0', sizeof(serverAddr));
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(PORT);
	serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	//______________________________________________________________________________________/


	//____________________________/ BIND AND LISTEN ________________________________________
	ret = bind(sockfd, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
	if(ret < 0){
		printf("[-]Error in binding.\n");
		exit(1);
	}
	printf("[+]Bind to port %d\n", 4444);

	if(listen(sockfd, 10) == 0){
		printf("[+]Listening....\n");
	}else{
		printf("[-]Error in binding.\n");
	}
	//______________________________________________________________________________________/


	//____________________________/ START FROM THIS POINT __________________________________
	while(1){

		//____________________________/ SOCKET ACCEPTANCE FROM VOTER ______________________/
		newSocket = accept(sockfd, (struct sockaddr*)&newAddr, &addr_size);


		//____________________________/ INCASE OF FAILED ACCEPTANCE ________________________
		if(newSocket < 0)
		{
			exit(1);
		}


		//____________________________/ SOCKET ACCEPTED __________________________________/
		printf("Connection accepted from %s:%d\n", inet_ntoa(newAddr.sin_addr), ntohs(newAddr.sin_port));


		//____________________________/ CHILD PROCESS FOR EACH VOTER ______________________
		//__________ KEEP EVERY ACTIVTTY OF EACH VOTER WITHIN THIS IF BLOCK ______________/
		if((childpid = fork()) == 0)
		{

			//______________/ KEEPING CORRESPONDING SOCKET FOR VOTER OPEN ________________/
			while(1)
			{
				//__________________/ RECIEVING MESSAGE FROM VOTER ________________________
				recv(newSocket, buffer, 1024, 0);

				//__________________ ACKNOWLEDGMENT TO CLOSE SOCKET ______________________
				if(strcmp(buffer, ":exit") == 0)
				{
					printf("Disconnected from %s:%d\n", inet_ntoa(newAddr.sin_addr), ntohs(newAddr.sin_port));
					break;
				}
				
				//________________________ ACTIVITY FOR MESSAGE __________________________
				else{
					printf("Client: %s\n", buffer);
					send(newSocket, buffer, strlen(buffer), 0);
					bzero(buffer, sizeof(buffer));
				}
			}

			//____________________ CONNECTING TO SERVER 2 ____________________________

	


			//____________________ COMMUNICATING WITH S2  __________________________/
			while(1)
			{

			//____________________ MESSAGE SENT BY S1 ______________________________________
				printf("S1: \t");
				scanf("%s", &buffer2[0]);
				send(S2Socket, buffer2, strlen(buffer2), 0);


				//__ S1 ENDING CONNECTION BY COMPARING STRING THAT THE S1 SENT TO S2 ____
				if(strcmp(buffer2, ":exit") == 0)
				{
					close(S2Socket);
					printf("[-]Disconnected from server.\n");
					exit(1);
				}


				//____________________ RECEIVING A MESSAGE FROM SERVER 1 ________________________/
				if(recv(S2Socket, buffer2, 1024, 0) < 0)
				{
					printf("[-]Error in receiving data.\n");
				}else
				{
					printf("Server: \t%s\n", buffer2);
				}
			}

		}

	}

	close(newSocket);




	return 0;
}