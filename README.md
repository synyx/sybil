Sybil
=====

A project to illuminate and inform the people in the office(s).
Uses WS2812 LED strips and AZ943-1CH-5DE relays connected to Tinkerforge bricks.

This project is under heavy development and further documentation is forthcoming.


## Table of contents

- [Working so far](#working-so-far)
- [Execution](#execution)
    - [Configuration](#configuration)
    - [Running](#running)
    - [Deployment](#deployment)
        - [Standalone](#standalone)
        - [With an application server](#with-an-application-server)
    - [Debugging](#debugging)
    - [Integration Tests](#integration-tests)
- [Structure](#structure)
- [How to use](#how-to-use)
    - [Extending Sybil](#extending-sybil)

## Working so far

* Saves and loads Tinkerforge bricks, LED Strip, Dual Relay, Ambient Light, and Motion
  Detector bricklets to/from Neo4j database.
* Reads configuration from JSON files.
* Outputs statuses on LED strips
    * Outputs statuses of Jenkins build jobs, updated every 60 seconds.
* Outputs arbitrary pixels and sprites on LED strips
    * Does this via a HTTP API.
* Controls relays
    * Does this via a HTTP API.
* Listens to Illuminance sensors and has them increase the LED strip brightness if illuminance falls under a threshold
  value.
* Listens to IO-Modules to which buttons can be connected and turns off relays when buttons are pressed. 
* Serves a *very barebones* REST API showing the configured the bricks and bricklets. 

## Execution

### Configuration

To run or deploy the server you need `bricks.json`, `ledstrips.json`, `relays.json`, `sensors.json`, and `jenkins.json`
in `/home/sybil-config/`, and `jenkinsservers.json` in `/home/sybil/`.  
See in `src/test/resources/config/` for example files, or `docs/configfiles` for simplified schemata.  
The locations of these configuration files can be configured in `src/main/resources/config.properties`.  

Note that all the names used in the configuration files *must* be lowercase.

### Running

To run the server:

`gradlew appRun`

### Deployment

#### Standalone

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

#### With an application server

Run 

`gradlew build`

and copy the war file from `build/libs/` to an application server of your choice.

### Debugging

First run

`gradlew appStartDebug`

Now connect your debugger to port 5005.

e.g. in IntelliJ IDEA create a Run Configuration of type `Remote`, leave all the defaults.
Now you can run this Configuration with the Debug command.

When you are done, run

`gradlew appStop`

### Integration Tests

Integration tests require [TFStubserver](https://github.com/PlayWithIt/TFStubserver), [node.js](https://nodejs.org/)
and (the \*nix commands) `lsof` and `kill`.  
Run them with `gradlew integTest`.

If any of the tests fail with a `NullPointerException`, try running the tests again, it should eventually work.  
This is a known bug.

Integration tests are probably \*nix specific right now.  
TODO: Make integration tests cross-platform.

## Structure

```
docs/                               Documentation sources.
| +-api/                            Source for the (unused) API documentation.
| +-configfiles/                    Simplified schemas for the config files.
| +-staticwebsite/                  Sources of the sybil website.
src/                                Source code.
  +-test/                           Unit & Integration tests.
  +-main/                           Main.
    +-java/org/synyx/sybil/         Java code base package.
    | +-AppInitializer              Found by Servlet 3.0 container, starts app.
    | |
    | +-api/                        API-controller & helper for non-specific subject.
    | | +-ConfigurationController   MVC REST Controller for /configuration/ root.
    | | +-HealthController          MVC Controller for setting & showing app health.
    | | +-PatchResource             Object for de-serialising HTTP PATCH actions. 
    | | +-RootController            MVC Root (/) controller.
    | | +-SinglePatchResource       Object for de-serialising a single PATCH action.
    | |
    | +-brick/                      Brick-specific classes.
    | | +-api/                      API-controller & helper for bricks.
    | | | +-BrickResource           Spring HATEOAS wrapper around brick configuration.
    | | | +-Config…BricksController MVC Rest Controller for brick configuration.
    | | |
    | | +-database/                 Database classes for bricks
    | | | +-BrickDomain             Domain for Tinkerforge brick configurations.
    | | | +-BrickRepository         DB-Interface for Tinkerforge brick configs.
    | | |
    | | +-BrickConnectionListener   Listener for Tinkerforge IP Connection callbacks.
    | | +-BrickRegistry             Registers Tinkerforge bricks & their connections.
    | |
    | +-bricklet/                   Bricklet-specific classes.
    | | +-input/                    Bricklets that input data.
    | | | |
    | | | +-button/                 Button-inputs (connected to IO4-bricklets).
    | | | | +-api/                  API-controller & helper for buttons.
    | | | | | +-ButtonResource      Spring HATEOAS wrapper around button configuration.
    | | | | | +-Co…ButtonController MVC controller for reading button configurations.
    | | | | |
    | | | | +-database/             Database classes & intefaces for buttons.
    | | | | | +-ButtonDomain        Domain for button configurations.
    | | | | | +-ButtonRepository    DB-Interface for button configurations.
    | | | | |
    | | | | +-ButtonListener        Listener for Button interrupts.
    | | | | +-ButtonSensorRegistry  Registers Buttons, so they can listen f. interrupts.
    | | | |
    | | | +-illuminance/            Illuminance sensors (Ambient Brightness bricklets).
    | | | | +-api/                  API-controller & helper for illumination sensors.
    | | | | | +-Co…IlluminanceCont… MVC Controller for reading ill. sensor configuration.
    | | | | | +-IlluminanceResource Spring HATEOAS wrapper around ill. sensor config.
    | | | | |
    | | | | +-database/             Database classes & interfaces for ill. sensors.
    | | | | | +-Illumi…SensorDomain Domain for illuminance sensor configurations.
    | | | | | +-Il…SensorRepository DB-Interface for illuminance sensor configs.
    | | | | |
    | | | | +-IlluminanceListener   Listener for illuminance sensors.
    | | | | +-Illumi…SensorRegistry Registers illuminance sensors.
    | | | |
    | | | +-SensorType              Enum for sensor types (LUMINANCE, MOTION, BUTTON).
    | | |
    | | +-output/                   Bricklets that output data.
    | | | |
    | | | +-ledstrip/               LED strip bricklets.
    | | | | +-api/                  API-controller & helpers for LED strips.
    | | | | | +-Config…LEDStripCon… MVC Controller for interacting w/ LED strips.
    | | | | | +-DisplayResource     Spring HATEOAS wrapper for LED strip direct access.
    | | | | | +-LEDStripResource    Spring HATEOAS wrapper around LED strip config.
    | | | | |
    | | | | +-database/             Database classes & interfaces for LED strips.
    | | | | | +-LEDStripDomain      Domain for LED strips.
    | | | | | +-LEDStripRepository  DB-Interface for LED strips.
    | | | | +-Color                 Color object, for LEDs.
    | | | | +-LEDStrip              LED strip object, communicates with LEDs.
    | | | | +-LEDStripRegistry      Provides LEDStrip objects.
    | | | | +-SingleStatusOnL…Strip Display a single statusInformation on a LED strip.
    | | | | +-SingleStatus…Registry Provides SingleStatusOnLEDStrip objects.
    | | | | +-SingleStatusOutput    Interface for displaying a single status.
    | | | | +-Sprite1D              Sprite object, for LED strips.
    | | | |
    | | | +-relay/                  Relay bricklets (technically Dual Relay bricklets).
    | | |   +api/                   API-controller & helper for relays.
    | | |   | +-Con…RelayController MVC controller for interacting w/ relays.
    | | |   | +-RelayResource       Spring HATEOAS wrapper around Relay configuration.
    | | |   |
    | | |   +database/              Database classes & interfaces for relays.
    | | |   | +-RelayDomain         Domain for relays.
    | | |   | +-RelayRepository     DB-Interface for relays.
    | | |   |
    | | |   +-EnumRelay             Relay helper Enum.
    | | |   +-Relay                 Relay object, communicates with Relay bricklets.
    | | |   +-RelayRegistry         Provides OutputRelay objects.
    | | |
    | | +-Bricklet                  Interface all Bricklets inherit from. 
    | | +-BrickletRegistry          Interface all registries for bricklets inherit from.
    | |
    | +-config/                     Configuration classes.
    | | +-ConfigLoader              Loads configuration from JSON files.
    | | +-Neo4jConfig               Database configuration.
    | | +-SpringConfig              Spring configuration.
    | | +-StartupLoader             Pulls up ConfigLoader at startup.
    | | +-WebConfig                 Configures the web app.
    | |
    | +-jenkins/                    Jenkins-specific classes.
    |   |
    |   +-config/                   Jenkins configuration classes.
    |   | +-JenkinsConfig           Saves the configured Jenkins servers & jobs.
    |   |
    |   +-domain/                   Jenkins-specific domains.
    |   | +-JenkinsJob              Object for a single returned Jenkins job.
    |   | +-JenkinsProperties       Object for Jenkins jobs returned from Jenkins API.
    |   | +-Status                  Enum for statuses (OKAY, WARNING & CRITICAL)
    |   | +-StatusInformation       Status with additional information.
    |   |
    |   +-JenkinsService            Polls Jenkins servers and feeds statuses to LEDs.
    |
    +-resources/                    Resources.
      +-logback.xml                 Configures the logback logging engine.
      +-config.properties           Contains the path to the config files.
```  
        
## How to use

The Servlet Container loads the configurations from **AppInitializer**, since it extends a ServletInitializer.  
This then loads:

* The **WebConfig** class.
    * Which in turn loads all the __*Controller__ classes, since they're annotated with @RestController
* The Spring configuration in **SpringConfig**, which loads:
    * All the __*Registry__ classes, since they're annotated with @Service.
    * The database configuration from **Neo4jConfig**.
        * Which in turn loads the __*Repository__ and __*Domain__ classes.
    * The **StartupLoader** class, since it's annotated with @Component.
        * Which in turn runs the **ConfigLoader**'s *loadConfig* method.
    * The **JenkinsService** since it's annotated with @Service.

The *loadConfig* method loads the brick, LED strip, relay, and sensor configurations from JSON files (the location of
which is defined in the **config.properties** file) and saves them to the database. It then loads the configured Jenkins
jobs from the Jenkins JSON configuration file. It also activates the __*Listener__ classes for the sensors, enabling
them to react to changes.

The **JenkinsService** has a *handleJobs* method, which is annotated with @Scheduled which means it is run every 60
seconds. This method gets a list of all jobs from the Jenkins server(s), compares it to the list loaded from
`jenkins.json` and then instructs the associated LED strips to show the jobs' statuses. 

Bricks saved in the database can be listed via the REST API at `/configuration/bricks` and
`/configuration/bricks/{name}` respectively. Same goes for LED strips at `/configuration/ledstrips` and 
`/configuration/ledstrips/{name}`, relays at `/configuration/relays` and `/configuration/relays/{name}`, buttons at
`/configuration/buttons/` and `/configuration/buttons/{name}`, and illuminance sensors at
`/configuration/illuminancesensors/` and `/configuration/illuminancesensors/{name}`.

A direct API for reading the LED strips' state and for writing to it (i.e. displaying things on it) is provided at
`/configuration/ledstrips/{name}/display/`.  
A direct API for switching relays is provided at `/configuration/relays/{name}`.
 
For further information on this see [the wiki](https://github.com/synyx/sybil-wiki).

### Extending Sybil

If you want to extend Sybil's functionality, here's how you operate it "manually":

You can create new **BrickDomain**s with a host and optionally a port.
Save those to the **BrickRepository** with it's *save* method.  
Create new **LEDStripDomain**s with a name to identify the Output, a Bricklet's UID, the number of LEDs on the 
strip and the **BrickDomain** you created earlier.
Save these to the **LEDStripRepository** with it's *save* method.  
Now you are ready to get a **LEDStrip** from the **LEDStripRegistry** with it's *get* method, to which you pass the
**LEDStripDomain**, which you either have left over from creating them for the database, or you fetch them fresh from
the database with **LEDStripRepository**'s *findByName* method. 

The support classes **Color** and **Sprite1D** can be instantiated without any dependencies.  
A **Color** is created by passing it an integer between 0 and 255 for red, green, and blue each. It can be passed to a
**LEDStrip**'s or a **Sprite1D**'s methods.  
A **Sprite1D** is simply an array of **Color**'s, it is created by passing it it's length and optionally a name.
Then it can be drawn on with it's *setFill* and *setPixel* methods, both of which accept a **Color**. 

Now you can call the **LEDStrip**'s methods, the most important of which are:

* drawSprite - accepts a **Sprite1D**, it's position on the LED strip and an optional boolean, deciding whether it
should wrap around at the end of the LED strip or not.
* setBrightness - accepts a number of type double, between 0.0 - off - and 255.0 - maximum brightness.
* setFill - sets the given **Color** for the whole LED strip.
* setPixel -  accepts a number of type int as the position of the pixel on the LED strip and a **Color**.
* updateDisplay - this must be called for any of the changes made with above methods to show on the LED strip.

Additionally, you can create a new **SingleStatusOnLEDStrip** by passing it a **LEDStrip**, and then call it's
*showStatus* method with a **StatusInformation** object, which is instantiated with a source String, a **Status** and
optionally a priority.
