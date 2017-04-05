#!/usr/bin/env python
# Capture frame-by-frame
import string,cgi,time
from os import curdir, sep
from BaseHTTPServer import BaseHTTPRequestHandler, HTTPServer
from SocketServer import ThreadingMixIn
import re

cameraQuality=75
from grip2 import GripPipeline
import cv2
import cv
import sys
import numpy as np
from time import sleep
from time import clock
from threading import Thread
import threading
from imutils.video import WebcamVideoStream
from imutils.video import FPS
import imutils
import Queue
from networktables import NetworkTables
import datetime
#NetworkTables.setTeam(1721)
NetworkTables.initialize(server='roboRIO-1721-FRC.local')
BUF_SIZE=6
q = Queue.Queue(BUF_SIZE)
qOut = Queue.Queue(2)    
visionSample = 0

        
class VideoOutThread(threading.Thread):
    def __init__(self):
        super(VideoOutThread,self).__init__()
        self.running = True
        self.frame = None
        self.jpg = None
        self.frameID = 0
        self.fd = None
        self.sd = None
        self.vout = None
        self.robotMode = 'disabled'
        self.sampleID = 0
        
    def run(self):
        while self.running:
            if not qOut.empty():
                self.frame = qOut.get()  
                self.frameID += 1   
                self.writeData(self.frame)        
                #cv2.imshow('frame',self.frame)
                #if cv2.waitKey(1) & 0xFF == ord('q'):
                #    self.running = False
                
            sleep(0.03)
        if self.fd is not None:
            self.fd.close()
        if self.vout is not None:
            self.vout.release()
            
    def get_jpg(self):
        jpg = cv2.imencode('.jpg',self.frame)
        return jpg.tobytes()

    def setFile(self, name):
        if self.fd is not None:
            self.fd.close()
        if self.vout is not None:
            self.vout.release()
        timeStr =  datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
        parts = timeStr.split('-')
        timeStr = '-'.join(parts[3:])
        fileBase = 'vision.' +name +'.' + timeStr 
        fileName = fileBase + '.dat'
        self.fd = open(fileName,'w')
        self.fd.write('# visionSample, x, y, visX, visY, angle, rawDist, rawAngle, ' + 
                      'distM, distC, angleC, angleM, distRSquared, angleRSquared\n')
        # Define the codec and create VideoWriter object
        #fourcc = cv2.VideoWriter_fourcc(*args["codec"])
        fourcc  = cv2.cv.CV_FOURCC(*'MJPG')
        self.vout = cv2.VideoWriter(fileBase + '.avi',fourcc, 30, (640,480))


    def writeData(self,frame):
        global visionSample
        if self.sd is None:
            self.sd = NetworkTables.getTable('SmartDashboard')
        if self.sd is not None:
            try:
                robotMode = self.sd.getString('robotMode', 'disabled')
                if robotMode == 'disabled':
                    if self.fd is not None:
                        self.fd.close()
                        self.fd = None
                    if self.vout is not None:
                        print ('releasing vout')
                        self.vout.release()
                        self.vout = None
                    self.robotMode = robotMode
                    return
                if robotMode != self.robotMode:
                    self.setFile(robotMode)
                    self.robotMode = robotMode
                sampleID = self.sd.getNumber('visionSample',-1.0)
                cv2.putText(frame,str(sampleID),(10,460), cv2.FONT_HERSHEY_SIMPLEX, 1,(255,255,255),2)
                
                rawDist = self.sd.getNumber('visionRawDist', -1.0)
                rawAngle = self.sd.getNumber('visionRawAngle',-1000.0)

                
                x = self.sd.getNumber('PositionEstX', -1000.0)
                y = self.sd.getNumber('PositionEstY', -1000.0)

                angle = self.sd.getNumber('NavControllerHeading', -370.0)
                # if not getting angle, not point in writing data, maybe robot is off, or disabled
                #if angle < -360.0:
                #    return
                distM = self.sd.getNumber("Vision distM", 0.0)
                distC = self.sd.getNumber("Vision distC", 0.0)
                angleC = self.sd.getNumber("Vision angleC", 0.0)
                angleM = self.sd.getNumber("Vision angleM", 0.0)     
                visX = self.sd.getNumber("VisionEstX", -1000.0)
                visY = self.sd.getNumber("VisionEstY", -1000.0)                    
                distRSquared = self.sd.getNumber("distRSquared", 0.0)
                angleRSquared = self.sd.getNumber("angleRSquared",0.0)
                posString = 'pos: %s,%s'%(x,y)
                visPosString = 'vis: %s,%s'%(visX,visY)
                cv2.putText(frame,posString,(10,430), cv2.FONT_HERSHEY_SIMPLEX, 1,(255,255,255),2)
                cv2.putText(frame,visPosString,(10,400), cv2.FONT_HERSHEY_SIMPLEX, 1,(255,255,255),2)
                self.fd.write('%s %s %s %s %s %s %s %s %s %s %s %s %s %s \n'%(visionSample, x, y, visX, visY, angle, rawDist, rawAngle,
                                                  distM, distC, angleC, angleM, distRSquared, angleRSquared))
                self.vout.write(frame)
            except:
                print('unable to write data')




