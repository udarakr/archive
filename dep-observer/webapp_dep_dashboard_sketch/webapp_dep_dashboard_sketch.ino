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
