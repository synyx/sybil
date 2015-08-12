Sybil
=====
A project to illuminate and inform the people in the office(s).
Uses WS2812 LED strips connected to Tinkerforge bricks.

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
- [Structure](#structure)
- [How to use](#how-to-use)
    - [Extending Sybil](#extending-sybil)

## Working so far

* Reads configuration from JSON files.
* Outputs statuses on LED strips.
    * Outputs statuses of Jenkins build jobs, updated every 60 seconds.
    * Adjusts brightness of LED strips based on ambient illuminance.
* Outputs arbitrary pixels and sprites on LED strips.
    * Does this via a HTTP API.

## Execution

### Configuration

To run or deploy the server you need `bricks.json`, `ledstrips.json`, `illuminances.json` and `jenkins.json` in
`/home/sybil-config/`, and `jenkinsservers.json` in `/home/sybil/`.  
See `docs/configfiles` for examples and simplified schemata.  
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

## Structure

```
docs/                               Documentation sources.
| +-configfiles/                    Examples and simple schemata for the config files.
| +-staticwebsite/                  Sources of the sybil website.
|
src/                                Source code.
  +-test/                           Unit tests.
  +-main/                           Main.
    +-java/org/synyx/sybil/         Java code base package.
    | +-AppInitializer              Found by Servlet 3.0 container, starts app.
    | +-LoadFailedException         Exception for when loading a file fails.
    | |
    | +-brick/                      Brick-specific classes.
    | | +-persistence/              Data persistence for bricks.
    | | | +-Brick                   Configuration data for Tinkerforge bricks.
    | | | +-BrickRepository         Repository for brick configurations. 
    | | |
    | | +-service/
    | |   +-BrickConnectionExcepti… Exception for connection errors.
    | |   +-BrickNotFoundException  Exception for non-existent bricks.
    | |   +-BrickService            Service for communicating with bricks.
    | |
    | +-bricklet/                   Bricklet-specific classes.
    | | +-input/                    Bricklets that input data.
    | | | +-illuminance/            Ambient Illuminance sensor bricklets.
    | | |   +-persistence/          Data persistence for illuminance sensors.
    | | |   | +-Illuminance         Configuration data for illuminance sensors.
    | | |   | +-IlluminanceRepo…    Repository for illuminance sensor configs.
    | | |   |
    | | |   +-service/              Services and their utility classes.
    | | |     +-BrickletAmb…Wrapper Wrapper for Tinkerforge ill. sensor objects.
    | | |     +-BrickletAmb…Service Service for providing said objects.
    | | |     +-I…ConnectionExcept… Exception for connection errors.
    | | |     +-I…NotFoundException Exception for non-existent ill. sensors.
    | | |     +-IlluminanceService  Service for communicating with ill. sensors.
    | | |
    | | +-output/                   Bricklets that output data.
    | |   +-ledstrip/               LED strip bricklets.
    | |     +-api/                  API-controller & helpers for LED strips.
    | |     | +-APIError            Object for returning errors.
    | |     | +-BadRequestException Exception for incorrect input to the API.
    | |     | +-DisplayController   MVC Controller for interacting w/ LED strips.
    | |     |
    | |     +-dto/                  Data Transfer to the outside world.
    | |     | +-LEDStripDTO         Data Transfer Object for LED strips.
    | |     | +-LEDStripDTOService  Service for communicating with outside world.
    | |     |
    | |     +-persistence/
    | |     | +-LEDStrip            Configuration data for LED strips.
    | |     | +-LEDStripRepository  Repository for LED strip configs.
    | |     |
    | |     +-service/   
    | |     | +-BrickletLED…Wrapper Wrapper for Tinkerforge LED strip objects.
    | |     | +-BrickletLED…Service Service for providing said objects.
    | |     | +-L…ConnectionExcept… Exception for connection errors.
    | |     | +-L…NotFoundException Exception for non-existent LED strips.
    | |     | +-LEDStripService     Service for communicating with LED strips.
    | |     | +-Sprite1D            Sprite object, for LED strips.
    | |     |
    | |     +-Color                 Color object, for LEDs.
    | |
    | +-config/                     Configuration classes.
    | | +-SpringConfig              Spring configuration.
    | | +-WebConfig                 Configures the web app.
    | |
    | +-jenkins/                    Jenkins-specific classes.
    |   +-persistence/              Data Persistence for Jenkins configs.
    |   | +-JenkinsConfigRepository Repository for Jenkins configs.
    |   | +-JobConfig               Configuration for Jenkins jobs.
    |   | +-ServerConfig            Configuration for Jenkins servers.
    |   |
    |   +-service
    |   | +-JenkinsService          Polls Jenkins servers and feeds statuses to LEDs.
    |   |
    |   +-JenkinsJob                Object for a single returned Jenkins job.
    |   +-JenkinsProperties         Object for Jenkins jobs returned from Jenkins API.
    |   +-Status                    Enum for statuses (OKAY, WARNING & CRITICAL)
    |   +-StatusInformation         Status with additional information.
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
    * All the __*Service__ classes, since they're annotated with @Service, etc.

The **JenkinsService** has a *runScheduled* method, which is annotated with @Scheduled which means it is run every 60
seconds. This method gets a list of all jobs from the Jenkins server(s), compares it to the list loaded from
`jenkins.json` and then instructs the associated LED strips to show the jobs' statuses.  
If so configured, the LED strips will adjust their brightness depending on the ambient illuminance measured by their
associated sensors.

A direct API for reading the LED strips' state and for writing to it (i.e. displaying things on it) is provided at
`/configuration/ledstrips/{name}/display/`.

For further information on this see [the wiki](https://github.com/synyx/sybil-wiki).

### Extending Sybil

If you want to extend Sybil's functionality, here's how you operate it "manually":

The **DisplayController** communicates with the **LEDStripDTOService**, which communicates with the **LEDStripService**
which reads the LED strip configuration from the **LEDStripRepository** and communicates with the Tinkerforge API, the
**BrickService** and the **IlluminanceService**, who each communicate with their own __*Repository__s.

If you want to add support for further hardware, try to replicate this pattern.
