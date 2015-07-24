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
    | +-AttributeEmptyException     Custom exception, when accessing undef. attributes.
    | +-DeviceDomain                Interface all Tinkerforge devices inherit from.
    | +-LoadFailedException         Exception for when loading a file fails.
    | |
    | +-brick/                      Brick-specific classes.
    | | +-domain/                   Domain classes for bricks.
    | | | +-BrickDomain             Domain for Tinkerforge brick configurations.
    | | | +-BrickDTO                Data Transfer Object for bricks. 
    | | |
    | | +-BrickConnectionException  Exception for connection errors.
    | | +-BrickDTOService           Creates pre-configured Brick DTOs.
    | | +-BrickNotFoundException    Exception for non-existent bricks.
    | | +-BrickService              Accepts Brick DTOs and handles them.
    | |
    | +-bricklet/                   Bricklet-specific classes.
    | | +-input/                    Bricklets that input data.
    | | | +-illuminance/            Ambient Illuminance sensor bricklets.
    | | |   +-domain/               Domain classes for illuminance sensors.
    | | |   | +-IlluminanceDomain   Domain for illuminance sensors.
    | | |   | +-IlluminanceDTO      Data Transfer Object for ill. sensors.
    | | |   |
    | | |   +-BrickletAmbie…Wrapper Wrapper for Tinkerforge ill. sensor objects.
    | | |   +-I…ConnectionException Exception for connection errors.
    | | |   +-IlluminanceDTOService Creates pre-configured ill. sensor DTOs.
    | | |   +-Ill…NotFoundException Exception for non-existent ill. sensors.
    | | |   +-IlluminanceService    Accepts Ill. DTOs and handles them.
    | | |
    | | +-output/                   Bricklets that output data.
    | | | +-ledstrip/               LED strip bricklets.
    | | |   +-api/                  API-controller & helpers for LED strips.
    | | |   | +-APIError            Object for returning errors.
    | | |   | +-DisplayController   MVC Controller for interacting w/ LED strips.
    | | |   |
    | | |   +-domain/               Domain classes for LED strips.
    | | |   | +-LEDStripDomain      Domain for LED strips.
    | | |   | +-LEDStripDTO         Data Transfer Object for LED strips.
    | | |   |
    | | |   +-BrickletLEDS…Wrapper  Wrapper for Tinkerforge LED strip objects.
    | | |   +-Color                 Color object, for LEDs.
    | | |   +-L…ConnectionException Exception for connection errors.
    | | |   +-LEDStripDTOService    Creates pre-configured LED strip DTOs.
    | | |   +-LED…NotFoundException Exception for non-existent LED strips.
    | | |   +-LEDStripService       Accepts LED strip DTOs and handles them.
    | | |   +-Sprite1D              Sprite object, for LED strips.
    | | |
    | | +-BrickletProvider          Provides Tinkerforge bricklet objects (or wrappers). 
    | |
    | +-config/                     Configuration classes.
    | | +-SpringConfig              Spring configuration.
    | | +-WebConfig                 Configures the web app.
    | |
    | +-jenkins/                    Jenkins-specific classes.
    |   +-domain/                   Jenkins-specific domains.
    |   | +-ConfiguredJob           Object for job configuration.
    |   | +-ConfiuredServer         Object for server configuration.
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
    * All the __*Service__ classes, since they're annotated with @Service.

The **JenkinsService** has a *runScheduled* method, which is annotated with @Scheduled which means it is run every 60
seconds. This method gets a list of all jobs from the Jenkins server(s), compares it to the list loaded from
`jenkins.json` and then instructs the associated LED strips to show the jobs' statuses. 

A direct API for reading the LED strips' state and for writing to it (i.e. displaying things on it) is provided at
`/configuration/ledstrips/{name}/display/`.

For further information on this see [the wiki](https://github.com/synyx/sybil-wiki).

### Extending Sybil

If you want to extend Sybil's functionality, here's how you operate it "manually":

First, use the **LEDStripDTOService**'s *getDTO* method, to get a DTO, pre-configured with the configuration loaded from
`ledstrips.json`. Then use the DTO's *setSprite* or *setStatus* methods to add either a sprite or a status to the DTO,
and pass it to the corresponding method of the **LEDStripServic**, i.e. either *handleSprite* or *handleStatus*.

If you want to add support for further hardware, try to replicate this pattern.