videoOut = VideoOutThread()

# Performance improvement from http://www.pyimagesearch.com/2015/12/21/increasing-webcam-fps-with-python-and-opencv/
class RobotGripPipeline(GripPipeline):
    def __init__(self):
        GripPipeline.__init__(self)
        self.iSample = 0
        self.centers = []
        self.areas = []
        self.sd = NetworkTables.getTable('SmartDashboard')
        
    def process(self, frame):
        GripPipeline.process(self,frame)
        areaTot = 0.0
        center = 0.0
        nSamples = 0
        rawData = []
        badData = []
        for contour in self.filter_contours_output:
            x,y,w,h = cv2.boundingRect(contour)
            
            AR = h/w
            if (AR<1.8) or (AR> 2.5) or (y+h < 120) or (y >380) \
                     or (x < 80) or (x+w > 560):
                badData.append([x,y,w,h])
                continue
            rawData.append([x,y,w,h])
            center += x  
            areaTot += w*h 
            nSamples +=1
        if nSamples == 2:
            self.publishNT(areaTot,center)
        else:
            badData.extend(rawData)
            rawData = []
        color = (0,255,0)
        for i in range(len(rawData)):
            [x,y,w,h] = rawData[i]
            cv2.rectangle(frame,(x,y),(x+w,y+h),color,2)
        color = (255,0,0)
        for i in range(len(badData)):
            [x,y,w,h] = badData[i]
            cv2.rectangle(frame,(x,y),(x+w,y+h),color,2)
        #print(rawData)
        return frame 
         
    def publishNT(self, areaTot, center):
        global visionSample
        visionSample+=1
        
        rawDist = 25.0/np.sqrt(float(areaTot))
        rawAngle = 10.0*(float(center)/640.0 - 1.0)
        
        if self.sd is None:
            self.sd = NetworkTables.getTable('SmartDashboard')
        if self.sd is not None:
            try:
                self.sd.putNumber('visionRawDist',rawDist )
                self.sd.putNumber('visionRawAngle',rawAngle)
                self.sd.putNumber('visionSample',visionSample)
            except:
                print('unable to publish data')

class ProducerThread(threading.Thread):
    def __init__(self,cam,fps):
        super(ProducerThread,self).__init__()
        self.cam = cam 
        self.fps = fps
        self.running = True
        self.sd = None
        self.fd = None
        self.robotMode = 'disabled'

    
    def setFile(self, name):
        if self.fd is not None:
            self.fd.close()
        timeStr =  datetime.datetime.now().strftime("%Y-%m-%d-%H-%M-%S")
        parts = timeStr.split('-')
        timeStr = '-'.join(parts[3:])
        fileName = 'vision.' +name +'.' + timeStr + '.dat'
        self.fd = open(fileName,'w')
        self.fd.write('# visionSample, x, y, visX, visY, angle, rawDist, rawAngle, ' + 
                      'distM, distC, angleC, angleM, distRSquared, angleRSquared\n')
         
    def run(self):
        while self.running:
            if not q.full():
                frame = self.cam.read()
                q.put(frame)
                self.fps.update()
                #self.writeData()
            sleep(0.03) # For roughly 30 fps
        #self.fd.close()
        return

    def writeData(self):
        global visionSample
        if self.sd is None:
            self.sd = NetworkTables.getTable('SmartDashboard')
        if self.sd is not None:
            try:
                robotMode = self.sd.getString('robotMode', 'disabled')
                if robotMode == 'disabled':
                    if self.fd is not None:
                        self.fd.close()
                        self.fd = None
                    self.robotMode = robotMode
                    return
                if robotMode != self.robotMode:
                    self.setFile(robotMode)
                    self.robotMode = robotMode
                rawDist = self.sd.getNumber('visionRawDist', -1.0)
                rawAngle = self.sd.getNumber('visionRawAngle',-1000.0)
                sampleID = self.sd.getNumber('visionSample',-1.0)
                x = self.sd.getNumber('PositionEstX', -1000.0)
                y = self.sd.getNumber('PositionEstY', -1000.0)
                angle = self.sd.getNumber('NavControllerHeading', -370.0)
                # if not getting angle, not point in writing data, maybe robot is off, or disabled
                #if angle < -360.0:
                #    return
                distM = self.sd.getNumber("Vision distM", 0.0)
                distC = self.sd.getNumber("Vision distC", 0.0)
                angleC = self.sd.getNumber("Vision angleC", 0.0)
                angleM = self.sd.getNumber("Vision angleM", 0.0)     
                visX = self.sd.getNumber("VisionEstX", -1000.0)
                visY = self.sd.getNumber("VisionEstY", -1000.0)                    
                distRSquared = self.sd.getNumber("distRSquared", 0.0)
                angleRSquared = self.sd.getNumber("angleRSquared",0.0)
                self.fd.write('%s %s %s %s %s %s %s %s %s %s %s %s %s %s \n'%(visionSample, x, y, visX, visY, angle, rawDist, rawAngle,
                                                  distM, distC, angleC, angleM, distRSquared, angleRSquared))
            except:
                print('unable to write data')

        

