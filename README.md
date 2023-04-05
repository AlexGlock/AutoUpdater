      
                   _        _    _           _       _       
        /\        | |      | |  | |         | |     | |      
       /  \  _   _| |_ ___ | |  | |_ __   __| | __ _| |_ ___ 
      / /\ \| | | | __/ _ \| |  | | '_ \ / _` |/ _` | __/ _ \
     / ____ \ |_| | || (_) | |__| | |_) | (_| | (_| | ||  __/
    /_/    \_\__,_|\__\___/ \____/| .__/ \__,_|\__,_|\__\___|
                                  | |                        
                                  |_|                        

## Netburner AutoUpdate for ARM based platforms ##
___

This project contains a gradle development project for a running .jar application of the Netburner-AutoUpdate Tool for ARM based systems.
The project is based on the Java code for the Netburner Autoupdate for OS X (autoupdate-java.zip) from October 2018.
Your development machine will need the default-jdk to run the cradle build, install it via:

    sudo apt update
    sudo apt install default-jdk

The build process can be started by:

    ./gradlew build

Another usefull command, that lists all available gradle properties:

    ./gradlew tasks

