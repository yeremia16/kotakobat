#include <ArduinoJson.h>
#include <ESP8266WiFi.h>
//#include <ESP8266WiFiMulti.h>
#include "Wire.h"
#include "PCF8574.h"
#include <LiquidCrystal_I2C.h>
#include <DNSServer.h>
#include <ESP8266WebServer.h>
#include <WiFiManager.h>
#include "EEPROM.h"
#define EEPROM_SIZE 128

//RTC Clock
#define DS3231_I2C_ADDRESS 0x68

//EEPROM
bool testWifi(void);
void launchWeb(void);
void setupAP(void);

int i = 0;
int statusCode;
const char* ssid = "text";
const char* passphrase = "text";
String st;
String content;

//Establishing Local server at port 80 whenever required
ESP8266WebServer server(80);

//Https
const char* host = "vhost.ti.ukdw.ac.id";
const int httpsPort = 443;
const char fingerprint[] PROGMEM = "f7d90371ea3eb2f38f23a2b455f7a152ce6ea879";

String nama_alat="A1";

//JSON data
String rcv="";
String finale="";
int interrupt=0;
int counter=0;

//JSON id_user
int id_user=0;
String s_iduser="";
String iduser="";

//counter
int ctrDelay=0;
int id_obat_old=0;
int jam_old=0;
int ctrJam=0;

// Config IO Expander
PCF8574 exp1(0x20);

//Config Buzzer
int buzzer=0;
int cekBuzzer=0;

//Config Limit Switch
int LS_1=14; //D5
int ST_1=1;

int LS_2=13; //D7
int ST_2=1;

//Config LED laci
int Laci1=16;
int Laci2=12;

WiFiClientSecure client;

//Config Json
StaticJsonDocument<9999> jsondoc;
//DynamicJsonDocument jsondoc(9999999999999);

//Config Struct
typedef struct structData{
   String id_obat;
   int id_waktu;
   int nomor_laci;
   int nomor_tempat;
   int waktu;
   int waktu_hitung;
   int jumlah_obat;
   int aturan_makan;
   int done;
};
structData waktuobat;

//Config RTC
LiquidCrystal_I2C lcd(0x27, 20, 4);
int jam=0;
int menit=0;
int intTime=0;
byte second, minute, hour, dayOfWeek, dayOfMonth, month, year;

//Cek LED
int led=16;

//Buat array of struct
structData arrData[100];

//RTC function
// Convert normal decimal numbers to binary coded decimal
byte decToBcd(byte val)
{
  return( (val/10*16) + (val%10) );
}
// Convert binary coded decimal to normal decimal numbers
byte bcdToDec(byte val)
{
  return( (val/16*10) + (val%16) );
}

void getData()
{
  client.setFingerprint(fingerprint);
  if (client.connect(host, httpsPort)) {
    Serial.println("Get JSON data");
    client.print(String("GET ") + "/~yeremia/appskripsi/api/iot/iotget?id_user="+ id_user + "/" + " HTTP/1.1\r\n" + "Host: " + host + "\r\n" + "Connection: close\r\n\r\n"); //GET request for server response.
    while(client.connected())
    {
      rcv="";
      String line = client.readStringUntil('\r'); //Read the server response line by line..
      rcv+=line;
    }
    Serial.println("Get Data Done");
    client.stop(); // Close the connection.
  } else{
    Serial.println("Gagal get Data");
  }
}

void getUser()
{
  //Set fingerprint supaya dapat mengambil data dari server HTTPS
  client.setFingerprint(fingerprint);
  //Menghubungkan NodeMCU dengan server
  if(client.connect(host, httpsPort)) {
    Serial.println("Get USER ID");
    //Request Path
    client.print(String("GET ") + "/~yeremia/appskripsi/api/iot/getuser?nama_alat="+nama_alat
    +" HTTP/1.1\r\n"+"Host:"+host
    +"\r\n"+"Connection:close\r\n\r\n");
    while(client.connected())
    {
      s_iduser=" ";
      iduser="";
      String a = client.readStringUntil('\r'); //Read the server response line by line..
      s_iduser+=a; //And store it in rcv.
    }
    Serial.println("Get User done");
    client.stop(); // Close the connection.
  } else{
    Serial.println("Get user ID");
  }
}

