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
