

import java.io.*;
import java.net.*;
import java.util.*;

public class myHTTPServer extends Thread {

static final String HTML_START =
"<html>" +
"<title>HTTP Server in java</title>" +
"<body>";

static final String HTML_END =
"</body>" +
"</html>";

Socket connectedClient = null;
BufferedReader inFromClient = null;
DataOutputStream outToClient = null;


public myHTTPServer(Socket client) {
connectedClient = client;
}

public void run() {

try {

System.out.println( "The Client "+
  connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");

  inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream()));
  outToClient = new DataOutputStream(connectedClient.getOutputStream());

String requestString = inFromClient.readLine();
  String headerLine = requestString;
  System.out.println(headerLine);
  StringTokenizer tokenizer = new StringTokenizer(headerLine);
String httpMethod = tokenizer.nextToken();
String httpQueryString = tokenizer.nextToken();

StringBuffer responseBuffer = new StringBuffer();
responseBuffer.append("<b> This is the HTTP Server Home Page.... </b><BR>");
  responseBuffer.append("The HTTP Client request is ....<BR>");

  System.out.println("The HTTP request string is ....");
  
  InetAddress me = connectedClient.getInetAddress();
  String dottedQuad = me.getHostAddress();
  System.out.println("My address is " + dottedQuad);
 // if (httpMethod.equals(""))
  
  while (inFromClient.ready())
  {
    // Read the HTTP complete HTTP Query
    responseBuffer.append(requestString + "<BR>");
System.out.println(requestString);
requestString = inFromClient.readLine();
}
  System.out.println("asabhsda");
  /*System.out.println(+startTime);
  if(startTime > 1426090700){
	  System.out.println("badddd\n");
	  sendResponse(502, "bad" , false);
  }*/
  /*if(dottedQuad.equals("127.0.0.1")){
	  sendResponse(403, " " , false);
  }*/

if (httpMethod.equals("GET")) {
if (httpQueryString.equals("/")) {
 // The default home page
sendResponse(200, responseBuffer.toString(), false);
} else {
//This is interpreted as a file name
String fileName = httpQueryString.replaceFirst("/", "");
System.out.println("Now here");
System.out.println(fileName);

fileName = URLDecoder.decode(fileName);

if(fileName.equals("test")){
	//System.out.println("la la la\n");
	sendResponse(302, " " , false);
}
if (new File(fileName).isFile()){
sendResponse(200, fileName, true);
}
else {
sendResponse(404, "<b>404: The Requested resource not found ...." +
"</b>", false);
}
}
}

else if(httpMethod.equals("POST"))
	
{
	
	if (httpQueryString.equals("/")) {
		// The default home page
		sendResponse(200, responseBuffer.toString(), false);
	} else 
	{
		// This is interpreted as a file name
		String fileName = httpQueryString.replaceFirst("/", "");
		fileName = URLDecoder.decode(fileName);
		if (new File(fileName).isFile()) 
		{
			sendResponse(200, fileName, true);
		} else 
		{
			sendResponse(
					404,
					"<b>The Requested resource not found ...."
							+ "Usage: http://127.0.0.1:5000 or http://127.0.0.1:5000/</b>",
					false);
		}
	}
	
	
}
else sendResponse(404, "<b>404: The Requested resource not found ...." +
"</b>", false);
} catch (Exception e) {
e.printStackTrace();
}
}

public void sendResponse (int statusCode, String responseString, boolean isFile) throws Exception {

String statusLine = null;
String serverdetails = "Aman Server";
String contentLengthLine = null;
String fileName = null;
String contentTypeLine = "Content-Type: text/html" + "\r\n";
FileInputStream fin = null;
int flag =0;

if (statusCode == 200)
statusLine = "HTTP/1.1 200 OK" + "\r\n";
else if(statusCode == 404)
statusLine = "502:Bad Gateway" + "\r\n";
else if(statusCode == 404)
statusLine = "HTTP/1.1 404 Not Found" + "\r\n";
else if(statusCode == 301)
statusLine = "Moved Permnantly" + "\r\n";
else if(statusCode == 403){
statusLine = "403 Forbidden" + "\r\n";
flag = 1;

}
else if (statusCode == 302){

statusLine= "<b> Found " +
"</b>" + "<a href=\"http://www.google.com\"> www.google.com" + "</a>"+ "\n";
flag = 1;

}
else if (statusCode == 301)
statusLine = "Moved Permanantly" + "\r\n";

if (isFile) {
fileName = responseString;
fin = new FileInputStream(fileName);
contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
contentTypeLine = "Content-Type: \r\n";
}
else {
responseString = myHTTPServer.HTML_START + responseString + myHTTPServer.HTML_END;
contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";
}

outToClient.writeBytes(statusLine);
if(flag == 0){
outToClient.writeBytes(serverdetails);
outToClient.writeBytes(contentTypeLine);
outToClient.writeBytes(contentLengthLine);
outToClient.writeBytes("Connection: close\r\n");
outToClient.writeBytes("\r\n");
}
flag =0;
if (isFile) sendFile(fin, outToClient);
else outToClient.writeBytes(responseString);

outToClient.close();
}

public void sendFile (FileInputStream fin, DataOutputStream out) throws Exception {
byte[] buffer = new byte[1024] ;
int bytesRead;

while ((bytesRead = fin.read(buffer)) != -1 ) {
out.write(buffer, 0, bytesRead);
}
fin.close();
}
static long startTime;
public static void main (String args[]) throws Exception {

ServerSocket Server = new ServerSocket (8095, 10, InetAddress.getByName("127.0.0.1"));
System.out.println ("Server Waiting for client");
//startTime=System.currentTimeMillis()/1000;


while(true) {
//startTime = System.currentTimeMillis()/1000;
Socket connected = Server.accept();
    (new myHTTPServer(connected)).start();
}
}
}