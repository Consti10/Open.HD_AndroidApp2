
#this server runs on the ground pi
#It shall be the main entry point for external devices to connect to the Open.HD system
#Currently it only processes 'CHANGE and GET SETTINGS' request coming from the Android Settings module,but it 
#should not be limited to only that - e.g. starting/stopping an rtsp server for video over TCP (not udp)
#could be easily implemented here by adding more supported messages

import socket
import sys
from io import StringIO
import io
from FileParser import *
import time
import threading
from threading import Thread
from Forwarder import ForwardMessageToAirPiAndAwaitResponse,ForwardMessageToAirPi
from Message import *
from ServerAir import ReplyLoop

from queue import Queue


allSettingsInDictionary={}
allSettingsInDictionary.update(read_bash_file(WFBCSettingsFile))
allSettingsInDictionary.update(read_header_file(OSDSettingsFile))
allSettingsInDictionary.update(read_header_file(JoyconfigSettingsFile))

#Thread-safe queue for messages coming from the air pi. Read by the TCP thread, written by the WFB receiver thread
responsesFromAirPi=Queue()
#responsesFromAirPi.put("Hello from queue")


#Change value on ground pi
#forward message to air pi
def processChangeMessage(key,value):
    print("Changing Key on ground pi:",key,"Value:",value)
    #Change value from x to y on ground pi
    #TODO change value
    #forward message to air pi, we will receive the response in a different Thread
    ForwardMessageToAirPi(BuildMessageCHANGE(key,value))
    return BuildMessageCHANGE_OK("G",key,value)


#return value on ground pi
#forward message to air pi
def processGetMessage(key):
    print("Optaining value for Key on ground pi:",key)
    ForwardMessageToAirPi(BuildMessageGET(key))
    if(key=="VERSION"):
        value="OpenHD_1.1.1"
    else:
        value=allSettingsInDictionary.get(key)
        if(value==None):
            value="INVALID_SETTING"
    return BuildMessageGET_OK("G",key,value)
    

#process messages coming from the settings app (external devices)
def processMessageFromClient(msg):
    cmd,data=ParseMessage(msg)
    if(cmd=="CHANGE"):
        key,value=data.split("=")
        return processChangeMessage(key,value)
    elif(cmd=="GET"):
        return processGetMessage(data)
    return BuildMessageERROR()


#only call this one from the TCP loop context (connection must be valid)
def sendMessageToExternalDevice(message):
    global connection
    connection.sendall((message+"\n").encode())
    print("Sent to external device:",message)


#Listen for messages coming from the air pi
#add them to the message queue such that they can be forwarded to External devices
#blocking, execute in its own threaad
def ListenForAirPiMessages():
    while(True):
        receiveSock=socket.socket(socket.AF_INET,socket.SOCK_DGRAM)
        receiveSock.bind(('localhost',9090))
        data=receiveSock.recv(1024)
        if(data):
            global responsesFromAirPi
            responsesFromAirPi.put(data)


#create a new Thread which listens for incoming responses from the air pi and adds them to the queue,
#such that they can be forwarded to the app via TCP connection
thread1 = Thread(target = ListenForAirPiMessages)
thread1.start()


thread2 = Thread(target = ReplyLoop)
thread2.start()

# Create a TCP/IP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# Bind the socket to the address given on the command line
server_address = ("0.0.0.0", 5601)
print('starting up on %s port %s' % server_address)
sock.bind(server_address)
sock.listen(1)


while True:
    print('waiting for a connection')
    #this one hangs until someone connects
    #(exactly what we want,since blocking operations use almost no CPU)
    sock.settimeout(None)
    connection,client_address = sock.accept()
    lineBuffer = ''
    try:
        print('client connected:', client_address)
        #But as soon as there is an connection, we use a timeout - else we cannot implement the
        #Hello->HelloOK connection check that is required to test if the connection was forcibly closed
        #without notifying the server (e.g. the user disabled WIFI but didn't close the app first)
        connection.settimeout(2.0)
        lastHELLO_OKmessage=time.time()
        lastHELLOmessage=time.time()
        while True:
            #first,try to receive some data from the socket, and parse it into a line
            try:
                data = connection.recv(1024).decode()
                #Parse bytes into lines (makeFile() caused issues I could not solve)
                for x in data:
                    if(x=='\n'):
                        #print('Received line',lineBuffer)
                        if(lineBuffer=="HELLO_OK"):
                            lastHELLO_OKmessage=time.time()
                        else:
                            response=processMessageFromClient(lineBuffer)
                            sendMessageToExternalDevice(response)
                        lineBuffer=''
                    else:
                        lineBuffer+=x
            except socket.timeout as e:
                #print("Timeout exception",e)
                #Receiving timeout here is no problem
                pass
            #second,check if there are any messages in the queue that handles communication with the
            #air pi communication thread
            try:
                while (True):
                    message=responsesFromAirPi.get_nowait()
                    #print("Message in queue"+message)
                    sendMessageToExternalDevice(message)
            except Exception as e:
                #print("Queue exception",e)
                #when the queue is empty it throws an exception,do nothing in this case
                pass
            #third, check first if we didn't receive a HELLO_OK message in the last X=5 seconds. If so, we can assume
            #that the connection was closed but we didn't get notified
            currTime=time.time()
            if((currTime-lastHELLO_OKmessage)>5.0):
                print("No response from client in >5 seconds. CLosing connecction")
                connection.close()
            #if there was no error yet, send the 'HELLO' message to the client (every 2 seconds)
            if((currTime-lastHELLOmessage)>=2.0):
                lastHELLOmessage=currTime
                try:
                    sendMessageToExternalDevice(BuildMessageHELLO())
                except socket.timeout as e:
                    #timeout here would be strange 
                    print("Timeout exception 2",e)
    except Exception as e:
        print("ExceptionC",e)
        #don't forget to reset the lineBuffer
        lineBuffer=''