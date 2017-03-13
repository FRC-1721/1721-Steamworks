#!/usr/bin/env python
# Capture frame-by-frame

from grip2 import GripPipeline
import cv2
from pprint import PrettyPrinter
import sys
import numpy as np
from time import sleep

pp = PrettyPrinter()

class CameraSystem:
    def __init__(self, file = 0):
        self.areas = []
        self.centers = []
        self.gp = GripPipeline()
        self.cap = cv2.VideoCapture(file)
        self.file = file
        print self.file
        self.fArea = open('area.dat','w')
    def process(self, frame):
        self.gp.process(frame)
        self.areas = []
        self.centers = []
        areaTot = 0.0
        nSamples = 0
        rawData = []
        for contour in self.gp.filter_contours_output:
            x,y,w,h = cv2.boundingRect(contour)
            rawData.append([x,y,w,h])
            AR = h/w
            if (AR<1.8) or (AR> 2.5):
                continue
            self.centers.append(x)
            self.areas.append(w*h)  
            areaTot += w*h 
            nSamples +=1
        if nSamples== 2:
            color = (0,255,0)
            self.fArea.write(str(1.0/np.sqrt(areaTot/nSamples)) + '\n')
        else:
            color = (255,0,0)
        for i in range(len(rawData)):
            [x,y,w,h] = rawData[i]
            cv2.rectangle(frame,(x,y),(x+w,y+h),color,2)
        print('centers:',self.centers)
        print('areas',self.areas)
        return frame

    
    def loop(self):
        if self.file != 0:
            while self.cap.isOpened():
                ret, frame = self.cap.read()
                self.process(frame)
                gray = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                cv2.imshow('frame',gray)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                        break
                sleep(0.025)
            self.cap.release()
            cv2.destroyAllWindows()
        else:
            while True:
                ret, frame = self.cap.read()
                if ret == True:
                    frame = self.process(frame)
                    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2RGB)
                    cv2.imshow('frame',gray)
                    if cv2.waitKey(1) & 0xFF == ord('q'):
                        break
if len(sys.argv) > 1:
    cs = CameraSystem(sys.argv[1])
else:
    cs = CameraSystem()
cs.loop()

    
    
