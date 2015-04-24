Sybil
=====

A project to illuminate and inform the people in the office(s).
Uses WS2812 LED Strips connected to Tinkerforge bricks.

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

* Saves and loads Tinkerforge bricks, LED Strips bricklets and Dual Relay bricklets to/from Neo4j database.
* Reads configuration from JSON files.
* Outputs Statuses on LED Strips *(see integration test org.synyx.sybil.out.SingleStatusOnLEDStripTest)*
    * Outputs Statuses of Jenkins build jobs, updated every 60 seconds.
* Outputs arbitrary pixels and sprites on LED Strips *(see integration test org.synyx.sybil.out.OutputLEDStripTest)*
    * Does this via a HTTP API.
* Controls relays
    * Does this via a HTTP API.
* Serves a *very barebones* REST API showing the configured the bricks and bricklets. 

## Execution

### Configuration

To run or deploy the server you need `bricks.json`, `ledstrips.json`, `relays.json` and `jenkins.json` in
`/home/sybil-config/`, and `jenkinsservers.json` in `/home/sybil/`.  
See in `config/` for example files, or `src/docs/configfiles` for simplified schemata.  
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
    src/
    +-docs/                             Documentation sources.
    | +-api/                            Source for the API documentation.
    | +-configfiles/                    Simplified schemas for the config files.
    | +-staticwebsite/                  Sources of the sybil website.
    +-integTest/                        Integration tests.
    +-test/                             Unit tests.
    +-main/                             Main.
      +-java/org/synyx/sybil/           Java code base package.
      | +-api/                          Controllers for the REST API.
      | | +-resources/                  API Resources, wrappers around other objects.
      | | | +-BrickResource             Spring HATEOAS wrapper around Brick configuration.
      | | | +-DisplayResource           Spring HATEOAS wrapper for LED Strip direct access.
      | | | +-LEDStripResource          Spring HATEOAS wrapper around LED Strip config.
      | | | +-PatchResource             Object for deserialising HTTP PATCH actions. 
      | | | +-RelayResource             Spring HATEOAS wrapper around Relay configuration.
      | | | +-SinglePatchResource       Object for deserialising a single PATCH action.
      | | +-Config…BricksController     MVC Rest Controller for Brick configuration.
      | | +-ConfigurationController     MVC Rest Controller for /configuration/ root.
      | | +-Config…LEDStripController   MVC Rest Controller for LED Strip configuration.
      | | +-Config…RelayController      MVC Rest Controller for Relay configuration.
      | | +-HealthController            MVC Controller for setting & showing app health.
      | | +-RootController              MVC Root controller.
      | +-common/                       Common modules.
      | | +-jenkins/                    JenkinsService-specific modules
      | | | +-JenkinsConfig             Saves the configured Jenkins servers & jobs.
      | | | +-JenkinsJob                Object for a single returned Jenkins job.
      | | | +-JenkinsProperties         Object for Jenkins jobs returned from Jenkins API.
      | | +-Bricklet                    Interface all Bricklets inherit from. 
      | | +-BrickletRegistry            Interface all registries for bricklets inherit from.
      | | +-BrickRegistry               Registers Tinkerforge bricks & their connections.
      | | +-Listener                    Listeners for Tinkerforge callbacks.
      | +-config/                       Configuration files.
      | | +-ConfigLoader                Loads configuration from JSON files.
      | | +-Neo4jConfig                 Database configuration.
      | | +-SpringConfig                Spring configuration.
      | | +-StartupLoader               Pulls up ConfigLoader at startup.
      | +-database/                     Database interfaces.
      | | +-BrickRepository             DB-Interface for Tinkerforge Bricks.
      | | +-OutputLEDStripRepository    DB-Interface for LED Strips 
      | | +-OutputRelayRepository       DB-Interface for Relays.
      | +-domain/                       Domain classes.
      | | +-BrickDomain                 Domain for Tinkerforge Bricks.
      | | +-OutputLEDStripDomain        Domain for LED Strips, inherits from BrickletDomain.
      | | +-OutputRelayDomain           Domain for Relays, inherits from BrickletDomain.
      | +-in/                           Inputs.
      | | +-JenkinsService              Pulls Jenkins servers and feeds statuses to LEDs.
      | | +-Status                      Enum for statuses (OKAY, WARNING & CRITICAL)
      | | +-StatusInformation           Status with additional information.       
      | +-out/                          Outputs.
      | | +-Color                       Color object, for LEDs.
      | | +-EnumRelay                   Relay helper Enum.
      | | +-OutputLEDStrip              LED Strip object, communicates with LEDs.
      | | +-OutputLEDStripRegistry      Provides OutputLEDStrip objects.
      | | +-OutputRelay                 Relay object, communicates with Relay bricklets.
      | | +-OutputRelayRegistry         Provides OutputRelay objects.
      | | +-SingleStatusOnLEDStrip      Display a single statusInformation on a LED Strip.
      | | +-SingleStatusOn…Registry     Provides SingleStatusOnLEDStrip objects.
      | | +-SingleStatusOutput          Interface for displaying a single status.
      | | +-Sprite1D                    Sprite object, for LED Strips.
      | +-webconfig/                    Web app configuration.
      |   +-ApiWebAppInitializer        Initializes the API web app.
      |   +-WebConfig                   Configures the web app.
      +-resources/                      Resources.
        +-logback.xml                   Configures the logback logging engine.
        +-config.properties             Contains the path to the config files.

## How to use

The Servlet Container loads the configuration from **ApiWebAppInitializer**, since it extends a ServletInitializer.
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

The *loadConfig* method loads the brick, LED Strip, & relay configurations from JSON files (the location of which is
defined in the **config.properties** file) and saves them to the database. It then loads the configured Jenkins jobs
from the Jenkins JSON configuration file.

The **JenkinsService** has a *handleJobs* method, which is annotated with @Scheduled which means it is run every 60
seconds. This method gets a list of all jobs from the Jenkins server(s), compares it to the list loaded from
jenkins.json and then instructs the associated LED strips to show the jobs' statuses. 

Bricks saved in the database can be listed via the REST API at `/configuration/bricks` and
`/configuration/bricks/{name}` respectively. Same goes for LED Strips at `/configuration/ledstrips` and 
`/configuration/ledstrips/{name}`, and relays at `/configuration/relays` and `/configuration/relays/{name}`.

A direct API for reading the LED strips state and for writing to it (i.e. displaying things on it) is provided at
`/configuration/ledstrips/{name}/display/`.  
A direct API for switching relays is provided at `/configuration/relays/{name}`.
 
For further information on this see [the wiki](https://github.com/synyx/sybil-wiki).

### Extending Sybil

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
