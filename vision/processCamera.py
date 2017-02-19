#!/usr/bin/env python
# Capture frame-by-frame

from grip import GripPipeline
import cv2
from pprint import PrettyPrinter

cap = cv2.VideoCapture(0)
gp = GripPipeline()
pp = PrettyPrinter()
while True:
    ret, frame = cap.read()
    gp.process(frame)
    #pp.pprint( gp.find_contours_output)
