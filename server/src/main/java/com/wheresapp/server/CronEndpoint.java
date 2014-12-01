package com.wheresapp.server;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.api.server.spi.response.BadRequestException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.wheresapp.domain.CallState;
import com.wheresapp.domain.ContactClient;
import com.wheresapp.domain.ContactList;

import static com.wheresapp.server.OfyService.ofy;

/**
 * Created by Sergio on 01/12/2014.
 */
@Api(name = "cron",  version = "v1", namespace = @ApiNamespace(ownerDomain = "server.wheresapp.com", ownerName = "server.wheresapp.com", packagePath=""))
public class CronEndpoint {

    /** Api Keys can be obtained from the google cloud console */
    private static final String API_KEY = System.getProperty("gcm.api.key");

    @ApiMethod(name = "check", path = "/check")
    public void contacList() {
        Iterable<Call> queryTransmit = ofy().load().type(Call.class).filter("state", CallState.TRANSMIT).iterable();
        for (Call call : queryTransmit) {
            if ((System.currentTimeMillis() - call.getDateUpdate().getValue()) > 60000 ) {
                call.setDateEnd(new DateTime(System.currentTimeMillis()));
                call.setState(CallState.FAILED);
                ofy().save().entity(call).now();
            }
        }

        Iterable<Call> queryWait = ofy().load().type(Call.class).filter("state", CallState.WAIT).iterable();
        for (Call call : queryWait) {
            if ((System.currentTimeMillis() - call.getDateUpdate().getValue()) > 120000 ) {
                call.setDateEnd(new DateTime(System.currentTimeMillis()));
                call.setState(CallState.FAILED);
                ofy().save().entity(call).now();
            }
        }
    }

}