int updateObat(int id_obat_new, int id_waktu)
{
  digitalWrite(buzzer, HIGH);
  client.setFingerprint(fingerprint);
  if (client.connect(host, httpsPort)) 
  {
    Serial.println("Update jumlah obat");
    client.print(String("GET ")+"/~yeremia/appskripsi/api/iot/updateobat?id_obat="+id_obat_new+"&id_waktu="+id_waktu+" HTTP/1.1\r\n"+"Host:"+host+"\r\n"+"Connection:close\r\n\r\n");
    while(client.connected())
    {
      s_iduser=" ";
      iduser="";
      String a = client.readStringUntil('\r'); //Read the server response line by line..
      s_iduser+=a; //And store it in rcv.
    }
  } else {
    Serial.println("Gagal update obat");
  }
}

int updateStateObat(int id_waktu)
{
  client.setFingerprint(fingerprint);
  if (client.connect(host, httpsPort)) {
    Serial.println("Update jumlah obat");
    client.print(String("GET ")+"/~yeremia/appskripsi/api/iot/updateobatstate?id_waktu="+id_waktu+" HTTP/1.1\r\n"+"Host:"+host+"\r\n"+"Connection:close\r\n\r\n");
    while(client.connected()){
      s_iduser=" ";
      iduser="";
      String a = client.readStringUntil('\r'); //Read the server response line by line..
      s_iduser+=a; //And store it in rcv.
    }
  } else {
    Serial.println("Gagal update state obat");
  }
}

//Buzzer pattern
void buzzerOn(){
  digitalWrite(buzzer, HIGH);
  delay(500);
  digitalWrite(buzzer, LOW);
  delay(500);
}

