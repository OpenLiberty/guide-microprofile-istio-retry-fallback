// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017, 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::copyright[]
package io.openliberty.guides.system;

// CDI
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.ApplicationScoped;

import javax.ws.rs.GET;
// JAX-RS
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.faulttolerance.Fallback;
import java.io.IOException;
import org.eclipse.microprofile.faulttolerance.Retry;



//@RequestScoped
@ApplicationScoped
@Path("/properties")
public class SystemResource {

  private int count = 0;

  @Fallback(fallbackMethod = "getPropertiesFallback")
  @Retry(maxRetries=3, retryOn=IOException.class)
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties() throws IOException {
    count++;
    getPropertiesWithException();
    return Response.ok(System.getProperties())
        .header("X-Pod-Name", System.getenv("HOSTNAME"))
        .header("X-From-Fallback", "no")
        .build();
  } 

public void getPropertiesWithException() throws IOException{
  throw(new IOException("try"));
}

public Response getPropertiesFallback() {
  return Response.ok(System.getProperties())
        .header("X-Pod-Name", System.getenv("HOSTNAME"))
        .header("X-From-Fallback", "yes")
        .build(); 
}

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("count")  
  public Response getCount() {
    return Response.ok(count)
      .build();
  }

}
