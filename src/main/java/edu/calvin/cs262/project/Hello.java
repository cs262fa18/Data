package edu.calvin.cs262.project;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiIssuer;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import static com.google.api.server.spi.config.ApiMethod.HttpMethod.GET;

@Api(
    name = "teama",
    version = "v1",
    namespace =
    @ApiNamespace(
        ownerDomain = "project.cs262.calvin.edu",
        ownerName = "project.cs262.calvin.edu",
        packagePath = ""
    ),
    issuers = {
        @ApiIssuer(
            name = "firebase",
            issuer = "https://securetoken.google.com/calvincs262-fall2018-teama",
            jwksUri =
                "https://www.googleapis.com/robot/v1/metadata/x509/securetoken@system"
                    + ".gserviceaccount.com"
        )
    }
)

/**
 * This class implements a simple hello-world endpoint using Google Endpoints.
 */
public class Hello {

    /**
     * This method returns a simple hello-world message.
     *
     * N.b., a Google Endpoint must return an entity (not, e.g., a String), so the method
     * returns a "hello" person object.
     *
     * @return a hello-world entity in JSON format
     */
    @ApiMethod(httpMethod=GET)
    public Time hello() {
        return new Time(-1, null, null, null, -1, -1);
    }

}
