package com.wheresapp.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.wheresapp.server.domain.CallServer;
import com.wheresapp.server.domain.CallStateServer;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 01/12/2014.
 */
@Api(name = "cronApi",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class CronEndpoint {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "check", path = "/check")
    public void contacList() {
        Iterable<CallServer> queryTransmit = ofy().load().type(CallServer.class).filter("state", CallStateServer.TRANSMIT).iterable();
        for (CallServer call : queryTransmit) {
            if ((System.currentTimeMillis() - call.getDateUpdate().getValue()) > 60000 ) {
                call.setDateEnd(new DateTime(System.currentTimeMillis()));
                call.setState(CallStateServer.FAILED);
                ofy().save().entity(call).now();
            }
        }

        Iterable<CallServer> queryWait = ofy().load().type(CallServer.class).filter("state", CallStateServer.WAIT).iterable();
        for (CallServer call : queryWait) {
            if ((System.currentTimeMillis() - call.getDateUpdate().getValue()) > 120000 ) {
                call.setDateEnd(new DateTime(System.currentTimeMillis()));
                call.setState(CallStateServer.FAILED);
                ofy().save().entity(call).now();
            }
        }
    }

}
