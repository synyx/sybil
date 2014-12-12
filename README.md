Sybil
=====

synyx Büro - Information & Licht
--------------------------------

A project to illuminate and inform the people in the office(s).
Uses WS2812 LED Strips connected to Tinkerforge bricks.

### Working so far ###

* Outputs Statuses on LED Strips via Tinkerforge (see integration test org.synyx.sybil.out.SingleStatusOnLEDStripTest)
* Outputs arbitrary pixel data " " (see integration test org.synyx.sybil.out.OutputLEDStripTest)

To run, run an integration test, e.g.:

gradlew integTest --tests org.synyx.sybil.out.OutputLEDStripTest

### Structure ###
    src/
    +---integTest/                          Integration tests.
    +---test/                               Unit tests.
    +---main/                               Main.
        +---java/                           Code.
        |   +---org/synyx/sybil/            Package.
        |       +---config/                 Configuration files.
        |       |   +---SpringConfigDev     Spring configuration, Development profile.
        |       +---in/                     Inputs, fairly self-explanatory.
        |       +---out/                    Outputs.
        |           +---Color               Color object, for LEDs.
        |           +---OutputLEDStrip      LED Strip object, communicates with LEDs.
        |           +---SingleStatusOutput  Interface for displaying a single status.
        |           +---SingleStatusOnLEDStrip  Display a single status on a LED Strip.
        +---resources/                      Resources.
            +---logback.xml                 Configures the logback logging engine.
            +---SpringConfigDev.properties  Configuration values for Development profile.
