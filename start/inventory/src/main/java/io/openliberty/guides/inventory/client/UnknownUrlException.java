// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
// end::copyright[]
// tag::exception[]
package io.openliberty.guides.inventory.client;

public class UnknownUrlException extends Exception {

  private static final long serialVersionUID = 1L;

  public UnknownUrlException() {
    super();
  }

  public UnknownUrlException(String message) {
    super(message);
  }
}
// end::exception[]
