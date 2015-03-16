Sybil
=====

A project to illuminate and inform the people in the office(s).
Uses WS2812 LED Strips connected to Tinkerforge bricks.

This project is under heavy development and further documentation is forthcoming.

### Working so far ###

* Saves and loads Tinkerforge bricks and LED Strips bricklets to/from Neo4j database.
* Reads configuration from JSON files.
* Outputs Statuses on LED Strips *(see integration test org.synyx.sybil.out.SingleStatusOnLEDStripTest)*
* Outputs arbitrary pixels and sprites " " *(see integration test org.synyx.sybil.out.OutputLEDStripTest)*
* Serves a *very barebones* REST API showing the configured the bricks and bricklets. 

### Execution ###

#### Configuration ####

To run or deploy the server you need `bricks.json`, `ledstrips.json`, and `jenkins.json` in the `config/` directory.  
Note that all the names used in the configuration files *must* be lowercase.

#### Running ####

To run the server:

`gradlew appRun`

#### Deployment ####

Run

`gradlew buildProduct`

and it will produce a self-contained web-app with all the required dependencies in `build/output/sybil`, that can run
on any computer that has Java 8 installed.

Copy this directory to wherever you want to deploy Sybil and run

`start.sh` or `start.bat`.

Run

`restart.sh` or `restart.bat`

to restart the server or

`stop.sh` or `stop.bat`

to stop the server.


#### Debugging ####

First run

`gradlew appStartDebug`

Now connect your debugger to port 5005.

e.g. in IntelliJ IDEA create a Run Configuration of type `Remote`, leave all the defaults.
Now you can run this Configuration with the Debug command.

When you are done, run

`gradlew appStop`

#### Colored Lights ####

To see colored lights on the connected & configured LED Strips, run an integration test:

`gradlew integTest`

*(Might not currently work as expected!  
**TODO: Fix Integration tests**)*


### Structure ###
    src/
    +-docs/                             Documentation sources.
    | +-api/                            Source for the API documentation.
    |
    +-integTest/                        Integration tests.
    +-test/                             Unit tests.
    +-main/                             Main.
      +-java/                           Code.
      | +-org/synyx/sybil/              Base package.
      |   +-api/                        Controllers for the REST API.
      |   | +-resources/                API Resources, wrappers around other objects.
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
        +-config.properties             Contains the path to the config files.

### How to use ###

The Servlet Container loads the configuration from **ApiWebAppInitializer**, since it extends a ServletInitializer.
This then loads:

* The **WebConfig** class.
    * Which in turn loads all the __*Controller__ classes, since they're annotated with @RestController
* The Spring configuration in **SpringConfig**, which loads:
    * All the __*Registry__ classes, since they're annotated with @Service.
    * The database configuration from **Neo4jConfig**.
        * Which in turn loads the __*Repository__ and __*Domain__ classes.
    * The **StartupLoader** class, since it's annotated with @Component.
        * Which in turn runs the **JSONConfigLoader**'s *loadConfig* method.
    * The **JenkinsService** since it's annotated with @Service.

The *loadConfig* method loads the bricks & LED Strip configurations from JSON files (the location of which is defined in
the **config.properties** file) and saves them to the database. It then loads the configured Jenkins jobs from the
Jenkins JSON configuration file.

The **JenkinsService** has a *handleJobs* method, which is annotated with @Scheduled which means it is run every 60
seconds. This method gets a list of all jobs from the Jenkins server(s), compares it to the list loaded from
jenkins.json and then instructs the associated LED strips to show the jobs' statuses. 

Bricks saved in the database can be listed via the REST API at /configuration/bricks and
/configuration/bricks/{hostname} respectively. Same goes for LED Strips at /configuration/ledstrips and 
/configuration/ledstrips/{name}.

#### Extending Sybil ####

If you want to extend Sybil's functionality, here's how you operate it "manually":

You can create new **BrickDomain**s with a host and optionally a port.
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
* setFill - sets the given **Color** for the whole LED Strip.
* setPixel -  accepts a number of type int as the position of the pixel on the LED Strip and a **Color**.
* updateDisplay - this must be called for any of the changes made with above methods to show on the LED Strip.

Additionally, you can create a new **SingleStatusOnLEDStrip** by passing it an **OutputLEDStrip**, and then call it's
*showStatus* method with a **StatusInformation** object, which is instantiated with a source String, a **Status** and
optionally a priority.