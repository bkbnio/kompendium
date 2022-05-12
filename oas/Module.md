# Module kompendium-oas

## Open Api Specification

This module contains the models that represent the Open Api Specification 3.0 (OAS).

It is a purely domain-based module, with no logic whatsoever.

The specification can be found [here](https://swagger.io/specification).

# Package io.bkbn.kompendium.oas

This is the root package that contains the top level spec that is ultimately serialized into the specification JSON
payload.

# Package io.bkbn.kompendium.oas.common

Here we house data models that will be used across the module.

# Package io.bkbn.kompendium.oas.component

This package correlates to the OAS Component layer, which at the moment is relatively bare bones. It will just contain a
reference to any security schemas, as adding objects here as components severely limits future ability to add cool
features such as route level object validations. Got issues with that, bring it up with the Open API Team :)

# Package io.bkbn.kompendium.oas.info

This package houses the data models for information metadata such as contact and licensing info

# Package io.bkbn.kompendium.oas.path

Now we're getting to the good stuff. This is where the details on each path level operation will live. Your `gets`,
your `puts`, so on and so forth.

# Package io.bkbn.kompendium.oas.payload

This is another good one, this is where the actual payload types live. Request and response body specifications,
parameter details, collection support. That all lives here.

# Package io.bkbn.kompendium.oas.schema

A bit confusingly, in the OAS, there is a distinction between a payload and a schema. You can think of payloads as
containing schemas. So here we dive into the true object level definitions that we want to map out. Models for
supporting collections, dictionaries, polymorphic classes, enums, along with your standard library classes all live
here.

# Package io.bkbn.kompendium.oas.security

Separated from the core schema models are the models that represent security schemas. Despite being referred to as
schemas, and despite living as part of the component data structure, these models are drastically different from your
core data model schemas, and thus earn their own package

# Package io.bkbn.kompendium.oas.server

Here we detail any server information that you wish to attach to your specification