void setup() {
  Serial.begin(115200);
  pinMode(led, OUTPUT);
  
  //Setting RTC
  Wire.begin();
//  detik, menit, jam, week of days, tanggal, bulan, tahun
//  setDS3231time(0,8,10,1, 26, 5, 20);
  
  //Setting LCD
  lcd.begin();
  lcd.clear();
  lcd.noCursor();

  //Setting limitswitch
  pinMode(LS_1, INPUT);
  pinMode(LS_2, INPUT);

  //Setting LED laci
  pinMode(Laci1, OUTPUT);
  pinMode(Laci2, OUTPUT);
  digitalWrite(Laci1, LOW);
  digitalWrite(Laci2, LOW);

  //Setting buzzer
  pinMode(buzzer, OUTPUT);
  //Set buzzer mati
  digitalWrite(buzzer, HIGH);
  
  //Setting IO Expander
  exp1.pinMode(P0, OUTPUT);
  exp1.pinMode(P1, OUTPUT);
  exp1.pinMode(P2, OUTPUT);
  exp1.pinMode(P3, OUTPUT);
  exp1.pinMode(P4, OUTPUT);
  exp1.pinMode(P5, OUTPUT);
  exp1.pinMode(P6, OUTPUT);
  exp1.pinMode(P7, OUTPUT);

  exp1.begin();
    
  exp1.digitalWrite(P0, LOW);
  exp1.digitalWrite(P1, LOW);
  exp1.digitalWrite(P2, LOW);
  exp1.digitalWrite(P3, LOW);
  exp1.digitalWrite(P4, LOW);
  exp1.digitalWrite(P5, LOW);
  exp1.digitalWrite(P6, LOW);
  exp1.digitalWrite(P7, LOW);

  lcd.setCursor(0,0);
  lcd.print("Sedang mengatur alat");

  EEPROM.begin(512); //Initialasing EEPROM
  Serial.println();
  String ssid="";
  
  for(int i=0; i<32; i++){
    ssid+=char(EEPROM.read(i));
  }
  
  String pass="";
  for(int i=32; i<96; ++i){
    pass+=char(EEPROM.read(i));
  }
  
  Serial.print("SSID: ");
  Serial.print(ssid);
  Serial.print(" SSID Length: ");
  Serial.println(ssid.length());
  
  Serial.print("Password WiFi: ");
  Serial.print(pass);
  Serial.print(" Password Length: ");
  Serial.println(pass.length());
  
  WiFi.mode(WIFI_STA);
  WiFi.begin(ssid,pass);

  WiFi.begin(ssid.c_str(), pass.c_str());
  if (testWifi())
  {
    Serial.println("Succesfully Connected!!!");
    iduser="0";
  
    getUser();
    
    for(int i=0; i<s_iduser.length(); i++){
      if(isDigit(s_iduser[i])){
       iduser+=s_iduser[i];
      }else{
       continue;
      }
    }
    
    id_user = iduser.toInt();
    Serial.println(id_user);
    if(id_user==0){
      lcd.clear();
      lcd.setCursor(0,1);
      lcd.print("Alat tidak terhubung");
      lcd.setCursor(0,2);
      lcd.print("Nyalakan ulang alat");
    }
    delay(1000);
    getData();
    
    //Fetch JSON ke dalam data
    DeserializationError err = deserializeJson(jsondoc, rcv);
    if(err){
      Serial.print("isi err: ");
      Serial.println(err.c_str());
      return;
    }
    
    //Buat array of struct
  //  structData arrData[jsondoc.size()];
  
    //Masukkan Json ke struct arrData
    for(int i=0; i<jsondoc.size(); i++){
      String idobat=jsondoc[i]["id_obat"];
      int nomor_laci=jsondoc[i]["nomor_laci"];
      int nomor_tempat=jsondoc[i]["nomor_tempat"];
      int jumlah_obat=jsondoc[i]["jumlah_obat"];
      int id_waktu=jsondoc[i]["id_waktu"];
      String aturankonsum=jsondoc[i]["aturan_makan"];
      int cAturankonsum=aturankonsum.toInt();
      
      waktuobat.aturan_makan=cAturankonsum;
      waktuobat.id_waktu=id_waktu;
      waktuobat.nomor_laci=nomor_laci;
      waktuobat.nomor_tempat=nomor_tempat;
      waktuobat.id_obat=idobat;
      waktuobat.jumlah_obat=jumlah_obat;
      waktuobat.done=jsondoc[i]["done"];
  
      //Pemrosesan waktu
      String jam="";
      String menit="";
      String waktu=jsondoc[i]["waktu"];
      
      jam=waktu[0];
      jam+=waktu[1];
      
      menit=waktu[3];
      menit.concat(waktu[4]);
  
      int cJam=jam.toInt();
      int cMenit=menit.toInt();
      
      int waktutotal=0;
      waktutotal=(cJam*100)+cMenit;
      waktuobat.waktu=waktutotal;
      
      Serial.println("========================================");
      Serial.print("JAM: ");
      Serial.println(cJam);
      Serial.print("MENIT: ");
      Serial.println(cMenit);
      Serial.println("========================================");
  
      int total=0;
      if(cAturankonsum==0){
        //Setelah makan
        Serial.println("SESUDAH MAKAN");
        if(cMenit+30>=60){
          cJam=(cJam+1)%24;
          cMenit=(cMenit+30)-60;
        }else{
          cMenit=cMenit+30;
          cJam=cJam;
        }
      }else{
        //Sebelum makan
        Serial.println("SEBELUM MAKAN");
        if(cMenit-30<0){
          Serial.print("cMenit before count: ");
          Serial.println(cMenit);
          cMenit=(60+cMenit)-30;
          Serial.print("cMenit after count: ");
          Serial.println(cMenit);
          if(cJam-1<0){
            cJam=23;
          }else{
            cJam=cJam-1;
          }
        }else{
          cJam=cJam;
          cMenit=cMenit-10;
        }
      }
      cJam=cJam*100;
      total=cJam+cMenit;
      Serial.print("cJam: ");
      Serial.println(cJam);
      Serial.print("cMenit: ");
      Serial.println(cMenit);
      Serial.print("Total: ");
      Serial.println(total);
      Serial.println("===============================");
      waktuobat.waktu_hitung=total;
      arrData[i]=waktuobat;
    }
    lcd.clear();
    return;
  }
  else
  {
    String password_alat="123456";
    Serial.println("Turning the HotSpot On");
    lcd.setCursor(0,1);
    lcd.print(nama_alat);
    lcd.setCursor(0,2);
    lcd.print(password_alat);
    lcd.setCursor(0,3);
    lcd.print("192.168.4.1");
    launchWeb();
    setupAP();// Setup HotSpot
  }

  Serial.println();
  Serial.println("Waiting.");
  
  while ((WiFi.status() != WL_CONNECTED))
  {
    Serial.print(".");
    delay(100);
    server.handleClient();
  }
}

