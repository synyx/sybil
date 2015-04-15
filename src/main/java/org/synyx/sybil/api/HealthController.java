package org.synyx.sybil.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import org.synyx.sybil.in.Status;

import java.util.HashMap;
import java.util.Map;


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

    private static Map<String, Status> healthSources = new HashMap<>();

    public static Status getHealth() {

        Status health = Status.OKAY;

        for (Status status : healthSources.values()) {
            if (status != null && status.ordinal() > health.ordinal()) {
                health = status;
            }
        }

        return health;
    }


    public static void setHealth(Status newHealth, String source) {

        if (newHealth == Status.OKAY) {
            healthSources.remove(source);
        } else {
            healthSources.put(source, newHealth);
        }
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Status> health() {

        return new ResponseEntity<>(getHealth(), HttpStatus.OK);
    }
}
