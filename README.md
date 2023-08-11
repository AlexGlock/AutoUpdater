      
                   _        _    _           _       _       
        /\        | |      | |  | |         | |     | |      
       /  \  _   _| |_ ___ | |  | |_ __   __| | __ _| |_ ___ 
      / /\ \| | | | __/ _ \| |  | | '_ \ / _` |/ _` | __/ _ \
     / ____ \ |_| | || (_) | |__| | |_) | (_| | (_| | ||  __/
    /_/    \_\__,_|\__\___/ \____/| .__/ \__,_|\__,_|\__\___|
                                  | |                        
                                  |_|                        

## Netburner AutoUpdate for ARM based platforms ##


This project contains a gradle development project for a running .jar application of the Netburner-AutoUpdate Tool for ARM based systems.
The project is based on the Java code for the Netburner Autoupdate for OS X (autoupdate-java.zip) from October 2018.
Your development machine will need the default-jdk to run the cradle build, install it via:

    sudo apt update
    sudo apt upgrade
    sudo apt-get install default-jre

Project depencies setup:

    sudo apt-get install make libtool pkg-config autoconf automake texinfo git libusb-1.0 libusb-0.1 libusb-dev
    sudo apt-get install cmake subversion libftdi1 libftdi1-dev
    
Clone the repository:

    git clone https://github.com/AlexGlock/AutoUpdater.git
    cd AutoUpdater

The build process can be started by:

    ./gradlew build

Your Java Application will be build in AutoUpdater/Autoupdate/build/libs
Another usefull command, that lists all available gradle properties:

    ./gradlew tasks

Run your AutoUpdate.jar build in the terminal with:

    java -jar AutoUpdate.jar -C

----

# Setup on a RaspberryPi with external Adapter

This project can be used to run AutoUpdate on a RaspberryPi over an external USB-ETH connector.
However, you will need a complicated dhcp-Server setup to get AutoUpdate to run over this connector.
Iam going to add a full tutorial at some point ...