void loop() {
  ST_1 = digitalRead(LS_1);
  ST_2 = digitalRead(LS_2);

  displayTime();

  //Cek Alarm
  Serial.println("Data Array: ");
  for(int i=0; i<jsondoc.size(); i++){
    if(arrData[i].jumlah_obat<3){
      lcd.setCursor(0,3);
      lcd.print("Obat laci ");
      lcd.print(arrData[i].nomor_laci);
      lcd.print(" tempat ");
      lcd.print(arrData[i].nomor_tempat);
    }
    Serial.print(arrData[i].id_waktu);
    Serial.print(", ");
    Serial.print(arrData[i].waktu);
    Serial.print(", ");
    Serial.print(arrData[i].waktu_hitung);
    Serial.print(", ");
    Serial.print(arrData[i].aturan_makan);
    Serial.print(", ");
    Serial.print(arrData[i].done);
    Serial.println(".");
    
    //Aturan makan=Sebelum Makan
    if(arrData[i].aturan_makan==0){
      if(arrData[i].waktu <= intTime && intTime <= arrData[i].waktu_hitung && arrData[i].done==0){
//      if(arrData[i].waktu_hitung <= intTime && arrData[i].done==0){
        Serial.println("=========IF SEBELUM MAKAN=========");
        Serial.println("TRIGGERED SEBELUM MAKAN: ");
        Serial.print("Waktu: ");
        Serial.println(arrData[i].waktu);
        Serial.print("Waktu_hitung: ");
        Serial.println(arrData[i].waktu_hitung);
        Serial.print("Nomor Laci: ");
        Serial.println(arrData[i].nomor_laci);
        Serial.print("Nomor Tempat: ");
        Serial.println(arrData[i].nomor_tempat);
        Serial.println("=========Cek buzzer=========");
        buzzerOn();
        Serial.println("============================");
        
        //Nomor Laci 1
        if(arrData[i].nomor_laci==1){
          digitalWrite(Laci1, HIGH);
          //Jika laci pertama dibuka
          //Laci 1 kebuka
          if(ST_1==1){
            if(arrData[i].nomor_tempat==1 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P0, HIGH);
            }else if(arrData[i].nomor_tempat==2 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P1, HIGH);
            }else if(arrData[i].nomor_tempat==3 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P2, HIGH); 
            }else if(arrData[i].nomor_tempat==4 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P3, HIGH);
            }
            cekBuzzer=1;
            Serial.println("================");
            Serial.println("Laci 1 terbuka");
            Serial.print("Laci: "); 
            Serial.println(arrData[i].nomor_laci);
            Serial.print("Tempat: "); 
            Serial.println(arrData[i].nomor_tempat);
            Serial.println("================");
            Serial.print("ID_OBAT: ");
            Serial.println(arrData[i].id_obat);
            Serial.println("================");
            arrData[i].done=1;
            updateObat(arrData[i].id_obat.toInt(), arrData[i].id_waktu);
          }
        }
        //Nomor Laci 2
        if(arrData[i].nomor_laci==2){
          digitalWrite(Laci2, HIGH);
          //Jika laci kedua dibuka
          //Laci 2 kebuka
          if(ST_2==1){
            if(arrData[i].nomor_tempat==1 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P4, HIGH);
            }else if(arrData[i].nomor_tempat==2 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P5, HIGH);
            }else if(arrData[i].nomor_tempat==3 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P6, HIGH);
            }else if(arrData[i].nomor_tempat==4 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P7, HIGH);
            }
            cekBuzzer=1;
            Serial.println("================");
            Serial.println("Laci 2 terbuka");
            Serial.print("Laci: "); 
            Serial.println(arrData[i].nomor_laci);
            Serial.print("Tempat: "); 
            Serial.println(arrData[i].nomor_tempat);
            Serial.println("================");
            Serial.print("ID_OBAT: ");
            Serial.println(arrData[i].id_obat);
            Serial.println("================");
            arrData[i].done=1;
            updateObat(arrData[i].id_obat.toInt(), arrData[i].id_waktu);
          }
        }
      }else if(intTime > arrData[i].waktu_hitung){
        digitalWrite(buzzer, HIGH);
        digitalWrite(Laci1, LOW);
        digitalWrite(Laci2, LOW);
      }
    }
    
    //=============================================================================================    
    //Aturan makan = Sesudah Makan
    else if(arrData[i].aturan_makan==1){
      if(arrData[i].waktu_hitung <= intTime && intTime <= arrData[i].waktu && arrData[i].done==0){  
//      if(arrData[i].waktu <= intTime && arrData[i].done==0){
        Serial.println("=========IF SESUDAH MAKAN=========");
        Serial.println("TRIGGERED SESUDAH MAKAN: ");
        Serial.print("Waktu: ");
        Serial.println(arrData[i].waktu);
        Serial.print("Waktu_hitung: ");
        Serial.println(arrData[i].waktu_hitung);
        Serial.print("Nomor Laci: ");
        Serial.println(arrData[i].nomor_laci);
        Serial.print("Nomor Tempat: ");
        Serial.println(arrData[i].nomor_tempat);
        Serial.println("=========Cek buzzer=========");
        buzzerOn();
        Serial.println("============================");
        
        //Tampilkan di LCD untuk jgn lupa makan
        lcd.setCursor(4,1);
        lcd.print("Jangan lupa");
        lcd.setCursor(4,2);
        lcd.print("Makan dahulu");
        
        //Nomor Laci 1
        if(arrData[i].nomor_laci==1){
          digitalWrite(Laci1, HIGH);
          //Jika laci pertama dibuka
          //Laci 1 kebuka
          if(ST_1==1){
            if(arrData[i].nomor_tempat==1 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P0, HIGH);
            }else if(arrData[i].nomor_tempat==2 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P1, HIGH);
            }else if(arrData[i].nomor_tempat==3 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P2, HIGH);
            }else if(arrData[i].nomor_tempat==4 && arrData[i].nomor_laci==1){
              exp1.digitalWrite(P3, HIGH);
            }
            cekBuzzer=1;
            Serial.println("================");
            Serial.println("Laci 1 terbuka");
            Serial.print("Laci: "); 
            Serial.println(arrData[i].nomor_laci);
            Serial.print("Tempat: "); 
            Serial.println(arrData[i].nomor_tempat);
            Serial.println("================");
            Serial.print("ID_OBAT: ");
            Serial.println(arrData[i].id_obat);
            Serial.println("================");
            arrData[i].done=1;
            updateObat(arrData[i].id_obat.toInt(), arrData[i].id_waktu);
          }
        }
        //Nomor Laci 2
        if(arrData[i].nomor_laci==2){
          digitalWrite(Laci2, HIGH);
          //Jika laci kedua dibuka
          //Laci 2 kebuka
          if(ST_2==1){
            if(arrData[i].nomor_tempat==1 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P4, HIGH);
            }else if(arrData[i].nomor_tempat==2 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P5, HIGH);
            }else if(arrData[i].nomor_tempat==3 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P6, HIGH);
            }else if(arrData[i].nomor_tempat==4 && arrData[i].nomor_laci==2){
              exp1.digitalWrite(P7, HIGH);
            }
            cekBuzzer=1;
            Serial.println("================");
            Serial.println("Laci 2 terbuka");
            Serial.print("Laci: "); 
            Serial.println(arrData[i].nomor_laci);
            Serial.print("Tempat: "); 
            Serial.println(arrData[i].nomor_tempat);
            Serial.println("================");
            Serial.print("ID_OBAT: ");
            Serial.println(arrData[i].id_obat);
            Serial.println("================");
            arrData[i].done=1;
            updateObat(arrData[i].id_obat.toInt(), arrData[i].id_waktu);
          }
        }
      }else if(intTime > arrData[i].waktu){
        digitalWrite(buzzer, HIGH);
        digitalWrite(Laci1, LOW);
        digitalWrite(Laci2, LOW);
      }
    }
    if(ST_1==0 || ST_2==0){
//      Serial.println("===========================");
//      Serial.print("LACI YANG NYALA: ");
//      Serial.println(arrData[i].nomor_laci);
//      Serial.print("NOMOR TEMPAT YANG NYALA: ");
//      Serial.println(arrData[i].nomor_tempat);
//      Serial.println("===========================");
      if(ST_1==0 && arrData[i].nomor_laci==1){
        switch(arrData[i].nomor_tempat){
          case 1:
            exp1.digitalWrite(P0, LOW);
            break;
          case 2:
            exp1.digitalWrite(P1, LOW);
            break;
          case 3:
            exp1.digitalWrite(P2, LOW);
            break;
          case 4:
            exp1.digitalWrite(P3, LOW);
            break;
        }
      }else if(ST_2==0 && arrData[i].nomor_laci==2){
        switch(arrData[i].nomor_tempat){
          case 1:
            exp1.digitalWrite(P4, LOW);
            break;
          case 2:
            exp1.digitalWrite(P5, LOW);
            break;
          case 3:
            exp1.digitalWrite(P6, LOW);
            break;
          case 4:
            exp1.digitalWrite(P7, LOW);
            break;
        }
      }
    }
  }
  ctrDelay+=1;
  delay(1000);
}

