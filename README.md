This project doen't use any built tool. So, it might be a little challenging to set-up this project. But it is pretty easy

download required library here
https://jar-download.com/tags/purejavacomm
https://opencv.org/releases/

Since this is running through microcontroller, there should be arduino code. I also added ino file in 
/////////////////////////////////////////////////////////////////////////////////////
#include <ESP32Servo.h>
Servo myservo;

int servoPin = 18;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
  myservo.attach(servoPin, 500, 2400);
}
void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
    char receivedChar = Serial.read();
    if(receivedChar == 'r'){
      myservo.write(0);
    } else if(receivedChar == 'g'){
      myservo.write(90);
    } else if(receivedChar == 'b'){
      myservo.write(180);
    }
  }
}
/////////////////////////////////////////////////////////////////////////////////////

