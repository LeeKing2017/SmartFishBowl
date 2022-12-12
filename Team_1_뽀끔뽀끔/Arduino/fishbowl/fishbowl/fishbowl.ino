#include <Arduino.h>
#include <WiFi.h>
#include <WiFiUdp.h>
#include <WebSocketClient.h>
#include <NTPClient.h>
#include <OneWire.h>
#include <DallasTemperature.h>
#include <ESP32_Servo.h>
#include <ArduinoJson.h>

#define temp_pin 5          
#define level_pin 35       
#define turbidity_pin 32    
#define pH_pin 33          
#define servo_pin 23        
#define relay_heater 18     
#define relay_cooler 19     
#define red_pin 26          
#define blue_pin 25

OneWire oneWire(temp_pin);
DallasTemperature temp_sensor(&oneWire);
Servo servo;
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);
WiFiClient client;
WebSocketClient webSocketClient;

const char* ssid = "";
const char* password = "";

char path[] = "/bowl";
char host[] = "222.238.106.88:8080";

double received_temp = 0;
String feeding_time1 = "";
String feeding_time2 = "";
String feeding_time3 = "";
int numOfFeeding1 = 0;
int numOfFeeding2 = 0;
int numOfFeeding3 = 0;
int totalFeeding = 0;
String check_feeding = "\"\"";
int device_id = 789;
String current_time = "";
String string_minute = "";
int current_minute = 0;
int compare_minute = 0;

void setup()
{
  Serial.begin(115200);
  
  WiFi.begin(ssid, password);
  
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000); 
    Serial.println("Connection to WiFi..");
  }

  Serial.print("WiFi connected with IP: ");
  Serial.println(WiFi.localIP());

  // Connect to the websocket server
  if (client.connect("222.238.106.88", 8080)) {
    Serial.println("Connected");
  } else {
    Serial.println("Connection failed.");
    while(1) {
      // Hang on failure
    }
  }

   // Handshake with the server
  webSocketClient.path = path;
  webSocketClient.host = host;
  if (webSocketClient.handshake(client)) {
    Serial.println("Handshake successful");
  } else {
    Serial.println("Handshake failed.");
    while(1) {
      // Hang on failure
    }  
  }
   
  timeClient.begin();
  timeClient.setTimeOffset(32400);    // GMT + 9
  timeClient.forceUpdate();
  current_time = (String)timeClient.getFormattedTime().substring(0, 5);
  string_minute = current_time.substring(3, 5);
  current_minute = string_minute.toInt();
  compare_minute = current_minute;
  
  temp_sensor.begin();
  servo.attach(servo_pin);
  pinMode(relay_heater, OUTPUT);
  pinMode(relay_cooler, OUTPUT);
  pinMode(red_pin, OUTPUT);
  pinMode(blue_pin, OUTPUT);
  delay(1000);
}