//RTC Function
void setDS3231time(byte second, byte minute, byte hour, byte dayOfWeek, byte
dayOfMonth, byte month, byte year)
{
  // sets time and date data to DS3231
  Wire.beginTransmission(DS3231_I2C_ADDRESS);
  Wire.write(0); // set next input to start at the seconds register
  Wire.write(decToBcd(second)); // set seconds
  Wire.write(decToBcd(minute)); // set minutes
  Wire.write(decToBcd(hour)); // set hours
  Wire.write(decToBcd(dayOfWeek)); // set day of week (1=Sunday, 7=Saturday)
  Wire.write(decToBcd(dayOfMonth)); // set date (1 to 31)
  Wire.write(decToBcd(month)); // set month
  Wire.write(decToBcd(year)); // set year (0 to 99)
  Wire.endTransmission();
}
void readDS3231time(byte *second, byte *minute, byte *hour, byte *dayOfWeek, byte *dayOfMonth, byte *month, byte *year)
{
  Wire.beginTransmission(DS3231_I2C_ADDRESS);
  Wire.write(0); // set DS3231 register pointer to 00h
  Wire.endTransmission();
  Wire.requestFrom(DS3231_I2C_ADDRESS, 7);
  // request seven bytes of data from DS3231 starting from register 00h
  *second = bcdToDec(Wire.read() & 0x7f);
  *minute = bcdToDec(Wire.read());
  *hour = bcdToDec(Wire.read() & 0x3f);
  *dayOfWeek = bcdToDec(Wire.read());
  *dayOfMonth = bcdToDec(Wire.read());
  *month = bcdToDec(Wire.read());
  *year = bcdToDec(Wire.read());
}
void displayTime()
{
  // retrieve data from DS3231
  readDS3231time(&second, &minute, &hour, &dayOfWeek, &dayOfMonth, &month,&year);
  if(ctrJam==0){
    Serial.println("ctrJam == 0");
    jam_old=hour;
  }else{
    
  }
  jam = hour;
  menit = minute;
  
  lcd.setCursor(7,0);
//  lcd.print("Kotak Obat Pintar ");
  if(jam<10){
    lcd.print("0");
    lcd.print(jam);
  }else{
    lcd.print(jam);
  }
  lcd.print(":");
  if(menit<10){
    lcd.print("0");
    lcd.print(menit);
  }else{
    lcd.print(menit); 
  }
  intTime = (jam*100)+menit;
  Serial.print("Isi intTime: ");
  Serial.println(intTime);
  Serial.println("-==========UPD========-");
  Serial.print("JAM: ");
  Serial.println(jam);
  Serial.print("MENIT: ");
  Serial.println(menit);
  Serial.println("-=====================-");
//  Jika sudah lewat jamnya, maka alarm yang sudah ditrigger tadi, statenya dikembalikan ke 0 lagi
  if(jam_old!=jam){
//    Update si state data yang sudah tertrigger
    Serial.println("-=====================-");
    Serial.print("JAM_OLD: ");
    Serial.println(jam_old);
    Serial.print("JAM_N OW: ");
    Serial.println(jam);
    Serial.println("-=====================-");
    jam_old=jam;
    Serial.println("-==========UPD========-");
    Serial.print("JAM_OLD: ");
    Serial.println(jam_old);
    Serial.print("JAM_N OW: ");
    Serial.println(jam);
    Serial.println("-=====================-");
    lcd.setCursor(1,3);
    lcd.print(" Update state obat");
    Serial.println(" Update state obat");
    Serial.println("-=====================-");
    for(int i=0; i<jsondoc.size(); i++){
      if(arrData[i].done==1){
        Serial.println("=================================");
        updateStateObat(arrData[i].id_waktu);
        Serial.print("YANG DIUPDATE: ");
        arrData[i].done=0;
        Serial.println(arrData[i].id_waktu);
        Serial.println("=================================");
      }
    }
    getData();
  
    //Fetch JSON ke dalam data
    DeserializationError err = deserializeJson(jsondoc, rcv);
    if(err){
      Serial.print("isi err: ");
      Serial.println(err.c_str());
      return;
    }
    
    //Buat array of struct
  //  structData arrData[jsondoc.size()];
  
    //Masukkan Json ke struct arrData
    for(int i=0; i<jsondoc.size(); i++){
      String idobat=jsondoc[i]["id_obat"];
      int nomor_laci=jsondoc[i]["nomor_laci"];
      int nomor_tempat=jsondoc[i]["nomor_tempat"];
      int jumlah_obat=jsondoc[i]["jumlah_obat"];
      int id_waktu=jsondoc[i]["id_waktu"];
      String aturankonsum=jsondoc[i]["aturan_makan"];
      int cAturankonsum=aturankonsum.toInt();
      
      waktuobat.aturan_makan=cAturankonsum;
      waktuobat.id_waktu=id_waktu;
      waktuobat.nomor_laci=nomor_laci;
      waktuobat.nomor_tempat=nomor_tempat;
      waktuobat.id_obat=idobat;
      waktuobat.jumlah_obat=jumlah_obat;
      waktuobat.done=jsondoc[i]["done"];
  
      //Pemrosesan waktu
      String jam="";
      String menit="";
      String waktu=jsondoc[i]["waktu"];
      
      jam=waktu[0];
      jam+=waktu[1];
      
      menit=waktu[3];
      menit.concat(waktu[4]);
  
      int cJam=jam.toInt();
      int cMenit=menit.toInt();
      
      int waktutotal=0;
      waktutotal=(cJam*100)+cMenit;
      waktuobat.waktu=waktutotal;
      
      Serial.println("========================================");
      Serial.print("JAM: ");
      Serial.println(cJam);
      Serial.print("MENIT: ");
      Serial.println(cMenit);
      Serial.println("========================================");
  
      int total=0;
      if(cAturankonsum==0){
        //Setelah makan
        Serial.println("SESUDAH MAKAN");
        if(cMenit+30>=60){
          cJam=(cJam+1)%24;
          cMenit=(cMenit+30)-60;
        }else{
          cMenit=cMenit+30;
          cJam=cJam;
        }
      }else{
        //Sebelum makan
        Serial.println("SEBELUM MAKAN");
        if(cMenit-30<0){
          Serial.print("cMenit before count: ");
          Serial.println(cMenit);
          cMenit=(60+cMenit)-30;
          Serial.print("cMenit after count: ");
          Serial.println(cMenit);
          if(cJam-1<0){
            cJam=23;
          }else{
            cJam=cJam-1;
          }
        }else{
          cJam=cJam;
          cMenit=cMenit-10;
        }
      }
      cJam=cJam*100;
      total=cJam+cMenit;
      waktuobat.waktu_hitung=total;
      arrData[i]=waktuobat;
    }
    lcd.clear();
  }
  lcd.setCursor(4,1);
  lcd.print("           ");
  lcd.setCursor(4,2);
  lcd.print("            ");
  ctrJam++;
}

