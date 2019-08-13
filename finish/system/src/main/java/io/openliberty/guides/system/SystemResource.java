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

import java.io.IOException;
import java.lang.Thread;
import java.lang.InterruptedException;



// CDI
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
// JAX-RS
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/properties")
public class SystemResource {

	private static int count = 0;
	
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getProperties() throws Exception {
	  count++;
    slow();
    // tag::throwException[]
	  //getPropertiesThrowException();
    // end::throwException[]
    return Response.ok(System.getProperties())
      .header("X-Pod-Name", System.getenv("HOSTNAME"))
      .build();
  } 
  
  public void getPropertiesThrowException() throws IOException {
	  throw(new IOException("try"));
  }
  
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("count")  
  public Response getCount() {
    return Response.ok(count)
      .build();
  }

  private boolean slow() throws InterruptedException {
    if (count==0 || Math.random() > 0.6) {
        // sleep when count==0 so we know retry will happen at least once
        // 0.6 - approx 40% chance it will sleep
        // meaning that since maxRetries=4, 200 response should happen no matter what
        Thread.sleep(2100);
        return true;
    }
    return false;
  }
}