void loop()
{
    temp_sensor.requestTemperatures();
    double current_temp = temp_sensor.getTempCByIndex(0);
    
    int level_value = analogRead(level_pin);
    Serial.print("level_value : ");
    Serial.println(level_value);
    int current_level;
    if (level_value > 300 && level_value < 1300) {
      current_level = 4;
    }
    else if (level_value >= 1300 && level_value < 1700) {
      current_level = 3;
    }
    else if (level_value >= 1700 && level_value < 1900) {
      current_level = 2;
    }
    else if (level_value >= 1900 && level_value < 2100) {
      current_level = 1;
    }
    else {
      current_level = 0;
    }
    Serial.print("current_level : ");
    Serial.println(current_level);
    int tur_sensorValue = analogRead(turbidity_pin);
    double current_tur = tur_sensorValue * (5.0 / 1024.0);
    
    double pH_sensorValue = analogRead(pH_pin);
    Serial.print("pH_sensorValue : ");
    Serial.println(pH_sensorValue);
    double pH_voltage = pH_sensorValue * (3.6 / 4095.0);
    Serial.print("pH_voltage : ");
    Serial.println(pH_voltage);
    double current_pH = (3.6 * pH_voltage);
    Serial.print("current_pH : ");
    Serial.println(current_pH);
    
    String send_data = "";
    String received_data = "";
    
    StaticJsonDocument<300> doc;
   
    Serial.print("Celsius temperature: ");
    Serial.println(current_temp); 
  
    Serial.print("Water level : ");
    Serial.println(current_level);
  
    Serial.print("Turbidity : ");
    Serial.println(current_tur);
   
    Serial.print("pH : ");
    Serial.println(current_pH);
    
    Serial.println(current_time);

     // 먹이 잔량
    if (totalFeeding > 20) {
      check_feeding = "lack";
      totalFeeding = 0;
    }
    else {
      check_feeding = "\"\"";
    }
    
    // 5분마다 데이터 전송 및 수신
    if (current_minute == compare_minute) {
      // 데이터 수신
      if (client.connected()) {
        webSocketClient.getData(received_data);
        
        if (received_data.length() > 0) {
          Serial.print("Received Data : ");
          Serial.println(received_data);
    
          DeserializationError error = deserializeJson(doc, received_data);
    
          if (error) {   //Check for errors in parsing
    
            Serial.println("deserialzeJson() failed!");
            Serial.println(error.c_str());
       
            return;
          }
          
          received_temp = doc["temperature"];
          feeding_time1 = doc["firstTime"].as<String>().substring(0, 5);
          feeding_time2 = doc["secondTime"].as<String>().substring(0, 5);
          feeding_time3 = doc["thirdTime"].as<String>().substring(0, 5);
          numOfFeeding1 = doc["numberOfFirstFeedings"];
          numOfFeeding2 = doc["numberOfSecondFeeings"];
          numOfFeeding3 = doc["numberOfThirdFeedinga"];
          Serial.println("data recevied!");
        }
        else {
          Serial.println("no data!");  
        }
       }
       
      // 데이터 전송
      if (client.connected()) {
        send_data = "{\"temperature\" : " + (String)current_temp + "," +
                    "\"waterLevel\" : " + (String)current_level + "," +
                    "\"turbidity\" : " + (String)current_tur + "," +
                    "\"ph\" : " + (String)current_pH + "," +
                    "\"deviceId\" : " + (String)device_id + "," +
                    "\"checkLeftovers\" : " + check_feeding + "}";
    
        Serial.println("data sent!");
        webSocketClient.sendData(send_data);
      }
      if (compare_minute + 5 < 60) {
        compare_minute += 5;
      }
      else {
        compare_minute = (compare_minute + 5) - 60;
      }

      // 먹이주기
    if (numOfFeeding1 > 0 && current_time == feeding_time1) {
      Serial.println("feeding..");
      
      for (int i = 0; i < numOfFeeding1; i++) {
        for (int posDegrees = 60; posDegrees >= 0; posDegrees--) {
          servo.write(posDegrees);
          delay(20);
        }
  
        for (int posDegrees = 0; posDegrees <= 60; posDegrees++) {
           servo.write(posDegrees);
           delay(20);
        }
        totalFeeding += 1;     
      }
    }
    else if (numOfFeeding2 > 0 && current_time == feeding_time2) {
      Serial.println("feeding..");
      
      for (int i = 0; i < numOfFeeding2; i++) {
        for (int posDegrees = 60; posDegrees >= 0; posDegrees--) {
          servo.write(posDegrees);
          delay(20);
        }
  
        for (int posDegrees = 0; posDegrees <= 60; posDegrees++) {
           servo.write(posDegrees);
           delay(20);
         }
        totalFeeding += 1;
       }
    }
  
    else if (numOfFeeding3 > 0 && current_time == feeding_time3) {
      Serial.println("feeding..");
      
      for (int i = 0; i < numOfFeeding3; i++) {
        for (int posDegrees = 60; posDegrees >= 0; posDegrees--) {
           servo.write(posDegrees);
           delay(20);
        }
  
        for (int posDegrees = 0; posDegrees <= 60; posDegrees++) {
           servo.write(posDegrees);
           delay(20);
        }
        totalFeeding += 1;
      }
    }
    
   }
    
    // 온도 조절
    if (received_temp != 0 && (current_temp + 1) < received_temp) {
      digitalWrite(relay_cooler, LOW);
      Serial.println("heater on");
      digitalWrite(relay_heater, HIGH);  
      analogWrite(red_pin, 255); 
      analogWrite(blue_pin, 0);
     }
    else if (received_temp != 0 && (current_temp - 1) > received_temp) {
      digitalWrite(relay_heater, LOW);
      Serial.println("cooler on");
      digitalWrite(relay_cooler, HIGH);
      analogWrite(blue_pin, 255);
      analogWrite(red_pin, 0);
     }
    else {
      digitalWrite(relay_heater, LOW);
      digitalWrite(relay_cooler, LOW);
      analogWrite(red_pin, 0);
      analogWrite(blue_pin, 0);
     }
      
    timeClient.forceUpdate();
    current_time = (String)timeClient.getFormattedTime().substring(0, 5);
    string_minute = current_time.substring(3, 5);
    current_minute = string_minute.toInt();

    delay(5000);   
}
   

  
