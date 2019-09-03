// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.inventory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.rest.client.RestClientBuilder;

import io.openliberty.guides.inventory.client.SystemClient;
import io.openliberty.guides.inventory.client.UnknownUrlException;
import io.openliberty.guides.inventory.client.UnknownUrlExceptionMapper;
import io.openliberty.guides.inventory.model.InventoryList;

@RequestScoped
@Path("/systems")
public class InventoryResource {

  private final String SYS_HTTP_PORT = System.getProperty("system.http.port");
    
  @Inject
  InventoryManager manager;

  @GET
  @Path("/{hostname}")
  @Produces(MediaType.APPLICATION_JSON)
  // tag::fallback[]
  @Fallback(fallbackMethod = "getPropertiesFallback")
  // end::fallback[]
  // tag::mpRetry[]
  @Retry(maxRetries=3, retryOn=WebApplicationException.class)
  // end::mpRetry[]
  // tag::getPropertiesForHost[]
  public Response getPropertiesForHost(@PathParam("hostname") String hostname) 
         throws WebApplicationException, ProcessingException, UnknownUrlException {
  // end::getPropertiesForHost[] 
    
    String customURLString = "http://" + hostname + ":" + SYS_HTTP_PORT + "/system";
    URL customURL = null;
    Properties props = null;
    try {
        customURL = new URL(customURLString);
        SystemClient systemClient = RestClientBuilder.newBuilder()
                .baseUrl(customURL)
                .register(UnknownUrlExceptionMapper.class)
                .build(SystemClient.class);
        // tag::getProperties[]
        props = systemClient.getProperties();
        // end::getProperties[]
    } catch (MalformedURLException e) {
      System.err.println("The given URL is not formatted correctly: " + customURLString);
    }
    
    if (props == null) {
      return Response.status(Response.Status.NOT_FOUND)
                     .entity("ERROR: Unknown hostname or the system service may not be "
                             + "running on " + hostname)
                     .build();
    }

    manager.add(hostname, props);
    return Response.ok(props).build();
  }

  // tag::fallbackMethod[]
  @Produces(MediaType.APPLICATION_JSON)
  public Response getPropertiesFallback(@PathParam("hostname") String hostname) {
      Properties props = new Properties();
      props.put("error", "Unknown hostname or the system service may not be running.");
        return Response.ok(props)
                  .header("X-From-Fallback", "yes")
                  .build();
  }
  // end::fallbackMethod[]
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public InventoryList listContents() {
    return manager.list();
  }

  @POST
  @Path("/reset")
  public void reset() {
    manager.reset();
  }
}
