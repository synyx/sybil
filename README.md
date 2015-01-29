Sybil
=====

synyx Büro - Information & Licht
--------------------------------

A project to illuminate and inform the people in the office(s).
Uses WS2812 LED Strips connected to Tinkerforge bricks.

This project is under heavy development and further documentation is forthcoming.

### Working so far ###

* Saves and loads Tinkerforge bricks and LED Strips bricklets to/from Neo4j database.
* Outputs Statuses on LED Strips *(see integration test org.synyx.sybil.out.SingleStatusOnLEDStripTest)*
* Outputs arbitrary pixels and sprites " " *(see integration test org.synyx.sybil.out.OutputLEDStripTest)*

To run, run an integration test, e.g.:

gradlew integTest --tests org.synyx.sybil.out.OutputLEDStripTest

### Structure ###
    src/
    +-integTest/                        Integration tests.
    +-test/                             Unit tests.
    +-main/                             Main.
      +-java/                           Code.
      | +-org/synyx/sybil/              Base package.
      |   +-common/                     Common modules.
      |   | +-Bricklet                  Interface all Bricklets inherit from. 
      |   | +-BrickletRegistry          Interface all registries for bricklets inherit from.
      |   | +-BrickRegistry             Registers Tinkerforge bricks & their connections.
      |   +-config/                     Configuration files.
      |   | +-Neo4jConfig               Database configuration.
      |   | +-SpringConfig              Spring configuration.
      |   +-database/                   Database interfaces.
      |   | +-BrickRepository           Interface for Tinkerforge Bricks.
      |   | +-OutputLEDStripRepository  Interface for LED Strips.
      |   +-domain/                     Domain classes.
      |   | +-BrickDomain               Domain for Tinkerforge Bricks.
      |   | +-BrickletDomain            Interface all Bricklet domains inherit from.
      |   | +-OutputLEDStripDomain      Domain for LED Strips, inherits from BrickletDomain.
      |   +-in/                         Inputs.
      |   | +-Status                    Enum for statuses (OKAY, WARNING & CRITICAL)
      |   | +-StatusInformation         Status with additional information.       
      |   +-out/                        Outputs.
      |     +-Color                     Color object, for LEDs.
      |     +-OutputLEDStrip            LED Strip object, communicates with LEDs.
      |     +-OutputLEDStripRegistry    Provides OutputLEDStrip objects.
      |     +-SingleStatusOnLEDStrip    Display a single statusInformation on a LED Strip.
      |     +-SingleStatusOutput        Interface for displaying a single status.
      |     +-Sprite1D                  Sprite object, for LED Strips.
      +-resources/                      Resources.
        +-logback.xml                   Configures the logback logging engine.

### How to use ###

The Spring configuration in **SpringConfig** loads:

* All the **＊Registry** classes, since they're annotated with @Service
* The database configuration from **Neo4jConfig**
* Which in turn loads the **＊Repository** and **＊Domain** classes.

Now you can create new **BrickDomain**s with a host and optionally a port.
Save those to the **BrickRepository** with it's *save* method.  
Create new **OutputLEDStripDomain**s with a name to identify the Output, a Bricklet's UID, the number of LEDs on the 
Strip and the **BrickDomain** you created earlier.
Save these to the **OutputLEDStripRepository** with it's *save* method.  
Now you are ready to get an **OutputLEDStrip** from the **OutputLEDStripRegistry** with it's *get* method, to
which you pass the **OutputLEDStripDomain**, which you either have left over from creating them for the database, or you
fetch them fresh from the database with **OutputLEDStripRepository**'s *findByName* method. 

The support classes **Color** and **Sprite1D** can be instantiated without any dependencies.  
A **Color** is created by passing it an integer between 0 and 127 for red, green, and blue each. It can be passed to an
**OutputLEDStrip**'s or a **Sprite1D**'s methods.  
A **Sprite1D** is simply an array of **Color**'s, it is created by passing it it's length and optionally a name.
Then it can be drawn on with it's *setFill* and *setPixel* methods, both of which accept a **Color**. 

Now you can call the **OutputLEDStrip**'s methods, the most important of which are:

* drawSprite - accepts a **Sprite1D**, it's position on the LED Strip and an optional boolean, deciding whether it
should wrap around at the end of the LED Strip or not.
* setBrightness - accepts a number of type double, between 0.0 - off - and 2.0 - full brightness.
* setFill - sets the given **Color** for the whole LED Strip
* setPixel -  accepts a number of type int as the position of the pixel on the LED Strip and a **Color**
* updateDisplay - this must be called for any of the changes made with above methods to show on the LED Strip.

Additionally, you can create a new **SingleStatusOnLEDStrip** by passing it an **OutputLEDStrip**, and then call it's
*showStatus* method with a **StatusInformation** object, which is instantiated with a source String, a **Status** and
optionally a priority.