// This #include statement was automatically added by the Particle IDE.
#include <InternetButton.h>

InternetButton button = InternetButton();


void setup() {
    button.begin();
    Particle.function("stime",startTime);
    Particle.function("mtime",middleTime);
    Particle.function("etime",endTime);
}

void loop() {

}
int startTime(String cmd)
{
    button.allLedsOff();
    for(int i = 1; i <= 11; i++)
    {
        button.ledOn(i, 255, 255, 255);
    }
}

int middleTime(String cmd)
{
    int count = cmd.toInt();
    button.ledOff(1 + count);
    button.ledOff(11 - count);
}

int endTime(String cmd)
{
    button.allLedsOn(255, 0, 0);
}
