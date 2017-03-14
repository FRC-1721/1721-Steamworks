#!/usr/bin/env python
# Capture frame-by-frame

from grip2 import GripPipeline
import cv2
from pprint import PrettyPrinter
import sys
import numpy as np
from time import sleep
from time import clock
pp = PrettyPrinter()
import datetime
from threading import Thread
import threading
from imutils.video import WebcamVideoStream
from imutils.video import FPS
import imutils
import Queue

BUF_SIZE=3
q = Queue.Queue(BUF_SIZE)

# Performance improvement from http://www.pyimagesearch.com/2015/12/21/increasing-webcam-fps-with-python-and-opencv/
class RobotGripPipeline(GripPipeline):
    def __init__(self):
        GripPipeline.__init__(self)
        self.iSample = 0
        self.centers = []
        self.areas = []
        self.fArea = open('area.dat','w')
        
    def process(self, frame):
        GripPipeline.process(self,frame)
        areas = []
        centers = []
        areaTot = 0.0
        nSamples = 0
        rawData = []
        for contour in self.filter_contours_output:
            x,y,w,h = cv2.boundingRect(contour)
            rawData.append([x,y,w,h])
            AR = h/w
            if (AR<1.8) or (AR> 2.5):
                continue
            centers.append(x)
            areas.append(w*h)  
            areaTot += w*h 
            nSamples +=1
        if nSamples== 2:
            color = (0,255,0)
            self.centers = centers
            self.areas = areas
            self.fArea.write(str(1.0/np.sqrt(areaTot/nSamples)) + '\n')
        else:
            color = (255,0,0)
        for i in range(len(rawData)):
            [x,y,w,h] = rawData[i]
            cv2.rectangle(frame,(x,y),(x+w,y+h),color,2)
        print(rawData)
        return frame      
    
class ProducerThread(threading.Thread):
    def __init__(self,cam,fps):
        super(ProducerThread,self).__init__()
        self.cam = cam 
        self.fps = fps
        self.running = True
        
    def run(self):
        while self.running:
            if not q.full():
                frame = self.cam.read()
                q.put(frame)
                self.fps.update()
            sleep(0.01)
        
        return

class ConsumerThread(threading.Thread):
    def __init__(self,gp):
        super(ConsumerThread,self).__init__()
        self.gp = gp 
        self.running = True
        
    def run(self):
        while self.running:
            if not q.empty():
                frame = q.get()
                self.gp.process(frame)
            sleep(0.01)
            
class CameraSystem:
    def __init__(self, file = 0):
        self.cap = WebcamVideoStream(src=file).start()
        #self.cap.stream.set(3,640)
        #self.cap.stream.set(4,480)
        self.fps = FPS().start()
        # old method cv2.VideoCapture(file)
        self.file = file
        self.fArea = open('area.dat','w')
        self.pipeline = RobotGripPipeline()
        self.producer = ProducerThread(self.cap,self.fps)
        self.producer.start()
        self.consumers = []
        for i in range(BUF_SIZE):
            consumer = ConsumerThread(RobotGripPipeline())
            consumer.start()
            self.consumers.append(consumer)
            
    
    def loop(self):
        
        while self.fps._numFrames < 100:
            sleep(0.5)
        

    def wrapup(self):
        self.fps.stop()
        self.producer.running = False
        for consumer in self.consumers:
            consumer.running = False
        #self.producer.stop()
        #for consumer in self.consumers:
        #    consumer.stop()
        print("[INFO] elasped time: {:.2f}".format(self.fps.elapsed()))
        print("[INFO] approx. FPS: {:.2f}".format(self.fps.fps()))
        exit()
if len(sys.argv) > 1:
    cs = CameraSystem(sys.argv[1])
else:
    cs = CameraSystem()
cs.loop()
cs.wrapup()

    
    
