#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <sys/types.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define PORT 4444

int main(){

	///////////////////////////// SOCKET CREATION //////////////////////////////////////////
	int clientSocket, ret;
	struct sockaddr_in serverAddr;
	char buffer[1024];

	clientSocket = socket(AF_INET, SOCK_STREAM, 0);
	if(clientSocket < 0){
		printf("[-]Error in connection.\n");
		exit(1);
	}
	printf("[+]Client Socket is created.\n");
	////////////////////////////////////////////////////////////////////////////////////////

	///////////////////////////// CONFIGURING SOCKET //////////////////////////////////////
	memset(&serverAddr, '\0', sizeof(serverAddr));
	serverAddr.sin_family = AF_INET;
	serverAddr.sin_port = htons(PORT);
	serverAddr.sin_addr.s_addr = inet_addr("127.0.0.1");
	////////////////////////////////////////////////////////////////////////////////////////


	///////////////////////////// CONNECTINGG ..... ////////////////////////////////////////
	ret = connect(clientSocket, (struct sockaddr*)&serverAddr, sizeof(serverAddr));
	if(ret < 0){
		printf("[-]Error in connection.\n");
		exit(1);
	}
	printf("[+]Connected to Server.\n");
	///////////////////////////////////////////////////////////////////////////////////////


	//////////////////// KEEPING CORRESPONDING SOCKET FOR VOTER OPEN //////////////////////
	while(1){

		//////////////////// MESSAGE SENT BY CLIENT //////////////////////////////////////
		printf("Client: \t");
		scanf("%s", &buffer[0]);
		send(clientSocket, buffer, strlen(buffer), 0);
		/////////////////////////////////////////////////////////////////////////////////

		// VOTER ENDING CONNECTION BY COMPARING STRING THAT THE CLIENT SENT TO VOTER ////
		if(strcmp(buffer, ":exit") == 0){
			close(clientSocket);
			printf("[-]Disconnected from server.\n");
			exit(1);
		}
		/////////////////////////////////////////////////////////////////////////////////

		//////////////////// RECEIVING A MESSAGE FROM SERVER 1 /////////////////////////
		if(recv(clientSocket, buffer, 1024, 0) < 0){
			printf("[-]Error in receiving data.\n");
		}else{
			printf("Server: \t%s\n", buffer);
		}
	}

	return 0;
}