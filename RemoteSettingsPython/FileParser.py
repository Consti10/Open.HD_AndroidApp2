from tempfile import mkstemp
import os
import sys
import shutil
from tempfile import mkstemp
from shutil import move
from os import fdopen, remove


OSDSettingsFile = "test/osdconfig.txt"
WFBCSettingsFile = "test/openhd-settings-1.txt"
JoyconfigSettingsFile = "test/joyconfig.txt"


#there are 2 different types of files we need to read / modify
#1) files that contain '#define's' and are included in .c files. e.g. the osdconfig.txt file
#2) files that are used in a bash context, they use '#' for uncomment else lines can contain up to 1 key=value pair


#file_path: input file
#return: Dictionary containing key-value-pairs of input heeader file
def read_header_file(file_path):
    d={}
    with open (file_path, 'rt') as file:
        for line in file:
            if(line.startswith('#define')):
                _,_,afterDefine=line.partition('#define')
                #remove all whitespaces at beginning and at the end
                afterDefine=afterDefine.strip()
                #remove everything coming after an '//'
                afterDefine=afterDefine.split('//')[0]
                #print("afterDefine",afterDefine)
                #now split whitespace
                key,_,value=afterDefine.partition(' ')
                key.strip()
                value.strip()
                #print("key:",key,"value:",value)
                d.update({key : value})
    return d

#file_path: input file
#return: Dictionary containing key-value pairs of input bash file
def read_bash_file(file_path):
    d={}
    with open (file_path, 'rt') as file:
        for line in file:
            line=line.strip()
            if(line.startswith('#')):
                #print("uncomment",line)
                pass
            else:
                key,_,value=line.partition('=')
                #print("key:",key,"value:",value)
                d.update({key : value})
    return d

#replaces the line containing '#define KEY OLD_VALUE' with '#define KEY NEW_VALUE'
def replace_in_header_file(file_path,key,value):
    fh, abs_path = mkstemp()
    with fdopen(fh,'w') as new_file:
        with open(file_path) as old_file:
            for line in old_file:
                if  line.startswith("#define "+key):
                    new_file.write("#define "+key+" "+value+" //modified by app\n")
                else:
                    new_file.write(line)
    #Remove original file
    remove(file_path)
    #Move new file
    move(abs_path, file_path)


#replaces the line containing key=oldValue with key=newValue
def replace_in_bash_file(file_path,key,value):
    fh, abs_path = mkstemp()
    with fdopen(fh,'w') as new_file:
        with open(file_path) as old_file:
            for line in old_file:
                if  line.startswith(key+"="):
                    new_file.write(key+"="+value+"\n")
                else:
                    new_file.write(line)
    #Remove original file
    remove(file_path)
    #Move new file
    move(abs_path, file_path)


#determine in which file this key is stored
def determineSettingsLocation(key):
    pass

#place: Determines in which file the tuple is saved
def changeSetting(place,key,value):
    if(place=='OpenHD'):
        replace_in_bash_file(OSDSettingsFile,key,value)
    elif (place==''):
        pass



def test1():
    print("read files into dictionary and print dictionary's values")
    dictionary=read_header_file(OSDSettingsFile)
    for i in dictionary:
        print(i,dictionary[i])
    print('------------------------')
    dictionary=read_bash_file(WFBCSettingsFile)
    for i in dictionary:
        print(i,dictionary[i])


def test2():
    allSettingsInDictionary={}
    #allSettingsInDictionary.update(read_bash_file(WFBCSettingsFile))
    #allSettingsInDictionary.update(read_header_file(OSDSettingsFile))
    allSettingsInDictionary.update(read_header_file(JoyconfigSettingsFile))
    for i in allSettingsInDictionary:
        print(i,allSettingsInDictionary[i])


#replace 1 value in a header and 1 value in a bash file
def test3():
    replace_in_bash_file(WFBCSettingsFile,"FREQ","2372")
    replace_in_header_file(OSDSettingsFile,"DOWNLINK_RSSI_POS_X", "13")


#used this one to read the OpenHD-settings-1 file into the SynchronizedSettings list
def readForApp():
    dictionary=read_bash_file(WFBCSettingsFile)
    for i in dictionary:
        print("list.add(new ASetting(\""+i+"\",this));")

#test1()
#test2()
#test3()

#readForApp()