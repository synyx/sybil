package org.synyx.sybil.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.synyx.sybil.in.Status;


/**
 * HealthController.
 *
 * <p>If there is a non-recoverable error, this shows this to the world. Helps with Nagios monitoring.</p>
 *
 * @author  Tobias Theuer - theuer@synyx.de
 */

@Controller
@RequestMapping("/health")
public class HealthController {

    private static Status health = Status.OKAY;

    public static Status getHealth() {

        return health;
    }


    public static void setHealth(Status newHealth) {

        health = newHealth;
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Status> health() {

        return new ResponseEntity<>(health, HttpStatus.OK);
    }
}