class ConsumerThread(threading.Thread):
    def __init__(self,gp):
        super(ConsumerThread,self).__init__()
        self.gp = gp 
        self.running = True
        
    def run(self):
        while self.running:
            if not q.empty():
                frame = q.get()
                frame = self.gp.process(frame)
                if not qOut.full():
                    qOut.put(frame)
            sleep(0.01)

          
class CameraSystem:
    def __init__(self, file = 0):
        self.cap = WebcamVideoStream(src=file).start()
        #self.cap.stream.set(3,640)
        #self.cap.stream.set(4,480)
        self.fps = FPS().start()
        # old method cv2.VideoCapture(file)
        self.producer = ProducerThread(self.cap,self.fps)
        self.producer.start()
        self.consumers = []
        for i in range(BUF_SIZE):
            consumer = ConsumerThread(RobotGripPipeline())
            consumer.start()
            self.consumers.append(consumer)

        videoOut.start()  
    
    def loop(self):
        pass
        while videoOut.running:
           sleep(0.5)
        

    def wrapup(self):
        self.fps.stop()
        self.producer.running = False
        for consumer in self.consumers:
            consumer.running = False
        videoOut.running = False
        print("[INFO] elasped time: {:.2f}".format(self.fps.elapsed()))
        print("[INFO] approx. FPS: {:.2f}".format(self.fps.fps()))
        exit()

class MyHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        global cameraQuality
        try:
            self.path=re.sub('[^.a-zA-Z0-9]', "",str(self.path))
            if self.path=="" or self.path==None or self.path[:1]==".":
                return
            if self.path.endswith(".html"):
                f = open(curdir + sep + self.path)
                self.send_response(200)
                self.send_header('Content-type',    'text/html')
                self.end_headers()
                self.wfile.write(f.read())
                f.close()
                return
            if self.path.endswith(".mjpeg"):
                self.send_response(200)
                self.wfile.write("Content-Type: multipart/x-mixed-replace; boundary=--aaboundary")
                self.wfile.write("\r\n\r\n")
                while 1:
                    if not qOut.empty():
                        img = qOut.get() 
                        rv, cv2mat=cv2.imencode(".jpg",img, [cv2.IMWRITE_JPEG_QUALITY, cameraQuality])
                        JpegData=cv2mat.tostring()
                        self.wfile.write("--aaboundary\r\n")
                        self.wfile.write("Content-Type: image/jpeg\r\n")
                        self.wfile.write("Content-length: "+str(len(JpegData))+"\r\n\r\n" )
                        self.wfile.write(JpegData)
                        self.wfile.write("\r\n\r\n\r\n")
                    time.sleep(0.1) # Roughtly controls fps of mjpeg server
                return
            if self.path.endswith(".jpeg"):
                f = open(curdir + sep + self.path)
                self.send_response(200)
                self.send_header('Content-type','image/jpeg')
                self.end_headers()
                self.wfile.write(f.read())
                f.close()
                return
            return
        except IOError:
            self.send_error(404,'File Not Found: %s' % self.path)
    def do_POST(self):
        global rootnode, cameraQuality
        try:
            ctype, pdict = cgi.parse_header(self.headers.getheader('content-type'))
            if ctype == 'multipart/form-data':
                query=cgi.parse_multipart(self.rfile, pdict)
            self.send_response(301)

            self.end_headers()
            upfilecontent = query.get('upfile')
            print "filecontent", upfilecontent[0]
            value=int(upfilecontent[0])
            cameraQuality=max(2, min(99, value))
            self.wfile.write("<HTML>POST OK. Camera Set to<BR><BR>");
            self.wfile.write(str(cameraQuality));

        except :
            pass

class ThreadedHTTPServer(ThreadingMixIn, HTTPServer):
#class ThreadedHTTPServer(HTTPServer):
    """Handle requests in a separate thread."""

def main():
    if len(sys.argv) > 1:
        cs = CameraSystem(sys.argv[1])
    else:
        cs = CameraSystem()
    try:
        #server = ThreadedHTTPServer(('localhost', 5808), MyHandler)
        print 'started video saving...'
        cs.loop()
        #server.serve_forever()
    except:
        print '^ Server failed or shut down'
        cs.wrapup()
        sleep(0.1) #Allow threads to stop
        #server.socket.close()  
    #cs.loop()
    #cs.wrapup()

if __name__ == '__main__':
    main()
        


    
    
