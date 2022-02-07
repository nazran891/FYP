#include <ESP8266WiFi.h>
#include <PubSubClient.h>
#include <Servo.h>
const char* ssid = ""; //wifi name (saya padam sbb private information)
const char* password = ""; //wifi password (saya padam sbb private information)
 const char* mqtt_server = "broker.mobilepit.com";

Servo myservo;  // create servo object to control a servo
const int trigPin = 2;
const int echoPin = 0;

long duration;
int distance, inPercentage2, inPercentage;
char sensor[5];

WiFiClient espClient;
PubSubClient client(espClient);

void setup_wifi() {
   delay(100);
   WiFi.mode(WIFI_STA);
    Serial.print("Connecting to ");
    Serial.println(ssid);
    WiFi.begin(ssid, password);
    while (WiFi.status() != WL_CONNECTED) 
    {
      delay(500);
      Serial.print(".");
    }
  randomSeed(micros());
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}

void callback(char* topic, byte* payload, unsigned int length) 
{
  Serial.print("Command from MQTT broker is : ");
  Serial.print(topic);
  //if (strcmp(topic, "/feeds/motor")== 1)
  //{
    myservo.write(180);
    delay(2000);
    myservo.write(0);
  //}
  
}//end callback

void reconnect() {
  // Loop until we're reconnected
  while (!client.connected()) 
  {
    Serial.print("Attempting MQTT connection...");
    // Create a random client ID
    String clientId = "ESP8266Client-";
    clientId += String(random(0xffff), HEX);
    if (client.connect(clientId.c_str()))//if anything bad check here
    {
      Serial.println("connected");
     //once connected to MQTT broker, subscribe command if any
      client.subscribe("/feeds/motor");
      
    } else {
      Serial.print("failed, rc=");
      Serial.print(client.state());
      Serial.println(" try again in 5 seconds");
      // Wait 6 seconds before retrying
      delay(6000);
    }
  }
} //end reconnect()

void setup() {
  Serial.begin(9600);
  setup_wifi();
  client.setServer(mqtt_server, 1883);
  client.setCallback(callback);
  myservo.attach(5);  // attaches the servo on pin D1 to the servo object
  pinMode(trigPin, OUTPUT); // Sets the trigPin as an OUTPUT
  pinMode(echoPin, INPUT); // Sets the echoPin as an INPUT // attaches the ultra sonic sensor
  
}

void loop() {
  if (!client.connected()) {
    reconnect();
  }
  client.loop();
  
  digitalWrite(trigPin, LOW);
  delayMicroseconds(3);

  digitalWrite(trigPin, HIGH);
  delayMicroseconds(12);
  digitalWrite(trigPin, LOW);

  duration = pulseIn(echoPin, HIGH);
  distance = (duration * 0.034 / 2);
  inPercentage = 20 - distance;
  inPercentage2 = (inPercentage * 100 / 20);
  Serial.print(inPercentage2);
   Serial.print(inPercentage);

  String temp_str = String (inPercentage2); //converting distance (the int variable above) to a string 
  temp_str.toCharArray(sensor, temp_str.length() +1); //packaging up the data to publish to mqtt whoa

  delay (5000);
  client.publish("/feeds/monitor", sensor);

}
