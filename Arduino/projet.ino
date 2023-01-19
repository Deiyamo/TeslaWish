int pinSpeaker = A0;
double distance = 255.0;

void setup() {
  Serial.begin(9600);
  pinMode(pinSpeaker, OUTPUT);
}

void distanceBeeper() {
  double waitingBeeper = (distance / 255.0) * 1000.0;
  tone(pinSpeaker, 300);
  delay(waitingBeeper);
  noTone(pinSpeaker);
  delay(waitingBeeper);
}

void loop() {
  tone(pinSpeaker, 300);
}