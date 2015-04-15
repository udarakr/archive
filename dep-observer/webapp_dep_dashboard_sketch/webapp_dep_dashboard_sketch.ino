/*
Copyright 2015 https://github.com/udarakr

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

int redLed = 9;
int greenLed = 8;

void setup(){
Serial.begin(9600);
pinMode(redLed, OUTPUT);
pinMode(greenLed, OUTPUT);
}

void loop(){
digitalWrite(greenLed, LOW);
digitalWrite(redLed, LOW);
int val = Serial.read();

if(val == '1'){
digitalWrite(greenLed, HIGH);
digitalWrite(redLed, LOW);
}

if(val == '0'){
digitalWrite(greenLed, LOW);
digitalWrite(redLed, HIGH);
}

delay(1000);
}
