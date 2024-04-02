This project doesn't use any build tool. So, it might be a little challenging to set up. However, it is pretty easy.

Download the required libraries from the following links:
https://jar-download.com/tags/purejavacomm
https://opencv.org/releases/

Since this project involves communication with a microcontroller, there is an accompanying .ino code file located at MicroControllerCode/serialTest.ino. (I used an Esp32 in my project, but you can use whatever microcontroller you prefer.)

SENDING DATA TO ARDUINO:
I have written a method in the main file to send data, which will be printed as 'r', 'g', or 'b' to the Serial port.

CHECK SENSOR:
There is also a checkSensor() method in the main file. If you plan to use a sensor such as ultrasonic or infrared and automate the capture process, you can uncomment the relevant code section.
