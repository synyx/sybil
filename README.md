Sybil
=====

synyx Büro - Information & Licht
--------------------------------

A project to illuminate and inform the people in the office(s).
Uses WS2812 LED Strips connected to Tinkerforge bricks.

### Working so far ###

* Outputs Statuses on LED Strips via Tinkerforge (see integration test org.synyx.sybil.out.SingleStatusOnLEDStripTest)
* Outputs arbitrary pixels and sprites " " (see integration test org.synyx.sybil.out.OutputLEDStripTest)

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
      |   | +-OutputLEDStripDomain      Domain for LED Strips.
      |   +-in/                         Inputs, fairly self-explanatory.
      |   +-out/                        Outputs.
      |     +-Color                     Color object, for LEDs.
      |     +-OutputLEDStrip            LED Strip object, communicates with LEDs.
      |     +-OutputLEDStripRegistry    Handles OutputLEDStrip objects.
      |     +-SingleStatusOnLEDStrip    Display a single status on a LED Strip.
      |     +-SingleStatusOutput        Interface for displaying a single status.
      |     +-Sprite1D                  Sprite object.
      +-resources/                      Resources.
        +-logback.xml                   Configures the logback logging engine.

The Spring configuratin in o.s.s.config.SpringConfig loads:

* All the ***Registry** classes, since they're annotated with @Service
* The database configuration from **o.s.s.config.Neo4jConfig**
* Which in turn loads the ***Repository** and ***Domain** classes.

Now you can create new **BrickDomain**s with a host and - optionally - a port.
Save those to the **BrickRepository** with it's *save* method.  
Create new **OutputLEDStripDomain**s with a name to identify the Output, a Bricklet's UID, the number of LEDs on the 
Strip and the **BrickDomain** you created earlier.
Save these to the **OutputLEDStripRepository** with it's *save* method.  
Now you are ready to get an **OutputLEDStrip** from the **OutputLEDStripRegistry** with it's *get* method, to
which you pass the **OutputLEDStripDomain**, which you either have left over from creating them for the database, or you
fetch them fresh from the database with **OutputLEDStripRepository**'s *findByName* method. 

Look at **o.s.s.out.OutputLEDStrip** to see what you can do with it.