//----------------------------------------------- Fuctions used for WiFi credentials saving and connecting to it which you do not need to change 
bool testWifi(void)
{
  int c = 0;
  Serial.println("Waiting for Wifi to connect");
  while ( c < 20 ) {
    if (WiFi.status() == WL_CONNECTED)
    {
      return true;
    }
    delay(500);
    Serial.print("*");
    c++;
  }
  Serial.println("");
  Serial.println("Connect timed out, opening AP");
  return false;
}

void launchWeb()
{
  Serial.println("");
  if (WiFi.status() == WL_CONNECTED)
    Serial.println("WiFi connected");
  Serial.print("Local IP: ");
  Serial.println(WiFi.localIP());
  Serial.print("SoftAP IP: ");
  Serial.println(WiFi.softAPIP());
  createWebServer();
  // Start the server
  server.begin();
  Serial.println("Server started");
}

void setupAP(void)
{
  WiFi.mode(WIFI_STA);
  WiFi.disconnect();
  delay(100);
  int n = WiFi.scanNetworks();
  Serial.println("scan done");
  if (n == 0)
    Serial.println("no networks found");
  else
  {
    Serial.print(n);
    Serial.println(" networks found");
    for (int i = 0; i < n; ++i)
    {
      // Print SSID and RSSI for each network found
      Serial.print(i + 1);
      Serial.print(": ");
      Serial.print(WiFi.SSID(i));
      Serial.print(" (");
      Serial.print(WiFi.RSSI(i));
      Serial.print(")");
      Serial.println((WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*");
      delay(10);
    }
  }
  Serial.println("");
  st = "<ol>";
  for (int i = 0; i < n; ++i)
  {
    // Print SSID and RSSI for each network found
    st += "<li>";
    st += WiFi.SSID(i);
    st += " (";
    st += WiFi.RSSI(i);

    st += ")";
    st += (WiFi.encryptionType(i) == ENC_TYPE_NONE) ? " " : "*";
    st += "</li>";
  }
  st += "</ol>";
  delay(100);
  WiFi.softAP("KotakObat", "");
  Serial.println("softap");
  launchWeb();
  Serial.println("over");
}

void createWebServer()
{
 {
    server.on("/", []() {

      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);
      content = "<!DOCTYPE HTML>\r\n<html>Hello from ESP8266 at ";
      content += "<form action=\"/scan\" method=\"POST\"><input type=\"submit\" value=\"scan\"></form>";
      content += ipStr;
      content += "<p>";
      content += st;
      content += "</p><form method='get' action='setting'><label>SSID: </label><input name='ssid' length=32></br><label>PASSWORD: </label><input name='pass' length=64><input type='submit'></form>";
      content += "</html>";
      server.send(200, "text/html", content);
    });
    server.on("/scan", []() {
      //setupAP();
      IPAddress ip = WiFi.softAPIP();
      String ipStr = String(ip[0]) + '.' + String(ip[1]) + '.' + String(ip[2]) + '.' + String(ip[3]);

      content = "<!DOCTYPE HTML>\r\n<html>go back";
      server.send(200, "text/html", content);
    });

    server.on("/setting", []() {
      String qsid = server.arg("ssid");
      String qpass = server.arg("pass");
      if (qsid.length() > 0 && qpass.length() > 0) {
        Serial.println("clearing eeprom");
        for (int i = 0; i < 96; ++i) {
          EEPROM.write(i, 0);
        }
        Serial.println(qsid);
        Serial.println("");
        Serial.println(qpass);
        Serial.println("");

        Serial.println("writing eeprom ssid:");
        for (int i = 0; i < qsid.length(); ++i)
        {
          EEPROM.write(i, qsid[i]);
          Serial.print("Wrote: ");
          Serial.println(qsid[i]);
        }
        Serial.println("writing eeprom pass:");
        for (int i = 0; i < qpass.length(); ++i)
        {
          EEPROM.write(32 + i, qpass[i]);
          Serial.print("Wrote: ");
          Serial.println(qpass[i]);
        }
        EEPROM.commit();

        content = "{\"Success\":\"saved to eeprom... reset to boot into new wifi\"}";
        statusCode = 200;
        ESP.reset();
      } else {
        content = "{\"Error\":\"404 not found\"}";
        statusCode = 404;
        Serial.println("Sending 404");
      }
      server.sendHeader("Access-Control-Allow-Origin", "*");
      server.send(statusCode, "application/json", content);

    });
  } 
}
